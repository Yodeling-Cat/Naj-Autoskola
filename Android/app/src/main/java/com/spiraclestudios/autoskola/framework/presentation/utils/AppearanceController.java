// Copyright (c) 2015-2017. Spiracle Software. All Rights Reserved.

package com.spiraclestudios.autoskola.framework.presentation.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.support.annotation.StyleRes;
import com.spiraclestudios.autoskola.G;
import com.spiraclestudios.autoskola.R;

public class AppearanceController {

  @StyleRes public static int activeThemeResId;

  public void setActiveThemeOnContext(Context ctx) {
    Resources res = ctx.getResources();
    SharedPreferences prefsSettings =
        ctx.getSharedPreferences(G.PREFS_SETTINGS, Context.MODE_PRIVATE);

    String theme = prefsSettings.getString("theme", res.getString(R.string.value__theme__light));

    if (theme.equals(res.getString(R.string.value__theme__light))) {
      activeThemeResId = R.style.AppTheme_Light;
    } else if (theme.equals(res.getString(R.string.value__theme__blue))) {
      activeThemeResId = R.style.AppTheme_Light_Blue;
    } else if (theme.equals(res.getString(R.string.value__theme__dark_blue))) {
      activeThemeResId = R.style.AppTheme_Dark_NightBlue;
    }
    ctx.setTheme(activeThemeResId);
  }
}
