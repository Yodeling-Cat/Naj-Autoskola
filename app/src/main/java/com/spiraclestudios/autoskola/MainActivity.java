package com.spiraclestudios.autoskola;

import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.support.v4.app.DialogFragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import junit.framework.Test;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private static final String TAG = "MainActivity";
    private String mActivityName = "MainActivity";
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
        setContentView(R.layout.activity_main);


        // [Obtain the shared Tracker instance]
        AnalyticsApplication application = (AnalyticsApplication) getApplication();
        mTracker = application.getDefaultTracker();


        // [SetUp Toolbar]
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(R.string.app_name);
        toolbar.setSubtitle(R.string.title_testy);


        // [SetUp Floating Action Button]
        /*FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/


        // [Load an ad]
        AdView adView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .addTestDevice("A053777425A9926103BE02DE879DA5A1")
                .build();
        adView.loadAd(adRequest);


        // [SetUp Navigation Drawer]
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.cd_navigation_drawer_open, R.string.cd_navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();

        Log.i(TAG, "Setting analytics tracker screen name: " + mActivityName);
        mTracker.setScreenName(mActivityName);
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_activity, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        /*if (id == R.id.action_night_theme) {
            if (themeId == R.style.MyTheme_Light) {
                themeId = R.style.MyTheme_Dark;
            }
            else {
                themeId = R.style.MyTheme_Light;
            }

            //recreate();
            return true;
        }*/

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_testy) {
            Intent intent = new Intent(this, TestActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_novinky) {
            return true;
        } else if (id == R.id.nav_dopravne_znacky) {
            return true;
        } else if (id == R.id.nav_vyhlaska) {
            return true;
        } else if (id == R.id.nav_najst_autoskolu) {
            return true;
        } else if (id == R.id.nav_nastavenia) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_pomoc_a_pripomienky) {
            return true;
        } else if (id == R.id.nav_o_aplikacii) {
            mTracker.send(new HitBuilders.EventBuilder()
                    .setCategory("Navigation")
                    .setAction("O Aplikácii")
                    .build());

            DialogFragment newFragment = new AboutDialogFragment();
            newFragment.show(getSupportFragmentManager(), "about");
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
