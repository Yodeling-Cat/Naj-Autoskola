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

    boolean isFreshInstall = lastApplicationVersion == 0;
    boolean wasNotUpdated = lastApplicationVersion == currentApplicationVersion;
    // TODO: Remove hack in next release
    // Since the pref that we're checking for didn't exist before v11 and since we don't want to show
    // changelogs for fresh installs (when the pref doesn't exist) then we need to check if we come from
    boolean upgradingFromV11 = isFreshInstall && currentApplicationVersion == 12;

    if (!upgradingFromV11 && (isFreshInstall || wasNotUpdated)) {
      Intent intent = new Intent(this, HomeActivity.class);
      startActivity(intent);
    } else {
      Intent homeIntent = new Intent(this, HomeActivity.class);
      Intent changelogIntent =
          ChangelogActivity.createIntentWithRangeOfChangelogs(this, lastApplicationVersion + 1,
              currentApplicationVersion);

      TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
      stackBuilder.addNextIntent(homeIntent);
      stackBuilder.addNextIntent(changelogIntent);
      stackBuilder.startActivities();
    }
  }
}
