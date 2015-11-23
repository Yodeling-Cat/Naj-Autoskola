package com.spiraclestudios.autoskola.Activities;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.WindowManager;

import com.spiraclestudios.autoskola.Fragments.TestActivityFragment;
import com.spiraclestudios.autoskola.Helper;
import com.spiraclestudios.autoskola.Interfaces.IBaseActivity;
import com.spiraclestudios.autoskola.R;

import java.util.Random;

public class ResultsActivity extends BaseActivity
        implements IBaseActivity {

    private static final String TAG = "ResultsActivity";
    public String mActivityName = "ResultsActivity";

    public final static String EXTRA_GROUP =
            "com.spiraclestudios.autoskola.GROUP";
    public final static String EXTRA_INDEX =
            "com.spiraclestudios.autoskola.INDEX";

    int scoredPoints;
    int maxPoints;
    long elapsedTime;

    public String getActivityName() {
        return mActivityName;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Helper.setTheme(this);
        setContentView(R.layout.activity_results);

        // Read extras from the intent
        Intent intent = getIntent();

        /*int selectedIndexId = intent.getIntExtra(EXTRA_INDEX, 1);
        Helper.Groups selectedGroup = (Helper.Groups) intent.getSerializableExtra(EXTRA_GROUP);
        useQuestions = intent.getBooleanExtra(EXTRA_USE_QUESTIONS, true);
        useRoadSigns = intent.getBooleanExtra(EXTRA_USE_ROAD_SIGNS, true);
        useIntersections = intent.getBooleanExtra(EXTRA_USE_INTERSECTIONS, true);
        markCorrectAnswers = intent.getBooleanExtra(EXTRA_MARK_CORRECT_ANSWERS, false);*/

        // SetUp Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            //getSupportActionBar().setTitle("Test #" + testIndexToUse);
            //getSupportActionBar().setSubtitle(groupString);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    /*@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.results_activity, menu);
        return true;
    }*/
}
