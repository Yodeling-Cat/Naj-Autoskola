package com.spiraclestudios.autoskola;

import android.os.Bundle;
import android.app.Activity;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

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


        // [SetUp Activity]
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);


        // [Obtain the shared Tracker instance]
        AnalyticsApplication application = (AnalyticsApplication) getApplication();
        mTracker = application.getDefaultTracker();


        // [SetUp Toolbar]
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Test #3");
            getSupportActionBar().setSubtitle("Kategória A,B");
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
