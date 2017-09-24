// Copyright (c) 2015-2017. Spiracle Software. All Rights Reserved.

package com.spiraclestudios.autoskola.framework.platform;

import android.content.Context;
import android.content.res.Resources.Theme;
import android.support.annotation.AttrRes;
import android.support.annotation.ColorInt;
import android.util.TypedValue;
import com.spiraclestudios.autoskola.R;

public class AttributeResolver {

  public static int resolveThemeAttr(Context context, @AttrRes int attrRes) {
    Theme theme = context.getTheme();
    TypedValue typedValue = new TypedValue();
    theme.resolveAttribute(attrRes, typedValue, true);
    return typedValue.data;
  }

  @ColorInt public static int resolveColorAttr(Context context, @AttrRes int colorAttr) {
    return resolveThemeAttr(context, colorAttr);
  }

  public static boolean resolveBooleanAttr(Context context, @AttrRes int booleanAttr) {
    int booleanInt = resolveThemeAttr(context, booleanAttr);
    // true == -1; false == 0;
    return booleanInt == -1;
  }
}
