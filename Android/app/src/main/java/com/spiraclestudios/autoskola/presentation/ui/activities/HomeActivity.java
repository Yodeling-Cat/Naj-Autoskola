// Copyright (c) 2015-2017. Spiracle Software. All Rights Reserved.

package com.spiraclestudios.autoskola.presentation.ui.activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import com.crashlytics.android.Crashlytics;
import com.spiraclestudios.autoskola.G;
import com.spiraclestudios.autoskola.MainActivityPagerAdapter;
import com.spiraclestudios.autoskola.R;
import com.spiraclestudios.autoskola.domain.Groups;
import com.spiraclestudios.autoskola.framework.presentation.ui.BaseActivity;
import com.spiraclestudios.autoskola.framework.presentation.ui.MainActivity;
import com.spiraclestudios.autoskola.presentation.ui.dialogs.GoingFreeDialog;
import com.spiraclestudios.autoskola.presentation.ui.dialogs.TestOptionsDialog;
import com.spiraclestudios.autoskola.presentation.ui.fragments.MainActivityFragment;
import timber.log.Timber;

import static android.os.Build.VERSION.SDK_INT;
import static android.os.Build.VERSION_CODES.LOLLIPOP;

public class HomeActivity extends MainActivity {

  @Override protected BaseActivity getThis() {
    return this;
  }

  @Override public void setActivityContentView() {
    setContentView(R.layout.home__activity);
  }

  @Override protected void onCreate(Bundle savedInstanceState) {
    // TODO: Move first launch to the framework's HomeActivity class.
    handleFirstLaunch();
    super.onCreate(savedInstanceState);

    GoingFreeDialog.handleGoingFreeDialog(this);

    setCrashlyticsUserInfo();

    // Set up Toolbar
    Toolbar toolbar = findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);

    // Set up TabLayout
    SharedPreferences prefsSettings = getSharedPreferences(G.PREFS_SETTINGS, MODE_PRIVATE);
    final boolean isCDTMainGroup = prefsSettings.getBoolean("cdt_main_group", false);

    int tabTitle1;
    int tabTitle2;
    int tabIcon1;
    int tabIcon2;

    if (isCDTMainGroup) {
      tabTitle1 = R.string.text__group_cdt;
      tabTitle2 = R.string.text__group_ab;
      if (SDK_INT >= LOLLIPOP) {
        tabIcon1 = R.drawable.selector_cdt_group;
        tabIcon2 = R.drawable.selector_ab_group;
      } else {
        tabIcon1 = R.drawable.ic_cdt_group__normal;
        tabIcon2 = R.drawable.ic_ab_group__normal;
      }
    } else {
      tabTitle1 = R.string.text__group_ab;
      tabTitle2 = R.string.text__group_cdt;
      if (SDK_INT >= LOLLIPOP) {
        tabIcon1 = R.drawable.selector_ab_group;
        tabIcon2 = R.drawable.selector_cdt_group;
      } else {
        tabIcon1 = R.drawable.ic_ab_group__normal;
        tabIcon2 = R.drawable.ic_cdt_group__normal;
      }
    }

    TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
    if (tabLayout != null) {
      tabLayout.addTab(tabLayout.newTab().setText(tabTitle1).setIcon(tabIcon1));
      tabLayout.addTab(tabLayout.newTab().setText(tabTitle2).setIcon(tabIcon2));
      tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
    }

    final ViewPager viewPager = (ViewPager) findViewById(R.id.view_pager);
    final MainActivityPagerAdapter adapter =
        new MainActivityPagerAdapter(this, tabLayout.getTabCount());
    viewPager.setAdapter(adapter);
    viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
    tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
      @Override public void onTabSelected(TabLayout.Tab tab) {
        viewPager.setCurrentItem(tab.getPosition());
      }

      @Override public void onTabUnselected(TabLayout.Tab tab) {
      }

      @Override public void onTabReselected(TabLayout.Tab tab) {
      }
    });

    // Set up Navigation Drawer
    DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
    ActionBarDrawerToggle toggle =
        new ActionBarDrawerToggle(this, drawer, toolbar, R.string.content_desc__open_nav_drawer,
            R.string.content_desc__close_nav_drawer);
    drawer.addDrawerListener(toggle);
    toggle.syncState();

    NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
    navigationView.setNavigationItemSelectedListener(this);

    // setOnClickListener for the Floating Action Button.
    FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
    fab.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View view) {

        Groups group1 = isCDTMainGroup ? Groups.CDT : Groups.AB;
        Groups group2 = isCDTMainGroup ? Groups.AB : Groups.CDT;
        TestOptionsDialog dialog =
            TestOptionsDialog.newInstance(viewPager.getCurrentItem() == 0 ? group1 : group2);

        dialog.show(getSupportFragmentManager(), "TestOptions");
      }
    });
  }

  private void handleFirstLaunch() {
    SharedPreferences prefs = getSharedPreferences(G.PREFS_GENERIC, MODE_PRIVATE);

    if (prefs.getBoolean("first_launch", true)) {
      prefs.edit().putBoolean("first_launch", false).apply();
      firstLaunch();
    }
  }

  private void setCrashlyticsUserInfo() {
    SharedPreferences prefs = getSharedPreferences(G.PREFS_GENERIC, MODE_PRIVATE);

    // Set Crashlytics user email and name.
    String userEmailAddress = prefs.getString("user_email_address", "");
    String userFullName =
        getFullName(prefs.getString("user_first_name", ""), prefs.getString("user_last_name", ""));

    if (!userEmailAddress.isEmpty()) {
      Crashlytics.setUserEmail(userEmailAddress);
    }
    if (!userFullName.isEmpty()) {
      Crashlytics.setUserName(userFullName);
    }
    Timber.d("Crashlytics user info:\n->Email: %s\n->Name: %s", userEmailAddress, userFullName);
  }

  private String getFullName(String firstName, String lastName) {
    String userFullName = firstName;
    if (!lastName.isEmpty()) {
      if (!firstName.isEmpty()) {
        userFullName += " ";
      }
      userFullName += lastName;
    }

    return userFullName;
  }

  public void firstLaunch() {
    /*Intent intent = new Intent(this, IntroActivity.class);
    startActivity(intent);*/
  }
}
