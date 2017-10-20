// Copyright (c) 2015-2017. Spiracle Software. All Rights Reserved.

package com.spiraclestudios.autoskola.presentation.ui.activities;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import butterknife.BindView;
import butterknife.OnClick;
import butterknife.OnLongClick;
import com.mikepenz.aboutlibraries.Libs.ActivityStyle;
import com.mikepenz.aboutlibraries.LibsBuilder;
import com.spiraclestudios.autoskola.BuildConfig;
import com.spiraclestudios.autoskola.R;
import com.spiraclestudios.autoskola.databinding.InformationActivityBinding;
import com.spiraclestudios.autoskola.databinding.InformationContentBinding;
import com.spiraclestudios.autoskola.framework.platform.StoreRating;
import com.spiraclestudios.autoskola.framework.presentation.ui.BaseActivity;
import com.spiraclestudios.autoskola.framework.presentation.ui.StandardActivity;
import com.spiraclestudios.autoskola.presentation.ui.dialogs.DevToolsDialog;

import static com.spiraclestudios.autoskola.framework.platform.AttributeResolver.resolveBooleanAttr;

public class InformationActivity extends StandardActivity {

  private String versionName;
  private int versionCode;

  private static final String STATE_VERSION_NAME = "VERSION_NAME";
  private static final String STATE_VERSION_CODE = "VERSION_CODE";

  @BindView(R.id.app_version) TextView appVersionView;

  @Override protected BaseActivity getThis() {
    return this;
  }

  @Override public void setActivityContentView() {
    InformationActivityBinding activityBinding =
        DataBindingUtil.setContentView(this, R.layout.information__activity);

    InformationContentBinding contentBinding = activityBinding.informationContent;
    contentBinding.appVersion.setText("TEST OF DATA BINDING");
  }

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    Toolbar toolbar = findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);

    ActionBar actionBar = getSupportActionBar();
    if (actionBar != null) {
      actionBar.setDisplayHomeAsUpEnabled(true);
    }

    if (savedInstanceState == null) {
      versionName = BuildConfig.VERSION_NAME;
      versionCode = BuildConfig.VERSION_CODE;
    } else {
      versionName = savedInstanceState.getString(STATE_VERSION_NAME);
      versionCode = savedInstanceState.getInt(STATE_VERSION_CODE);
    }
    appVersionView.setText(
        getString(R.string.information__text__version, versionName, versionCode));
  }

  @Override protected void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);

    outState.putString(STATE_VERSION_NAME, versionName);
    outState.putInt(STATE_VERSION_CODE, versionCode);
  }

  @Override public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case android.R.id.home:
        super.onBackPressed();
        return true;
    }
    return super.onOptionsItemSelected(item);
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
    ClipData clip =
        ClipData.newPlainText(getString(R.string.information__clipboard_label__social_link), url);
    clipboard.setPrimaryClip(clip);

    Toast.makeText(this, R.string.information__toast__link_was_copied, Toast.LENGTH_SHORT).show();
    return true;
  }

  @OnClick(R.id.show_changelog) public void show_changelog_onClick() {
    startActivity(ChangelogActivity.createIntentWithAllChangelogs(this));
  }

  @OnClick(R.id.show_about_libraries) public void show_about_libraries_onClick() {
    boolean isLightTheme = resolveBooleanAttr(this, R.attr.isLightTheme);
    new LibsBuilder().withActivityTitle(getString(R.string.screen_title__about_libraries))
        .withActivityStyle(isLightTheme ? ActivityStyle.LIGHT_DARK_TOOLBAR : ActivityStyle.DARK)
        .start(this);
  }

  @OnClick(R.id.rate_our_app) public void rate_our_app_onClick() {
    StoreRating.rateApp(this);
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
}
