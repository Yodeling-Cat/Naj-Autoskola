package com.spiraclestudios.autoskola.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

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

        // Get references to views
        LinearLayout send_a_suggestion = (LinearLayout) findViewById(R.id.send_a_suggestion);
        LinearLayout report_a_bug = (LinearLayout) findViewById(R.id.report_a_bug);
        LinearLayout ask_for_help = (LinearLayout) findViewById(R.id.ask_for_help);

        // [SetUp Toolbar]
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

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

        // Set onClickListeners
        send_a_suggestion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start SendFeedbackActivity
                Intent intent = new Intent(getApplicationContext(), SendFeedbackActivity.class);
                intent.putExtra(SendFeedbackActivity.EXTRA_FEEDBACK_TYPE, 0);
                startActivity(intent);
            }
        });

        report_a_bug.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start SendFeedbackActivity
                Intent intent = new Intent(getApplicationContext(), SendFeedbackActivity.class);
                intent.putExtra(SendFeedbackActivity.EXTRA_FEEDBACK_TYPE, 1);
                startActivity(intent);
            }
        });

        ask_for_help.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(FeedbackActivity.this, R.string.toast_not_yet_implemented, Toast.LENGTH_SHORT)
                        .show();
            }
        });
    }
}
