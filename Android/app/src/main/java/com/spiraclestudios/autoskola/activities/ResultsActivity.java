/*
 * Copyright 2015-2016 Spiracle Studios. All Rights Reserved.
 */

package com.spiraclestudios.autoskola.activities;

import android.content.ContentValues;
import android.content.Intent;
import android.content.res.Resources;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.romainpiel.shimmer.Shimmer;
import com.romainpiel.shimmer.ShimmerTextView;
import com.spiraclestudios.autoskola.DbContract;
import com.spiraclestudios.autoskola.DbHelper;
import com.spiraclestudios.autoskola.Helper;
import com.spiraclestudios.autoskola.R;
import com.spiraclestudios.autoskola.interfaces.IBaseActivity;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import timber.log.Timber;

@EActivity(R.layout.activity_results)
public class ResultsActivity extends BaseActivity
        implements IBaseActivity {

    public String mActivityName = "ResultsActivity";

    // TODO: Use Android Annotations
    public final static String EXTRA_TEST_ID =
            "com.spiraclestudios.autoskola.INDEX";
    public final static String EXTRA_TEST_VERSION =
            "com.spiraclestudios.autoskola.VERSION";
    public final static String EXTRA_USES_QUESTIONS =
            "com.spiraclestudios.autoskola.USES_QUESTIONS";
    public final static String EXTRA_USES_ROAD_SIGNS =
            "com.spiraclestudios.autoskola.USES_ROAD_SIGNS";
    public final static String EXTRA_USES_INTERSECTIONS =
            "com.spiraclestudios.autoskola.USES_INTERSECTIONS";
    public final static String EXTRA_POINTS =
            "com.spiraclestudios.autoskola.POINTS";
    public final static String EXTRA_MAX_POINTS =
            "com.spiraclestudios.autoskola.MAX_POINTS";
    public final static String EXTRA_ELAPSED_TIME =
            "com.spiraclestudios.autoskola.TIME";
    public final static String EXTRA_ELAPSED_TIME_TEXT =
            "com.spiraclestudios.autoskola.TIME_TEXT";
    public final static String EXTRA_ANSWERS =
            "com.spiraclestudios.autoskola.ANSWERS";
    public final static String EXTRA_CORRECT =
            "com.spiraclestudios.autoskola.CORRECT";
    public final static String EXTRA_INCORRECT =
            "com.spiraclestudios.autoskola.INCORRECT";
    public final static String EXTRA_ANSWERED =
            "com.spiraclestudios.autoskola.ANSWERED";

    int testId;
    int testVersion;
    boolean usesQuestions;
    boolean usesRoadSigns;
    boolean usesIntersections;
    int points;
    int maxPoints;
    long elapsedTime;
    String elapsedTimeText;
    List<Integer> chosenAnswersList = new ArrayList<>();
    int amountCorrect;
    int amountIncorrect;
    int amountAnswered;

    @ViewById
    ShimmerTextView results_title;
    @ViewById
    TextView results_summary;
    @ViewById
    TextView results_points;
    @ViewById
    TextView results_correct;
    @ViewById
    TextView results_incorrect;
    @ViewById
    TextView results_unanswered;
    @ViewById
    TextView results_time;

    public String getActivityName() {
        return mActivityName;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Helper.setTheme(this);
        super.onCreate(savedInstanceState);

        // Read extras from the intent.
        Intent intent = getIntent();
        testId = intent.getIntExtra(EXTRA_TEST_ID, 1);
        testVersion = intent.getIntExtra(EXTRA_TEST_VERSION, 1);
        usesQuestions = intent.getBooleanExtra(EXTRA_USES_QUESTIONS, true);
        usesRoadSigns = intent.getBooleanExtra(EXTRA_USES_ROAD_SIGNS, true);
        usesIntersections = intent.getBooleanExtra(EXTRA_USES_INTERSECTIONS, true);
        points = intent.getIntExtra(EXTRA_POINTS, 0);
        maxPoints = intent.getIntExtra(EXTRA_MAX_POINTS, 0);
        elapsedTime = intent.getLongExtra(EXTRA_ELAPSED_TIME, 0);
        elapsedTimeText = intent.getStringExtra(EXTRA_ELAPSED_TIME_TEXT);
        chosenAnswersList = intent.getIntegerArrayListExtra(EXTRA_ANSWERS);
        amountCorrect = intent.getIntExtra(EXTRA_CORRECT, 0);
        amountIncorrect = intent.getIntExtra(EXTRA_INCORRECT, 0);
        amountAnswered = intent.getIntExtra(EXTRA_ANSWERED, 0);

        // TODO: Re-enable saving results to history
        // Store the result to history
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
        values.put(DbContract.History.COLUMN_ELAPSED_TIME_TEXT, elapsedTimeText);
        values.put(DbContract.History.COLUMN_ANSWERS, chosenAnswersList.toString()
                .replace("[", "").replace("]", ""));

        db.insert(DbContract.History.TABLE_NAME, null, values);

        // Add the scored points to the user's rewards.
        /*ContentValues values = new ContentValues();
        values.put(DbContract.Rewards.COLUMN_TEST_ID, testId);
        values.put(DbContract.Rewards.COLUMN_TEST_VERSION, testVersion);

        db.insert(DbContract.Rewards.TABLE_NAME, null, values);*/

        dbHelper.close();
        db.close();
    }

    @AfterViews
    void afterViews() {
        Resources res = getResources();

        // Setup Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();

        if (actionBar != null) {
            // Returns "Skupina A,B" or "Skupina C,D,T"
            String groupString = (Helper.getGroupFromTestIndex(testId) == Helper.Groups.AB)
                    ? res.getString(R.string.group_ab) : res.getString(R.string.group_cdt);

            actionBar.setTitle("Test #" + testId);
            actionBar.setSubtitle(groupString);
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_close_white_24dp);
        }

        // Did the user pass the test?
        boolean wasSuccessful = false;
        if (points >= 50 && (elapsedTime / 1000) / 60 <= 20) {
            wasSuccessful = true;
        }

        String pointsSuffix;
        if (points == 1) {
            pointsSuffix = res.getString(R.string.point);
        } else if (points > 1 && points < 5) {
            pointsSuffix = res.getString(R.string.points_2to4);
        } else {
            pointsSuffix = res.getString(R.string.points);
        }

        String titleText;
        String summaryText = String.format(res.getString(R.string.results_summary),
                points, pointsSuffix, elapsedTimeText);

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
            } else {
                titleText = res.getString(R.string.results_failed);
                summaryText += "\n\n" + res.getString(R.string.results_summary_failed);
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

        results_points.setText(String.format(Locale.ENGLISH, "%s: %d/%d",
                res.getString(R.string.results_points), points, maxPoints));
        results_correct.setText(String.format(Locale.ENGLISH, "%s: %d",
                res.getString(R.string.results_correct), amountCorrect));
        results_incorrect.setText(String.format(Locale.ENGLISH, "%s: %d",
                res.getString(R.string.results_incorrect), amountIncorrect));
        results_unanswered.setText(String.format(Locale.ENGLISH, "%s: %d",
                res.getString(R.string.results_unanswered), chosenAnswersList.size() - amountAnswered));
        results_time.setText(String.format(Locale.ENGLISH, "%s: %s",
                res.getString(R.string.results_time), elapsedTimeText));

        Helper.initializeDebugDrawer(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.results_activity, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        } else if (id == R.id.action_share) {
            Toast.makeText(this, R.string.toast_not_yet_implemented, Toast.LENGTH_SHORT).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
