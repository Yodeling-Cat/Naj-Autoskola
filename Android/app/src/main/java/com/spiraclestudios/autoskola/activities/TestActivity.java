/*
 * Copyright (c) 2015-2016. Spiracle Studios. All Rights Reserved.
 */

package com.spiraclestudios.autoskola.activities;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.Toolbar;
import android.text.format.DateUtils;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.CustomEvent;
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

    private static final String STATE_CURRENT_QUESTION_INDEX = "currentQuestionIdx";
    private static final String STATE_COMPLETED = "completed";
    private static final String STATE_CHOSEN_ANSWERS_LIST = "chosenAnswersList";
    private static final String STATE_AMOUNT_ANSWERED = "amountAnswered";
    private static final String STATE_ALL_QUESTIONS_ANSWERED = "allQuestionsAnswered";
    private static final String STATE_ALLOW_CLICKING_ANSWERS = "allowClickingAnswers";
    private static final String STATE_MARK_CORRECT_ANSWERS = "markCorrectAnswers";
    private static final String STATE_COLOR_CORRECT_ANSWERS = "colorCorrectAnswers";
    private static final String STATE_ELAPSED_TIME = "elapsedTime";
    private static final String STATE_POINTS = "points";
    private static final String STATE_MAX_POINTS = "maxPoints";
    private static final String STATE_CORRECT_ANSWER = "correctAnswer";

    private static final String STATE_TEST_TYPE = "testType";
    private static final String STATE_TEST_ID = "testId";
    private static final String STATE_TEST_GROUP = "testGroup";
    private static final String STATE_TEST_VERSION = "testVersion";
    private static final String STATE_DATE_STARTED = "dateStarted";
    private static final String STATE_QUESTION_TYPES = "questionTypes";
    private static final String STATE_QUESTIONS_LIST = "questionsList";
    private static final String STATE_IMAGES_LIST = "imagesList";
    private static final String STATE_CORRECT_ANSWERS_LIST = "correctAnswersList";
    private static final String STATE_ANSWER_1_LIST = "answer1List";
    private static final String STATE_ANSWER_2_LIST = "answer2List";
    private static final String STATE_ANSWER_3_LIST = "answer3List";
    private static final String STATE_POINTS_LIST = "pointsList";
    private static final String STATE_QUESTIONS_COUNT = "questionsCount";

    private static final String STATE_USES_QUESTIONS = "usesQuestions";
    private static final String STATE_USES_ROAD_SIGNS = "usesRoadSigns";
    private static final String STATE_USES_INTERSECTIONS = "usesIntersections";

    public enum TestTypes {
        NORMAL,
        CORRECT_ANSWERS,
        HISTORY
    }

    // [Internal]
    private int currentQuestionIdx = 1;
    private boolean isQuestionImageExpanded;
    private boolean pressedBackOnce;
    private boolean completed = false;
    private boolean allQuestionsAnswered = false;
    private boolean allowClickingAnswers = true;
    private boolean markCorrectAnswers = false;
    private boolean colorCorrectAnswers = false;
    private long elapsedTime;
    private int amountAnswered;
    private int points = 0;
    private int correctAnswer = 0;
    private List<Integer> chosenAnswersList = new ArrayList<>();

    // [Test Info]
    private TestTypes testType;
    private int testId = 1;
    private Helper.Groups testGroup = null;
    private int testVersion = 1;
    private boolean usesQuestions;
    private boolean usesRoadSigns;
    private boolean usesIntersections;
    private int questionsCount;
    private int maxPoints;
    // TODO: Needs to be saved on instance state changed?
    private int amountCorrect;
    private long dateStarted;

    // [Cached data from database]
    private List<Integer> questionTypes;
    private List<String> questionsList;
    private List<String> imagesList;
    private List<Integer> correctAnswersList;
    private List<String> answer1List;
    private List<String> answer2List;
    private List<String> answer3List;
    private List<Integer> pointsList;

    // [Layout views]
    @Bind(R.id.ad_view)
    AdView ad_view;
    @Bind(R.id.question_text)
    TextView question_text;
    //@Bind(R.id.intersection_canvas)
    //IntersectionCanvas intersection_canvas;
    @Bind(R.id.question_image)
    ImageButton question_image;
    @Bind(R.id.answer1)
    AppCompatButton answer_button_1;
    @Bind(R.id.answer2)
    AppCompatButton answer_button_2;
    @Bind(R.id.answer3)
    AppCompatButton answer_button_3;
    @Bind(R.id.points_value)
    TextView points_value;
    @Bind(R.id.question_counter)
    TextView question_counter;
    @Bind(R.id.elapsed_time)
    Chronometer elapsed_time;
    @Bind(R.id.progress_bar)
    ProgressBar progress_bar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Helper.setTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        ButterKnife.bind(this);

        // Read Intent or Restore savedInstanceState.
        int selectedIndexId = 1;
        Helper.Groups selectedGroup = null;
        String passedAnswersString = null;

        if (savedInstanceState == null) {
            Intent intent = getIntent();
            testType = (TestTypes) intent.getSerializableExtra(EXTRA_TEST_TYPE);
            if (testType == null) {
                testType = TestTypes.NORMAL;
            }
            selectedIndexId = intent.getIntExtra(EXTRA_TEST_ID, 1);
            selectedGroup = (Helper.Groups) intent.getSerializableExtra(EXTRA_TEST_GROUP);
            usesQuestions = intent.getBooleanExtra(EXTRA_USES_QUESTIONS, true);
            usesRoadSigns = intent.getBooleanExtra(EXTRA_USES_ROAD_SIGNS, true);
            usesIntersections = intent.getBooleanExtra(EXTRA_USES_INTERSECTIONS, true);
            points = intent.getIntExtra(EXTRA_POINTS, 0);
            maxPoints = intent.getIntExtra(EXTRA_MAX_POINTS, 0);
            elapsedTime = intent.getLongExtra(EXTRA_ELAPSED_TIME, 0);
            passedAnswersString = intent.getStringExtra(EXTRA_ANSWERS);
        } else {
            // Restore currentQuestionIdx.
            currentQuestionIdx = savedInstanceState.getInt(STATE_CURRENT_QUESTION_INDEX);

            // Restore completed.
            completed = savedInstanceState.getBoolean(STATE_COMPLETED);

            // Restore elapsedTime.
            elapsedTime = savedInstanceState.getLong(STATE_ELAPSED_TIME);

            // Restore chosenAnswersList.
            chosenAnswersList = savedInstanceState.getIntegerArrayList(STATE_CHOSEN_ANSWERS_LIST);

            // Restore amountAnswered.
            amountAnswered = savedInstanceState.getInt(STATE_AMOUNT_ANSWERED);

            // Restore allQuestionsAnswered.
            allQuestionsAnswered = savedInstanceState.getBoolean(STATE_ALL_QUESTIONS_ANSWERED);

            // Restore allowClickingAnswers.
            allowClickingAnswers = savedInstanceState.getBoolean(STATE_ALLOW_CLICKING_ANSWERS);

            // Restore markCorrectAnswers.
            markCorrectAnswers = savedInstanceState.getBoolean(STATE_MARK_CORRECT_ANSWERS);

            // Restore colorCorrectAnswers.
            colorCorrectAnswers = savedInstanceState.getBoolean(STATE_COLOR_CORRECT_ANSWERS);

            // Restore points.
            points = savedInstanceState.getInt(STATE_POINTS);

            // Restore maxPoints.
            maxPoints = savedInstanceState.getInt(STATE_MAX_POINTS);

            // Restore correctAnswer.
            correctAnswer = savedInstanceState.getInt(STATE_CORRECT_ANSWER);


            // setTest() related variables.


            // Restore testType.
            testType = (TestTypes) savedInstanceState.getSerializable(STATE_TEST_TYPE);

            // Restore testId.
            testId = savedInstanceState.getInt(STATE_TEST_ID);

            // Restore testGroup.
            testGroup = (Helper.Groups) savedInstanceState.getSerializable(STATE_TEST_GROUP);

            // Restore testVersion.
            testVersion = savedInstanceState.getInt(STATE_TEST_VERSION);

            // Restore dateStarted.
            dateStarted = savedInstanceState.getLong(STATE_DATE_STARTED);

            // Restore questionTypes.
            questionTypes = savedInstanceState.getIntegerArrayList(STATE_QUESTION_TYPES);

            // Restore questionsList.
            questionsList = savedInstanceState.getStringArrayList(STATE_QUESTIONS_LIST);

            // Restore imagesList.
            imagesList = savedInstanceState.getStringArrayList(STATE_IMAGES_LIST);

            // Restore correctAnswersList.
            correctAnswersList = savedInstanceState.getIntegerArrayList(STATE_CORRECT_ANSWERS_LIST);

            // Restore answer1List.
            answer1List = savedInstanceState.getStringArrayList(STATE_ANSWER_1_LIST);

            // Restore answer2List.
            answer2List = savedInstanceState.getStringArrayList(STATE_ANSWER_2_LIST);

            // Restore answer3List.
            answer3List = savedInstanceState.getStringArrayList(STATE_ANSWER_3_LIST);

            // Restore pointsList.
            pointsList = savedInstanceState.getIntegerArrayList(STATE_POINTS_LIST);

            // Restore questionsCount.
            questionsCount = savedInstanceState.getInt(STATE_QUESTIONS_COUNT);


            // Restore usesQuestions.
            usesQuestions = savedInstanceState.getBoolean(STATE_USES_QUESTIONS);

            // Restore usesRoadSigns.
            usesRoadSigns = savedInstanceState.getBoolean(STATE_USES_ROAD_SIGNS);

            // Restore usesIntersections.
            usesIntersections = savedInstanceState.getBoolean(STATE_USES_INTERSECTIONS);


            // Highlight the current answer.
            highlightAnswer(chosenAnswersList.get(currentQuestionIdx - 1));
        }

        // TODO: Move this inside the above if statement when reading intents.
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

        // Set up Toolbar
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
                if (savedInstanceState == null) {
                    restartTimer();
                    Answers.getInstance().logCustom(new CustomEvent("Test Start")
                            .putCustomAttribute("Index", testId)
                            .putCustomAttribute("Group", Helper.getGroupFromTestIndex(testIndexToUse).ordinal())
                            .putCustomAttribute("Is Random", selectedGroup != null ? 1 : 0)
                            .putCustomAttribute("Uses Questions", usesQuestions ? 1 : 0)
                            .putCustomAttribute("Uses RoadSigns", usesRoadSigns ? 1 : 0)
                            .putCustomAttribute("Uses Intersections", usesIntersections ? 1 : 0));
                } else {
                    if (completed) {
                        elapsed_time.setText(points + "/" + maxPoints + "\n" + DateUtils.formatElapsedTime(elapsedTime / 1000));
                        elapsed_time.setTextColor(Color.parseColor("#b2ffffff"));
                        elapsed_time.setTextSize(13);
                    }
                }
                break;
            case CORRECT_ANSWERS:
                completed = true;
                markCorrectAnswers = true;
                colorCorrectAnswers = true;
                allowClickingAnswers = false;
                elapsed_time.setText(getString(R.string.correct_answers));
                elapsed_time.setTextColor(Color.parseColor("#b2ffffff"));
                elapsed_time.setTextSize(13);
                progress_bar.setVisibility(View.GONE);
                break;
            case HISTORY:
                completed = true;
                markCorrectAnswers = true;
                colorCorrectAnswers = true;
                allowClickingAnswers = false;
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

        if (savedInstanceState == null) {
            setTest(testIndexToUse);
        } else {
            // Set progress bar range.
            progress_bar.setMax(questionsCount);

            setQuestion(currentQuestionIdx);
        }

        // Load an ad.
        Helper.loadAd(ad_view);

        // Keep the screen on.
        SharedPreferences prefsSettings = getSharedPreferences(G.PREFS_SETTINGS, MODE_PRIVATE);
        if (prefsSettings.getBoolean("keep_screen_on", true)) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }

        // Set up Debug Drawer.
        ButtonAction buttonAction = new ButtonAction("Successful test", new ButtonAction.Listener() {

            @Override
            public void onClick() {
                // Finish the test with max score.
                chosenAnswersList = correctAnswersList;
                amountAnswered = questionsCount;
                evaluateTest();
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
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        // Store currentQuestionIdx.
        outState.putInt(STATE_CURRENT_QUESTION_INDEX, currentQuestionIdx);

        // Store completed.
        outState.putBoolean(STATE_COMPLETED, completed);

        // Store elapsedTime.
        outState.putLong(STATE_ELAPSED_TIME, elapsedTime);

        // Store chosenAnswersList.
        outState.putIntegerArrayList(STATE_CHOSEN_ANSWERS_LIST, (ArrayList<Integer>) chosenAnswersList);

        // Store amountAnswered.
        outState.putInt(STATE_AMOUNT_ANSWERED, amountAnswered);

        // Store allQuestionsAnswered.
        outState.putBoolean(STATE_ALL_QUESTIONS_ANSWERED, allQuestionsAnswered);

        // Store allowClickingAnswers.
        outState.putBoolean(STATE_ALLOW_CLICKING_ANSWERS, allowClickingAnswers);

        // Store markCorrectAnswers.
        outState.putBoolean(STATE_MARK_CORRECT_ANSWERS, markCorrectAnswers);

        // Store colorCorrectAnswers.
        outState.putBoolean(STATE_COLOR_CORRECT_ANSWERS, colorCorrectAnswers);

        // Store points.
        outState.putInt(STATE_POINTS, points);

        // Store maxPoints.
        outState.putInt(STATE_MAX_POINTS, maxPoints);

        // Store correctAnswer.
        outState.putInt(STATE_CORRECT_ANSWER, correctAnswer);


        // setTest() related variables.


        // Store testType.
        outState.putSerializable(STATE_TEST_TYPE, testType);

        // Store testId.
        outState.putInt(STATE_TEST_ID, testId);

        // Store testGroup.
        outState.putSerializable(STATE_TEST_GROUP, testGroup);

        // Store testVersion.
        outState.putInt(STATE_TEST_VERSION, testVersion);

        // Store dateStarted.
        outState.putLong(STATE_DATE_STARTED, dateStarted);

        // Store questionTypes.
        outState.putIntegerArrayList(STATE_QUESTION_TYPES, (ArrayList<Integer>) questionTypes);

        // Store questionsList.
        outState.putStringArrayList(STATE_QUESTIONS_LIST, (ArrayList<String>) questionsList);

        // Store imagesList.
        outState.putStringArrayList(STATE_IMAGES_LIST, (ArrayList<String>) imagesList);

        // Store correctAnswersList.
        outState.putIntegerArrayList(STATE_CORRECT_ANSWERS_LIST, (ArrayList<Integer>) correctAnswersList);

        // Store answer1List.
        outState.putStringArrayList(STATE_ANSWER_1_LIST, (ArrayList<String>) answer1List);

        // Store answer2List.
        outState.putStringArrayList(STATE_ANSWER_2_LIST, (ArrayList<String>) answer2List);

        // Store answer3List.
        outState.putStringArrayList(STATE_ANSWER_3_LIST, (ArrayList<String>) answer3List);

        // Store pointsList.
        outState.putIntegerArrayList(STATE_POINTS_LIST, (ArrayList<Integer>) pointsList);

        // Store questionsCount.
        outState.putInt(STATE_QUESTIONS_COUNT, questionsCount);


        // Store usesQuestions.
        outState.putBoolean(STATE_USES_QUESTIONS, usesQuestions);

        // Store usesRoadSigns.
        outState.putBoolean(STATE_USES_ROAD_SIGNS, usesRoadSigns);

        // Store usesIntersections.
        outState.putBoolean(STATE_USES_INTERSECTIONS, usesIntersections);
    }

    /**
     * Retrieves data from db, sets text and onClickListeners, resets everything.
     */
    public void setTest(int id) {
        testId = id;
        dateStarted = System.currentTimeMillis() / 1000;

        // TODO: Do I need to call this when restoring state?
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

        setQuestion(currentQuestionIdx);
    }

    @Override
    public void onPause() {
        ad_view.pause();
        if (!completed) {
            pauseTimer();
        }
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
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.action_evaluate:
                evaluateTest();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Expand the question_image on click.
     */
    @OnClick(R.id.question_image)
    public void question_image_onClick() {
        int questionType = questionTypes.get(currentQuestionIdx - 1);
        if (questionType == 2)
            return;

        ViewGroup.LayoutParams layoutParams = question_image.getLayoutParams();
        if (!isQuestionImageExpanded) {
            layoutParams.height = (int) (layoutParams.height * 1.5f);
            isQuestionImageExpanded = true;
        } else {
            layoutParams.height = (int) (layoutParams.height / 1.5f);
            isQuestionImageExpanded = false;
        }
        question_image.setLayoutParams(layoutParams);
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
            setQuestion(currentQuestionIdx + 1);
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
            setQuestion(currentQuestionIdx - 1);
    }

    @OnClick(R.id.answer1)
    public void answer1_onClick() {
        selectAnswer(1);
    }

    @OnClick(R.id.answer2)
    public void answer2_onClick() {
        selectAnswer(2);
    }

    @OnClick(R.id.answer3)
    public void answer3_onClick() {
        selectAnswer(3);
    }

    /**
     * @param answer The index of the answer button. In range 1-3.
     */
    private void selectAnswer(int answer) {
        if (!allowClickingAnswers)
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
    private void evaluateTest() {
        Intent intent = new Intent(this, ResultsActivity.class);
        intent.putExtra(ResultsActivity.EXTRA_ALREADY_OPENED_RESULTS, completed);

        if (!completed) {
            // Calculate scored points
            amountCorrect = 0;
            for (int i = 0; i < questionsCount; i++) {
                if (chosenAnswersList.get(i).equals(correctAnswersList.get(i))) {
                    addPoints(pointsList.get(i));
                    amountCorrect++;
                }
            }

            completed = true;
            markCorrectAnswers = true;
            colorCorrectAnswers = true;
            allowClickingAnswers = false;
            pauseTimer();
            highlightAnswer(chosenAnswersList.get(currentQuestionIdx - 1));
            elapsed_time.setText(points + "/" + maxPoints + "\n" + DateUtils.formatElapsedTime(elapsedTime / 1000));
            elapsed_time.setTextColor(Color.parseColor("#b2ffffff"));
            elapsed_time.setTextSize(13);
        }

        intent.putExtra(ResultsActivity.EXTRA_TEST_ID, testId);
        intent.putExtra(ResultsActivity.EXTRA_TEST_VERSION, testVersion);
        intent.putExtra(ResultsActivity.EXTRA_USES_QUESTIONS, usesQuestions);
        intent.putExtra(ResultsActivity.EXTRA_USES_ROAD_SIGNS, usesRoadSigns);
        intent.putExtra(ResultsActivity.EXTRA_USES_INTERSECTIONS, usesIntersections);
        intent.putExtra(ResultsActivity.EXTRA_POINTS, points);
        intent.putExtra(ResultsActivity.EXTRA_MAX_POINTS, maxPoints);
        intent.putExtra(ResultsActivity.EXTRA_ELAPSED_TIME, elapsedTime);
        intent.putIntegerArrayListExtra(ResultsActivity.EXTRA_ANSWERS, (ArrayList<Integer>) chosenAnswersList);
        intent.putExtra(ResultsActivity.EXTRA_CORRECT, amountCorrect);
        intent.putExtra(ResultsActivity.EXTRA_INCORRECT, questionsCount - amountCorrect);
        intent.putExtra(ResultsActivity.EXTRA_ANSWERED, amountAnswered);
        intent.putExtra(ResultsActivity.EXTRA_DATE_TIME, dateStarted);

        startActivity(intent);
    }

    private void setQuestion(int index) {
        currentQuestionIdx = index;
        int questionId = currentQuestionIdx - 1;

        isQuestionImageExpanded = false;
        setQuestionText(questionsList.get(questionId));
        setImage(imagesList.get(questionId));
        setCorrectAnswer(correctAnswersList.get(questionId));
        setPointsValue(pointsList.get(questionId));
        setAnswers(answer1List.get(questionId), answer2List.get(questionId),
                answer3List.get(questionId));
        setQuestionCounter(currentQuestionIdx);
        highlightAnswer(chosenAnswersList.get(questionId));

        // Show or hide the image view based on question type.
        // Types: 0 - text only, 1 - road sign, 2 - intersection
        int questionType = questionTypes.get(questionId);
        if (questionType == 0) {
            question_image.setVisibility(View.GONE);
        } else {
            question_image.setVisibility(View.VISIBLE);

            int height;
            if (questionType == 1) {
                height = (int) getResources().getDimension(R.dimen.tests_road_sign_height);
            } else {
                //height = (int) getResources().getDimension(R.dimen.tests_intersection_height);
                height = LinearLayout.LayoutParams.WRAP_CONTENT;
            }

            ViewGroup.LayoutParams layoutParams = question_image.getLayoutParams();
            layoutParams.height = height;
            question_image.setLayoutParams(layoutParams);
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
     * Uses the right method of tinting for each API version.
     *
     * @param button The button to tint.
     * @param color  The color to tint the button with.
     */
    @SuppressWarnings("deprecation")
    private void tintAnswerButton(View button, int color) {
        Drawable oldDrawable = button.getBackground();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            oldDrawable.setColorFilter(color, PorterDuff.Mode.MULTIPLY);
        } else {
            Drawable newDrawable = DrawableCompat.wrap(oldDrawable);
            DrawableCompat.setTint(newDrawable, color);
            if (Build.VERSION.SDK_INT >= 16) {
                button.setBackground(newDrawable);
            } else {
                button.setBackgroundDrawable(newDrawable);
            }
            button.invalidate();
        }
    }

    /**
     * Highlight the appropriate buttons.
     *
     * @param answer The index of the button that was pressed, from 1 to 3.
     */
    private void highlightAnswer(int answer) {
        List<AppCompatButton> buttons = new ArrayList<>();
        buttons.add(answer_button_1);
        buttons.add(answer_button_2);
        buttons.add(answer_button_3);

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

        // Change all buttons color to normal.
        for (AppCompatButton button : buttons) {
            tintAnswerButton(button, colorNormal);
            button.setTextColor(colorNormalText);
        }

        // No answer chosen. Highlight the correct answer - Gray.
        if (answer == 0) {
            if (completed) {
                AppCompatButton correctButton = buttons.get(correctAnswer - 1);
                tintAnswerButton(correctButton, colorSelected);
                correctButton.setTextColor(colorNormalText);
            }
            return;
        }

        // Color the buttons.
        AppCompatButton selectedButton = buttons.get(answer - 1);

        if (colorCorrectAnswers) {
            if (answer == correctAnswer || completed) {
                // Correct answer - Green
                AppCompatButton correctButton = buttons.get(correctAnswer - 1);
                tintAnswerButton(correctButton, colorCorrect);
                correctButton.setTextColor(colorSelectedText);
            }
            if (answer != correctAnswer) {
                // Incorrect answer - Red
                tintAnswerButton(selectedButton, colorIncorrect);
                selectedButton.setTextColor(colorSelectedText);
            }
        } else {
            // Correct answer is not revealed.
            // Just color the selected button - Gray.
            tintAnswerButton(selectedButton, colorSelected);
            selectedButton.setTextColor(colorNormalText);
        }
    }

    private void setQuestionText(String questionText) {
        question_text.setText(questionText);
    }

    public void setImage(String path) {
        if (path != null && !path.isEmpty()) {
            InputStream inputStream;
            Drawable image = null;
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
            question_image.setVisibility(View.GONE);
        }
    }

    public void setPoints(int points) {
        this.points = points;
    }

    private void addPoints(int amount) {
        setPoints(points + amount);
    }

    private void setCorrectAnswer(int index) {
        correctAnswer = index;
    }

    private void setAnswers(String answer1, String answer2, String answer3) {
        answer_button_1.setText(answer1);
        answer_button_2.setText(answer2);
        answer_button_3.setText(answer3);
    }

    private void setQuestionCounter(int current) {
        question_counter.setText(String.format(Locale.ENGLISH, "%d/%d", current, questionsCount));
    }

    private void setPointsValue(int points) {
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

    private void restartTimer() {
        elapsed_time.setBase(SystemClock.elapsedRealtime());
        elapsed_time.start();
    }

    private void pauseTimer() {
        elapsedTime = getElapsedTime();
        elapsed_time.stop();
    }

    private void resumeTimer() {
        elapsed_time.setBase(SystemClock.elapsedRealtime() - elapsedTime);
        elapsed_time.start();
    }

    private long getElapsedTime() {
        return SystemClock.elapsedRealtime() - elapsed_time.getBase();
    }
}
