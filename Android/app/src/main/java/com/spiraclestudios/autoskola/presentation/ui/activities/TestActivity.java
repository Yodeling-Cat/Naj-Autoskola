// Copyright (c) 2015-2017. Spiracle Software. All Rights Reserved.

package com.spiraclestudios.autoskola.presentation.ui.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.text.format.DateUtils;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Chronometer;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import butterknife.BindView;
import butterknife.OnClick;
import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.CustomEvent;
import com.google.android.gms.ads.AdView;
import com.spiraclestudios.autoskola.BaseApplication;
import com.spiraclestudios.autoskola.DbContract;
import com.spiraclestudios.autoskola.DbHelper;
import com.spiraclestudios.autoskola.G;
import com.spiraclestudios.autoskola.R;
import com.spiraclestudios.autoskola.TestFragmentInteractor;
import com.spiraclestudios.autoskola.TestPagerAdapter;
import com.spiraclestudios.autoskola.Utils;
import com.spiraclestudios.autoskola.domain.Groups;
import com.spiraclestudios.autoskola.framework.platform.AdLoader;
import com.spiraclestudios.autoskola.framework.platform.RemoveAds;
import com.spiraclestudios.autoskola.framework.presentation.ui.BaseActivity;
import com.spiraclestudios.autoskola.framework.presentation.ui.StandardActivity;
import com.zplesac.connectionbuddy.ConnectionBuddy;
import com.zplesac.connectionbuddy.cache.ConnectionBuddyCache;
import com.zplesac.connectionbuddy.interfaces.ConnectivityChangeListener;
import com.zplesac.connectionbuddy.models.ConnectivityEvent;
import com.zplesac.connectionbuddy.models.ConnectivityState;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.solovyev.android.checkout.ActivityCheckout;
import org.solovyev.android.checkout.Billing;
import org.solovyev.android.checkout.Checkout;
import timber.log.Timber;

import static android.os.Build.VERSION.SDK_INT;
import static android.os.Build.VERSION_CODES.LOLLIPOP;

/**
 * Added by benji on 21/11/2015.
 */
public class TestActivity extends StandardActivity
    implements ConnectivityChangeListener, TestFragmentInteractor {

  public final static String EXTRA_TEST_TYPE = "com.spiraclestudios.autoskola.TEST_TYPE";
  public final static String EXTRA_TEST_GROUP = "com.spiraclestudios.autoskola.TEST_GROUP";
  public final static String EXTRA_TEST_ID = "com.spiraclestudios.autoskola.TEST_ID";
  public final static String EXTRA_USES_QUESTIONS = "com.spiraclestudios.autoskola.USE_QUESTIONS";
  public final static String EXTRA_USES_ROAD_SIGNS = "com.spiraclestudios.autoskola.USE_ROAD_SIGNS";
  public final static String EXTRA_USES_INTERSECTIONS =
      "com.spiraclestudios.autoskola.USE_INTERSECTIONS";
  public final static String EXTRA_POINTS = "com.spiraclestudios.autoskola.POINTS";
  public final static String EXTRA_MAX_POINTS = "com.spiraclestudios.autoskola.MAX_POINTS";
  public final static String EXTRA_ELAPSED_TIME = "com.spiraclestudios.autoskola.ELAPSED_TIME";
  public final static String EXTRA_ANSWERS = "com.spiraclestudios.autoskola.ANSWERS";

  private static final String STATE_CURRENT_QUESTION_INDEX = "currentQuestionIdx";
  private static final String STATE_COMPLETED = "completed";
  private static final String STATE_CHOSEN_ANSWERS_LIST = "chosenAnswersList";
  private static final String STATE_AMOUNT_ANSWERED = "amountAnswered";
  private static final String STATE_TIME_LIMIT_HAS_RAN_OUT = "timeLimitHasRanOut";
  private static final String STATE_ALL_QUESTIONS_ANSWERED = "allQuestionsAnswered";
  private static final String STATE_ALLOW_CLICKING_ANSWERS = "allowClickingAnswers";
  private static final String STATE_REVEAL_ANSWER_IMMEDIATELY = "revealAnswerImmediately";
  private static final String STATE_GO_TO_START_AFTER_COMPLETING_TEST =
      "goToStartAfterCompletingTest";
  private static final String STATE_ELAPSED_TIME = "elapsedTime";
  private static final String STATE_POINTS = "points";
  private static final String STATE_MAX_POINTS = "maxPoints";
  private static final String STATE_AMOUNT_CORRECT = "amountCorrect";
  private static final String STATE_INTERSECTION_CAR_POSITION_NOTICE_WAS_CLOSED =
      "intersectionCarPositionNoticeWasClosed";

  private static final String STATE_TEST_TYPE = "testType";
  private static final String STATE_TEST_ID = "testIndex";
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
    NORMAL, CORRECT_ANSWERS, HISTORY
  }

  // [Internal]
  private int currentQuestionIdx = 0;
  private boolean intersectionCarPositionNoticeWasClosed;
  private boolean nextClickOfBackReturns;
  // 20 minutes in milliseconds
  private final int TIME_LIMIT = (20 * 60) * 1000;
  private boolean timeLimitHasRanOut = false;
  private boolean completed = false;
  private boolean allQuestionsAnswered = false;
  private boolean allowClickingAnswers = true;
  private boolean revealAnswerImmediately;
  private boolean goToStartAfterCompletingTest;
  private long elapsedTime;
  private int amountAnswered;
  private int points = 0;
  private List<Integer> chosenAnswersList = new ArrayList<>();
  private TestPagerAdapter testPagerAdapter;
  private ActivityCheckout checkout;
  private RemoveAds removeAds;
  private AdLoader adLoader;

  // [Test Info]
  private TestTypes testType;
  private int testId = 1;
  private Groups testGroup = null;
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
  @BindView(R.id.questions_view_pager) ViewPager questions_view_pager;
  @BindView(R.id.ad_view) AdView ad_view;
  @BindView(R.id.points_value) TextView points_value;
  @BindView(R.id.question_counter) TextView question_counter;
  @BindView(R.id.elapsed_time) Chronometer elapsed_time;
  @BindView(R.id.progress_bar) ProgressBar progress_bar;
  @BindView(R.id.intersection_car_position_notice) LinearLayout intersection_car_position_notice;

  @Override protected BaseActivity getThis() {
    return this;
  }

  @Override public void setActivityContentView() {
    setContentView(R.layout.test__activity);
  }

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    final Billing billing = BaseApplication.get().getBilling();
    checkout = Checkout.forActivity(this, billing);
    checkout.start();
    adLoader = new AdLoader(this);
    removeAds = new RemoveAds();

    if (savedInstanceState != null) {
      ConnectionBuddyCache.clearLastNetworkState(this);
    }

    boolean isRandomTest;
    String passedAnswersString = null;

    if (savedInstanceState == null) {
      Crashlytics.getInstance().core.setInt("current_test", testId);
      dateStarted = System.currentTimeMillis() / 1000;

      // Read preferences
      SharedPreferences prefsGeneric = getSharedPreferences(G.PREFS_GENERIC, MODE_PRIVATE);
      SharedPreferences prefsSettings = getSharedPreferences(G.PREFS_SETTINGS, MODE_PRIVATE);
      intersectionCarPositionNoticeWasClosed =
          prefsGeneric.getBoolean("intersection_car_position_notice_was_closed", false);
      revealAnswerImmediately = prefsSettings.getBoolean("reveal_answer_immediately", false);
      goToStartAfterCompletingTest =
          prefsSettings.getBoolean("go_to_start_after_completing_test", true);

      Intent intent = getIntent();
      if (intent.hasExtra(EXTRA_TEST_TYPE)) {
        testType = (TestTypes) intent.getSerializableExtra(EXTRA_TEST_TYPE);
      } else {
        testType = TestTypes.NORMAL;
      }

      int selectedIndexId = intent.getIntExtra(EXTRA_TEST_ID, 1);
      Groups selectedGroup = (Groups) intent.getSerializableExtra(EXTRA_TEST_GROUP);
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
        if (selectedGroup == Groups.AB) {
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

      Answers.getInstance()
          .logCustom(new CustomEvent("Test Start").putCustomAttribute("Index", testId)
              .putCustomAttribute("Group", Utils.getGroupFromTestIndex(testId).ordinal())
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
      timeLimitHasRanOut = savedInstanceState.getBoolean(STATE_TIME_LIMIT_HAS_RAN_OUT);
      allQuestionsAnswered = savedInstanceState.getBoolean(STATE_ALL_QUESTIONS_ANSWERED);
      allowClickingAnswers = savedInstanceState.getBoolean(STATE_ALLOW_CLICKING_ANSWERS);
      revealAnswerImmediately = savedInstanceState.getBoolean(STATE_REVEAL_ANSWER_IMMEDIATELY);
      goToStartAfterCompletingTest =
          savedInstanceState.getBoolean(STATE_GO_TO_START_AFTER_COMPLETING_TEST);
      points = savedInstanceState.getInt(STATE_POINTS);
      maxPoints = savedInstanceState.getInt(STATE_MAX_POINTS);
      amountCorrect = savedInstanceState.getInt(STATE_AMOUNT_CORRECT);
      intersectionCarPositionNoticeWasClosed =
          savedInstanceState.getBoolean(STATE_INTERSECTION_CAR_POSITION_NOTICE_WAS_CLOSED);

      // setTest() related variables.

      testType = (TestTypes) savedInstanceState.getSerializable(STATE_TEST_TYPE);
      testId = savedInstanceState.getInt(STATE_TEST_ID);
      testGroup = (Groups) savedInstanceState.getSerializable(STATE_TEST_GROUP);
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

    tintProgressBarWithAccentColor();

    switch (testType) {
      case NORMAL:
        if (completed) {
          elapsed_time.setText(
              getString(R.string.test__text__completed_test_scored_points_and_time, points,
                  maxPoints, DateUtils.formatElapsedTime(elapsedTime / 1000)));
          //elapsed_time.setTextColor(Color.parseColor("#b2ffffff"));
          elapsed_time.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(
              R.dimen.tests__app_bar__statistics__important_smaller_text_size));
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
        elapsed_time.setText(getString(R.string.test__text__correct_answers));
        //elapsed_time.setTextColor(Color.parseColor("#b2ffffff"));
        elapsed_time.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(
            R.dimen.tests__app_bar__statistics__important_smaller_text_size));
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
            getString(R.string.test__text__completed_test_scored_points_and_time, points, maxPoints,
                DateUtils.formatElapsedTime(elapsedTime / 1000)));
        //elapsed_time.setTextColor(Color.parseColor("#b2ffffff"));
        elapsed_time.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(
            R.dimen.tests__app_bar__statistics__important_smaller_text_size));
        if (passedAnswersString != null && !passedAnswersString.isEmpty()) {
          for (String answer : passedAnswersString.split(",")) {
            int chosenAnswer = Integer.parseInt(answer);
            chosenAnswersList.add(chosenAnswer);
            if (chosenAnswer != 0) amountAnswered++;
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

    // Set up Toolbar.
    Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);

    ActionBar actionBar = getSupportActionBar();
    if (actionBar != null) {
      // Returns "Skupina A,B" or "Skupina C,D,T"
      String groupString =
          (Utils.getGroupFromTestIndex(testId) == Groups.AB) ? getString(R.string.text__group_ab)
              : getString(R.string.text__group_cdt);

      actionBar.setTitle(getString(R.string.screen_title__test, testId));
      actionBar.setSubtitle(groupString);
      actionBar.setDisplayHomeAsUpEnabled(true);
    }

    // Set up ViewPager.
    testPagerAdapter = new TestPagerAdapter(getSupportFragmentManager(), questionsCount);
    questions_view_pager.setAdapter(testPagerAdapter);

    questions_view_pager.addOnPageChangeListener(new OnPageChangeListener() {
      @Override
      public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

      }

      @Override public void onPageSelected(int position) {
        testPagerAdapter.scrollToTop();
        setQuestion(position);
      }

      @Override public void onPageScrollStateChanged(int state) {

      }
    });

    // Keep the screen on.
    if (!completed && testType == TestTypes.NORMAL) {
      SharedPreferences prefsSettings = getSharedPreferences(G.PREFS_SETTINGS, MODE_PRIVATE);
      if (prefsSettings.getBoolean("keep_screen_on", true)) {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
      }
    }

    // Checks if the 20 minute time limit has ran out.
    if (testType == TestTypes.NORMAL) {
      registerTimeLimitCallback();
    }

    setQuestion(currentQuestionIdx);
  }

  @Override public TestPagerAdapter getTestPagerAdapter() {
    return testPagerAdapter;
  }

  @Override public int getQuestionType(int index) {
    return questionTypes.get(index);
  }

  @Override public String getQuestionText(int index) {
    return questionsList.get(index);
  }

  @Override public Drawable getQuestionImage(int index) {
    return getImageFromAssets(index);
  }

  @Override public List<String> getQuestionAnswers(int index) {
    ArrayList<String> answers = new ArrayList<>();
    answers.add(answer1List.get(index));
    answers.add(answer2List.get(index));
    answers.add(answer3List.get(index));
    return answers;
  }

  @Override public int getChosenAnswer(int index) {
    return chosenAnswersList.get(index);
  }

  @Override public TestTypes getTestType() {
    return testType;
  }

  @Override public boolean getCompleted() {
    return completed;
  }

  @Override public boolean getAllowClickingAnswers() {
    return allowClickingAnswers;
  }

  @Override public int getCorrectAnswer(int index) {
    return correctAnswersList.get(index);
  }

  @Override public boolean getShouldRevealAnswersImmediately() {
    return revealAnswerImmediately;
  }

  @Override public void onConnectionChange(ConnectivityEvent event) {
    if (event.getState().equals(ConnectivityState.CONNECTED)) {
      removeAds.hasPurchasedRemoveAds(checkout, isRemoveAdsPurchased -> {
        if (isRemoveAdsPurchased) {
          adLoader.hideAdView(ad_view);
        } else {
          adLoader.loadAd(ad_view);
        }
      });
    } else {
      adLoader.hideAdView(ad_view);
    }
  }

  private void tintProgressBarWithAccentColor() {
    if (SDK_INT < LOLLIPOP) {
      Drawable wrapDrawable = DrawableCompat.wrap(progress_bar.getProgressDrawable());

      TypedValue colorAccent = new TypedValue();
      getTheme().resolveAttribute(R.attr.colorAccent, colorAccent, true);

      DrawableCompat.setTint(wrapDrawable, colorAccent.data);
      progress_bar.setProgressDrawable(DrawableCompat.unwrap(wrapDrawable));
    } else {
      //progress_bar.getProgressDrawable().setColorFilter(ContextCompat.getColor(this, R.color.accent), PorterDuff.Mode.SRC_IN);
    }
  }

  @Override protected void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);

    outState.putInt(STATE_CURRENT_QUESTION_INDEX, currentQuestionIdx);
    outState.putBoolean(STATE_COMPLETED, completed);
    outState.putLong(STATE_ELAPSED_TIME, elapsedTime);
    outState.putIntegerArrayList(STATE_CHOSEN_ANSWERS_LIST, (ArrayList<Integer>) chosenAnswersList);
    outState.putInt(STATE_AMOUNT_ANSWERED, amountAnswered);
    outState.putBoolean(STATE_TIME_LIMIT_HAS_RAN_OUT, timeLimitHasRanOut);
    outState.putBoolean(STATE_ALL_QUESTIONS_ANSWERED, allQuestionsAnswered);
    outState.putBoolean(STATE_ALLOW_CLICKING_ANSWERS, allowClickingAnswers);
    outState.putBoolean(STATE_REVEAL_ANSWER_IMMEDIATELY, revealAnswerImmediately);
    outState.putBoolean(STATE_GO_TO_START_AFTER_COMPLETING_TEST, goToStartAfterCompletingTest);
    outState.putInt(STATE_POINTS, points);
    outState.putInt(STATE_MAX_POINTS, maxPoints);
    outState.putInt(STATE_AMOUNT_CORRECT, amountCorrect);
    outState.putBoolean(STATE_INTERSECTION_CAR_POSITION_NOTICE_WAS_CLOSED,
        intersectionCarPositionNoticeWasClosed);

    // setTest() related variables.

    outState.putSerializable(STATE_TEST_TYPE, testType);
    outState.putInt(STATE_TEST_ID, testId);
    outState.putSerializable(STATE_TEST_GROUP, testGroup);
    outState.putInt(STATE_TEST_VERSION, testVersion);
    outState.putLong(STATE_DATE_STARTED, dateStarted);
    outState.putIntegerArrayList(STATE_QUESTION_TYPES, (ArrayList<Integer>) questionTypes);
    outState.putStringArrayList(STATE_QUESTIONS_LIST, (ArrayList<String>) questionsList);
    outState.putStringArrayList(STATE_IMAGES_LIST, (ArrayList<String>) imagesList);
    outState.putIntegerArrayList(STATE_CORRECT_ANSWERS_LIST,
        (ArrayList<Integer>) correctAnswersList);
    outState.putStringArrayList(STATE_ANSWER_1_LIST, (ArrayList<String>) answer1List);
    outState.putStringArrayList(STATE_ANSWER_2_LIST, (ArrayList<String>) answer2List);
    outState.putStringArrayList(STATE_ANSWER_3_LIST, (ArrayList<String>) answer3List);
    outState.putIntegerArrayList(STATE_POINTS_LIST, (ArrayList<Integer>) pointsList);
    outState.putInt(STATE_QUESTIONS_COUNT, questionsCount);
    outState.putBoolean(STATE_USES_QUESTIONS, usesQuestions);
    outState.putBoolean(STATE_USES_ROAD_SIGNS, usesRoadSigns);
    outState.putBoolean(STATE_USES_INTERSECTIONS, usesIntersections);
  }

  private void loadTestDataFromDb() {
    DbHelper dbHelper = new DbHelper(this);
    SQLiteDatabase db = dbHelper.getReadableDatabase();

    // Get latest version of this test.
    Cursor cTest = db.rawQuery("SELECT "
        + DbContract.Tests.COLUMN_QUESTIONS
        + ", "
        + DbContract.Tests.COLUMN_VERSION_CODE
        + " FROM "
        + DbContract.Tests.TABLE_NAME
        + " WHERE "
        + DbContract.Tests.COLUMN_TEST_ID
        + " = ?", new String[] { Integer.toString(testId) });

    cTest.moveToFirst();

    // The raw 'questions' string from the Tests table.
    String questionsString =
        cTest.getString(cTest.getColumnIndexOrThrow(DbContract.Tests.COLUMN_QUESTIONS));

    testVersion = cTest.getInt(cTest.getColumnIndexOrThrow(DbContract.Tests.COLUMN_VERSION_CODE));

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
    String query = "SELECT * FROM "
        + DbContract.Questions.TABLE_NAME
        + " WHERE "
        + DbContract.Questions.COLUMN_QUESTION_ID
        + " IN ("
        + questionsString
        + ") AND "
        + DbContract.Questions.COLUMN_VERSION
        + " <= ? "
        + typeSelector;

    Cursor cFilteredQuestions = db.rawQuery(query, new String[] { Integer.toString(testVersion) });
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

    for (cFilteredQuestions.moveToFirst(); !cFilteredQuestions.isAfterLast();
        cFilteredQuestions.moveToNext()) {
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

  @Override public void onPause() {
    ad_view.pause();
    if (!completed) pauseTimer();
    super.onPause();
  }

  @Override public void onResume() {
    ad_view.resume();
    if (!completed) resumeTimer();
    super.onResume();
  }

  @Override protected void onStart() {
    super.onStart();
    ConnectionBuddy.getInstance().registerForConnectivityEvents(this, this);
  }

  @Override protected void onStop() {
    super.onStop();
    ConnectionBuddy.getInstance().unregisterFromConnectivityEvents(this);
  }

  @Override public void onDestroy() {
    checkout.stop();
    ad_view.destroy();
    super.onDestroy();
  }

  @Override public void onBackPressed() {
    if (nextClickOfBackReturns) {
      super.onBackPressed();
      return;
    }
    nextClickOfBackReturns = true;
    Toast.makeText(this, R.string.test__toast__press_again_to_leave, Toast.LENGTH_SHORT).show();

    new Handler().postDelayed(new Runnable() {
      @Override public void run() {
        nextClickOfBackReturns = false;
      }
    }, 2000);
  }

  @Override public boolean onCreateOptionsMenu(Menu menu) {
    if (testType == TestTypes.NORMAL) {
      getMenuInflater().inflate(R.menu.activity__test, menu);
    }
    return true;
  }

  @Override public boolean onOptionsItemSelected(MenuItem item) {
    int id = item.getItemId();

    switch (id) {
      case android.R.id.home:
        onBackPressed();
        return true;
      case R.id.action__evaluate:
        evaluateTest();
        return true;
    }
    return super.onOptionsItemSelected(item);
  }

  @OnClick(R.id.intersection_car_position_notice_close)
  public void closeIntersectionCarPositionNotice() {
    intersection_car_position_notice.setVisibility(View.GONE);
    intersectionCarPositionNoticeWasClosed = true;

    SharedPreferences prefsGeneric = getSharedPreferences(G.PREFS_GENERIC, MODE_PRIVATE);
    SharedPreferences.Editor prefsEdit = prefsGeneric.edit();

    prefsEdit.putBoolean("intersection_car_position_notice_was_closed", true);
    prefsEdit.apply();
  }

  /**
   * Moves to the specified question.
   */
  @Override public void goToQuestion(int index, boolean smoothScroll) {
    setQuestion(index);
    questions_view_pager.setCurrentItem(currentQuestionIdx, smoothScroll);
  }

  /**
   * Moves to the next question and highlights it.
   */
  @Override public void nextQuestion() {
    if (currentQuestionIdx < questionsList.size() - 1) {
      setQuestion(currentQuestionIdx + 1);
      questions_view_pager.setCurrentItem(currentQuestionIdx, true);
    }
  }

  /**
   * @param answer The index of the answer button. In range 1-3.
   */
  @Override public void selectAnswer(int answer) {
    int currentAnswer = chosenAnswersList.get(currentQuestionIdx);
    if (currentAnswer != 0 && revealAnswerImmediately) return;

    // Un-check the answer if the user clicks on the current answer.
    if (currentAnswer == answer) {
      amountAnswered--;
      allQuestionsAnswered = false;
      chosenAnswersList.set(currentQuestionIdx, 0);
    }
    // If there is currently no answer or a different answer than the current one was chosen
    else {
      chosenAnswersList.set(currentQuestionIdx, answer);

      if (currentAnswer == 0) {
        amountAnswered++;

        if (revealAnswerImmediately && chosenAnswersList.get(currentQuestionIdx)
            .equals(correctAnswersList.get(currentQuestionIdx))) {
          addPoints(pointsList.get(currentQuestionIdx));
          setPointsValue(pointsList.get(currentQuestionIdx));
        }
      }
    }

    // If the toast wasn't shown yet, then show it.
    if (!allQuestionsAnswered && amountAnswered == questionsCount) {
      allQuestionsAnswered = true;
      Toast.makeText(this, R.string.test__toast__all_questions_answered, Toast.LENGTH_SHORT).show();
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
    boolean alreadyOpenedResults = completed;

    if (!completed) {
      completed = true;
      allowClickingAnswers = false;

      pauseTimer();
      unregisterTimeLimitCallback();

      // Calculate scored points
      amountCorrect = 0;
      for (int i = 0; i < questionsCount; i++) {
        if (chosenAnswersList.get(i).equals(correctAnswersList.get(i))) {
          if (!revealAnswerImmediately) {
            addPoints(pointsList.get(i));
          }
          amountCorrect++;
        }
      }

      elapsed_time.setText(
          getString(R.string.test__text__completed_test_scored_points_and_time, points, maxPoints,
              DateUtils.formatElapsedTime(elapsedTime / 1000)));
      // TODO: Don't use hard-coded color.
      //elapsed_time.setTextColor(Color.parseColor("#b2ffffff"));
      elapsed_time.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(
          R.dimen.tests__app_bar__statistics__important_smaller_text_size));
      getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

      testPagerAdapter.onTestCompleted();

      if (goToStartAfterCompletingTest) {
        goToQuestion(0, false);
      }
    }

    Intent intent = new Intent(this, ResultActivity.class);
    intent.putExtra(ResultActivity.EXTRA_ALREADY_OPENED_RESULTS, alreadyOpenedResults);
    intent.putExtra(ResultActivity.EXTRA_TEST_ID, testId);
    intent.putExtra(ResultActivity.EXTRA_TEST_VERSION, testVersion);
    intent.putExtra(ResultActivity.EXTRA_USES_QUESTIONS, usesQuestions);
    intent.putExtra(ResultActivity.EXTRA_USES_ROAD_SIGNS, usesRoadSigns);
    intent.putExtra(ResultActivity.EXTRA_USES_INTERSECTIONS, usesIntersections);
    intent.putExtra(ResultActivity.EXTRA_POINTS, points);
    intent.putExtra(ResultActivity.EXTRA_MAX_POINTS, maxPoints);
    intent.putExtra(ResultActivity.EXTRA_ELAPSED_TIME, elapsedTime);
    intent.putIntegerArrayListExtra(ResultActivity.EXTRA_ANSWERS,
        (ArrayList<Integer>) chosenAnswersList);
    intent.putExtra(ResultActivity.EXTRA_CORRECT, amountCorrect);
    intent.putExtra(ResultActivity.EXTRA_INCORRECT, questionsCount - amountCorrect);
    intent.putExtra(ResultActivity.EXTRA_ANSWERED, amountAnswered);
    intent.putExtra(ResultActivity.EXTRA_DATE_TIME, dateStarted);

    startActivity(intent);
  }

  private void setQuestion(int index) {
    currentQuestionIdx = Utils.clamp(0, index, questionsCount);

    setPointsValue(pointsList.get(currentQuestionIdx));
    setQuestionCounter(currentQuestionIdx);

    int questionType = questionTypes.get(currentQuestionIdx);

    if (questionType == 2) {
      intersection_car_position_notice.setVisibility(
          intersectionCarPositionNoticeWasClosed ? View.GONE : View.VISIBLE);
    } else {
      intersection_car_position_notice.setVisibility(View.GONE);
    }
  }

  private void setPoints(int points) {
    this.points = points;
  }

  private void addPoints(int amount) {
    setPoints(points + amount);
  }

  private void setQuestionCounter(int current) {
    question_counter.setText(String.format(Locale.ENGLISH, "%d/%d", current + 1, questionsCount));
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

    String immediatePoints = "";
    if (revealAnswerImmediately) {
      immediatePoints = " (" + this.points + "/" + this.maxPoints + ")";
    }

    points_value.setText(
        String.format(Locale.ENGLISH, "%d %s%s", points, pointsSuffix, immediatePoints));
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
  private void registerTimeLimitCallback() {
    if (!timeLimitHasRanOut && !completed) {
      elapsed_time.setOnChronometerTickListener(chronometer -> {
        if (getElapsedTime() > TIME_LIMIT) {
          timeLimitHasRanOut = true;
          unregisterTimeLimitCallback();
          Toast.makeText(TestActivity.this, R.string.test__toast__time_limit_passed,
              Toast.LENGTH_LONG).show();
        }
      });
    }
  }

  private void unregisterTimeLimitCallback() {
    elapsed_time.setOnChronometerTickListener(null);
  }

  public Drawable getImageFromAssets(int index) {
    String path = imagesList.get(index);
    Drawable image = null;
    int type = getQuestionType(index);

    if (type != 0 && path != null && !path.isEmpty()) {
      InputStream inputStream;

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
          inputStream =
              getAssets().open("images/road_signs/" + category + "/" + signImage + ".png");
          image = Drawable.createFromStream(inputStream, null);
        } catch (IOException ex) {
          // If file doesn't exist, use the placeholder image.
          image = ContextCompat.getDrawable(this, R.drawable.placeholder_small);
          Timber.d("Image \"images/road_signs/%s/%s.png\" does not exist.", category, signImage);
        }
      }
      // Intersections
      else if (type == 2) {
        // Use image from the assets folder.
        try {
          inputStream = getAssets().open("images/intersections/" + path + ".png");
          image = Drawable.createFromStream(inputStream, null);
        } catch (IOException ex) {
          // If file doesn't exist, use the placeholder image.
          image = ContextCompat.getDrawable(this, R.drawable.placeholder_large);
          Timber.d("Image \"images/intersections/%s.png\" does not exist.", path);
        }
      }
    }
    return image;
  }
}
