package com.spiraclestudios.autoskola.Activities;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.widget.TextView;

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

        Resources res = getResources();

        // Returns "Skupina A,B" or "Skupina C,D,T"
        String groupString = (Helper.getGroupFromTestIndex(
                testIndex) == Helper.Groups.AB) ? res.getString(R.string.group_ab) : res.getString(R.string.group_cdt);

        result_points.setText(res.getString(R.string.result_points) + ": " + points + "/" + maxPoints);
        result_correct.setText(res.getString(R.string.result_correct) + ": " + amountCorrect);
        result_incorrect.setText(res.getString(R.string.result_incorrect) + ": " + amountIncorrect);
        result_time.setText(res.getString(R.string.result_time) + ": " + elapsedTime);

        // SetUp Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Test #" + testIndex);
            getSupportActionBar().setSubtitle(groupString);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.results_activity, menu);
        return true;
    }
}
