/*
 * Copyright (c) 2015-2016. Spiracle Studios. All Rights Reserved.
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
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.ShareActionProvider;
import android.support.v7.widget.Toolbar;
import android.text.format.DateUtils;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.google.android.gms.ads.AdView;
import com.spiraclestudios.autoskola.DbContract;
import com.spiraclestudios.autoskola.DbHelper;
import com.spiraclestudios.autoskola.G;
import com.spiraclestudios.autoskola.Helper;
import com.spiraclestudios.autoskola.R;
import com.spiraclestudios.autoskola.interfaces.IBaseActivity;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnLongClick;
import io.palaima.debugdrawer.DebugDrawer;
import io.palaima.debugdrawer.actions.ActionsModule;
import io.palaima.debugdrawer.actions.ButtonAction;
import io.palaima.debugdrawer.commons.BuildModule;
import io.palaima.debugdrawer.commons.DeviceModule;
import io.palaima.debugdrawer.commons.SettingsModule;
import io.palaima.debugdrawer.timber.TimberModule;
import timber.log.Timber;

/**
 * Added by benji on 21/11/2015.
 */
public class TestActivity extends BaseActivity
        implements IBaseActivity {

    public final static String EXTRA_TEST_TYPE = "com.spiraclestudios.autoskola.TEST_TYPE";
    public final static String EXTRA_TEST_GROUP = "com.spiraclestudios.autoskola.TEST_GROUP";
    public final static String EXTRA_TEST_ID = "com.spiraclestudios.autoskola.TEST_ID";
    public final static String EXTRA_USES_QUESTIONS = "com.spiraclestudios.autoskola.USE_QUESTIONS";
    public final static String EXTRA_USES_ROAD_SIGNS = "com.spiraclestudios.autoskola.USE_ROAD_SIGNS";
    public final static String EXTRA_USES_INTERSECTIONS = "com.spiraclestudios.autoskola.USE_INTERSECTIONS";
    public final static String EXTRA_POINTS = "com.spiraclestudios.autoskola.POINTS";
    public final static String EXTRA_MAX_POINTS = "com.spiraclestudios.autoskola.MAX_POINTS";
    public final static String EXTRA_ELAPSED_TIME = "com.spiraclestudios.autoskola.ELAPSED_TIME";
    public final static String EXTRA_ANSWERS = "com.spiraclestudios.autoskola.ANSWERS";

    public String activityName = "TestActivity";

    public enum TestTypes {
        NORMAL,
        CORRECT_ANSWERS,
        HISTORY
    }

    // [Test Info]
    private TestTypes testType;
    private int testId = 1;
    private int testVersion = 1;
    private int currentQuestionIdx = 1;
    private boolean usesQuestions;
    private boolean usesRoadSigns;
    private boolean usesIntersections;
    private int questionsCount;
    private int maxPoints;
    private int amountCorrect;
    private long dateStarted;

    // [Test Settings - Internal]
    private boolean allowClickingOnAnswers = true;
    private boolean markCorrectAnswers = false;
    private boolean colorCorrectAnswers = false;
    private Drawable image;
    private int points = 0;
    private int correctAnswer = 0;

    // [Cached data from database]
    private List<Integer> questionTypes;
    private List<String> questionsList;
    private List<String> imagesList;
    private List<Integer> correctAnswersList;
    private List<String> answer1List;
    private List<String> answer2List;
    private List<String> answer3List;
    private List<Integer> pointsList;

    // [Current data used by the layout views]
    private List<Integer> chosenAnswersList = new ArrayList<>();

    // [Layout views]
    @Bind(R.id.wrapper)
    CoordinatorLayout wrapper;
    @Bind(R.id.ad_view)
    AdView ad_view;
    @Bind(R.id.question_text)
    TextView question_text;
    //@Bind(R.id.intersection_canvas)
    //IntersectionCanvas intersection_canvas;
    @Bind(R.id.question_image)
    ImageButton question_image;
    @Bind(R.id.expanded_image)
    ImageView expanded_image;
    @Bind(R.id.answer1)
    AppCompatButton question_answer1;
    @Bind(R.id.answer2)
    AppCompatButton question_answer2;
    @Bind(R.id.answer3)
    AppCompatButton question_answer3;
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
    @Bind(R.id.progress_bar)
    ProgressBar progress_bar;

    // [Internal]
    /**
     * Did the user evaluate the test results?
     */
    private boolean completed = false;
    private boolean allQuestionsAnswered = false;
    private long elapsedTime;
    private int amountAnswered;

    // [Miscellaneous]
    private boolean pressedBackOnce;
    private Animator mExpandAnimator;
    private int mShortAnimationDuration;

    public String getActivityName() {
        return activityName;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Helper.setTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        ButterKnife.bind(this);

        // Keep the screen on.
        SharedPreferences prefsSettings = getSharedPreferences(G.PREFS_SETTINGS, MODE_PRIVATE);
        if (prefsSettings.getBoolean("keep_screen_on", true)) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }

        // Retrieve and cache the system's default "short" animation time.
        mShortAnimationDuration = getResources().getInteger(
                android.R.integer.config_shortAnimTime);

        // Read extras from the intent.
        Intent intent = getIntent();
        testType = (TestTypes) intent.getSerializableExtra(EXTRA_TEST_TYPE);
        if (testType == null) {
            testType = TestTypes.NORMAL;
        }
        int selectedIndexId = intent.getIntExtra(EXTRA_TEST_ID, 1);
        Helper.Groups selectedGroup = (Helper.Groups) intent.getSerializableExtra(EXTRA_TEST_GROUP);
        usesQuestions = intent.getBooleanExtra(EXTRA_USES_QUESTIONS, true);
        usesRoadSigns = intent.getBooleanExtra(EXTRA_USES_ROAD_SIGNS, true);
        usesIntersections = intent.getBooleanExtra(EXTRA_USES_INTERSECTIONS, true);
        points = intent.getIntExtra(EXTRA_POINTS, 0);
        maxPoints = intent.getIntExtra(EXTRA_MAX_POINTS, 0);
        elapsedTime = intent.getLongExtra(EXTRA_ELAPSED_TIME, 0);
        String passedAnswersString = intent.getStringExtra(EXTRA_ANSWERS);

        // Decide which test to open.
        String groupString;
        int testIndexToUse;
        Resources res = getResources();

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

        // Returns "Skupina A,B" or "Skupina C,D,T"
        groupString = (Helper.getGroupFromTestIndex(
                testIndexToUse) == Helper.Groups.AB) ? res.getString(R.string.group_ab_long) : res.getString(R.string.group_cdt_long);

        // Setup Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("Test " + testIndexToUse);
            actionBar.setSubtitle(groupString);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        switch (testType) {
            case NORMAL:
                restartTimer();
                // TODO: Remove after implementing intersections
                if (usesIntersections) {
                    Toast.makeText(this, R.string.toast_intersections_not_yet_available,
                            Toast.LENGTH_LONG)
                            .show();
                }
                break;
            case CORRECT_ANSWERS:
                completed = true;
                markCorrectAnswers = true;
                colorCorrectAnswers = true;
                allowClickingOnAnswers = false;
                elapsed_time.setText(getString(R.string.correct_answers));
                elapsed_time.setTextColor(Color.parseColor("#b2ffffff"));
                elapsed_time.setTextSize(13);
                progress_bar.setVisibility(View.GONE);
                break;
            case HISTORY:
                completed = true;
                markCorrectAnswers = true;
                colorCorrectAnswers = true;
                allowClickingOnAnswers = false;
                elapsed_time.setText(points + "/" + maxPoints + "\n" + DateUtils.formatElapsedTime(elapsedTime / 1000));
                elapsed_time.setTextColor(Color.parseColor("#b2ffffff"));
                elapsed_time.setTextSize(13);
                if (!passedAnswersString.isEmpty()) {
                    for (String answer : passedAnswersString.split(",")) {
                        int chosenAnswer = Integer.parseInt(answer);
                        chosenAnswersList.add(chosenAnswer);
                        if (chosenAnswer != 0)
                            amountAnswered++;
                    }
                }
                for (int i = 0; i < questionsCount; i++) {
                    if (chosenAnswersList.get(i).equals(correctAnswersList.get(i))) {
                        amountCorrect++;
                    }
                }
                updateProgressBar();
                break;
        }

        setTest(testIndexToUse);

        // Load an ad
        Helper.loadAd(ad_view);

        // Debug Drawer
        ButtonAction buttonAction = new ButtonAction("Successful test", new ButtonAction.Listener() {

            @Override
            public void onClick() {
                // Finish the test with max score
                chosenAnswersList = correctAnswersList;
                amountAnswered = questionsCount;
                evaluateResults();
            }
        });

        new DebugDrawer.Builder(this)
                .modules(
                        new ActionsModule(buttonAction),
                        new TimberModule(),
                        new DeviceModule(this),
                        new BuildModule(this),
                        new SettingsModule(this)
                ).build();
    }

    @Override
    public void onPause() {
        ad_view.pause();
        pauseTimer();
        super.onPause();
    }

    @Override
    public void onResume() {
        ad_view.resume();
        if (!completed && !markCorrectAnswers)
            resumeTimer();
        super.onResume();
    }

    @Override
    public void onDestroy() {
        ad_view.destroy();
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        if (!completed) {
            if (pressedBackOnce) {
                super.onBackPressed();
                return;
            }
            pressedBackOnce = true;
            Toast.makeText(this, R.string.toast_press_again_to_leave, Toast.LENGTH_SHORT).show();

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    pressedBackOnce = false;
                }
            }, 2000);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (testType == TestTypes.NORMAL) {
            getMenuInflater().inflate(R.menu.activity_test, menu);
        } else if (testType == TestTypes.HISTORY) {
            getMenuInflater().inflate(R.menu.activity_test_completed, menu);

            // TODO: Make a shared method for TestActivity and ResultsActivity.
            // Set up Share action
            Resources res = getResources();

            int amountUnanswered = chosenAnswersList.size() - amountAnswered;
            int amountIncorrect = questionsCount - amountCorrect;

            String shareText = String.format(Locale.ENGLISH, res.getString(R.string.results_share_action_text), testId) + "\n\n" +
                    String.format(Locale.ENGLISH, "%s: %d/%d", res.getString(R.string.results_points), points, maxPoints) + "\n" +
                    String.format(Locale.ENGLISH, "%s: %d", res.getString(R.string.results_correct), amountCorrect) + "\n" +
                    String.format(Locale.ENGLISH, "%s: %d", res.getString(R.string.results_incorrect), amountIncorrect - amountUnanswered) + "\n";

            if (amountUnanswered > 0) {
                shareText += String.format(Locale.ENGLISH, "%s: %d", res.getString(R.string.results_unanswered), amountUnanswered) + "\n";
            }
            shareText += String.format(Locale.ENGLISH, "%s: %s", res.getString(R.string.results_time), DateUtils.formatElapsedTime(elapsedTime / 1000));

            Intent shareIntent = new Intent();
            shareIntent.setAction(Intent.ACTION_SEND);
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.results_share_action_subject));
            shareIntent.putExtra(Intent.EXTRA_TEXT, shareText);
            shareIntent.setType("text/plain");
            ((ShareActionProvider) MenuItemCompat.getActionProvider(menu.findItem(R.id.action_share))).setShareIntent(shareIntent);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        } else if (id == R.id.action_evaluate) {
            evaluateResults();
            return true;
        }/* else if (id == R.id.action_vyhlaska) {
      Toast.makeText(this, R.string.toast_not_yet_implemented, Toast.LENGTH_SHORT).show();
      return true;
    }*/
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
        expanded_image.setImageDrawable(image);

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
        if (!allowClickingOnAnswers)
            return;

        int currentAnswer = chosenAnswersList.get(currentQuestionIdx - 1);

        // Un check the answer if the user clicks on the current answer.
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
            Toast.makeText(this, R.string.toast_all_questions_answered, Toast.LENGTH_SHORT).show();
        }

        updateProgressBar();
    }

    private void updateProgressBar() {
        progress_bar.setProgress(amountAnswered);
    }

    /**
     * Calculate points, handle test review and show the results activity.
     */
    public void evaluateResults() {
        if (!completed) {
            // Calculate scored points
            amountCorrect = 0;
            for (int i = 0; i < questionsCount; i++) {
                if (chosenAnswersList.get(i).equals(correctAnswersList.get(i))) {
                    addPoints(pointsList.get(i));
                    amountCorrect++;
                }
            }

            markCorrectAnswers = true;
            colorCorrectAnswers = true;
            allowClickingOnAnswers = false;
            highlightAnswer(chosenAnswersList.get(currentQuestionIdx - 1));
            pauseTimer();
            elapsed_time.setText(points + "/" + maxPoints + "\n" + DateUtils.formatElapsedTime(elapsedTime / 1000));
            elapsed_time.setTextColor(Color.parseColor("#b2ffffff"));
            elapsed_time.setTextSize(13);
        }

        Intent intent = new Intent(this, ResultsActivity.class);
        intent.putExtra(ResultsActivity.EXTRA_ALREADY_OPENED_RESULTS, completed);
        intent.putExtra(ResultsActivity.EXTRA_TEST_ID, testId);
        intent.putExtra(ResultsActivity.EXTRA_TEST_VERSION, testVersion);
        intent.putExtra(ResultsActivity.EXTRA_USES_QUESTIONS, usesQuestions);
        intent.putExtra(ResultsActivity.EXTRA_USES_ROAD_SIGNS, usesRoadSigns);
        intent.putExtra(ResultsActivity.EXTRA_USES_INTERSECTIONS, usesIntersections);
        intent.putExtra(ResultsActivity.EXTRA_POINTS, points);
        intent.putExtra(ResultsActivity.EXTRA_MAX_POINTS, maxPoints);
        intent.putExtra(ResultsActivity.EXTRA_ELAPSED_TIME, getElapsedTime());
        intent.putIntegerArrayListExtra(ResultsActivity.EXTRA_ANSWERS, (ArrayList<Integer>) chosenAnswersList);
        intent.putExtra(ResultsActivity.EXTRA_CORRECT, amountCorrect);
        intent.putExtra(ResultsActivity.EXTRA_INCORRECT, questionsCount - amountCorrect);
        intent.putExtra(ResultsActivity.EXTRA_ANSWERED, amountAnswered);
        intent.putExtra(ResultsActivity.EXTRA_DATE_TIME, dateStarted);

        completed = true;

        startActivity(intent);
    }

    /**
     * Retrieves data from db, sets text and onClickListeners, resets everything.
     */
    public void setTest(int id) {
        testId = id;
        dateStarted = System.currentTimeMillis() / 1000;

        Crashlytics.getInstance().core.setInt("current_test", testId);

        // Set up the Database
        DbHelper dbHelper = new DbHelper(this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        //// [Tests] ////

        // Get latest version of this test.
        Cursor cTest = db.rawQuery(
                "SELECT " + DbContract.Tests.COLUMN_QUESTIONS + ", " +
                        DbContract.Tests.COLUMN_VERSION_CODE + " FROM " +
                        DbContract.Tests.TABLE_NAME + " WHERE " +
                        DbContract.Tests.COLUMN_TEST_ID + " = ?", new String[]
                        {Integer.toString(testId)});

        cTest.moveToFirst();

        // The whole 'questions' string from the Tests table.
        String questionsString = cTest.getString(cTest.getColumnIndexOrThrow(
                DbContract.Tests.COLUMN_QUESTIONS));

        testVersion = cTest.getInt(cTest.getColumnIndexOrThrow(
                DbContract.Tests.COLUMN_VERSION_CODE));

        cTest.close();


        //// [Questions] ////

        // Selector for the question type.
        String typeSelector = "AND (";
        List<String> concat = new ArrayList<>();

        if (usesQuestions) {
            concat.add(DbContract.Questions.COLUMN_TYPE + "=0");
        }
        if (usesRoadSigns) {
            concat.add(DbContract.Questions.COLUMN_TYPE + "=1");
        }
        if (usesIntersections) {
            concat.add(DbContract.Questions.COLUMN_TYPE + "=2");
        }

        for (int i = 0; i < concat.size(); i++) {
            String s = concat.get(i);

            typeSelector += s;

            if (i < concat.size() - 1) {
                typeSelector += " OR ";
            }
        }
        typeSelector += ")";

        // Get the Filtered Questions for this test version.
        String query = "SELECT * FROM " + DbContract.Questions.TABLE_NAME +
                " WHERE " + DbContract.Questions.COLUMN_QUESTION_ID + " IN (" + questionsString + ") AND " + DbContract.Questions.COLUMN_VERSION + " <= ? " + typeSelector;

        Cursor cFilteredQuestions = db.rawQuery(query, new String[]{Integer.toString(testVersion)});

        /* Questions after filtering by type. */
        List<Integer> questionIds = new ArrayList<>();
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

        // Get count of questions and amount of max points.
        questionsCount = questionIds.size();

        // Initialize the chosenAnswersList to the right size.
        for (int i = 0; i < questionsCount; i++) {
            chosenAnswersList.add(
                    (markCorrectAnswers) ? correctAnswersList.get(i) : 0);
        }

        // Set progress bar range.
        progress_bar.setMax(questionsCount);

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
        }

        // [CANVAS-CODE]
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

    /**
     * Colors the chosen button.
     *
     * @param answer The index of the button that was pressed, from 1 to 3.
     */
    public void highlightAnswer(int answer) {
        List<AppCompatButton> buttons = new ArrayList<>();
        buttons.add(question_answer1);
        buttons.add(question_answer2);
        buttons.add(question_answer3);

        Resources.Theme theme = getTheme();
        TypedValue typedValue = new TypedValue();

        theme.resolveAttribute(R.attr.colorAnswerNormal, typedValue, true);
        int colorNormal = typedValue.data;
        theme.resolveAttribute(R.attr.colorAnswerSelected, typedValue, true);
        int colorSelected = typedValue.data;
        theme.resolveAttribute(R.attr.colorAnswerCorrect, typedValue, true);
        int colorCorrect = typedValue.data;
        theme.resolveAttribute(R.attr.colorAnswerIncorrect, typedValue, true);
        int colorIncorrect = typedValue.data;
        theme.resolveAttribute(R.attr.colorAnswerNormalText, typedValue, true);
        int colorNormalText = typedValue.data;
        theme.resolveAttribute(R.attr.colorAnswerSelectedText, typedValue, true);
        int colorSelectedText = typedValue.data;

        // Tint all buttons with normal color.
        for (AppCompatButton button : buttons) {
            button.getBackground().setColorFilter(colorNormal, PorterDuff.Mode.MULTIPLY);
            button.setTextColor(colorNormalText);
        }

        if (answer == 0)
            return;

        // Color the selected button.
        AppCompatButton selectedButton = buttons.get(answer - 1);
        if (colorCorrectAnswers) {
            if (answer == correctAnswer || completed) {
                // Correct answer - Green
                AppCompatButton correctButton = buttons.get(correctAnswer - 1);
                correctButton.getBackground().setColorFilter(colorCorrect, PorterDuff.Mode.MULTIPLY);
                correctButton.setTextColor(colorSelectedText);
            }
            if (answer != correctAnswer) {
                // Incorrect answer - Red
                selectedButton.getBackground().setColorFilter(colorIncorrect, PorterDuff.Mode.MULTIPLY);
                selectedButton.setTextColor(colorSelectedText);
            }
        } else {
            // Correct answer is not revealed.
            // Just color the selected button - Gray.
            selectedButton.getBackground().setColorFilter(colorSelected, PorterDuff.Mode.MULTIPLY);
            selectedButton.setTextColor(colorNormalText);
        }
    }

    public void setQuestionText(String questionText) {
        question_text.setText(questionText);
    }

    public void setImage(String path) {
        if (path != null && !path.isEmpty()) {
            InputStream inputStream;
            int type = questionTypes.get(currentQuestionIdx - 1);

            // Road Signs
            if (type == 1) {
                String signImage = path.toLowerCase();
                String category = "";

                // Get the category from the signIdentifier.
                Pattern regex = Pattern.compile("^[^0-9]*");
                Matcher matcher = regex.matcher(signImage);

                if (matcher.find()) {
                    category = matcher.group(0).toUpperCase();
                }

                // Exception for "sp.png" file.
                if (category.equals("SP")) {
                    category = "S";
                }

                try {
                    inputStream = this.getAssets()
                            .open("images/road_signs/" + category + "/" + signImage + ".png");
                    image = Drawable.createFromStream(inputStream, null);
                } catch (IOException ex) {
                    // If file doesn't exist, use the placeholder image.
                    image = ContextCompat.getDrawable(this,
                            R.drawable.placeholder_small);
                    Timber.d("Image \"images/road_signs/%s/%s.png\" does not exist.", category, signImage);
                }
            }

            // Intersections
            else if (type == 2) {
                // Use image from the assets folder.
                try {
                    inputStream = this.getAssets()
                            .open("images/intersections/" + path + ".png");
                    image = Drawable.createFromStream(inputStream, null);
                } catch (IOException ex) {
                    // If file doesn't exist, use the placeholder image.
                    image = ContextCompat.getDrawable(this,
                            R.drawable.placeholder_large);
                    Timber.d("Image \"images/intersections/%s.png\" does not exist.", path);
                }
            }

            question_image.setImageDrawable(image);
            question_image.setVisibility(View.VISIBLE);
        } else {
            image = null;
            question_image.setVisibility(View.GONE);
        }
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public void addPoints(int amount) {
        setPoints(points + amount);
    }

    public void setCorrectAnswer(int index) {
        correctAnswer = index;
    }

    public void setAnswers(String answer1, String answer2, String answer3) {
        // Strip the colors from the strings.
        String regex = "red:|green:|blue:";
        answer1 = answer1.replaceFirst(regex, "");
        answer2 = answer2.replaceFirst(regex, "");
        answer3 = answer3.replaceFirst(regex, "");

        // TODO: Try to implement, currently not working, try the tinting code used with buttons.
        // Show a colorful circle in the button, representing the color of the car in the answer.
    /*if (answer1.startsWith("red:")) {
            Drawable drawable = (Drawable) ContextCompat.getDrawable(this, R.drawable.circle);
            //drawable.getPaint().setColor(Color.parseColor("#FF0000FF"));
            question_answer1.setCompoundDrawables(drawable, null, null, null);
        } else if (answer1.startsWith("green:")) {
            Drawable drawable = (Drawable) ContextCompat.getDrawable(this, R.drawable.circle);
            //drawable.getPaint().setColor(Color.parseColor("#FF0000FF"));
            question_answer1.setCompoundDrawables(drawable, null, null, null);
        } else if (answer1.startsWith("blue:")) {
            Drawable drawable = (Drawable) ContextCompat.getDrawable(this, R.drawable.circle);
            //drawable.getPaint().setColor(Color.parseColor("#FF00FF00"));
            question_answer1.setCompoundDrawables(drawable, null, null, null);
        } else {
            question_answer1.setCompoundDrawables(null, null, null, null);
        }*/

        question_answer1.setText(answer1);
        question_answer2.setText(answer2);
        question_answer3.setText(answer3);
    }

    public void setQuestionCounter(int current) {
        question_counter.setText(String.format(Locale.ENGLISH, "%d/%d", current, questionsCount));
    }

    public void setPointsValue(int points) {
        Resources res = getResources();
        String pointsSuffix;
        if (points == 1) {
            pointsSuffix = res.getString(R.string.point);
        } else if (points > 1 && points < 5) {
            pointsSuffix = res.getString(R.string.points_2to4);
        } else {
            pointsSuffix = res.getString(R.string.points);
        }
        points_value.setText(String.format(Locale.ENGLISH, "%d %s", points, pointsSuffix));
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
