/*
 * Copyright (c) 2015-2016. Spiracle Studios. All Rights Reserved.
 */

package com.spiraclestudios.autoskola;

import android.content.Context;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

/**
 * Original created by benji on 16/10/2015.
 */
public class Effects
{
  public static void slide_down(Context context, View view)
  {
    Animation anim = AnimationUtils.loadAnimation(context, R.anim.slide_down);
    if (anim != null) {
      anim.reset();
      if (view != null) {
        view.clearAnimation();
        view.startAnimation(anim);
      }
    }
  }

  public static void slide_up(Context context, View view)
  {
    Animation anim = AnimationUtils.loadAnimation(context, R.anim.slide_up);
    if (anim != null) {
      anim.reset();
      if (view != null) {
        view.clearAnimation();
        view.startAnimation(anim);
      }
    }
  }
}
