// Copyright (c) 2015-2017. Spiracle Software. All Rights Reserved.

package com.spiraclestudios.autoskola.framework.platform;

import android.content.Context;
import android.content.res.Resources.Theme;
import android.support.annotation.AttrRes;
import android.support.annotation.ColorInt;
import android.support.v4.content.ContextCompat;
import android.util.TypedValue;

public class AttributeResolver {

  public static TypedValue resolveThemeAttr(Context context, @AttrRes int attrRes) {
    Theme theme = context.getTheme();
    TypedValue typedValue = new TypedValue();
    theme.resolveAttribute(attrRes, typedValue, true);
    return typedValue;
  }

  @ColorInt public static int resolveColorAttr(Context context, @AttrRes int colorAttr) {
    TypedValue resolvedAttr = resolveThemeAttr(context, colorAttr);
    // resourceId is used if it's a ColorStateList, and data if it's a color reference or a hex color
    int colorRes = resolvedAttr.resourceId != 0 ? resolvedAttr.resourceId : resolvedAttr.data;
    return ContextCompat.getColor(context, colorRes);
  }

  public static boolean resolveBooleanAttr(Context context, @AttrRes int booleanAttr) {
    int booleanInt = resolveThemeAttr(context, booleanAttr).data;
    // true == -1; false == 0;
    return booleanInt == -1;
  }
}
