/*
 * Copyright (c) 2015-2016. Spiracle Studios. All Rights Reserved.
 */

package com.spiraclestudios.autoskola.activities;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.format.DateUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.CustomEvent;
import com.romainpiel.shimmer.Shimmer;
import com.romainpiel.shimmer.ShimmerTextView;
import com.spiraclestudios.autoskola.DbContract;
import com.spiraclestudios.autoskola.DbHelper;
import com.spiraclestudios.autoskola.G;
import com.spiraclestudios.autoskola.Helper;
import com.spiraclestudios.autoskola.R;
import com.spiraclestudios.autoskola.interfaces.IBaseActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import timber.log.Timber;

public class ResultsActivity extends BaseActivity
        implements IBaseActivity {

    public final static String EXTRA_ALREADY_OPENED_RESULTS = "com.spiraclestudios.autoskola.ALREADY_CHECKED_RESULTS";
    public final static String EXTRA_TEST_ID = "com.spiraclestudios.autoskola.TEST_ID";
    public final static String EXTRA_TEST_VERSION = "com.spiraclestudios.autoskola.TEST_VERSION";
    public final static String EXTRA_USES_QUESTIONS = "com.spiraclestudios.autoskola.USES_QUESTIONS";
    public final static String EXTRA_USES_ROAD_SIGNS = "com.spiraclestudios.autoskola.USES_ROAD_SIGNS";
    public final static String EXTRA_USES_INTERSECTIONS = "com.spiraclestudios.autoskola.USES_INTERSECTIONS";
    public final static String EXTRA_POINTS = "com.spiraclestudios.autoskola.POINTS";
    public final static String EXTRA_MAX_POINTS = "com.spiraclestudios.autoskola.MAX_POINTS";
    public final static String EXTRA_ELAPSED_TIME = "com.spiraclestudios.autoskola.ELAPSED_TIME";
    public final static String EXTRA_ANSWERS = "com.spiraclestudios.autoskola.ANSWERS";
    public final static String EXTRA_CORRECT = "com.spiraclestudios.autoskola.CORRECT";
    public final static String EXTRA_INCORRECT = "com.spiraclestudios.autoskola.INCORRECT";
    public final static String EXTRA_ANSWERED = "com.spiraclestudios.autoskola.ANSWERED";
    public final static String EXTRA_DATE_TIME = "com.spiraclestudios.autoskola.DATE_TIME";

    private final static String STATE_ALREADY_OPENED_RESULTS = "alreadyOpenedResults";
    private final static String STATE_TEST_ID = "testId";
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
    private boolean askWantsToSave;
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

    @Bind(R.id.results_title)
    ShimmerTextView results_title;
    @Bind(R.id.results_summary)
    TextView results_summary;
    @Bind(R.id.results_points)
    TextView results_points;
    @Bind(R.id.results_correct)
    TextView results_correct;
    @Bind(R.id.results_incorrect)
    TextView results_incorrect;
    @Bind(R.id.results_unanswered)
    TextView results_unanswered;
    @Bind(R.id.results_elapsed_time)
    TextView results_time;
    @Bind(R.id.rate_app)
    Button rate_app;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Helper.setTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);
        ButterKnife.bind(this);

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
        boolean wasSuccessful = Helper.getTestSuccessful(points, elapsedTime);

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
            String questions = usesQuestions ? res.getString(R.string.questions) : "";
            String roadSigns = usesRoadSigns ? res.getString(R.string.road_signs) : "";
            String intersections = usesIntersections ? res.getString(R.string.intersections) : "";

            String titleString = questions;
            if (!roadSigns.isEmpty()) {
                if (!questions.isEmpty()) {
                    titleString += " " + res.getString(R.string.and) + " ";
                }
                titleString += roadSigns;
            }
            if (!intersections.isEmpty()) {
                if (!questions.isEmpty() || !roadSigns.isEmpty()) {
                    titleString += " " + res.getString(R.string.and) + " ";
                }
                titleString += intersections;
            }

            titleText = titleString;
            summaryText = res.getString(R.string.results_summary_partial);
        } else {
            if (wasSuccessful) {
                titleText = res.getString(R.string.results_successful);
                summaryText = String.format(res.getString(R.string.results_summary_successful),
                        points,
                        pointsSuffix,
                        DateUtils.formatElapsedTime(elapsedTime / 1000));
            } else {
                titleText = res.getString(R.string.results_unsuccessful);
                summaryText = res.getString(R.string.results_summary_unsuccessful);
            }
        }

        // Shimmer effect on the summary text if the test was successful.
        if (wasSuccessful) {
            Shimmer shimmer = new Shimmer();
            shimmer.start(results_title);
            shimmer.setRepeatCount(0)
                    .setDuration(500)
                    .setStartDelay(500);
        }

        results_title.setText(titleText);
        results_summary.setText(summaryText);

        results_points.setText(String.format(Locale.ENGLISH, "%s: %d/%d", res.getString(R.string.results_points), points, maxPoints));
        results_correct.setText(String.format(Locale.ENGLISH, "%s: %d", res.getString(R.string.results_correct), amountCorrect));
        results_incorrect.setText(String.format(Locale.ENGLISH, "%s: %d", res.getString(R.string.results_incorrect), amountIncorrect - amountUnanswered));
        results_time.setText(String.format(Locale.ENGLISH, "%s: %s", res.getString(R.string.results_time), DateUtils.formatElapsedTime(elapsedTime / 1000)));
        if (amountUnanswered > 0) {
            results_unanswered.setText(String.format(Locale.ENGLISH, "%s: %d", res.getString(R.string.results_unanswered), amountUnanswered));
        } else {
            results_unanswered.setVisibility(View.GONE);
        }

        // Code to run only once.
        if (!alreadyOpenedResults) {
            // If the test was done too quickly or only a few answers were chosen, ask if the user wants to save the result.
            askWantsToSave = !isPartial && points < maxPoints / 2 && (elapsedTime / 1000) / 60 <= 3;
            if (!askWantsToSave) {
                saveToDatabase();
            }

            Answers.getInstance().logCustom(new CustomEvent("Test End")
                    .putCustomAttribute("Success", wasSuccessful ? 1 : 0)
                    .putCustomAttribute("Points", points)
                    .putCustomAttribute("Time", DateUtils.formatElapsedTime(elapsedTime / 1000)));

            alreadyOpenedResults = true;
        }

        Helper.initializeDebugDrawer(this);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
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
            String groupString = (Helper.getGroupFromTestIndex(testId) == Helper.Groups.AB)
                    ? getString(R.string.group_ab_long) : getString(R.string.group_cdt_long);

            actionBar.setTitle("Test " + testId);
            actionBar.setSubtitle(groupString);
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_close_white_24dp);
        }
    }

    @OnClick(R.id.rate_app)
    public void rate_app_onClick() {
        if (!Helper.isOnline()) {
            Toast.makeText(this, R.string.toast_connect_to_the_internet, Toast.LENGTH_SHORT).show();
            return;
        }

        SharedPreferences prefs = getSharedPreferences(G.PREFS_GENERIC, MODE_PRIVATE);
        SharedPreferences.Editor prefsEdit = prefs.edit();
        prefsEdit.putBoolean("has_rated_app", true);
        prefsEdit.apply();

        rate_app.setText(R.string.thanks_for_rating_the_app);

        try {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(Helper.googlePlayMarketURL)));
        } catch (android.content.ActivityNotFoundException e) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(Helper.googlePlayURL)));
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
        values.put(DbContract.History.COLUMN_ANSWERS, chosenAnswersList.toString()
                .replace("[", "").replace("]", "").replace(" ", ""));
        values.put(DbContract.History.COLUMN_DATE_TIME, dateStarted);


        db.insert(DbContract.History.TABLE_NAME, null, values);

        // Add the scored points to the user's rewards.
        SharedPreferences prefs = getSharedPreferences(G.PREFS_GENERIC, MODE_PRIVATE);
        SharedPreferences.Editor prefsEdit = prefs.edit();
        prefsEdit.putInt("rewards_stars", prefs.getInt("rewards_stars", 0) + points);
        prefsEdit.apply();

        dbHelper.close();
        db.close();
    }

    private void showSaveDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setMessage(R.string.dialog_save_result_message)
                .setPositiveButton(R.string.dialog_save_result_positive, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        askWantsToSave = false;
                        saveToDatabase();
                        onBackPressed();
                    }
                })
                .setNegativeButton(R.string.dialog_save_result_negative, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        askWantsToSave = false;
                        onBackPressed();
                    }
                });

        builder.create().show();
    }

    @Override
    public void onBackPressed() {
        if (askWantsToSave) {
            showSaveDialog();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_results, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.action_share:
                Helper.ShareTest(this, testId, points, maxPoints, amountCorrect, amountIncorrect, amountUnanswered, elapsedTime);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
