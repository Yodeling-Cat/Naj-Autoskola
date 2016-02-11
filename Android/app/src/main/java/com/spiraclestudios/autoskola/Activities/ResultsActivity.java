/*
 * Copyright 2015-2016 Spiracle Studios. All Rights Reserved.
 */

package com.spiraclestudios.autoskola.activities;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.romainpiel.shimmer.Shimmer;
import com.romainpiel.shimmer.ShimmerTextView;
import com.spiraclestudios.autoskola.Helper;
import com.spiraclestudios.autoskola.R;
import com.spiraclestudios.autoskola.interfaces.IBaseActivity;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.List;

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

    @ViewById
    ShimmerTextView result_title;
    @ViewById
    TextView result_summary;
    @ViewById
    TextView result_points;
    @ViewById
    TextView result_correct;
    @ViewById
    TextView result_incorrect;
    @ViewById
    TextView result_time;

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

        // TODO: Re-enable saving results to history
        // Store the result to history if the test was valid
        /*DbHelper dbHelper = new DbHelper(this);
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

        db.insert(DbContract.History.TABLE_NAME, null, values);*/

        // Award the scored points to the user's reward points
        /*ContentValues values = new ContentValues();
        values.put(DbContract.Rewards.COLUMN_TEST_ID, testId);
        values.put(DbContract.Rewards.COLUMN_TEST_VERSION, testVersion);

        db.insert(DbContract.Rewards.TABLE_NAME, null, values);

        db.close();*/
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

        String pointsSufix;
        if (points == 1) {
            pointsSufix = res.getString(R.string.point);
        } else if (points > 1 && points < 5) {
            pointsSufix = res.getString(R.string.points_2to4);
        } else {
            pointsSufix = res.getString(R.string.points);
        }

        String titleText;
        // TODO: Use resource string with placeholders.
        String summaryText = "Test ste spravily na " + points + " " + pointsSufix + " a za čas " + elapsedTimeText + " minút.";

        if (!usesQuestions || !usesRoadSigns || !usesIntersections) {
            titleText = getString(R.string.result_incomplete_test);
            // TODO: Use resource string.
            summaryText += "\nNeúplný test nemožno vyhodnotiť.";
        } else {
            if (wasSuccessful) {
                titleText = res.getString(R.string.result_successful);
            } else {
                titleText = res.getString(R.string.result_failed);
                // TODO: Use resource string.
                summaryText += "\nNa úspešné ukončenie testu máte 20 minút a potrebujete získať aspoň 50 z 55 bodov.";
            }
        }

        // Shimmer effect on the summary text if the test was successful.
        if (wasSuccessful) {
            Shimmer shimmer = new Shimmer();
            shimmer.start(result_title);
            shimmer.setRepeatCount(0)
                    .setDuration(500)
                    .setStartDelay(500);
        }

        result_title.setText(titleText);
        result_summary.setText(summaryText);
        result_points.setText(String.format("%s: %d/%d", res.getString(R.string.result_points), points, maxPoints));
        result_correct.setText(String.format("%s: %d", res.getString(R.string.result_correct), amountCorrect));
        result_incorrect.setText(String.format("%s: %d", res.getString(R.string.result_incorrect), amountIncorrect));
        result_time.setText(String.format("%s: %s", res.getString(R.string.result_time), elapsedTimeText));

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
