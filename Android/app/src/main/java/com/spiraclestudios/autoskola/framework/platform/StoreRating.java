// Copyright (c) 2015-2017. Spiracle Software. All Rights Reserved.

package com.spiraclestudios.autoskola.framework.platform;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.widget.Toast;
import com.spiraclestudios.autoskola.G;
import com.spiraclestudios.autoskola.R;
import com.zplesac.connectionbuddy.ConnectionBuddy;

import static android.content.Context.MODE_PRIVATE;

public class StoreRating {

  public static boolean rateApp(Context ctx) {
    if (hasRatedApp(ctx)) {
      Toast.makeText(ctx, R.string.rate_app__toast__already_rated, Toast.LENGTH_SHORT).show();
      return false;
    }

    if (!ConnectionBuddy.getInstance().hasNetworkConnection()) {
      Toast.makeText(ctx, R.string.toast__no_internet_connection, Toast.LENGTH_SHORT).show();
      return false;
    }

    SharedPreferences prefs = ctx.getSharedPreferences(G.PREFS_GENERIC, MODE_PRIVATE);
    SharedPreferences.Editor prefsEdit = prefs.edit();
    prefsEdit.putBoolean("has_rated_app", true);
    prefsEdit.apply();

    try {
      ctx.startActivity(new Intent(Intent.ACTION_VIEW,
          Uri.parse(ctx.getString(R.string.link__app__google_play__launch_store))));
    } catch (android.content.ActivityNotFoundException e) {
      ctx.startActivity(
          new Intent(Intent.ACTION_VIEW, Uri.parse(ctx.getString(R.string.link__app__google_play))));
    }
    return true;
  }

  public static boolean hasRatedApp(Context ctx) {
    SharedPreferences prefs = ctx.getSharedPreferences(G.PREFS_GENERIC, MODE_PRIVATE);
    return prefs.getBoolean("has_rated_app", false);
  }
}
