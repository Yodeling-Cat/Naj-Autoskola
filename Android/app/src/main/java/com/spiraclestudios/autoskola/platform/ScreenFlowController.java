// Copyright (c) 2015-2017. Spiracle Software. All Rights Reserved.

package com.spiraclestudios.autoskola.platform;

import android.app.Activity;
import android.content.Intent;
import com.spiraclestudios.autoskola.presentation.ui.activities.HistoryActivity;
import com.spiraclestudios.autoskola.presentation.ui.activities.HomeActivity;

public class ScreenFlowController {

  public static void showHomeActivity(Activity activity) {
    activity.startActivity(new Intent(activity.getApplicationContext(), HomeActivity.class));
  }

  public static void showHistoryActivity(Activity activity) {
    activity.startActivity(new Intent(activity.getApplicationContext(), HistoryActivity.class));
  }
}
