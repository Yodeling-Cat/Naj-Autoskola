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
    private static final String STATE_ELAPSED_TIME = "elapsedTime";
    private static final String STATE_POINTS = "points";
    private static final String STATE_MAX_POINTS = "maxPoints";
    private static final String STATE_AMOUNT_CORRECT = "amountCorrect";
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
    private boolean nextClickOfBackReturns;
    private boolean completed = false;
    private boolean allQuestionsAnswered = false;
    private boolean allowClickingAnswers = true;
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

        boolean isRandomTest;
        String passedAnswersString = null;

        if (savedInstanceState == null) {
            Crashlytics.getInstance().core.setInt("current_test", testId);
            dateStarted = System.currentTimeMillis() / 1000;

            Intent intent = getIntent();
            if (intent.hasExtra(EXTRA_TEST_TYPE)) {
                testType = (TestTypes) intent.getSerializableExtra(EXTRA_TEST_TYPE);
            } else {
                testType = TestTypes.NORMAL;
            }

            int selectedIndexId = intent.getIntExtra(EXTRA_TEST_ID, 1);
            Helper.Groups selectedGroup = (Helper.Groups) intent.getSerializableExtra(EXTRA_TEST_GROUP);
            usesQuestions = intent.getBooleanExtra(EXTRA_USES_QUESTIONS, true);
            usesRoadSigns = intent.getBooleanExtra(EXTRA_USES_ROAD_SIGNS, true);
            usesIntersections = intent.getBooleanExtra(EXTRA_USES_INTERSECTIONS, true);
            points = intent.getIntExtra(EXTRA_POINTS, 0);
            //maxPoints = intent.getIntExtra(EXTRA_MAX_POINTS, 0);
            elapsedTime = intent.getLongExtra(EXTRA_ELAPSED_TIME, 0);
            passedAnswersString = intent.getStringExtra(EXTRA_ANSWERS);

            // Decide which test to open.
            isRandomTest = selectedGroup != null;

            if (isRandomTest) {
                if (selectedGroup == Helper.Groups.AB) {
                    // Random number in range of 1-35
                    testId = new Random().nextInt(36 - 1) + 1;
                } else {
                    // Random number in range of 36-60
                    testId = new Random().nextInt(61 - 36) + 36;
                }
            } else {
                // Int between 1-60
                testId = selectedIndexId;
            }

            loadTestDataFromDb();

            // Start the timer.
            if (testType == TestTypes.NORMAL) {
                restartTimer();
            }

            Answers.getInstance().logCustom(new CustomEvent("Test Start")
                    .putCustomAttribute("Index", testId)
                    .putCustomAttribute("Group", Helper.getGroupFromTestIndex(testId).ordinal())
                    .putCustomAttribute("Is Random", isRandomTest ? 1 : 0)
                    .putCustomAttribute("Uses Questions", usesQuestions ? 1 : 0)
                    .putCustomAttribute("Uses RoadSigns", usesRoadSigns ? 1 : 0)
                    .putCustomAttribute("Uses Intersections", usesIntersections ? 1 : 0));

        } else {
            currentQuestionIdx = savedInstanceState.getInt(STATE_CURRENT_QUESTION_INDEX);
            completed = savedInstanceState.getBoolean(STATE_COMPLETED);
            elapsedTime = savedInstanceState.getLong(STATE_ELAPSED_TIME);
            chosenAnswersList = savedInstanceState.getIntegerArrayList(STATE_CHOSEN_ANSWERS_LIST);
            amountAnswered = savedInstanceState.getInt(STATE_AMOUNT_ANSWERED);
            allQuestionsAnswered = savedInstanceState.getBoolean(STATE_ALL_QUESTIONS_ANSWERED);
            allowClickingAnswers = savedInstanceState.getBoolean(STATE_ALLOW_CLICKING_ANSWERS);
            points = savedInstanceState.getInt(STATE_POINTS);
            maxPoints = savedInstanceState.getInt(STATE_MAX_POINTS);
            amountCorrect = savedInstanceState.getInt(STATE_AMOUNT_CORRECT);
            correctAnswer = savedInstanceState.getInt(STATE_CORRECT_ANSWER);

            // setTest() related variables.

            testType = (TestTypes) savedInstanceState.getSerializable(STATE_TEST_TYPE);
            testId = savedInstanceState.getInt(STATE_TEST_ID);
            testGroup = (Helper.Groups) savedInstanceState.getSerializable(STATE_TEST_GROUP);
            testVersion = savedInstanceState.getInt(STATE_TEST_VERSION);
            dateStarted = savedInstanceState.getLong(STATE_DATE_STARTED);
            questionTypes = savedInstanceState.getIntegerArrayList(STATE_QUESTION_TYPES);
            questionsList = savedInstanceState.getStringArrayList(STATE_QUESTIONS_LIST);
            imagesList = savedInstanceState.getStringArrayList(STATE_IMAGES_LIST);
            correctAnswersList = savedInstanceState.getIntegerArrayList(STATE_CORRECT_ANSWERS_LIST);
            answer1List = savedInstanceState.getStringArrayList(STATE_ANSWER_1_LIST);
            answer2List = savedInstanceState.getStringArrayList(STATE_ANSWER_2_LIST);
            answer3List = savedInstanceState.getStringArrayList(STATE_ANSWER_3_LIST);
            pointsList = savedInstanceState.getIntegerArrayList(STATE_POINTS_LIST);
            questionsCount = savedInstanceState.getInt(STATE_QUESTIONS_COUNT);

            usesQuestions = savedInstanceState.getBoolean(STATE_USES_QUESTIONS);
            usesRoadSigns = savedInstanceState.getBoolean(STATE_USES_ROAD_SIGNS);
            usesIntersections = savedInstanceState.getBoolean(STATE_USES_INTERSECTIONS);
        }

        progress_bar.setMax(questionsCount);

        switch (testType) {
            case NORMAL:
                if (completed) {
                    elapsed_time.setText(
                            getString(R.string.completed_test_scored_points_and_time,
                                    points, maxPoints, DateUtils.formatElapsedTime(elapsedTime / 1000)));
                    elapsed_time.setTextColor(Color.parseColor("#b2ffffff"));
                    elapsed_time.setTextSize(13);
                }
                if (chosenAnswersList.isEmpty()) {
                    // Initialize the chosenAnswersList to the right size.
                    for (int i = 0; i < questionsCount; i++) {
                        chosenAnswersList.add(0);
                    }
                }
                break;
            case CORRECT_ANSWERS:
                completed = true;
                allowClickingAnswers = false;
                elapsed_time.setText(getString(R.string.correct_answers));
                elapsed_time.setTextColor(Color.parseColor("#b2ffffff"));
                elapsed_time.setTextSize(13);
                progress_bar.setVisibility(View.GONE);
                if (chosenAnswersList.isEmpty()) {
                    // Initialize the chosenAnswersList to the right size.
                    for (int i = 0; i < questionsCount; i++) {
                        chosenAnswersList.add(correctAnswersList.get(i));
                    }
                }
                break;
            case HISTORY:
                completed = true;
                allowClickingAnswers = false;
                elapsed_time.setText(
                        getString(R.string.completed_test_scored_points_and_time,
                                points, maxPoints, DateUtils.formatElapsedTime(elapsedTime / 1000)));
                elapsed_time.setTextColor(Color.parseColor("#b2ffffff"));
                elapsed_time.setTextSize(13);
                if (passedAnswersString != null && !passedAnswersString.isEmpty()) {
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

        setQuestion(currentQuestionIdx);

        /*if (testType == TestTypes.NORMAL) {
            registerTimeLimitCallback();
        }*/

        // Set up Toolbar.
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Returns "Skupina A,B" or "Skupina C,D,T"
            String groupString = (Helper.getGroupFromTestIndex(testId) == Helper.Groups.AB)
                    ? getString(R.string.group_ab_long) : getString(R.string.group_cdt_long);

            actionBar.setTitle("Test " + testId);
            actionBar.setSubtitle(groupString);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        // Keep the screen on.
        SharedPreferences prefsSettings = getSharedPreferences(G.PREFS_SETTINGS, MODE_PRIVATE);
        if (prefsSettings.getBoolean("keep_screen_on", true)) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }

        // Load an ad.
        Helper.loadAd(ad_view);

        // Set up Debug Drawer.
        ButtonAction successfulTestAction = new ButtonAction("Successful test", new ButtonAction.Listener() {

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
                        new ActionsModule(successfulTestAction),
                        new TimberModule(),
                        new DeviceModule(this),
                        new BuildModule(this),
                        new SettingsModule(this)
                ).build();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putInt(STATE_CURRENT_QUESTION_INDEX, currentQuestionIdx);
        outState.putBoolean(STATE_COMPLETED, completed);
        outState.putLong(STATE_ELAPSED_TIME, elapsedTime);
        outState.putIntegerArrayList(STATE_CHOSEN_ANSWERS_LIST, (ArrayList<Integer>) chosenAnswersList);
        outState.putInt(STATE_AMOUNT_ANSWERED, amountAnswered);
        outState.putBoolean(STATE_ALL_QUESTIONS_ANSWERED, allQuestionsAnswered);
        outState.putBoolean(STATE_ALLOW_CLICKING_ANSWERS, allowClickingAnswers);
        outState.putInt(STATE_POINTS, points);
        outState.putInt(STATE_MAX_POINTS, maxPoints);
        outState.putInt(STATE_AMOUNT_CORRECT, amountCorrect);
        outState.putInt(STATE_CORRECT_ANSWER, correctAnswer);

        // setTest() related variables.

        outState.putSerializable(STATE_TEST_TYPE, testType);
        outState.putInt(STATE_TEST_ID, testId);
        outState.putSerializable(STATE_TEST_GROUP, testGroup);
        outState.putInt(STATE_TEST_VERSION, testVersion);
        outState.putLong(STATE_DATE_STARTED, dateStarted);
        outState.putIntegerArrayList(STATE_QUESTION_TYPES, (ArrayList<Integer>) questionTypes);
        outState.putStringArrayList(STATE_QUESTIONS_LIST, (ArrayList<String>) questionsList);
        outState.putStringArrayList(STATE_IMAGES_LIST, (ArrayList<String>) imagesList);
        outState.putIntegerArrayList(STATE_CORRECT_ANSWERS_LIST, (ArrayList<Integer>) correctAnswersList);
        outState.putStringArrayList(STATE_ANSWER_1_LIST, (ArrayList<String>) answer1List);
        outState.putStringArrayList(STATE_ANSWER_2_LIST, (ArrayList<String>) answer2List);
        outState.putStringArrayList(STATE_ANSWER_3_LIST, (ArrayList<String>) answer3List);
        outState.putIntegerArrayList(STATE_POINTS_LIST, (ArrayList<Integer>) pointsList);
        outState.putInt(STATE_QUESTIONS_COUNT, questionsCount);
        outState.putBoolean(STATE_USES_QUESTIONS, usesQuestions);
        outState.putBoolean(STATE_USES_ROAD_SIGNS, usesRoadSigns);
        outState.putBoolean(STATE_USES_INTERSECTIONS, usesIntersections);
    }

    public void loadTestDataFromDb() {
        DbHelper dbHelper = new DbHelper(this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        // Get latest version of this test.
        Cursor cTest = db.rawQuery(
                "SELECT " + DbContract.Tests.COLUMN_QUESTIONS + ", " +
                        DbContract.Tests.COLUMN_VERSION_CODE + " FROM " +
                        DbContract.Tests.TABLE_NAME + " WHERE " +
                        DbContract.Tests.COLUMN_TEST_ID + " = ?", new String[]
                        {Integer.toString(testId)});

        cTest.moveToFirst();

        // The raw 'questions' string from the Tests table.
        String questionsString = cTest.getString(cTest.getColumnIndexOrThrow(
                DbContract.Tests.COLUMN_QUESTIONS));

        testVersion = cTest.getInt(cTest.getColumnIndexOrThrow(
                DbContract.Tests.COLUMN_VERSION_CODE));

        cTest.close();


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
        questionsCount = cFilteredQuestions.getCount();

        /* Questions after filtering by type. */
        questionTypes = new ArrayList<>();
        questionsList = new ArrayList<>();
        imagesList = new ArrayList<>();
        correctAnswersList = new ArrayList<>();
        answer1List = new ArrayList<>();
        answer2List = new ArrayList<>();
        answer3List = new ArrayList<>();
        pointsList = new ArrayList<>();

        for (cFilteredQuestions.moveToFirst(); !cFilteredQuestions.isAfterLast(); cFilteredQuestions.moveToNext()) {
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
        if (!completed)
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
        if (nextClickOfBackReturns) {
            super.onBackPressed();
            return;
        }
        nextClickOfBackReturns = true;
        Toast.makeText(this, R.string.toast_press_again_to_leave, Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                nextClickOfBackReturns = false;
            }
        }, 2000);
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
            allowClickingAnswers = false;
            pauseTimer();
            //elapsed_time.setOnChronometerTickListener(null);
            highlightAnswer(chosenAnswersList.get(currentQuestionIdx - 1));
            elapsed_time.setText(
                    getString(R.string.completed_test_scored_points_and_time,
                            points, maxPoints, DateUtils.formatElapsedTime(elapsedTime / 1000)));
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
            //intersection_canvas.clearCanvas();
            //intersection_canvas.setVisibility(View.VISIBLE);
            //question_image.setVisibility(View.GONE);
        } else {
            //intersection_canvas.setVisibility(View.GONE);
            //question_image.setVisibility(View.VISIBLE);
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
            colorButton(button, colorNormal, colorNormalText);
        }

        if (testType == TestTypes.NORMAL || testType == TestTypes.HISTORY) {
            if (completed) {
                if (answer == 0) {
                    // color correct gray
                    colorButton(buttons.get(correctAnswer - 1), colorSelected, colorNormalText);
                } else {
                    // always color correctAnswer green
                    colorButton(buttons.get(correctAnswer - 1), colorCorrect, colorSelectedText);

                    if (answer != correctAnswer) {
                        // color incorrectAnswer red
                        colorButton(buttons.get(answer - 1), colorIncorrect, colorSelectedText);
                    }
                }
            } else {
                if (answer != 0) {
                    // color answer gray
                    colorButton(buttons.get(answer - 1), colorSelected, colorNormalText);
                }
            }
        } else if (testType == TestTypes.CORRECT_ANSWERS) {
            // just color correct green every time. This should probably be handle by some different function, some that doesn't take Answer as an argument.
            colorButton(buttons.get(correctAnswer - 1), colorCorrect, colorSelectedText);
        }
    }

    private void colorButton(AppCompatButton button, int color, int textColor) {
        if (button != null) {
            tintAnswerButton(button, color);
            button.setTextColor(textColor);
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

    /**
     * Checks if the 20 minute time limit has ran out.
     */
    /*private void registerTimeLimitCallback() {
        // TODO: Check if works after !orientation change! and after going to launcher or locking. And if it gets removed on complete.
        // TODO: Set to 20 mins both places.
        if (!completed && getElapsedTime() < 3000) {
            elapsed_time.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {
                @Override
                public void onChronometerTick(Chronometer chronometer) {
                    if (!completed && getElapsedTime() > 3000) {
                        Toast.makeText(getApplicationContext(), R.string.toast_time_limit_passed, Toast.LENGTH_LONG).show();
                        elapsed_time.setOnChronometerTickListener(null);
                    }
                }
            });
        }
    }*/
}
