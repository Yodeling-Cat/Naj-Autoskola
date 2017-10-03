// Copyright (c) 2015-2017. Spiracle Software. All Rights Reserved.

package com.spiraclestudios.autoskola.platform;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import com.spiraclestudios.autoskola.framework.platform.Analytics;
import com.spiraclestudios.autoskola.presentation.ui.activities.FeedbackActivity;
import com.spiraclestudios.autoskola.presentation.ui.activities.HistoryActivity;
import com.spiraclestudios.autoskola.presentation.ui.activities.HomeActivity;
import com.spiraclestudios.autoskola.presentation.ui.activities.InformationActivity;
import com.spiraclestudios.autoskola.presentation.ui.activities.RoadSignsCategoriesActivity;
import com.spiraclestudios.autoskola.presentation.ui.activities.SettingsActivity;

public class ScreenFlowController {

  public static void showHomeActivity(Activity activity) {
    activity.startActivity(new Intent(activity.getApplicationContext(), HomeActivity.class));
  }

  public static void showHistoryActivity(Activity activity) {
    activity.startActivity(new Intent(activity.getApplicationContext(), HistoryActivity.class));
  }

  public static void showRoadSignsCategoriesActivity(Activity activity) {
    activity.startActivity(new Intent(activity.getApplicationContext(), RoadSignsCategoriesActivity.class));
  }

  public static void showSettingsActivity(Activity activity) {
    activity.startActivity(new Intent(activity.getApplicationContext(), SettingsActivity.class));
  }

  public static void showFeedbackActivity(Activity activity) {
    activity.startActivity(new Intent(activity.getApplicationContext(), FeedbackActivity.class));
  }

  public static void showInformationActivity(Activity activity) {
    Bundle bundle = new Bundle();
    Analytics.getInstance(activity).logEvent("nav_about", bundle);
    activity.startActivity(new Intent(activity.getApplicationContext(), InformationActivity.class));
  }
}
