/*
 * Copyright 2015-2016 Spiracle Studios. All Rights Reserved.
 */

package com.spiraclestudios.autoskola.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;

import com.google.android.gms.ads.AdView;

import com.spiraclestudios.autoskola.DbContract;
import com.spiraclestudios.autoskola.DbHelper;
import com.spiraclestudios.autoskola.Helper;
import com.spiraclestudios.autoskola.R;
import com.spiraclestudios.autoskola.interfaces.IBaseActivity;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnLongClick;
import timber.log.Timber;

public class TestActivity extends BaseActivity
        implements IBaseActivity {

    public String mActivityName = "TestActivity";

    public final static String EXTRA_GROUP =
            "com.spiraclestudios.autoskola.GROUP";
    public final static String EXTRA_INDEX =
            "com.spiraclestudios.autoskola.INDEX";
    public final static String EXTRA_USES_QUESTIONS =
            "com.spiraclestudios.autoskola.USE_QUESTIONS";
    public final static String EXTRA_USES_ROAD_SIGNS =
            "com.spiraclestudios.autoskola.USE_ROAD_SIGNS";
    public final static String EXTRA_USES_INTERSECTIONS =
            "com.spiraclestudios.autoskola.USE_INTERSECTIONS";
    public final static String EXTRA_MARK_CORRECT_ANSWERS =
            "com.spiraclestudios.autoskola.MARK_CORRECT_ANSWERS";

    AdView mAdView;

    // [Test info]
    public int testId = 1;
    public int testVersion = 1;
    public ArrayList<Integer> allQuestionIds = new ArrayList<>();
    public int currentQuestionIdx = 1;
    public boolean usesQuestions;
    public boolean usesRoadSigns;
    public boolean usesIntersections;
    public int questionsCount;
    public int maxPoints;
    public int amountCorrect;

    // [Internal]
    // Did the user evaluate the test results?
    private boolean finished = false;
    private boolean allQuestionsAnswered = false;
    private long elapsedTime;
    private int amountAnswered;

    // [Test Settings - Internal]
    public boolean allowClickingOnAnswers = true;
    public boolean markCorrectAnswers = false;
    public boolean colorCorrectAnswers = false;

    // [Cached data from database]
    // Questions after filtering by type
    List<Integer> questionIds;
    List<Integer> questionTypes;
    List<String> questionsList;
    List<String> imagesList;
    List<Integer> correctAnswersList;
    List<String> answer1List;
    List<String> answer2List;
    List<String> answer3List;
    List<Integer> pointsList;

    // [Current data used by the layout views]
    List<Integer> chosenAnswersList = new ArrayList<>();
    public String mText;
    public Drawable mImage;
    public int mPoints = 0;
    public int mCorrectAnswer = 0;
    public String mAnswer1;
    public String mAnswer2;
    public String mAnswer3;

    // [Miscellaneous]
    private Animator mExpandAnimator;
    // The system "short" animation time duration, in milliseconds. This
    // duration is ideal for subtle animations or animations that occur
    // very frequently.
    private int mShortAnimationDuration;

    // [Layout views]
    @Bind(R.id.question_text)
    TextView question_text;
    //@Bind(R.id.intersection_canvas)
    //IntersectionCanvas intersection_canvas;
    @Bind(R.id.question_image)
    ImageButton question_image;
    @Bind(R.id.expanded_image)
    ImageView expanded_image;
    @Bind(R.id.answer1)
    Button question_answer1;
    @Bind(R.id.answer2)
    Button question_answer2;
    @Bind(R.id.answer3)
    Button question_answer3;
    @Bind(R.id.next_question)
    ImageButton next_question;
    @Bind(R.id.previous_question)
    ImageButton previous_question;
    @Bind(R.id.points_value)
    TextView points_value;
    @Bind(R.id.question_counter)
    TextView question_counter;
    @Bind(R.id.elapsed_time)
    Chronometer elapsed_time;

    public String getActivityName() {
        return mActivityName;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Helper.setTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        ButterKnife.bind(this);

        // Keep the screen on
        if (PreferenceManager.getDefaultSharedPreferences(this)
                .getBoolean("keep_screen_on_switch", true)) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }

        // Retrieve and cache the system's default "short" animation time.
        mShortAnimationDuration = getResources().getInteger(
                android.R.integer.config_shortAnimTime);

        // Read extras from the intent
        Intent intent = getIntent();
        int selectedIndexId = intent.getIntExtra(EXTRA_INDEX, 1);
        Helper.Groups selectedGroup = (Helper.Groups) intent.getSerializableExtra(EXTRA_GROUP);
        usesQuestions = intent.getBooleanExtra(EXTRA_USES_QUESTIONS, true);
        usesRoadSigns = intent.getBooleanExtra(EXTRA_USES_ROAD_SIGNS, true);
        usesIntersections = intent.getBooleanExtra(EXTRA_USES_INTERSECTIONS, true);
        markCorrectAnswers = intent.getBooleanExtra(EXTRA_MARK_CORRECT_ANSWERS, false);

        // Decide which test to open
        String groupString;
        int testIndexToUse;
        Resources resources = getResources();

        // [Index]
        // If random was chosen
        if (selectedGroup != null) {
            if (selectedGroup == Helper.Groups.AB) {
                // Random number in range of 1-35
                testIndexToUse = new Random().nextInt(36 - 1) + 1;
            } else {
                // Random number in range of 36-60
                testIndexToUse = new Random().nextInt(61 - 36) + 36;
            }
        } else {
            // Int between 1-60
            testIndexToUse = selectedIndexId;
        }

        // Create and add the TestActivityFragment to the layout
        /*TestActivityFragment testActivityFragment = TestActivityFragment
                .newInstance(testIndexToUse, usesQuestions, usesRoadSigns, usesIntersections
                        , markCorrectAnswers);
        getSupportFragmentManager().beginTransaction().add(
                R.id.fragment_container, testActivityFragment).commit();*/

        // Setup Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Returns "Skupina A,B" or "Skupina C,D,T"
        groupString = (Helper.getGroupFromTestIndex(
                testIndexToUse) == Helper.Groups.AB) ? resources.getString(R.string.group_ab) : resources.getString(R.string.group_cdt);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("Test #" + testIndexToUse);
            actionBar.setSubtitle(groupString);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        // Setup TabLayout
        //TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        //tabLayout.addTab(tabLayout.newTab().setText(R.string.title_test));
        //tabLayout.addTab(tabLayout.newTab().setText(R.string.title_vyhlaska));
        //tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        /*final ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
        final TestActivityPagerAdapter adapter = new TestActivityPagerAdapter
                (getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });*/

        // TODO: Remove after implementing intersections
        if (usesIntersections) {
            Toast.makeText(this, R.string.toast_intersections_not_yet_implemented,
                    Toast.LENGTH_SHORT)
                    .show();
        }

        if (markCorrectAnswers) {
            colorCorrectAnswers = true;
            allowClickingOnAnswers = false;
        }

        setTest(testIndexToUse);

        // Load an ad.
        mAdView = (AdView) findViewById(R.id.adView);
        Helper.loadAd(this, mAdView);

        Helper.initializeDebugDrawer(this);
    }

    @Override
    public void onPause() {
        mAdView.pause();
        pauseTimer();

        super.onPause();
    }

    @Override
    public void onResume() {
        mAdView.resume();
        if (!finished && !markCorrectAnswers)
            resumeTimer();

        super.onResume();
    }

    @Override
    public void onDestroy() {
        mAdView.destroy();

        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!markCorrectAnswers) {
            getMenuInflater().inflate(R.menu.test_activity, menu);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_evaluate) {
            evaluateResults();
            return true;
        } else if (id == R.id.action_vyhlaska) {
            Toast.makeText(this, R.string.toast_not_yet_implemented, Toast.LENGTH_SHORT).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Expand the question_image on click.
     */
    @OnClick(R.id.question_image)
    public void question_image_onClick() {
        // If there's an animation in progress, cancel it
        // immediately and proceed with this one.
        if (mExpandAnimator != null) {
            mExpandAnimator.cancel();
        }

        // Load the high-resolution "zoomed-in" image.
        expanded_image.setImageDrawable(mImage);

        // Calculate the starting and ending bounds for the zoomed-in image.
        // This step involves lots of math. Yay, math.
        final Rect startBounds = new Rect();
        final Rect finalBounds = new Rect();
        final Point globalOffset = new Point();

        // The start bounds are the global visible rectangle of the thumbnail,
        // and the final bounds are the global visible rectangle of the container
        // view. Also set the container view's offset as the origin for the
        // bounds, since that's the origin for the positioning animation
        // properties (X, Y).
        question_image.getGlobalVisibleRect(startBounds);
        findViewById(R.id.content)
                .getGlobalVisibleRect(finalBounds, globalOffset);
        startBounds.offset(-globalOffset.x, -globalOffset.y);
        finalBounds.offset(-globalOffset.x, -globalOffset.y);

        // Adjust the start bounds to be the same aspect ratio as the final
        // bounds using the "center crop" technique. This prevents undesirable
        // stretching during the animation. Also calculate the start scaling
        // factor (the end scaling factor is always 1.0).
        float startScale;
        if ((float) finalBounds.width() / finalBounds.height()
                > (float) startBounds.width() / startBounds.height()) {
            // Extend start bounds horizontally
            startScale = (float) startBounds.height() / finalBounds.height();
            float startWidth = startScale * finalBounds.width();
            float deltaWidth = (startWidth - startBounds.width()) / 2;
            startBounds.left -= deltaWidth;
            startBounds.right += deltaWidth;
        } else {
            // Extend start bounds vertically
            startScale = (float) startBounds.width() / finalBounds.width();
            float startHeight = startScale * finalBounds.height();
            float deltaHeight = (startHeight - startBounds.height()) / 2;
            startBounds.top -= deltaHeight;
            startBounds.bottom += deltaHeight;
        }

        // Hide the thumbnail and show the zoomed-in view. When the animation
        // begins, it will position the zoomed-in view in the place of the
        // thumbnail.
        question_image.setAlpha(0f);
        expanded_image.setVisibility(View.VISIBLE);

        // Set the pivot point for SCALE_X and SCALE_Y transformations
        // to the top-left corner of the zoomed-in view (the default
        // is the center of the view).
        expanded_image.setPivotX(0f);
        expanded_image.setPivotY(0f);

        // Construct and run the parallel animation of the four translation and
        // scale properties (X, Y, SCALE_X, and SCALE_Y).
        AnimatorSet set = new AnimatorSet();
        set
                .play(ObjectAnimator.ofFloat(expanded_image, View.X,
                        startBounds.left, finalBounds.left))
                .with(ObjectAnimator.ofFloat(expanded_image, View.Y,
                        startBounds.top, finalBounds.top))
                .with(ObjectAnimator.ofFloat(expanded_image, View.SCALE_X,
                        startScale, 1f)).with(ObjectAnimator.ofFloat(expanded_image,
                View.SCALE_Y, startScale, 1f));
        set.setDuration(mShortAnimationDuration);
        set.setInterpolator(new DecelerateInterpolator());
        set.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mExpandAnimator = null;
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                mExpandAnimator = null;
            }
        });
        set.start();
        mExpandAnimator = set;

        // Upon clicking the zoomed-in image, it should zoom back down
        // to the original bounds and show the thumbnail instead of
        // the expanded image.
        final float startScaleFinal = startScale;
        expanded_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mExpandAnimator != null) {
                    mExpandAnimator.cancel();
                }

                // Animate the four positioning/sizing properties in parallel,
                // back to their original values.
                AnimatorSet set = new AnimatorSet();
                set.play(ObjectAnimator
                        .ofFloat(expanded_image, View.X, startBounds.left))
                        .with(ObjectAnimator
                                .ofFloat(expanded_image,
                                        View.Y, startBounds.top))
                        .with(ObjectAnimator
                                .ofFloat(expanded_image,
                                        View.SCALE_X, startScaleFinal))
                        .with(ObjectAnimator
                                .ofFloat(expanded_image,
                                        View.SCALE_Y, startScaleFinal));
                set.setDuration(mShortAnimationDuration);
                set.setInterpolator(new DecelerateInterpolator());
                set.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        question_image.setAlpha(1f);
                        expanded_image.setVisibility(View.GONE);
                        mExpandAnimator = null;
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {
                        question_image.setAlpha(1f);
                        expanded_image.setVisibility(View.GONE);
                        mExpandAnimator = null;
                    }
                });
                set.start();
                mExpandAnimator = set;
            }
        });
    }

    /**
     * Copy question text to clipboard.
     */
    @OnLongClick(R.id.question_text)
    public boolean question_text_onLongClick() {
        ClipboardManager clipboard = (ClipboardManager) this.getSystemService(Context.CLIPBOARD_SERVICE);

        String label = String.format(getString(R.string.clip_label_question), currentQuestionIdx);
        ClipData clip = ClipData.newPlainText(label, question_text.getText().toString());

        clipboard.setPrimaryClip(clip);

        Toast.makeText(this, R.string.toast_question_was_copied, Toast.LENGTH_SHORT).show();
        return true;
    }

    /**
     * Copy answer text to clipboard.
     */
    @OnLongClick({R.id.answer1, R.id.answer2, R.id.answer3})
    public boolean answers_onLongClick(Button button) {
        ClipboardManager clipboard = (ClipboardManager) this.getSystemService(Context.CLIPBOARD_SERVICE);

        String label = getString(R.string.clip_label_answer);
        ClipData clip = ClipData.newPlainText(label, button.getText().toString());

        clipboard.setPrimaryClip(clip);

        Toast.makeText(this, R.string.toast_answer_was_copied, Toast.LENGTH_SHORT).show();
        return true;
    }

    /**
     * Moves to the next question and highlights it.
     */
    @OnClick(R.id.next_question)
    public void nextQuestion() {
        if (currentQuestionIdx < questionsList.size()) {
            changeQuestion(currentQuestionIdx + 1);
        } else {
            highlightAnswer(chosenAnswersList.get(currentQuestionIdx - 1));
        }
    }

    /**
     * Moves to the previous question and highlights it.
     */
    @OnClick(R.id.previous_question)
    public void previousQuestion() {
        if (currentQuestionIdx > 1)
            changeQuestion(currentQuestionIdx - 1);
    }

    @OnClick(R.id.answer1)
    public void answer1_onClick() {
        answerChosen(1);
    }

    @OnClick(R.id.answer2)
    public void answer2_onClick() {
        answerChosen(2);
    }

    @OnClick(R.id.answer3)
    public void answer3_onClick() {
        answerChosen(3);
    }

    /**
     * Check or un-check an answer.
     *
     * @param answer The index of the answer button.
     */
    private void answerChosen(int answer) {
        if (!allowClickingOnAnswers) {
            return;
        }

        int currentAnswer = chosenAnswersList.get(currentQuestionIdx - 1);

        // Un-check the answer if the user clicks on the current answer.
        if (currentAnswer == answer) {
            amountAnswered--;
            allQuestionsAnswered = false;
            chosenAnswersList.set(currentQuestionIdx - 1, 0);
            highlightAnswer(0);
        }
        // If there is currently no answer or a different answer than the current one was chosen
        else {
            if (currentAnswer == 0) {
                amountAnswered++;
            }
            chosenAnswersList.set(currentQuestionIdx - 1, answer);
            nextQuestion();
        }

        // If the toast wasn't shown yet, then show it.
        if (!allQuestionsAnswered && amountAnswered == questionsCount) {
            allQuestionsAnswered = true;
            Toast.makeText(this, R.string.toast_all_questions_answered, Toast.LENGTH_SHORT)
                    .show();
        }
    }

    /**
     * Calculate points, handle test review and show the results activity.
     */
    public void evaluateResults() {
        if (!finished) {
            // Calculate scored points
            amountCorrect = 0;
            for (int i = 0; i < questionsCount; i++) {
                if (chosenAnswersList.get(i).equals(correctAnswersList.get(i))) {
                    addPoints(pointsList.get(i));
                    amountCorrect++;
                }
            }

            // Mark the correct answers for if the user comes back to the test
            // after viewing the results
            markCorrectAnswers = true;
            colorCorrectAnswers = true;
            allowClickingOnAnswers = false;
            pauseTimer();
            highlightAnswer(chosenAnswersList.get(currentQuestionIdx - 1));

            finished = true;
        }

        // Start ResultsActivity
        Intent intent = new Intent(this, ResultsActivity_.class);
        intent.putExtra(ResultsActivity.EXTRA_TEST_ID, testId);
        intent.putExtra(ResultsActivity.EXTRA_TEST_VERSION, testVersion);
        intent.putExtra(ResultsActivity.EXTRA_USES_QUESTIONS, usesQuestions);
        intent.putExtra(ResultsActivity.EXTRA_USES_ROAD_SIGNS, usesRoadSigns);
        intent.putExtra(ResultsActivity.EXTRA_USES_INTERSECTIONS, usesIntersections);
        intent.putExtra(ResultsActivity.EXTRA_POINTS, mPoints);
        intent.putExtra(ResultsActivity.EXTRA_MAX_POINTS, maxPoints);
        intent.putExtra(ResultsActivity.EXTRA_ELAPSED_TIME, getElapsedTime());
        intent.putExtra(ResultsActivity.EXTRA_ELAPSED_TIME_TEXT, elapsed_time.getText().toString());
        intent.putIntegerArrayListExtra(ResultsActivity.EXTRA_ANSWERS,
                (ArrayList<Integer>) chosenAnswersList);
        intent.putExtra(ResultsActivity.EXTRA_CORRECT, amountCorrect);
        intent.putExtra(ResultsActivity.EXTRA_INCORRECT, questionsCount - amountCorrect);

        startActivity(intent);
    }

    /**
     * Retrieves data from db, sets all the text and onClickListeners, restarts everything.
     */
    public void setTest(int id) {
        testId = id;

        Crashlytics.getInstance().core.setInt("current_test", testId);

        // Setup the Database
        DbHelper dbHelper = new DbHelper(this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        //// [Tests] ////

        // Get latest version of this test
        Cursor cTest = db.rawQuery(
                "SELECT " + DbContract.Tests.COLUMN_QUESTIONS + ", " +
                        DbContract.Tests.COLUMN_VERSION_CODE + " FROM " +
                        DbContract.Tests.TABLE_NAME + " WHERE " +
                        DbContract.Tests.COLUMN_TEST_ID + " = ?", new String[]
                        {Integer.toString(testId)});

        cTest.moveToFirst();

        // The whole 'questions' string from the Tests table
        String questionsString = cTest.getString(cTest.getColumnIndexOrThrow(
                DbContract.Tests.COLUMN_QUESTIONS));

        // If this test has no questions_checkbox assigned, show a toast and return to MainActivity
        if (questionsString == null || questionsString.isEmpty()) {
            Toast.makeText(this, R.string.toast_test_is_empty, Toast.LENGTH_LONG).show();

            // TODO: Shouldn't this be replaced with simply finish()?
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            return;
        }

        // Split test questions
        String[] questionIdsSplit = questionsString.split(",");

        // All questions in the test (every type of question)
        for (String question : questionIdsSplit) {
            allQuestionIds.add(Integer.parseInt(question));
        }

        testVersion = cTest.getInt(cTest.getColumnIndexOrThrow(
                DbContract.Tests.COLUMN_VERSION_CODE));

        cTest.close();


        //// [Questions] ////

        // Selector for the question type.
        String typeSelector = "AND (";
        List<String> concatenation = new ArrayList<>();

        if (usesQuestions) {
            concatenation.add(DbContract.Questions.COLUMN_TYPE + "=0");
        }
        if (usesRoadSigns) {
            concatenation.add(DbContract.Questions.COLUMN_TYPE + "=1");
        }
        if (usesIntersections) {
            concatenation.add(DbContract.Questions.COLUMN_TYPE + "=2");
        }

        for (int i = 0; i < concatenation.size(); i++) {
            String s = concatenation.get(i);

            typeSelector += s;

            if (i < concatenation.size() - 1) {
                typeSelector += " OR ";
            }
        }
        typeSelector += ")";

        // Get the Filtered Questions for this test version
        String query = "SELECT * FROM " + DbContract.Questions.TABLE_NAME +
                " WHERE " + DbContract.Questions.COLUMN_QUESTION_ID + " IN (" + questionsString + ") AND " + DbContract.Questions.COLUMN_VERSION + " <= ? " + typeSelector;

        Cursor cFilteredQuestions = db.rawQuery(query, new String[]{Integer.toString(testVersion)});

        questionIds = new ArrayList<>();
        questionTypes = new ArrayList<>();
        questionsList = new ArrayList<>();
        imagesList = new ArrayList<>();
        correctAnswersList = new ArrayList<>();
        answer1List = new ArrayList<>();
        answer2List = new ArrayList<>();
        answer3List = new ArrayList<>();
        pointsList = new ArrayList<>();

        for (cFilteredQuestions.moveToFirst(); !cFilteredQuestions.isAfterLast(); cFilteredQuestions.moveToNext()) {
            questionIds.add(cFilteredQuestions.getInt(cFilteredQuestions.
                    getColumnIndexOrThrow(DbContract.Questions.COLUMN_QUESTION_ID)));

            questionTypes.add(cFilteredQuestions.getInt(cFilteredQuestions.
                    getColumnIndexOrThrow(DbContract.Questions.COLUMN_TYPE)));

            questionsList.add(cFilteredQuestions.getString(cFilteredQuestions.
                    getColumnIndexOrThrow(DbContract.Questions.COLUMN_QUESTION)));

            imagesList.add(cFilteredQuestions.getString(cFilteredQuestions.
                    getColumnIndexOrThrow(DbContract.Questions.COLUMN_IMAGE)));

            correctAnswersList.add(cFilteredQuestions.getInt(cFilteredQuestions.
                    getColumnIndexOrThrow(DbContract.Questions.COLUMN_CORRECT_ANSWER)));

            answer1List.add(cFilteredQuestions.getString(cFilteredQuestions.
                    getColumnIndexOrThrow(DbContract.Questions.COLUMN_ANSWER_1)));

            answer2List.add(cFilteredQuestions.getString(cFilteredQuestions.
                    getColumnIndexOrThrow(DbContract.Questions.COLUMN_ANSWER_2)));

            answer3List.add(cFilteredQuestions.getString(cFilteredQuestions.
                    getColumnIndexOrThrow(DbContract.Questions.COLUMN_ANSWER_3)));

            int points = cFilteredQuestions.getInt(cFilteredQuestions.
                    getColumnIndexOrThrow(DbContract.Questions.COLUMN_POINTS));
            pointsList.add(points);
            maxPoints += points;
        }

        cFilteredQuestions.close();
        db.close();
        dbHelper.close();

        // Get count of questions and amount of max points
        questionsCount = questionIds.size();

        // Initialize the chosenAnswersList to the right size
        for (int i = 0; i < questionsCount; i++) {
            chosenAnswersList.add(
                    (markCorrectAnswers) ? correctAnswersList.get(i) : 0);
        }

        // If previewing correct answers, display R.string.correct_answers_caps in elapsed_time
        if (!markCorrectAnswers) {
            restartTimer();
        } else {
            elapsed_time.setText(getResources().getString(R.string.correct_answers_caps));
            // colorSecondaryText dark
            elapsed_time.setTextColor(Color.parseColor("#b2ffffff"));
            elapsed_time.setTextSize(14);
        }

        changeQuestion(1);
    }

    public void changeQuestion(int index) {
        currentQuestionIdx = index;
        int questionId = currentQuestionIdx - 1;

        setQuestionText(questionsList.get(questionId));
        setImage(imagesList.get(questionId));
        setCorrectAnswer(correctAnswersList.get(questionId));
        setPointsValue(pointsList.get(questionId));
        setAnswers(answer1List.get(questionId), answer2List.get(questionId),
                answer3List.get(questionId));
        setQuestionCounter(currentQuestionIdx);
        highlightAnswer(chosenAnswersList.get(questionId));

        // Show or hide the image view based on question type.
        if (questionTypes.get(questionId) == 0) {
            question_image.setVisibility(View.GONE);
        } else {
            question_image.setVisibility(View.VISIBLE);
            // TODO: Check this code.
            /*if (questionTypes.get(questionId) == 1) {

            } else {
                // TODO: set top margin to 0 for intersections.
                //    question_image.
            }*/
        }

        // CANVAS-CODE
        // Show or hide the canvas based on question type.
        /*if (questionTypes.get(questionId) == 2) {
            intersection_canvas.clearCanvas();
            intersection_canvas.setVisibility(View.VISIBLE);
            question_image.setVisibility(View.GONE);
        } else {
            intersection_canvas.setVisibility(View.GONE);
            question_image.setVisibility(View.VISIBLE);
        }*/
    }

    public void highlightAnswer(int answer) {
        List<Button> buttons = new ArrayList<>();
        buttons.add(question_answer1);
        buttons.add(question_answer2);
        buttons.add(question_answer3);

        // Tint all buttons with default color
        for (int i = 0; i < buttons.size(); i++) {
            buttons.get(i).getBackground().setColorFilter(Color.LTGRAY, PorterDuff.Mode.MULTIPLY);
        }

        if (answer == 0) return;

        Drawable drawable = buttons.get(answer - 1).getBackground();

        // Color chosen button
        int correctAnswer = correctAnswersList.get(currentQuestionIdx - 1);
        if (colorCorrectAnswers) {
            if (answer == correctAnswer) {
                // Correct answer - Green
                drawable.setColorFilter(Color.parseColor("#4CAF50"), PorterDuff.Mode.MULTIPLY);
            } else {
                // Incorrect answer - Red
                drawable.setColorFilter(Color.parseColor("#F44336"), PorterDuff.Mode.MULTIPLY);

                if (finished) {
                    // Color the correct answer Green
                    Drawable drawable2 = buttons.get(correctAnswer - 1).getBackground();
                    drawable2.setColorFilter(Color.parseColor("#4CAF50"), PorterDuff.Mode.MULTIPLY);
                }
            }
        } else {
            // Correct answer is not revealed - Gray
            drawable.setColorFilter(Color.GRAY, PorterDuff.Mode.MULTIPLY);
        }

        // TODO: Test this
        // Force a redraw on pre-lollipop devices
        // Doesn't help/work?
        for (Button button : buttons) {
            button.invalidateDrawable(button.getBackground());
        }
    }

    public void setQuestionText(String text) {
        mText = text;
        question_text.setText(mText);
    }

    public void setImage(String path) {
        if (path != null && !path.isEmpty()) {
            InputStream inputStream;
            String _sign = "s:";
            String _inter = "i:";
            String _placeholder = "placeholder:";

            // Road Signs
            if (path.startsWith(_sign)) {
                String signIdentifier = path.substring(_sign.length()).toLowerCase();
                String signImage = signIdentifier.toLowerCase();
                String category = "";

                // Get the category from the signIdentifier
                Pattern regex = Pattern.compile("^[^0-9]*");
                Matcher matcher = regex.matcher(signIdentifier);

                if (matcher.find()) {
                    category = matcher.group(0).toUpperCase();
                }

                // Exception for "sp.png" file
                if (category.equals("SP")) {
                    category = "S";
                }

                try {
                    inputStream = this.getAssets()
                            .open("images/road_signs/" + category + "/" + signImage + ".png");
                    mImage = Drawable.createFromStream(inputStream, null);
                } catch (IOException ex) {
                    // If file doesn't exist, use the placeholder image
                    mImage = ContextCompat.getDrawable(this,
                            R.drawable.placeholder_small);
                    Timber.d("Image \"%s/%s.png\" does not exist.", category, signImage);
                }
            }

            // Intersections
            else if (path.startsWith(_inter)) {
                // Use image from the assets folder
                String intersectionName = path.substring(_inter.length());
                try {
                    inputStream = this.getAssets()
                            .open("images/intersections/" + intersectionName + ".png");
                    mImage = Drawable.createFromStream(inputStream, null);
                } catch (IOException ex) {
                    // If file doesn't exist, use the placeholder image
                    mImage = ContextCompat.getDrawable(this,
                            R.drawable.placeholder_large);
                    Timber.d("Image \"%s.png\" does not exist.", intersectionName);
                }
            }

            // Placeholders
            else if (path.startsWith(_placeholder)) {
                String image = path.substring(_placeholder.length());
                switch (image) {
                    case "small":
                        mImage = ContextCompat.getDrawable(this,
                                R.drawable.placeholder_small);
                        break;
                    case "large":
                        mImage = ContextCompat.getDrawable(this,
                                R.drawable.placeholder_large);
                        break;
                }
            }

            // Custom Images
            else {
                try {
                    inputStream = this.getAssets().open("images/" + path);
                    mImage = Drawable.createFromStream(inputStream, null);
                } catch (IOException ex) {
                    ex.printStackTrace();
                    mImage = null;
                    return;
                }
            }

            question_image.setImageDrawable(mImage);
            question_image.setVisibility(View.VISIBLE);
        } else {
            mImage = null;
            question_image.setVisibility(View.GONE);
        }
    }

    public void setPoints(int points) {
        mPoints = points;
    }

    public void addPoints(int amount) {
        setPoints(mPoints + amount);
    }

    public void setCorrectAnswer(int index) {
        mCorrectAnswer = index;
    }

    public void setAnswers(String answer1, String answer2, String answer3) {
        // Strip the colors from the strings
        String regex = "red:|green:|blue:";
        answer1 = answer1.replaceFirst(regex, "");
        answer2 = answer2.replaceFirst(regex, "");
        answer3 = answer3.replaceFirst(regex, "");

        mAnswer1 = answer1;
        mAnswer2 = answer2;
        mAnswer3 = answer3;

        // TODO: Try to implement, currently not working, try the tinting code used with buttons
        // Show a colorful circle in the button, representing the color of the car in the answer
        /*if (mAnswer1.startsWith("red:")) {
            Drawable drawable = (Drawable) ContextCompat.getDrawable(this, R.drawable.circle);
            //drawable.getPaint().setColor(Color.parseColor("#FF0000FF"));
            question_answer1.setCompoundDrawables(drawable, null, null, null);
        } else if (mAnswer1.startsWith("green:")) {
            Drawable drawable = (Drawable) ContextCompat.getDrawable(this, R.drawable.circle);
            //drawable.getPaint().setColor(Color.parseColor("#FF0000FF"));
            question_answer1.setCompoundDrawables(drawable, null, null, null);
        } else if (mAnswer1.startsWith("blue:")) {
            Drawable drawable = (Drawable) ContextCompat.getDrawable(this, R.drawable.circle);
            //drawable.getPaint().setColor(Color.parseColor("#FF00FF00"));
            question_answer1.setCompoundDrawables(drawable, null, null, null);
        } else {
            question_answer1.setCompoundDrawables(null, null, null, null);
        }*/

        question_answer1.setText(mAnswer1);
        question_answer2.setText(mAnswer2);
        question_answer3.setText(mAnswer3);
    }

    public void setQuestionCounter(int current) {
        question_counter.setText(String.format("%d/%d", current, questionsCount));
    }

    public void setPointsValue(int value) {
        Resources res = getResources();
        String sufix = value == 1 ? res.getString(R.string.point) : res.getString(R.string.points);
        points_value.setText(String.format("%d %s", value, sufix));
    }

    public void restartTimer() {
        elapsed_time.setBase(SystemClock.elapsedRealtime());
        elapsed_time.start();
    }

    public void pauseTimer() {
        elapsedTime = getElapsedTime();
        elapsed_time.stop();
    }

    public void resumeTimer() {
        elapsed_time.setBase(SystemClock.elapsedRealtime() - elapsedTime);
        elapsed_time.start();
    }

    public long getElapsedTime() {
        return SystemClock.elapsedRealtime() - elapsed_time.getBase();
    }
}
