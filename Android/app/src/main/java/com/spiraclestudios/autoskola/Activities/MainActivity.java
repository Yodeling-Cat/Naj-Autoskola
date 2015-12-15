package com.spiraclestudios.autoskola.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.spiraclestudios.autoskola.Dialogs.TestOptionsDialog;
import com.spiraclestudios.autoskola.Helper;
import com.spiraclestudios.autoskola.Interfaces.IBaseActivity;
import com.spiraclestudios.autoskola.Intros.IntroActivity;
import com.spiraclestudios.autoskola.MainActivityPagerAdapter;
import com.spiraclestudios.autoskola.R;

import io.palaima.debugdrawer.DebugDrawer;
import io.palaima.debugdrawer.commons.BuildModule;
import io.palaima.debugdrawer.commons.DeviceModule;
import io.palaima.debugdrawer.commons.SettingsModule;
import io.palaima.debugdrawer.log.LogModule;

public class MainActivity extends BaseActivity
        implements IBaseActivity {

    private static final String TAG = "MainActivity";
    public String mActivityName = "MainActivity";

    public String getActivityName() {
        return mActivityName;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Start IntroActivity if this is the first launch of the app
        SharedPreferences prefs = PreferenceManager
                .getDefaultSharedPreferences(getApplicationContext());

        boolean first_launch = prefs.getBoolean("first_launch", true);
        boolean tutorial_introduction = prefs.getBoolean("tutorial_introduction", false);

        if (first_launch) {
            prefs.edit().putBoolean("first_launch", false).apply();
            firstLaunch();
        }

        // SetUp MainActivity
        Helper.setTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // SetUp Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(R.string.app_name);

        // SetUp TabLayout
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.addTab(tabLayout.newTab().setText(R.string.group_ab).setIcon(R.drawable
                .ic_directions_car_white_24dp));
        tabLayout.addTab(tabLayout.newTab().setText(R.string.group_cdt).setIcon(R.drawable
                .ic_local_shipping_white_24dp));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

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
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });

        // SetUp Navigation Drawer
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.nav_drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar
                , R.string.cd_navigation_drawer_open,
                R.string.cd_navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // setOnClickListener for the Floating Action Button
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_random_test);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TestOptionsDialog dialog = TestOptionsDialog.newInstance(
                        Helper.Groups.values()[viewPager.getCurrentItem()]);

                dialog.show(getSupportFragmentManager(), "TestOptions");
            }
        });

        // [Tutorials and Tours]

        // TODO: Fix crashes on API ~15
        // Introductory tutorial of this activity
        /*if (!tutorial_introduction) {
            int offset = 0;
            Resources resources = getResources();
            int resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android");
            if (resourceId > 0) {
                offset = resources.getDimensionPixelSize(resourceId);
            }

            // Move the button a little higher so it doesn't get covered by the navigation bar
            RelativeLayout.LayoutParams buttonLayoutParams = new RelativeLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            buttonLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
            buttonLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            int margin = ((Number) (getResources().getDisplayMetrics().density * 12)).intValue();
            buttonLayoutParams.setMargins(margin, margin, margin, margin + offset);

            // Display the Showcase
            new ShowcaseView.Builder(this)
                    .setTarget(new ActionItemTarget(this, R.id.action_stars))
                    //.setTarget(new ViewTarget(findViewById(R.id.action_stars)))
                    .setStyle(R.style.ShowcaseTheme_Light)
                    .setContentTitle(R.string.showcase_stars)
                    .setContentText(R.string.showcase_stars_content)
                    .hideOnTouchOutside()
                    .build().setButtonPosition(buttonLayoutParams);

            prefs.edit().putBoolean("tutorial_introduction", true).apply();
        }*/

        new DebugDrawer.Builder(this)
                .modules(
                        new LogModule(),
                        new DeviceModule(this),
                        new BuildModule(this),
                        new SettingsModule(this)
                ).build();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_activity, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_stars) {
            Toast.makeText(this, R.string.toast_not_yet_implemented, Toast.LENGTH_SHORT).show();
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
