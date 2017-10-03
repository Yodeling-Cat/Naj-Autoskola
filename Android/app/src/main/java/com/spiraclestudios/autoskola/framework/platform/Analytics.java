// Copyright (c) 2015-2017. Spiracle Software. All Rights Reserved.

package com.spiraclestudios.autoskola.framework.platform;

import android.content.Context;
import com.google.firebase.analytics.FirebaseAnalytics;

public class Analytics {

  public static FirebaseAnalytics getInstance(Context ctx) {
    return FirebaseAnalytics.getInstance(ctx);
  }
}
