package com.spiraclestudios.autoskola.Activities;

import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;

import com.spiraclestudios.autoskola.Fragments.FeedbackActivityFragment;
import com.spiraclestudios.autoskola.Helper;
import com.spiraclestudios.autoskola.Interfaces.IBaseActivity;
import com.spiraclestudios.autoskola.R;

public class FeedbackActivity extends BaseActivity
        implements IBaseActivity {
    private static final String TAG = "FeedbackActivity";
    private String mActivityName = "FeedbackActivity";

    public String getActivityName() {
        return mActivityName;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // [SetUp Activity]
        super.onCreate(savedInstanceState);
        Helper.setTheme(this);
        setContentView(R.layout.activity_feedback);

        // [SetUp Toolbar]
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(R.string.title_pomoc_a_pripomienky);

        // TODO: Add the drawer to this activity
        // [SetUp Navigation Drawer]
        /*DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar
                , R.string.cd_navigation_drawer_open,
                R.string.cd_navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();*/

        /*NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);*/

        // [Create and add the FeedbackActivityFragment to the layout]
        FeedbackActivityFragment feedbackActivityFragment = new FeedbackActivityFragment();
        getSupportFragmentManager().beginTransaction().add(
                R.id.fragment_container, feedbackActivityFragment).commit();
    }
}
