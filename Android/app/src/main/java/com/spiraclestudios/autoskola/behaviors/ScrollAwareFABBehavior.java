package com.spiraclestudios.autoskola.behaviors;

import android.content.Context;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.View;

/**
 * Added by benji on 13/3/2016.
 */
public class ScrollAwareFABBehavior extends FloatingActionButton.Behavior {

  private int threshold = 200;
  private int accumulatedMovementY = 0;

  public ScrollAwareFABBehavior(Context context, AttributeSet attrs) {
    super();
  }

  @Override public boolean onStartNestedScroll(final CoordinatorLayout coordinatorLayout,
      final FloatingActionButton child, final View directTargetChild, final View target,
      final int nestedScrollAxes) {
    accumulatedMovementY = 0;
    return nestedScrollAxes == ViewCompat.SCROLL_AXIS_VERTICAL || super.onStartNestedScroll(
        coordinatorLayout, child, directTargetChild, target, nestedScrollAxes);
  }

  @Override public void onNestedScroll(final CoordinatorLayout coordinatorLayout,
      final FloatingActionButton child, final View target, final int dxConsumed,
      final int dyConsumed, final int dxUnconsumed, final int dyUnconsumed) {
    super.onNestedScroll(coordinatorLayout, child, target, dxConsumed, dyConsumed, dxUnconsumed,
        dyUnconsumed);

    accumulatedMovementY =
        Math.max(Math.min(accumulatedMovementY + dyConsumed, threshold), -threshold);
    if (accumulatedMovementY >= threshold && child.getVisibility() == View.VISIBLE) {
      // Scrolled down -> hide
      accumulatedMovementY = 0;
      child.hide();
    } else if (accumulatedMovementY <= -threshold && child.getVisibility() != View.VISIBLE) {
      // Scrolled up -> show
      accumulatedMovementY = 0;
      child.show();
    }
  }
}