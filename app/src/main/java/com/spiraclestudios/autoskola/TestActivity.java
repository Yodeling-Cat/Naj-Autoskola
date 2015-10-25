package com.spiraclestudios.autoskola;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import com.google.android.gms.ads.AdView;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import java.util.Random;

public class TestActivity extends AppCompatActivity {

    private static final String TAG = "TestActivity";
    private String mActivityName = "TestActivity";
    private Tracker mTracker;

    private int mThemeId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // [SetUp Activity]
        super.onCreate(savedInstanceState);
        Helper.setTheme(this);
        setContentView(R.layout.activity_test);

        // Obtain the shared Tracker instance
        mTracker = ((AnalyticsApplication) getApplication()).getDefaultTracker();


        // [Handle Intents]
        Intent intent = getIntent();
        int selectedGroupId = intent.getIntExtra(MoznostiTestuFragment.EXTRA_SKUPINA, 0);
        int selectedIndexId = intent.getIntExtra(MoznostiTestuFragment.EXTRA_INDEX, -1);


        // [Decide which test to open]
        String testGroupToUse;
        int testIndexToUse;
        Resources resources = getResources();

        // Returns Skupina A,B or Skupina C,D,T
        testGroupToUse = (selectedGroupId == 0) ? resources.getString(R.string.skupina_ab) : resources.getString(R.string.skupina_cdt);


        // [Index]
        // If random was chosen
        if (selectedIndexId == -1) {
            if (selectedGroupId == 0) {
                // Random number in range of 1-35
                testIndexToUse = new Random().nextInt(36 - 1) + 1;
            }
            else {
                testIndexToUse = new Random().nextInt(61 - 36) + 36;
            }
        }
        else {
            // Int between 1-35
            testIndexToUse = selectedIndexId;
        }


        // [Create and add the TestActivityFragment to the layout]
        TestActivityFragment testFragment = new TestActivityFragment().newInstance(testIndexToUse);
        getSupportFragmentManager().beginTransaction().add(R.id.content, testFragment).commit();
        //testFragment.setTest(testIndexToUse);


        // [SetUp Toolbar]
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Test #" + testIndexToUse);
            getSupportActionBar().setSubtitle(testGroupToUse);
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
}
