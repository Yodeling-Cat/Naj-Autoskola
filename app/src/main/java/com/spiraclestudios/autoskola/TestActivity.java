package com.spiraclestudios.autoskola;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
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
        long selectedGroupId = intent.getLongExtra(MoznostiTestuFragment.EXTRA_SKUPINA, 0);
        long selectedIndexId = intent.getLongExtra(MoznostiTestuFragment.EXTRA_INDEX, 0);

        // [Decide which test to open]
        // If a specific test wasn't selected then pick a random one
        String testGroupToUse;
        int testIndexToUse;
        Resources resources = getResources();

        // TODO: Test #1/2 based on category chosen
        // TODO: Rename category to skupina/group everywhere


        // [Category]
        // If random was chosen
        if (selectedGroupId == 0) {
            // Random number in range of 1-2
            selectedGroupId = new Random().nextInt(resources.getStringArray(R.array.test_groups).length -1) + 1;
        }

        // Returns Skupina A,B or Skupina C,D,T
        testGroupToUse = (selectedGroupId == 1) ? resources.getString(R.string.skupina_ab) : resources.getString(R.string.skupina_cdt);


        // [Index]
        // TODO: add support for C,D,T index numbers and so you also need to branch based on which category was chosen
        // If random was chosen
        if (selectedIndexId == 0) {
            // Random number in range of 1-35
            selectedIndexId = new Random().nextInt(36 - 1) + 1;
        }

        // Returns 1-35
        testIndexToUse = (int)selectedIndexId;


        // [SetUp Toolbar]
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Test #" + testIndexToUse);
            getSupportActionBar().setSubtitle(testGroupToUse);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Load an ad
        Helper.loadAd((AdView) findViewById(R.id.adView));
    }

    @Override
    public void onResume() {
        super.onResume();

        Log.i(TAG, "Setting analytics tracker screen name: " + mActivityName);
        mTracker.setScreenName(mActivityName);
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }
}
