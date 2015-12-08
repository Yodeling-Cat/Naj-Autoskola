package com.spiraclestudios.autoskola.Activities;

import android.content.ContentValues;
import android.content.Intent;
import android.content.res.Resources;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.spiraclestudios.autoskola.DbContract;
import com.spiraclestudios.autoskola.DbHelper;
import com.spiraclestudios.autoskola.Helper;
import com.spiraclestudios.autoskola.Interfaces.IBaseActivity;
import com.spiraclestudios.autoskola.R;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ResultsActivity extends BaseActivity
        implements IBaseActivity {

    private static final String TAG = "ResultsActivity";
    public String mActivityName = "ResultsActivity";

    public final static String EXTRA_INDEX =
            "com.spiraclestudios.autoskola.INDEX";
    public final static String EXTRA_POINTS =
            "com.spiraclestudios.autoskola.POINTS";
    public final static String EXTRA_MAX_POINTS =
            "com.spiraclestudios.autoskola.MAX_POINTS";
    public final static String EXTRA_TIME =
            "com.spiraclestudios.autoskola.TIME";
    public final static String EXTRA_CORRECT =
            "com.spiraclestudios.autoskola.CORRECT";
    public final static String EXTRA_INCORRECT =
            "com.spiraclestudios.autoskola.INCORRECT";

    int testIndex;
    Helper.Groups testGroup;
    int points;
    int maxPoints;
    String elapsedTime;
    int amountCorrect;
    int amountIncorrect;

    @Bind(R.id.result_summary)
    TextView result_summary;
    @Bind(R.id.result_points)
    TextView result_points;
    @Bind(R.id.result_correct)
    TextView result_correct;
    @Bind(R.id.result_incorrect)
    TextView result_incorrect;
    @Bind(R.id.result_time)
    TextView result_time;

    public String getActivityName() {
        return mActivityName;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Helper.setTheme(this);
        setContentView(R.layout.activity_results);
        ButterKnife.bind(this);

        // Read extras from the intent
        Intent intent = getIntent();

        testIndex = intent.getIntExtra(EXTRA_INDEX, 1);
        points = intent.getIntExtra(EXTRA_POINTS, 0);
        maxPoints = intent.getIntExtra(EXTRA_MAX_POINTS, 0);
        elapsedTime = intent.getStringExtra(EXTRA_TIME);
        amountCorrect = intent.getIntExtra(EXTRA_CORRECT, 0);
        amountIncorrect = intent.getIntExtra(EXTRA_INCORRECT, 0);

        boolean wasSuccesful = false;

        if (points >= 50) {
            wasSuccesful = true;
        }

        Resources res = getResources();

        result_summary.setText((wasSuccesful) ? R.string.result_succesful : R.string.result_failed);
        result_points.setText(res.getString(R.string.result_points) + ": " + points + "/" + maxPoints);
        result_correct.setText(res.getString(R.string.result_correct) + ": " + amountCorrect);
        result_incorrect.setText(res.getString(R.string.result_incorrect) + ": " + amountIncorrect);
        result_time.setText(res.getString(R.string.result_time) + ": " + elapsedTime);

        // SetUp Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();

        if (actionBar != null) {
            // Returns "Skupina A,B" or "Skupina C,D,T"
            String groupString = (Helper.getGroupFromTestIndex(testIndex) == Helper.Groups.AB)
                            ? res.getString(R.string.group_ab) : res.getString(R.string.group_cdt);
            actionBar.setTitle("Test #" + testIndex);
            actionBar.setSubtitle(groupString);
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_close_white_24dp);
        }

        // Save the result to history if the test was valid
        DbHelper dbHelper = new DbHelper(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        /*ContentValues values = new ContentValues();
        values.put(DbContract.History.COLUMN_TEST_ID, testIndex);
        values.put(DbContract.History.COLUMN_TEST_VERSION, version);
        values.put(DbContract.History.COLUMN_USES_QUESTIONS, usesQuestions);
        values.put(DbContract.History.COLUMN_USES_ROAD_SIGNS, usesRoadSigns);
        values.put(DbContract.History.COLUMN_USES_INTERSECTIONS, usesIntersections);
        values.put(DbContract.History.COLUMN_POINTS, points);
        values.put(DbContract.History.COLUMN_MAX_POINTS, maxPoints);
        values.put(DbContract.History.COLUMN_ELAPSED_TIME, elapsedTime);
        values.put(DbContract.History.COLUMN_ANSWERS, answers);

        db.insert(DbContract.History.TABLE_NAME, null, values);*/
        db.close();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.results_activity, menu);
        return true;
    }
}
