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

import static com.spiraclestudios.autoskola.repository.ChangelogListRepository.CHANGELOG_VERSION;

public class SplashActivity extends AppCompatActivity {

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    SharedPreferences prefs = getSharedPreferences(G.PREFS_GENERIC, MODE_PRIVATE);

    /* Handling application update */
    final int currentAppVersion = BuildConfig.VERSION_CODE;
    final int previousAppVersion = prefs.getInt("last_application_version", 0);

    if (previousAppVersion != currentAppVersion) {
      Editor editor = prefs.edit();
      editor.putInt("last_application_version", currentAppVersion);
      editor.apply();
    }

    final boolean isFreshInstall = previousAppVersion == 0;
    final boolean wasUpdated = !isFreshInstall && currentAppVersion > previousAppVersion;

    /* Handling showing the changelog */
    final int currentChangelogVersion = CHANGELOG_VERSION;
    final int previousChangelogVersion = prefs.getInt("last_changelog_version", 0);

    // Update saved changelog version.
    if (currentChangelogVersion != previousChangelogVersion) {
      Editor editor = prefs.edit();
      editor.putInt("last_changelog_version", currentChangelogVersion);
      editor.apply();
    }

    // Whether the CHANGELOG_VERSION wasn't incremented since the previousAppVersion.
    final boolean changelogWasUpdated =
        !isFreshInstall && currentChangelogVersion > previousChangelogVersion;

    // Changelog is shown if !isFreshInstall && wasUpdated && changelogWasUpdated
    final boolean shouldShowHome = isFreshInstall || !wasUpdated || !changelogWasUpdated;
    if (shouldShowHome) {
      Intent homeIntent = new Intent(this, HomeActivity.class);
      startActivity(homeIntent);
    } else {
      Intent homeIntent = new Intent(this, HomeActivity.class);
      Intent changelogIntent =
          ChangelogActivity.Companion.createIntentWithRangeOfChangelogs(this, previousChangelogVersion + 1,
              currentChangelogVersion);

      TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
      stackBuilder.addNextIntent(homeIntent);
      stackBuilder.addNextIntent(changelogIntent);
      stackBuilder.startActivities();
    }
  }
}
