// Copyright (c) 2015-2017. Spiracle Software. All Rights Reserved.

package com.spiraclestudios.autoskola.presentation.ui;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.AppCompatImageButton;
import android.util.AttributeSet;

public class ProportionalImageButton extends AppCompatImageButton {

  public ProportionalImageButton(Context context) {
    super(context);
  }

  public ProportionalImageButton(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  public ProportionalImageButton(Context context, AttributeSet attrs, int defStyle) {
    super(context, attrs, defStyle);
  }

  @Override protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    Drawable d = getDrawable();
    if (d != null) {
      int w = MeasureSpec.getSize(widthMeasureSpec);
      int h = w * d.getIntrinsicHeight() / d.getIntrinsicWidth();
      setMeasuredDimension(w, h);
    } else {
      super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
  }
}
