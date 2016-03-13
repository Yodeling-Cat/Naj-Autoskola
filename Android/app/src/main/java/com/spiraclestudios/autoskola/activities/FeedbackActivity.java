/*
 * Copyright (c) 2015-2016. Spiracle Studios. All Rights Reserved.
 */

package com.spiraclestudios.autoskola.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.Toast;

import com.spiraclestudios.autoskola.Helper;
import com.spiraclestudios.autoskola.R;
import com.spiraclestudios.autoskola.interfaces.IBaseActivity;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class FeedbackActivity extends BaseActivity
        implements IBaseActivity {

    public String activityName = "FeedbackActivity";

    public String getActivityName() {
        return activityName;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Helper.setTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);
        ButterKnife.bind(this);

        // Setup Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Helper.initializeDebugDrawer(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @OnClick(R.id.send_a_suggestion)
    public void send_a_suggestion_onClick() {
        Intent intent = new Intent(getApplicationContext(), SendFeedbackActivity.class);
        intent.putExtra(SendFeedbackActivity.EXTRA_FEEDBACK_TYPE, 0);
        startActivity(intent);
    }

    @OnClick(R.id.report_a_bug)
    public void report_a_bug_onClick() {
        Intent intent = new Intent(getApplicationContext(), SendFeedbackActivity.class);
        intent.putExtra(SendFeedbackActivity.EXTRA_FEEDBACK_TYPE, 1);
        startActivity(intent);
    }

    @OnClick(R.id.ask_for_help)
    public void ask_for_help_onClick() {
        Toast.makeText(FeedbackActivity.this, R.string.toast_not_yet_implemented,
                Toast.LENGTH_SHORT).show();
    }
}
