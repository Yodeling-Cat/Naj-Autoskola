/*
 * Copyright (c) 2015-2016. Spiracle Studios. All Rights Reserved.
 */

package com.spiraclestudios.autoskola.activities;

import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.ShareActionProvider;
import android.support.v7.widget.Toolbar;
import android.text.format.DateUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

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
import butterknife.BindString;
import butterknife.ButterKnife;

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

    public String activityName = "ResultsActivity";

    private boolean alreadyOpenedResults;
    private int testId;
    private int points;
    private int maxPoints;
    private long elapsedTime;
    private int amountCorrect;
    private int amountIncorrect;
    private int amountAnswered;
    private int amountUnanswered;
    private List<Integer> chosenAnswersList;

    @BindString(R.string.results_points)
    String str_results_points;
    @BindString(R.string.results_correct)
    String str_results_correct;
    @BindString(R.string.results_incorrect)
    String str_results_incorrect;
    @BindString(R.string.results_unanswered)
    String str_results_unanswered;
    @BindString(R.string.results_time)
    String str_results_time;

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
    @Bind(R.id.results_time)
    TextView results_time;

    public String getActivityName() {
        return activityName;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Helper.setTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);
        ButterKnife.bind(this);

        Intent intent = getIntent();
        alreadyOpenedResults = intent.getBooleanExtra(EXTRA_ALREADY_OPENED_RESULTS, false);
        testId = intent.getIntExtra(EXTRA_TEST_ID, 1);
        int testVersion = intent.getIntExtra(EXTRA_TEST_VERSION, 1);
        boolean usesQuestions = intent.getBooleanExtra(EXTRA_USES_QUESTIONS, true);
        boolean usesRoadSigns = intent.getBooleanExtra(EXTRA_USES_ROAD_SIGNS, true);
        boolean usesIntersections = intent.getBooleanExtra(EXTRA_USES_INTERSECTIONS, true);
        points = intent.getIntExtra(EXTRA_POINTS, 0);
        maxPoints = intent.getIntExtra(EXTRA_MAX_POINTS, 0);
        elapsedTime = intent.getLongExtra(EXTRA_ELAPSED_TIME, 0);
        chosenAnswersList = intent.getIntegerArrayListExtra(EXTRA_ANSWERS);
        amountCorrect = intent.getIntExtra(EXTRA_CORRECT, 0);
        amountIncorrect = intent.getIntExtra(EXTRA_INCORRECT, 0);
        amountAnswered = intent.getIntExtra(EXTRA_ANSWERED, 0);

        amountUnanswered = chosenAnswersList.size() - amountAnswered;

        // Store the result in database.
        if (!alreadyOpenedResults) {
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

            db.insert(DbContract.History.TABLE_NAME, null, values);

            // Add the scored points to the user's rewards.
            SharedPreferences prefs = getSharedPreferences(G.PREFS_GENERIC, MODE_PRIVATE);
            SharedPreferences.Editor prefsEdit = prefs.edit();
            prefsEdit.putInt("rewards_stars", prefs.getInt("rewards_stars", 0) + points).apply();

            dbHelper.close();
            db.close();
        }

        Resources res = getResources();

        // Set up Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();

        if (actionBar != null) {
            // Returns "Skupina A,B" or "Skupina C,D,T"
            String groupString = (Helper.getGroupFromTestIndex(testId) == Helper.Groups.AB)
                    ? res.getString(R.string.group_ab_long) : res.getString(R.string.group_cdt_long);

            actionBar.setTitle("Test " + testId);
            actionBar.setSubtitle(groupString);
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_close_white_24dp);
        }

        // Did the user pass the test?
        boolean wasSuccessful = points >= 50 && (elapsedTime / 1000) / 60 <= 20;

        String pointsSuffix;
        if (points == 1) {
            pointsSuffix = res.getString(R.string.point);
        } else if (points > 1 && points < 5) {
            pointsSuffix = res.getString(R.string.points_2to4);
        } else {
            pointsSuffix = res.getString(R.string.points);
        }

        String titleText;
        String summaryText = "";

        if (!usesQuestions || !usesRoadSigns || !usesIntersections) {
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
            results_summary.setVisibility(View.GONE);
        } else {
            if (wasSuccessful) {
                titleText = res.getString(R.string.results_successful);
                summaryText = String.format(res.getString(R.string.results_summary),
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

        results_points.setText(String.format(Locale.ENGLISH, "%s: %d/%d", str_results_points, points, maxPoints));
        results_correct.setText(String.format(Locale.ENGLISH, "%s: %d", str_results_correct, amountCorrect));
        results_incorrect.setText(String.format(Locale.ENGLISH, "%s: %d", str_results_incorrect, amountIncorrect - amountUnanswered));
        results_time.setText(String.format(Locale.ENGLISH, "%s: %s", str_results_time, DateUtils.formatElapsedTime(elapsedTime / 1000)));
        if (amountUnanswered > 0) {
            results_unanswered.setText(String.format(Locale.ENGLISH, "%s: %d", str_results_unanswered, amountUnanswered));
        } else {
            results_unanswered.setVisibility(View.GONE);
        }

        Helper.initializeDebugDrawer(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_results, menu);
        Resources res = getResources();

        // Set up Share action
        // TODO: Use string resource placeholders.
        String shareText = "Môj výsledok v teste č. " + testId + ":\n\n" +
                String.format(Locale.ENGLISH, "%s: %d/%d", str_results_points, points, maxPoints) + "\n" +
                String.format(Locale.ENGLISH, "%s: %d", str_results_correct, amountCorrect) + "\n" +
                String.format(Locale.ENGLISH, "%s: %d", str_results_incorrect, amountIncorrect - amountUnanswered) + "\n";

        if (amountUnanswered > 0) {
            shareText += String.format(Locale.ENGLISH, "%s: %d", str_results_unanswered, amountUnanswered) + "\n";
        }
        shareText += String.format(Locale.ENGLISH, "%s: %s", str_results_time, DateUtils.formatElapsedTime(elapsedTime / 1000));

        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.results_share_action_subject));
        shareIntent.putExtra(Intent.EXTRA_TEXT, shareText);
        shareIntent.setType("text/plain");
        ((ShareActionProvider) MenuItemCompat.getActionProvider(menu.findItem(R.id.action_share))).setShareIntent(shareIntent);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
