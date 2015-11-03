package com.spiraclestudios.autoskola;

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

import java.util.Random;

public class TestActivity extends AppCompatActivity {

    private static final String TAG = "TestActivity";
    private String mActivityName = "TestActivity";
    private Tracker mTracker;

    boolean useQuestions;
    boolean useRoadSigns;
    boolean useIntersections;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // [SetUp Activity]
        super.onCreate(savedInstanceState);
        Helper.setTheme(this);
        setContentView(R.layout.activity_test);

        // Obtain the shared Tracker instance
        mTracker = ((AutoskolaApplication) getApplication()).getDefaultTracker();


        // [Keep screen on]
        if (PreferenceManager.getDefaultSharedPreferences(this)
                .getBoolean("keep_screen_on_switch", true)) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }


        // [Read extras from the intent]
        Intent intent = getIntent();

        int selectedIndexId = intent.getIntExtra(TestOptionsDialog.EXTRA_INDEX, 1);
        Helper.Groups selectedGroup = (Helper.Groups) intent.getSerializableExtra(
                TestOptionsDialog.EXTRA_GROUP);
        useQuestions = intent.getBooleanExtra(TestOptionsDialog.EXTRA_USE_QUESTIONS, true);
        useRoadSigns = intent.getBooleanExtra(TestOptionsDialog.EXTRA_USE_ROAD_SIGNS, true);
        useIntersections = intent.getBooleanExtra(TestOptionsDialog.EXTRA_USE_INTERSECTIONS, true);


        // [Decide which test to open]
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


        // [Create and add the TestActivityFragment to the layout]
        TestActivityFragment testActivityFragment = TestActivityFragment
                .newInstance(testIndexToUse, useQuestions, useRoadSigns, useIntersections);
        getSupportFragmentManager().beginTransaction().add(
                R.id.fragment_container, testActivityFragment).commit();


        // [SetUp Toolbar]
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Returns Skupina A,B or Skupina C,D,T
        groupString = (Helper.getGroupFromTestIndex(
                testIndexToUse) == Helper.Groups.AB) ? resources.getString(R.string.skupina_ab) : resources.getString(R.string.skupina_cdt);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Test #" + testIndexToUse);
            getSupportActionBar().setSubtitle(groupString);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }


        // [SetUp TabLayout]
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
    public void onResume() {
        super.onResume();

        Log.i(TAG, "Setting analytics tracker screen name: " + mActivityName);
        mTracker.setScreenName(mActivityName);
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.test_activity, menu);
        return true;
    }
}
