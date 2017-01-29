// Copyright (c) 2015-2017. Spiracle Software. All Rights Reserved.

package com.spiraclestudios.autoskola;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.View;

/**
 * Added by benjiko99 on 2/21/16.
 */
public class ListItemDecoration extends RecyclerView.ItemDecoration {

  private Drawable mDivider;

  public ListItemDecoration(Context context) {
    TypedValue drawableRef = new TypedValue();
    context.getTheme().resolveAttribute(R.attr.line_divider, drawableRef, true);
    mDivider = ContextCompat.getDrawable(context, drawableRef.resourceId);
  }

  @Override public void onDrawOver(Canvas canvas, RecyclerView parent, RecyclerView.State state) {
    int left = parent.getPaddingLeft();
    int right = parent.getWidth() - parent.getPaddingRight();

    int childCount = parent.getChildCount();
    for (int i = 0; i < childCount; i++) {
      View child = parent.getChildAt(i);

      RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();

      int top = child.getBottom() + params.bottomMargin;
      int bottom = top + mDivider.getIntrinsicHeight();

      mDivider.setBounds(left, top, right, bottom);
      mDivider.draw(canvas);
    }
  }
}
