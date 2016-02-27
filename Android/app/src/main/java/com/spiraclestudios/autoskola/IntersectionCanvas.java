/*
 * Copyright (c) 2015-2016. Spiracle Studios. All Rights Reserved.
 */

package com.spiraclestudios.autoskola;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.widget.ImageView;

/**
 * Original created by benji on 19/12/2015.
 */
public class IntersectionCanvas extends ImageView
{

  private Canvas mCanvas;
  // The image that gets drawn to the screen.
  private Bitmap mFinalBitmap;
  private Bitmap mCarImage;
  private Paint mPaint;

  public IntersectionCanvas(Context c, AttributeSet attrs)
  {

    super(c, attrs);
  }

  // http://developer.android.com/reference/android/view/View.html#onMeasure(int, int)
  /*@Override
  protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // Try for a width based on our minimum
        int minw = getPaddingLeft() + getPaddingRight() + getSuggestedMinimumWidth();
        int w = resolveSizeAndState(minw, widthMeasureSpec, 1);

        // Whatever the width ends up being, ask for a height that would let the pie
        // get as big as it can
        int minh = MeasureSpec.getSize(w) - (int) mTextWidth + getPaddingBottom() + getPaddingTop();
        int h = resolveSizeAndState(MeasureSpec.getSize(w) - (int) mTextWidth, heightMeasureSpec, 0);

        setMeasuredDimension(w, h);
    }*/

  @Override
  protected void onSizeChanged(int w, int h, int oldw, int oldh)
  {

    super.onSizeChanged(w, h, oldw, oldh);
    init();
    drawMeLikeOneOfYourIntersections();
  }

  protected void init()
  {

    mFinalBitmap = Bitmap.createBitmap(480, 270, Bitmap.Config.ARGB_8888);
    mCanvas = new Canvas(mFinalBitmap);
    mCanvas.setDensity(DisplayMetrics.DENSITY_HIGH);

    BitmapFactory.Options options = new BitmapFactory.Options();
    options.inScaled = false;
    //options.inDensity = DisplayMetrics.DENSITY_HIGH;
    //options.inTargetDensity = res.getDisplayMetrics().densityDpi;

    // TODO: Cache all the images used by this intersection from assets.
    //InputStream inputStream = assetManager.open(path);
    mCarImage = BitmapFactory.decodeResource(getResources(), R.drawable.car, options);
  }

  protected void drawObject(Bitmap image, float x, float y, float angle)
  {

    Matrix trans = new Matrix();
    float w = image.getWidth();
    float h = image.getHeight();

    trans.setTranslate(x - w / 2, y - h / 2);
    trans.postRotate(angle, x, y);
    mCanvas.drawBitmap(image, trans, mPaint);
  }

  protected void drawMeLikeOneOfYourIntersections()
  {

    Matrix trans = new Matrix();
    float x, y;
    float w = mCarImage.getWidth();
    float h = mCarImage.getHeight();
    float canvasW = mCanvas.getWidth();
    float canvasH = mCanvas.getHeight();
    mPaint = new Paint();
    mPaint.setAntiAlias(true);

    // Clear screen
    mCanvas.drawColor(Color.parseColor("#e5e5e5"));

    // Center of screen, rotated
    //drawObject(mCarImage, canvasW / 2, canvasH / 2, rot);
    // Bottom right corner
    //drawObject(mCarImage, canvasW - w / 2, canvasH - h / 2, 0);
    // Bottom right corner, rotated
    //mPaint.setColorFilter(new PorterDuffColorFilter(Color.GREEN, PorterDuff.Mode.MULTIPLY));
    //drawObject(mCarImage, canvasW - h / 2, canvasH - w / 2 - h, 90);


    // Mark the canvas' center point
    x = canvasW / 2;
    y = canvasH / 2;
    float size = 2;
    trans.setTranslate(x, y);
    mPaint.setColor(Color.GREEN);
    mCanvas.drawRect(x - size, y - size, x + size, y + size, mPaint);

    setImageBitmap(mFinalBitmap);
  }

  public void clearCanvas()
  {

    mCanvas.drawColor(Color.parseColor("#e5e5e5"));
    //mFinalBitmap.recycle();
    //mFinalBitmap = null;
    //invalidate();
  }

  // private int rot = 0;
  // private float oldX = 0f;
    /*@Override
    public boolean onTouchEvent(MotionEvent e) {
        if (e.getAction() == MotionEvent.ACTION_DOWN) {
            oldX = e.getX();
            //oldY = e.getY();
        } else if (e.getAction() == MotionEvent.ACTION_MOVE) {
            float delta = (e.getX() - oldX) * 0.7f;
            rot += delta;
            oldX = e.getX();
            //oldY = e.getY();

            drawMeLikeOneOfYourIntersections();
        }
        return true;
    }*/
}
