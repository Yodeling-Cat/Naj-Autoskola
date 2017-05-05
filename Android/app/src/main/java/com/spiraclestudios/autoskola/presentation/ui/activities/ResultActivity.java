// Copyright (c) 2015-2017. Spiracle Software. All Rights Reserved.

package com.spiraclestudios.autoskola.presentation.ui.activities;

import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.text.format.DateUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import butterknife.Bind;
import butterknife.OnClick;
import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.CustomEvent;
import com.spiraclestudios.autoskola.DbContract;
import com.spiraclestudios.autoskola.DbHelper;
import com.spiraclestudios.autoskola.G;
import com.spiraclestudios.autoskola.R;
import com.spiraclestudios.autoskola.Utils;
import com.spiraclestudios.autoskola.domain.Groups;
import com.spiraclestudios.autoskola.domain.TestResult;
import com.spiraclestudios.autoskola.framework.platform.Sharing;
import com.spiraclestudios.autoskola.framework.presentation.ui.BaseActivity;
import com.spiraclestudios.autoskola.framework.presentation.ui.StandardActivity;
import com.zplesac.connectionbuddy.ConnectionBuddy;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import timber.log.Timber;

public class ResultActivity extends StandardActivity {

  public final static String EXTRA_ALREADY_OPENED_RESULTS =
      "com.spiraclestudios.autoskola.ALREADY_CHECKED_RESULTS";
  public final static String EXTRA_TEST_ID = "com.spiraclestudios.autoskola.TEST_ID";
  public final static String EXTRA_TEST_VERSION = "com.spiraclestudios.autoskola.TEST_VERSION";
  public final static String EXTRA_USES_QUESTIONS = "com.spiraclestudios.autoskola.USES_QUESTIONS";
  public final static String EXTRA_USES_ROAD_SIGNS =
      "com.spiraclestudios.autoskola.USES_ROAD_SIGNS";
  public final static String EXTRA_USES_INTERSECTIONS =
      "com.spiraclestudios.autoskola.USES_INTERSECTIONS";
  public final static String EXTRA_POINTS = "com.spiraclestudios.autoskola.POINTS";
  public final static String EXTRA_MAX_POINTS = "com.spiraclestudios.autoskola.MAX_POINTS";
  public final static String EXTRA_ELAPSED_TIME = "com.spiraclestudios.autoskola.ELAPSED_TIME";
  public final static String EXTRA_ANSWERS = "com.spiraclestudios.autoskola.ANSWERS";
  public final static String EXTRA_CORRECT = "com.spiraclestudios.autoskola.CORRECT";
  public final static String EXTRA_INCORRECT = "com.spiraclestudios.autoskola.INCORRECT";
  public final static String EXTRA_ANSWERED = "com.spiraclestudios.autoskola.ANSWERED";
  public final static String EXTRA_DATE_TIME = "com.spiraclestudios.autoskola.DATE_TIME";

  private final static String STATE_ALREADY_OPENED_RESULTS = "alreadyOpenedResults";
  private final static String STATE_TEST_ID = "testIndex";
  private final static String STATE_TEST_VERSION = "testVersion";
  private final static String STATE_USES_QUESTIONS = "usesQuestions";
  private final static String STATE_USES_ROAD_SIGNS = "usesRoadSigns";
  private final static String STATE_USES_INTERSECTIONS = "usesIntersections";
  private final static String STATE_POINTS = "points";
  private final static String STATE_MAX_POINTS = "maxPoints";
  private final static String STATE_ELAPSED_TIME = "elapsedTime";
  private final static String STATE_CHOSEN_ANSWERS = "chosenAnswersList";
  private final static String STATE_AMOUNT_CORRECT = "amountCorrect";
  private final static String STATE_AMOUNT_INCORRECT = "amountIncorrect";
  private final static String STATE_AMOUNT_ANSWERED = "amountAnswered";
  private final static String STATE_AMOUNT_UNANSWERED = "amountUnanswered";
  private final static String STATE_DATE_STARTED = "dateStarted";

  private boolean alreadyOpenedResults;
  private int testId;
  private int testVersion;
  private boolean usesQuestions;
  private boolean usesRoadSigns;
  private boolean usesIntersections;
  private int points;
  private int maxPoints;
  private long elapsedTime;
  private List<Integer> chosenAnswersList;
  private int amountCorrect;
  private int amountIncorrect;
  private int amountAnswered;
  private int amountUnanswered;
  private long dateStarted;

  @Bind(R.id.results_title) TextView results_title;
  @Bind(R.id.results_summary) TextView results_summary;
  @Bind(R.id.results_points) TextView results_points;
  @Bind(R.id.results_correct) TextView results_correct;
  @Bind(R.id.results_incorrect) TextView results_incorrect;
  @Bind(R.id.results_unanswered) TextView results_unanswered;
  @Bind(R.id.results_unanswered_container) LinearLayout results_unanswered_container;
  @Bind(R.id.results_elapsed_time) TextView results_time;
  @Bind(R.id.rate_app) Button rate_app;

  @Override protected BaseActivity getThis() {
    return this;
  }

  @Override public void setActivityContentView() {
    setContentView(R.layout.result__activity);
  }

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    // Show the rate_app button if the user hasn't rated the app before.
    SharedPreferences prefs = getSharedPreferences(G.PREFS_GENERIC, MODE_PRIVATE);
    if (prefs.getBoolean("has_rated_app", false)) {
      rate_app.setVisibility(View.GONE);
    }

    if (savedInstanceState == null) {
      // Read the intent.
      Intent intent = getIntent();
      alreadyOpenedResults = intent.getBooleanExtra(EXTRA_ALREADY_OPENED_RESULTS, false);
      testId = intent.getIntExtra(EXTRA_TEST_ID, 1);
      testVersion = intent.getIntExtra(EXTRA_TEST_VERSION, 1);
      usesQuestions = intent.getBooleanExtra(EXTRA_USES_QUESTIONS, true);
      usesRoadSigns = intent.getBooleanExtra(EXTRA_USES_ROAD_SIGNS, true);
      usesIntersections = intent.getBooleanExtra(EXTRA_USES_INTERSECTIONS, true);
      points = intent.getIntExtra(EXTRA_POINTS, 0);
      maxPoints = intent.getIntExtra(EXTRA_MAX_POINTS, 0);
      elapsedTime = intent.getLongExtra(EXTRA_ELAPSED_TIME, 0);
      chosenAnswersList = intent.getIntegerArrayListExtra(EXTRA_ANSWERS);
      amountCorrect = intent.getIntExtra(EXTRA_CORRECT, 0);
      amountIncorrect = intent.getIntExtra(EXTRA_INCORRECT, 0);
      amountAnswered = intent.getIntExtra(EXTRA_ANSWERED, 0);
      dateStarted = intent.getLongExtra(EXTRA_DATE_TIME, 0);

      amountUnanswered = chosenAnswersList.size() - amountAnswered;
    } else {
      alreadyOpenedResults = savedInstanceState.getBoolean(STATE_ALREADY_OPENED_RESULTS, false);
      testId = savedInstanceState.getInt(STATE_TEST_ID, 1);
      testVersion = savedInstanceState.getInt(STATE_TEST_VERSION, 1);
      usesQuestions = savedInstanceState.getBoolean(STATE_USES_QUESTIONS, true);
      usesRoadSigns = savedInstanceState.getBoolean(STATE_USES_ROAD_SIGNS, true);
      usesIntersections = savedInstanceState.getBoolean(STATE_USES_INTERSECTIONS, true);
      points = savedInstanceState.getInt(STATE_POINTS, 0);
      maxPoints = savedInstanceState.getInt(STATE_MAX_POINTS, 0);
      elapsedTime = savedInstanceState.getLong(STATE_ELAPSED_TIME, 0);
      chosenAnswersList = savedInstanceState.getIntegerArrayList(STATE_CHOSEN_ANSWERS);
      amountCorrect = savedInstanceState.getInt(STATE_AMOUNT_CORRECT, 0);
      amountIncorrect = savedInstanceState.getInt(STATE_AMOUNT_INCORRECT, 0);
      amountAnswered = savedInstanceState.getInt(STATE_AMOUNT_ANSWERED, 0);
      amountUnanswered = savedInstanceState.getInt(STATE_AMOUNT_UNANSWERED, 0);
      dateStarted = savedInstanceState.getLong(STATE_DATE_STARTED, 0);
    }

    Resources res = getResources();

    setUpToolbar((Toolbar) findViewById(R.id.toolbar));

    // Did the user pass the test?
    boolean wasSuccessful = Utils.getTestSuccessful(points, elapsedTime);

    String pointsSuffix;
    if (points == 1) {
      pointsSuffix = res.getString(R.string.point);
    } else if (points > 1 && points < 5) {
      pointsSuffix = res.getString(R.string.points_2to4);
    } else {
      pointsSuffix = res.getString(R.string.points);
    }

    String titleText;
    String summaryText;
    boolean isPartial = !usesQuestions || !usesRoadSigns || !usesIntersections;

    if (isPartial) {
      String questions = usesQuestions ? res.getString(R.string.text__questions) : "";
      String roadSigns = usesRoadSigns ? res.getString(R.string.text__road_signs) : "";
      String intersections = usesIntersections ? res.getString(R.string.text__intersections) : "";

      String titleString = questions;
      if (!roadSigns.isEmpty()) {
        if (!questions.isEmpty()) {
          titleString += " " + res.getString(R.string.conjunction__and) + " ";
        }
        titleString += roadSigns;
      }
      if (!intersections.isEmpty()) {
        if (!questions.isEmpty() || !roadSigns.isEmpty()) {
          titleString += " " + res.getString(R.string.conjunction__and) + " ";
        }
        titleString += intersections;
      }

      titleText = titleString;
      summaryText = res.getString(R.string.result__text__summary_partial);
    } else {
      if (wasSuccessful) {
        titleText = res.getString(R.string.result__text__successful);
        summaryText = res.getString(R.string.result__text__summary_successful);
      } else {
        titleText = res.getString(R.string.result__text__unsuccessful);
        summaryText = res.getString(R.string.result__text__summary_unsuccessful);
      }
    }

    results_title.setText(titleText);
    results_summary.setText(summaryText);

    results_points.setText(
        String.format(Locale.ENGLISH, "%s: %d/%d", res.getString(R.string.result__text__points),
            points, maxPoints));
    results_correct.setText(
        String.format(Locale.ENGLISH, "%s: %d", res.getString(R.string.result__text__correct),
            amountCorrect));
    results_incorrect.setText(
        String.format(Locale.ENGLISH, "%s: %d", res.getString(R.string.result__text__incorrect),
            amountIncorrect - amountUnanswered));
    results_time.setText(
        String.format(Locale.ENGLISH, "%s: %s", res.getString(R.string.result__text__time),
            DateUtils.formatElapsedTime(elapsedTime / 1000)));
    if (amountUnanswered > 0) {
      results_unanswered.setText(
          String.format(Locale.ENGLISH, "%s: %d", res.getString(R.string.result__text__unanswered),
              amountUnanswered));
    } else {
      results_unanswered.setVisibility(View.GONE);
      results_unanswered_container.setVisibility(View.GONE);
    }

    // Code to run only once.
    if (savedInstanceState == null && !alreadyOpenedResults) {
      saveToDatabase();

      Answers.getInstance()
          .logCustom(
              new CustomEvent("Test End").putCustomAttribute("Success", wasSuccessful ? 1 : 0)
                  .putCustomAttribute("Points", points)
                  .putCustomAttribute("Time", DateUtils.formatElapsedTime(elapsedTime / 1000)));
    }
  }

  @Override protected void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);

    outState.putBoolean(STATE_ALREADY_OPENED_RESULTS, alreadyOpenedResults);
    outState.putInt(STATE_TEST_ID, testId);
    outState.putInt(STATE_TEST_VERSION, testVersion);
    outState.putBoolean(STATE_USES_QUESTIONS, usesQuestions);
    outState.putBoolean(STATE_USES_ROAD_SIGNS, usesRoadSigns);
    outState.putBoolean(STATE_USES_INTERSECTIONS, usesIntersections);
    outState.putInt(STATE_POINTS, points);
    outState.putInt(STATE_MAX_POINTS, maxPoints);
    outState.putLong(STATE_ELAPSED_TIME, elapsedTime);
    outState.putIntegerArrayList(STATE_CHOSEN_ANSWERS, (ArrayList<Integer>) chosenAnswersList);
    outState.putInt(STATE_AMOUNT_CORRECT, amountCorrect);
    outState.putInt(STATE_AMOUNT_INCORRECT, amountIncorrect);
    outState.putInt(STATE_AMOUNT_ANSWERED, amountAnswered);
    outState.putInt(STATE_AMOUNT_UNANSWERED, amountUnanswered);
    outState.putLong(STATE_DATE_STARTED, dateStarted);
  }

  private void setUpToolbar(Toolbar toolbar) {
    setSupportActionBar(toolbar);
    ActionBar actionBar = getSupportActionBar();

    if (actionBar != null) {
      // Returns "Skupina A,B" or "Skupina C,D,T"
      String groupString =
          (Utils.getGroupFromTestIndex(testId) == Groups.AB) ? getString(R.string.text__group_ab)
              : getString(R.string.text__group_cdt);

      actionBar.setTitle(getString(R.string.screen_title__results, testId));
      actionBar.setSubtitle(groupString);
      actionBar.setDisplayHomeAsUpEnabled(true);
      actionBar.setHomeAsUpIndicator(R.drawable.ic_close);
    }
  }

  @OnClick(R.id.rate_app) public void rate_app_onClick() {
    if (!ConnectionBuddy.getInstance().hasNetworkConnection()) {
      Toast.makeText(this, R.string.result__toast__no_internet_connection, Toast.LENGTH_SHORT)
          .show();
      return;
    }

    SharedPreferences prefs = getSharedPreferences(G.PREFS_GENERIC, MODE_PRIVATE);
    SharedPreferences.Editor prefsEdit = prefs.edit();
    prefsEdit.putBoolean("has_rated_app", true);
    prefsEdit.apply();

    rate_app.setText(R.string.result__text__thanks_for_rating_the_app);

    try {
      startActivity(new Intent(Intent.ACTION_VIEW,
          Uri.parse(getString(R.string.link__app__google_play__launch_store))));
    } catch (android.content.ActivityNotFoundException e) {
      startActivity(
          new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.link__app__google_play))));
    }
  }

  /**
   * Store result in database
   */
  private void saveToDatabase() {
    Timber.d("saveToDatabase() called");

    DbHelper dbHelper = new DbHelper(this);
    SQLiteDatabase db = dbHelper.getWritableDatabase();

    ContentValues values = new ContentValues();
    values.put(DbContract.History.COLUMN_TEST_ID, testId);
    values.put(DbContract.History.COLUMN_TEST_VERSION, testVersion);
    values.put(DbContract.History.COLUMN_USES_QUESTIONS, usesQuestions);
    values.put(DbContract.History.COLUMN_USES_ROAD_SIGNS, usesRoadSigns);
    values.put(DbContract.History.COLUMN_USES_INTERSECTIONS, usesIntersections);
    values.put(DbContract.History.COLUMN_POINTS, points);
    values.put(DbContract.History.COLUMN_MAX_POINTS, maxPoints);
    values.put(DbContract.History.COLUMN_ELAPSED_TIME, elapsedTime);
    values.put(DbContract.History.COLUMN_ANSWERS,
        chosenAnswersList.toString().replace("[", "").replace("]", "").replace(" ", ""));
    values.put(DbContract.History.COLUMN_DATE_TIME, dateStarted);

    db.insert(DbContract.History.TABLE_NAME, null, values);

    dbHelper.close();
    db.close();
  }

  @Override public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.activity__results, menu);
    return true;
  }

  @Override public boolean onOptionsItemSelected(MenuItem item) {
    int id = item.getItemId();

    switch (id) {
      case android.R.id.home:
        onBackPressed();
        return true;
      case R.id.action__share:
        new Sharing(this).shareTestResult(
            new TestResult(testId, points, maxPoints, amountCorrect, amountIncorrect,
                amountUnanswered, elapsedTime));
        return true;
    }

    return super.onOptionsItemSelected(item);
  }
}
