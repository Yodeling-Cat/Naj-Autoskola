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
        int selectedGroupId = intent.getIntExtra(MoznostiTestuFragment.EXTRA_SKUPINA, 0);
        int selectedIndexId = intent.getIntExtra(MoznostiTestuFragment.EXTRA_INDEX, -1);


        // [Decide which test to open]
        String testGroupToUse;
        int testIndexToUse;
        Resources resources = getResources();

        // Returns Skupina A,B or Skupina C,D,T
        testGroupToUse = (selectedGroupId == 0) ? resources.getString(R.string.skupina_ab) : resources.getString(R.string.skupina_cdt);


        // [Index]
        // TODO: add support for C,D,T index numbers and so you also need to branch based on which category was chosen
        // If random was chosen
        if (selectedIndexId == -1) {
            // Random number in range of 1-35
            testIndexToUse = new Random().nextInt(36 - 1) + 1;
        }
        else {
            // Int between 1-35
            testIndexToUse = selectedIndexId;
        }

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
