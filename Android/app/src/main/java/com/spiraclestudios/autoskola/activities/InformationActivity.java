/*
 * Copyright 2016 Spiracle Software. All Rights Reserved.
 */

package com.spiraclestudios.autoskola.activities;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnLongClick;
import com.spiraclestudios.autoskola.BuildConfig;
import com.spiraclestudios.autoskola.Helper;
import com.spiraclestudios.autoskola.R;
import com.spiraclestudios.autoskola.dialogs.DevToolsDialog;
import com.spiraclestudios.autoskola.interfaces.IBaseActivity;

public class InformationActivity extends BaseActivity implements IBaseActivity {

  private String appVersion;

  private static final String STATE_APP_VERSION = "APP_VERSION";

  @Bind(R.id.app_version) TextView appVersionView;

  @Override protected void onCreate(Bundle savedInstanceState) {
    Helper.setTheme(this);
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_information);
    ButterKnife.bind(this);

    // Setup Toolbar
    Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);

    ActionBar actionBar = getSupportActionBar();
    if (actionBar != null) {
      actionBar.setDisplayHomeAsUpEnabled(true);
    }

    if (savedInstanceState == null) {
      setAppVersion(BuildConfig.VERSION_NAME);
    } else {
      setAppVersion(savedInstanceState.getString(STATE_APP_VERSION));
    }

    Helper.initializeDebugDrawer(this);
  }

  @Override protected void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);

    outState.putString(STATE_APP_VERSION, appVersion);
  }

  @OnClick({
      R.id.web_link, R.id.facebook_link, R.id.google_play_link
  }) public void onClick_SocialLinks(View view) {
    String url = getSocialLinkUrl(view.getId());

    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
    startActivity(browserIntent);
  }

  @OnLongClick({
      R.id.web_link, R.id.facebook_link, R.id.google_play_link
  }) public boolean onLongClick_SocialLinks(View view) {
    String url = getSocialLinkUrl(view.getId());

    ClipboardManager clipboard =
        (ClipboardManager) this.getSystemService(Context.CLIPBOARD_SERVICE);
    ClipData clip = ClipData.newPlainText(getString(R.string.clip_label_social_link), url);
    clipboard.setPrimaryClip(clip);

    Toast.makeText(this, R.string.toast_link_was_copied, Toast.LENGTH_SHORT).show();
    return true;
  }

  @OnLongClick(R.id.app_version) public boolean app_version_onLongClick() {
    if (!BuildConfig.DEBUG) {
      return false;
    }
    showDeveloperTools();
    return true;
  }

  private void showDeveloperTools() {
    Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
    vibrator.vibrate(20);

    DialogFragment dialog = new DevToolsDialog();
    dialog.show(getSupportFragmentManager(), "DevTools");
  }

  private String getSocialLinkUrl(int viewId) {
    switch (viewId) {
      case R.id.web_link:
        return getString(R.string.link__social__web);
      case R.id.facebook_link:
        return getString(R.string.link__social__facebook);
      //case R.id.twitter_link:
      //  return getString(R.string.link__social__twitter);
      //case R.id.youtube_link:
      //  return getString(R.string.link__social__youtube);
      case R.id.google_play_link:
        return getString(R.string.link__social__google_play__publisher_profile);
      default:
        return "";
    }
  }

  private void setAppVersion(String appVersion) {
    this.appVersion = appVersion;
    appVersionView.setText(getString(R.string.information__string__version, appVersion));
  }
}
