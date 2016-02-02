/*
 * Copyright 2015-2016 Spiracle Studios. All Rights Reserved.
 */

package com.spiraclestudios.autoskola.activities;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.spiraclestudios.autoskola.BuildConfig;
import com.spiraclestudios.autoskola.Helper;
import com.spiraclestudios.autoskola.R;
import com.spiraclestudios.autoskola.dialogs.PreviewSystemInfoDialog;
import com.spiraclestudios.autoskola.interfaces.IBaseActivity;
import com.zplesac.connectionbuddy.ConnectionBuddy;
import com.zplesac.connectionbuddy.cache.ConnectionBuddyCache;
import com.zplesac.connectionbuddy.interfaces.ConnectivityChangeListener;
import com.zplesac.connectionbuddy.models.ConnectivityEvent;
import com.zplesac.connectionbuddy.models.ConnectivityState;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import timber.log.Timber;

public class SendFeedbackActivity extends BaseActivity
        implements IBaseActivity, ConnectivityChangeListener {

    public String mActivityName = "SendFeedbackActivity";

    public final static String EXTRA_FEEDBACK_TYPE =
            "com.spiraclestudios.autoskola.FEEDBACK_TYPE";

    int mFeedbackType;
    boolean isConnected;

    @Bind(R.id.feedback_message)
    EditText feedback_message;
    @Bind(R.id.send_system_info)
    CheckBox send_system_info;
    @Bind(R.id.connectivity_error)
    TextView connectivity_error;
    @Bind(R.id.preview_system_info)
    ImageButton preview_system_info;

    public String getActivityName() {
        return mActivityName;
    }

    @Override
    public void onStart() {
        super.onStart();
        ConnectionBuddy.getInstance().registerForConnectivityEvents(this, this);
    }

    @Override
    public void onStop() {
        super.onStop();
        ConnectionBuddy.getInstance().unregisterFromConnectivityEvents(this);
    }

    @Override
    public void onConnectionChange(ConnectivityEvent event) {
        isConnected = event.getState() == ConnectivityState.CONNECTED;
        if (isConnected) {
            connectivity_error.setVisibility(View.GONE);
        } else {
            connectivity_error.setVisibility(View.VISIBLE);
        }
    }

    /* feedbackType:
     * 0 - Feedback
     * 1 - Bug
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Helper.setTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_feedback);
        ButterKnife.bind(this);

        if (savedInstanceState != null) {
            ConnectionBuddyCache.clearLastNetworkState(this);
        }

        // Read extras from the intent
        Intent intent = getIntent();
        mFeedbackType = intent.getIntExtra(EXTRA_FEEDBACK_TYPE, 0);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Set toolbar title based on feedback type
        toolbar.setTitle(mFeedbackType == 0 ? R.string.send_a_suggestion : R.string.report_a_bug);

        // Hide the Send System Info checkbox if the feedback type is not a bug report
        if (mFeedbackType != 1) {
            send_system_info.setVisibility(View.GONE);
            preview_system_info.setVisibility(View.GONE);
        }

        Helper.initializeDebugDrawer(this);
    }

    @OnClick(R.id.preview_system_info)
    public void preview_system_info_onClick() {
        PreviewSystemInfoDialog dialog = PreviewSystemInfoDialog
                .newInstance(getSystemInfo());
        dialog.show(getSupportFragmentManager(), "PreviewSystemInfo");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.send_feedback_activity, menu);
        return true;
    }

    /* If you stop using Intent.ACTION_SEND in the future, remember to ask
     * the user for his email so you can contact him back
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_send) {
            String subject = "[Naj Autoškola] ";
            String message = feedback_message.getText().toString();

            Timber.d("message: " + message);

            // Check if a message was entered
            if (TextUtils.isEmpty(message)) {
                Toast.makeText(this, R.string.toast_enter_a_message, Toast.LENGTH_SHORT).show();
                return true;
            }

            // Modify the subject
            if (mFeedbackType == 0) {
                subject += getResources().getString(R.string.send_feedback_subject_feedback);
            } else {
                subject += getResources().getString(R.string.send_feedback_subject_bug);
            }

            // Add system info to the message if reporting a bug and send_system_info is checked
            if (send_system_info.getVisibility() == View.VISIBLE && send_system_info.isChecked()) {
                message += "\n\n\n" + getSystemInfo();
            }

            // Send the feedback
            Intent Email = new Intent(Intent.ACTION_SEND);
            Email.setType("text/email");
            Email.putExtra(Intent.EXTRA_EMAIL, new String[]{"spiraclestudios@gmail.com"});
            Email.putExtra(Intent.EXTRA_SUBJECT, subject);
            Email.putExtra(Intent.EXTRA_TEXT, message);
            startActivity(Intent.createChooser(Email, getResources().
                    getString(R.string.send_feedback_chooser_title)));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public String getSystemInfo() {
        String text = "----------- SYSTEM -----------\n";
        text += "\n-- APPLICATION --\n";
        text += "Package: " + BuildConfig.APPLICATION_ID + "\n";
        text += "Build type: " + BuildConfig.BUILD_TYPE + "\n";
        text += "Flavor: " + ((BuildConfig.FLAVOR.equals("")) ? BuildConfig.FLAVOR : "none") + "\n";
        text += "Version name: " + BuildConfig.VERSION_NAME + "\n";
        text += "Version code: " + BuildConfig.VERSION_CODE + "\n";

        text += "\n-- OS --\n";
        text += "SDK version: " + Build.VERSION.SDK_INT + "\n";
        text += "Incremental: " + Build.VERSION.INCREMENTAL + "\n";

        text += "\n-- DEVICE --\n";
        text += "Device: " + Build.DEVICE + "\n";
        text += "Model: " + Build.MODEL + "\n";
        text += "Product: " + Build.PRODUCT + "\n";
        text += "Brand: " + Build.BRAND + "\n";
        text += "Manufacturer: " + Build.MANUFACTURER + "\n";
        text += "Time: " + new SimpleDateFormat("HH:mm:ss", Locale.US).format(new Date()) + "\n";
        text += "Date: " + new SimpleDateFormat("dd/MM/yyyy", Locale.US).format(new Date()) + "\n";

        Display display = getWindowManager().getDefaultDisplay();
        DisplayMetrics metrics = new DisplayMetrics();
        display.getMetrics(metrics);
        text += "\n-- DISPLAY --\n";
        text += "Width: " + metrics.widthPixels + "\n";
        text += "Height: " + metrics.heightPixels + "\n";
        text += "Density DPI: " + metrics.densityDpi + "\n";
        text += "Scaled density: " + metrics.scaledDensity;

        return text;
    }
}