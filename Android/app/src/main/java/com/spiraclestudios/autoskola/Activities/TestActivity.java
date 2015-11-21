package com.spiraclestudios.autoskola.Activities;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.WindowManager;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.spiraclestudios.autoskola.AnalyticsTrackers;
import com.spiraclestudios.autoskola.AutoskolaApplication;
import com.spiraclestudios.autoskola.Helper;
import com.spiraclestudios.autoskola.Interfaces.IBaseActivity;
import com.spiraclestudios.autoskola.R;
import com.spiraclestudios.autoskola.Fragments.TestActivityFragment;

import java.util.Random;

public class TestActivity extends BaseActivity
        implements IBaseActivity {

    private static final String TAG = "TestActivity";
    public String mActivityName = "TestActivity";

    public final static String EXTRA_GROUP =
            "com.spiraclestudios.autoskola.GROUP";
    public final static String EXTRA_INDEX =
            "com.spiraclestudios.autoskola.INDEX";
    public final static String EXTRA_USE_QUESTIONS =
            "com.spiraclestudios.autoskola.USE_QUESTIONS";
    public final static String EXTRA_USE_ROAD_SIGNS =
            "com.spiraclestudios.autoskola.USE_ROAD_SIGNS";
    public final static String EXTRA_USE_INTERSECTIONS =
            "com.spiraclestudios.autoskola.USE_INTERSECTIONS";
    public final static String EXTRA_MARK_CORRECT_ANSWERS =
            "com.spiraclestudios.autoskola.MARK_CORRECT_ANSWERS";

    boolean useQuestions;
    boolean useRoadSigns;
    boolean useIntersections;
    boolean markCorrectAnswers;

    public String getActivityName() {
        return mActivityName;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Helper.setTheme(this);
        setContentView(R.layout.activity_test);

        // Keep the screen on
        if (PreferenceManager.getDefaultSharedPreferences(this)
                .getBoolean("keep_screen_on_switch", true)) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }

        // Read extras from the intent
        Intent intent = getIntent();

        int selectedIndexId = intent.getIntExtra(EXTRA_INDEX, 1);
        Helper.Groups selectedGroup = (Helper.Groups) intent.getSerializableExtra(EXTRA_GROUP);
        useQuestions = intent.getBooleanExtra(EXTRA_USE_QUESTIONS, true);
        useRoadSigns = intent.getBooleanExtra(EXTRA_USE_ROAD_SIGNS, true);
        useIntersections = intent.getBooleanExtra(EXTRA_USE_INTERSECTIONS, true);
        markCorrectAnswers = intent.getBooleanExtra(EXTRA_MARK_CORRECT_ANSWERS, false);

        // Decide which test to open
        String groupString;
        int testIndexToUse;
        Resources resources = getResources();

        // [Index]
        // If random was chosen
        if (selectedGroup != null) {
            if (selectedGroup == Helper.Groups.AB) {
                // Random number in range of 1-35
                testIndexToUse = new Random().nextInt(36 - 1) + 1;
            } else {
                // Random number in range of 36-60
                testIndexToUse = new Random().nextInt(61 - 36) + 36;
            }
        } else {
            // Int between 1-60
            testIndexToUse = selectedIndexId;
        }

        // Create and add the TestActivityFragment to the layout
        TestActivityFragment testActivityFragment = TestActivityFragment
                .newInstance(testIndexToUse, useQuestions, useRoadSigns, useIntersections
                        , markCorrectAnswers);
        getSupportFragmentManager().beginTransaction().add(
                R.id.fragment_container, testActivityFragment).commit();

        // SetUp Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Returns Skupina A,B or Skupina C,D,T
        groupString = (Helper.getGroupFromTestIndex(
                testIndexToUse) == Helper.Groups.AB) ? resources.getString(R.string.group_ab) : resources.getString(R.string.group_cdt);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Test #" + testIndexToUse);
            getSupportActionBar().setSubtitle(groupString);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // SetUp TabLayout
        //TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        //tabLayout.addTab(tabLayout.newTab().setText(R.string.title_test));
        //tabLayout.addTab(tabLayout.newTab().setText(R.string.title_vyhlaska));
        //tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        /*final ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
        final TestActivityPagerAdapter adapter = new TestActivityPagerAdapter
                (getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });*/
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.test_activity, menu);
        return true;
    }
}
