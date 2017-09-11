// Copyright (c) 2015-2017. Spiracle Software. All Rights Reserved.

package com.spiraclestudios.autoskola.presentation.ui.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.AppCompatActivity;
import com.spiraclestudios.autoskola.BuildConfig;
import com.spiraclestudios.autoskola.G;

import static com.spiraclestudios.autoskola.presentation.ui.activities.ChangelogActivity.EXTRA_CURRENT_APPLICATION_VERSION;
import static com.spiraclestudios.autoskola.presentation.ui.activities.ChangelogActivity.EXTRA_LAST_APPLICATION_VERSION;

public class SplashActivity extends AppCompatActivity {

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    SharedPreferences prefs = getSharedPreferences(G.PREFS_GENERIC, MODE_PRIVATE);
    final int lastApplicationVersion = prefs.getInt("last_application_version", 0);
    final int currentApplicationVersion = BuildConfig.VERSION_CODE;

    if (lastApplicationVersion != currentApplicationVersion) {
      Editor editor = prefs.edit();
      editor.putInt("last_application_version", currentApplicationVersion);
      editor.apply();
    }

    // TODO: Uncomment
    //if (lastApplicationVersion == 0 || lastApplicationVersion == currentApplicationVersion) {
    //  Intent intent = new Intent(this, HomeActivity.class);
    //  startActivity(intent);
    //} else {
      Intent homeIntent = new Intent(this, HomeActivity.class);
      Intent changelogIntent = new Intent(this, ChangelogActivity.class);
      changelogIntent.putExtra(EXTRA_LAST_APPLICATION_VERSION, lastApplicationVersion);
      changelogIntent.putExtra(EXTRA_CURRENT_APPLICATION_VERSION, currentApplicationVersion);

      TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
      stackBuilder.addNextIntent(homeIntent);
      stackBuilder.addNextIntent(changelogIntent);
      stackBuilder.startActivities();
    //}
  }
}
