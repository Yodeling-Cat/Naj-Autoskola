package com.spiraclestudios.autoskola;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import com.google.android.gms.ads.AdRequest;
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
        // [Handle setting the Dark theme]
        if (PreferenceManager.getDefaultSharedPreferences(this).getBoolean("night_theme_switch", false)) {
            if (PreferenceManager.getDefaultSharedPreferences(this).getBoolean("amoled_mode_switch", false))
                mThemeId = R.style.MyTheme_Dark_AMOLED;
            else
                mThemeId = R.style.MyTheme_Dark;
        } else
            mThemeId = R.style.MyTheme_Light;

        setTheme(mThemeId);

        // [Obtain the shared Tracker instance]
        AnalyticsApplication application = (AnalyticsApplication) getApplication();
        mTracker = application.getDefaultTracker();

        // [SetUp Activity]
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        // [Handle Intents]
        Intent intent = getIntent();
        long selectedCategoryId = intent.getLongExtra(MainActivity.EXTRA_VLASTNY_TEST_CATEGORY, 0);
        long selectedIndexId = intent.getLongExtra(MainActivity.EXTRA_VLASTNY_TEST_INDEX, 0);

        // [Decide which test to open]
        // If a specific test wasn't selected then pick a random one
        String testCategoryToUse;
        int testIndexToUse;
        Resources resources = getResources();

        // TODO: Test #1/2 based on category chosen
        // TODO: Rename category to skupina/group everywhere


        // [Category]
        // If random was chosen
        if (selectedCategoryId == 0) {
            // Random number in range of 1-2
            selectedCategoryId = new Random().nextInt(resources.getStringArray(R.array.test_groups).length -1) + 1;
        }

        // Returns Skupina A,B or Skupina C,D,T
        testCategoryToUse = (selectedCategoryId == 1) ? resources.getString(R.string.skupina_ab) : resources.getString(R.string.skupina_cdt);


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
            getSupportActionBar().setSubtitle(testCategoryToUse);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }


        // [Load an ad]
        AdView adView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .addTestDevice("A053777425A9926103BE02DE879DA5A1")
                .build();
        adView.loadAd(adRequest);
    }

    @Override
    public void onResume() {
        super.onResume();

        Log.i(TAG, "Setting analytics tracker screen name: " + mActivityName);
        mTracker.setScreenName(mActivityName);
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }
}
