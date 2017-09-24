// Copyright (c) 2015-2017. Spiracle Software. All Rights Reserved.

package com.spiraclestudios.autoskola.framework.platform;

import android.content.Context;
import android.content.res.Resources.Theme;
import android.support.annotation.AttrRes;
import android.support.annotation.ColorInt;
import android.util.TypedValue;

public class AttributeResolver {

  @ColorInt public static int resolveColorAttr(Context context, @AttrRes int colorAttr) {
    Theme theme = context.getTheme();
    TypedValue colorValue = new TypedValue();

    theme.resolveAttribute(colorAttr, colorValue, true);
    return colorValue.data;
  }
}
