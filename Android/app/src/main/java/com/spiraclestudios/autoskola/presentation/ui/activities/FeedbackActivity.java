// Copyright (c) 2015-2017. Spiracle Software. All Rights Reserved.

package com.spiraclestudios.autoskola.presentation.ui.activities;

import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;
import butterknife.BindView;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import com.spiraclestudios.autoskola.BuildConfig;
import com.spiraclestudios.autoskola.framework.platform.EmailSender;
import com.spiraclestudios.autoskola.R;
import com.spiraclestudios.autoskola.framework.presentation.ui.BaseActivity;
import com.spiraclestudios.autoskola.framework.presentation.ui.StandardActivity;
import com.zplesac.connectionbuddy.ConnectionBuddy;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class FeedbackActivity extends StandardActivity {

  @BindView(R.id.message) EditText messageView;
  @BindView(R.id.include_system_information) CheckBox includeSystemInformation;

  @Override protected BaseActivity getThis() {
    return this;
  }

  @Override public void setActivityContentView() {
    setContentView(R.layout.feedback__activity);
  }

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
  }

  @Override public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.activity__feedback, menu);
    return true;
  }

  /**
   * If you stop using Intent.ACTION_SEND in the future, remember to ask
   * the user for his email so you can contact him back.
   */
  @Override public boolean onOptionsItemSelected(MenuItem item) {
    int id = item.getItemId();

    if (id == R.id.action__send_feedback) {
      if (!ConnectionBuddy.getInstance().hasNetworkConnection()) {
        Toast.makeText(this, R.string.feedback__toast__no_internet_connection, Toast.LENGTH_SHORT).show();
        return true;
      }

      if (TextUtils.isEmpty(messageView.getText().toString())) {
        messageView.setError(getString(R.string.feedback__toast__enter_a_message));
        return true;
      }

      String emailMessage = messageView.getText().toString();
      if (includeSystemInformation.isChecked()) {
        emailMessage += "\n\n[System Information]\n\n" + getSystemInfo();
      }

      new EmailSender(this).openMailingClient(
          getString(R.string.feedback__text__email_subject, getString(R.string.app_name)), emailMessage,
          getString(R.string.company__email),
          getString(R.string.feedback__intent_chooser__title_for_sending_email));
      return true;
    }
    return super.onOptionsItemSelected(item);
  }

  @OnTextChanged(R.id.message) public void message_onTextChanged() {
    messageView.setError(null);
  }

  @OnClick(R.id.preview_system_information) public void previewSystemInformation_onClick() {
    AlertDialog.Builder builder = new AlertDialog.Builder(this);
    builder.setTitle(R.string.system_information_preview__title)
        .setMessage(getSystemInfo())
        .setPositiveButton(R.string.system_information_preview__action__close, null);
    builder.create().show();
  }

  private String getSystemInfo() {
    Display display = getWindowManager().getDefaultDisplay();
    DisplayMetrics metrics = new DisplayMetrics();
    display.getMetrics(metrics);

    return "[APPLICATION]\n" +
        "Package: " + BuildConfig.APPLICATION_ID + "\n" +
        "Flavor: " + ((BuildConfig.FLAVOR.isEmpty()) ? "none" : BuildConfig.FLAVOR) + "\n" +
        "Build Type: " + BuildConfig.BUILD_TYPE + "\n" +
        "Version Name: " + BuildConfig.VERSION_NAME + "\n" +
        "Version Code: " + BuildConfig.VERSION_CODE + "\n" +

        "\n[DEVICE]\n" +
        "API Level: " + Build.VERSION.SDK_INT + "\n" +
        "Device: " + Build.DEVICE + "\n" +
        "Model: " + Build.MODEL + "\n" +
        "Manufacturer: " + Build.MANUFACTURER + "\n" +
        "Time: " + new SimpleDateFormat("HH:mm:ss", Locale.US).format(new Date()) + "\n" +
        "Date: " + new SimpleDateFormat("dd/MM/yyyy", Locale.US).format(new Date()) + "\n" +

        "\n[DISPLAY]\n" +
        "Width: " + metrics.widthPixels + "\n" +
        "Height: " + metrics.heightPixels + "\n" +
        "Density DPI: " + metrics.densityDpi + "\n" +
        "Scaled Density: " + metrics.scaledDensity;
  }
}
