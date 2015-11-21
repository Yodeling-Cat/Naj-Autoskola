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

import butterknife.ButterKnife;
import butterknife.OnClick;

public class FeedbackActivity extends BaseActivity
        implements IBaseActivity {

    private static final String TAG = "FeedbackActivity";
    public String mActivityName = "FeedbackActivity";

    public String getActivityName() {
        return mActivityName;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Helper.setTheme(this);
        setContentView(R.layout.activity_feedback);
        ButterKnife.bind(this);

        // SetUp Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    @OnClick(R.id.send_a_suggestion)
    public void send_a_suggestion_onClick() {
        // Start SendFeedbackActivity
        Intent intent = new Intent(getApplicationContext(), SendFeedbackActivity.class);
        intent.putExtra(SendFeedbackActivity.EXTRA_FEEDBACK_TYPE, 0);
        startActivity(intent);
    }

    @OnClick(R.id.report_a_bug)
    public void report_a_bug_onClick() {
        // Start SendFeedbackActivity
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
