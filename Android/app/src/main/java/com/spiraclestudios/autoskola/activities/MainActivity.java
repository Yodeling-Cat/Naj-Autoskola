/*
 * Copyright (c) 2015-2016. Spiracle Studios. All Rights Reserved.
 */

package com.spiraclestudios.autoskola.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.spiraclestudios.autoskola.BuildConfig;
import com.spiraclestudios.autoskola.G;
import com.spiraclestudios.autoskola.Helper;
import com.spiraclestudios.autoskola.MainActivityPagerAdapter;
import com.spiraclestudios.autoskola.R;
import com.spiraclestudios.autoskola.dialogs.TestOptionsDialog;
import com.spiraclestudios.autoskola.interfaces.IBaseActivity;
import com.spiraclestudios.autoskola.intros.IntroActivity;

import java.util.Locale;

import timber.log.Timber;

public class MainActivity extends BaseActivity
        implements IBaseActivity {

    private FirebaseAnalytics mFirebaseAnalytics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        // Start IntroActivity if this is the first launch of the app.
        SharedPreferences prefs = getSharedPreferences(G.PREFS_GENERIC, MODE_PRIVATE);
        SharedPreferences.Editor prefsEdit = prefs.edit();

        boolean isFirstLaunch = prefs.getBoolean("first_launch", true);

        if (isFirstLaunch) {
            prefsEdit.putBoolean("first_launch", false).apply();
            firstLaunch();
        }

        //boolean tutorialIntroduction = prefs.getBoolean("tutorial_introduction", false);

        // Set up MainActivity
        Helper.setTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Set Crashlytics user email and name.
        String userEmailAddress = prefs.getString("user_email_address", "");
        String userFullName = Helper.getFullName(prefs.getString("user_first_name", ""), prefs.getString("user_last_name", ""));

        if (!userEmailAddress.isEmpty()) {
            Crashlytics.setUserEmail(userEmailAddress);
        }
        if (!userFullName.isEmpty()) {
            Crashlytics.setUserName(userFullName);
        }
        Timber.d("Crashlytics user info:\n->Email: %s\n->Name: %s", userEmailAddress, userFullName);

        // Set up Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            if (BuildConfig.PREMIUM) {
                actionBar.setSubtitle(R.string.premium);
            }
        }

        // Set up TabLayout
        SharedPreferences prefsSettings = getSharedPreferences(G.PREFS_SETTINGS, MODE_PRIVATE);
        final boolean isCDTMainGroup = prefsSettings.getBoolean("cdt_main_group", false);

        int tabTitle1;
        int tabTitle2;
        int tabIcon1;
        int tabIcon2;

        if (isCDTMainGroup) {
            tabTitle1 = R.string.group_cdt_long;
            tabTitle2 = R.string.group_ab_long;
            tabIcon1 = R.drawable.ic_local_shipping_white_24dp;
            tabIcon2 = R.drawable.ic_directions_car_white_24dp;
        } else {
            tabTitle1 = R.string.group_ab_long;
            tabTitle2 = R.string.group_cdt_long;
            tabIcon1 = R.drawable.ic_directions_car_white_24dp;
            tabIcon2 = R.drawable.ic_local_shipping_white_24dp;
        }

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        if (tabLayout != null) {
            tabLayout.addTab(tabLayout.newTab().setText(tabTitle1).setIcon(tabIcon1));
            tabLayout.addTab(tabLayout.newTab().setText(tabTitle2).setIcon(tabIcon2));
            tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        }

        final ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
        final MainActivityPagerAdapter adapter = new MainActivityPagerAdapter
                (getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });

        // Set up Navigation Drawer
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.nav_drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar
                , R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // setOnClickListener for the Floating Action Button.
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_random_test);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Helper.Groups group1 = isCDTMainGroup ? Helper.Groups.CDT : Helper.Groups.AB;
                Helper.Groups group2 = isCDTMainGroup ? Helper.Groups.AB : Helper.Groups.CDT;
                TestOptionsDialog dialog = TestOptionsDialog
                        .newInstance(viewPager.getCurrentItem() == 0 ? group1 : group2);

                dialog.show(getSupportFragmentManager(), "TestOptions");
            }
        });

        // [Tutorials and Tours]

        // TODO: Fix crashes on API ~15, check if theres an update.
        // Introductory tutorial of this activity.
    /*if (!tutorialIntroduction) {
      int offset = 0;
            Resources resources = getResources();
            int resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android");
            if (resourceId > 0) {
                offset = resources.getDimensionPixelSize(resourceId);
            }

            // Move the button a little higher so it doesn't get covered by the navigation bar.
            RelativeLayout.LayoutParams buttonLayoutParams = new RelativeLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            buttonLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
            buttonLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            int margin = ((Number) (getResources().getDisplayMetrics().density * 12)).intValue();
            buttonLayoutParams.setMargins(margin, margin, margin, margin + offset);

            // Display the Showcase.
            new ShowcaseView.Builder(this)
                    .setTarget(new ActionItemTarget(this, R.id.action_stars))
                    //.setTarget(new ViewTarget(findViewById(R.id.action_stars)))
                    .setStyle(R.style.ShowcaseTheme_Light)
                    .setContentTitle(R.string.showcase_stars)
                    .setContentText(R.string.showcase_stars_content)
                    .hideOnTouchOutside()
                    .build().setButtonPosition(buttonLayoutParams);

            prefsEdit.putBoolean("tutorial_introduction", true).apply();
        }*/

        Helper.initializeDebugDrawer(this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.nav_drawer_layout);
        if (drawer != null && drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_stars) {
            SharedPreferences prefs = getSharedPreferences(G.PREFS_GENERIC, MODE_PRIVATE);
            int stars = prefs.getInt("rewards_stars", 0);
            Toast.makeText(this,
                    String.format(Locale.ENGLISH, getString(R.string.toast_rewards_stars_count), stars),
                    Toast.LENGTH_SHORT).show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void firstLaunch() {
        // Start the IntroActivity
        Intent intent = new Intent(this, IntroActivity.class);
        startActivity(intent);
    }
}
