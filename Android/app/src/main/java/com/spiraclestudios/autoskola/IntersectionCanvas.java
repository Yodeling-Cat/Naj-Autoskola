/*
 * Copyright (c) 2015-2016. Spiracle Studios. All Rights Reserved.
 */

package com.spiraclestudios.autoskola;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.widget.ImageView;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import timber.log.Timber;

/**
 * Added by benji on 19/12/2015.
 */
public class IntersectionCanvas extends ImageView {

  private static final String contentPath = "images\\Designer Content\\";

  private Context context;
  private Canvas canvas;
  private Bitmap finalBitmap;
  private Bitmap carImage;
  private List<IntersectionObject> objects = new ArrayList<>();
  private Paint paint;

  public IntersectionCanvas(Context c, AttributeSet attrs) {
    super(c, attrs);
    context = c;
    initCanvas();
    loadAssets();
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

  @Override protected void onSizeChanged(int w, int h, int oldw, int oldh) {
    super.onSizeChanged(w, h, oldw, oldh);
    //initCanvas();
    drawIntersection();
  }

  // TODO: Cache all the images used by this intersection from assets.
  // TODO: Take an param for which intersection json to load the assets from.
  private void loadAssets() {
    BitmapFactory.Options options = new BitmapFactory.Options();
    options.inScaled = false;
    //options.inDensity = DisplayMetrics.DENSITY_HIGH;
    //options.inTargetDensity = res.getDisplayMetrics().densityDpi;

    //carImage = BitmapFactory.decodeResource(getResources(), R.drawable.car, options);

    Bitmap image = null;

    getBitmapFromAsset(context, contentPath + "Roads\\road_1.png");
  }

  @SuppressLint("BinaryOperationInTimber")
  private static Bitmap getBitmapFromAsset(Context context, String filePath) {
    AssetManager assetManager = context.getAssets();

    InputStream inputStream;
    Bitmap bitmap = null;
    try {
      inputStream = assetManager.open(filePath);
      bitmap = BitmapFactory.decodeStream(inputStream);
    } catch (IOException e) {
      Timber.d("Image \"%s\" does not exist.", contentPath + filePath);
    }

    return bitmap;
  }

  private void initCanvas() {
    finalBitmap = Bitmap.createBitmap(960, 540, Bitmap.Config.ARGB_8888);
    canvas = new Canvas(finalBitmap);
    canvas.setDensity(DisplayMetrics.DENSITY_HIGH);
  }

  private void drawObject(Bitmap image, float x, float y, float angle) {
    Matrix matrix = new Matrix();
    float w = image.getWidth();
    float h = image.getHeight();

    matrix.setTranslate(x - w / 2, y - h / 2);
    matrix.postRotate(angle, x, y);
    canvas.drawBitmap(image, matrix, paint);
  }

  private void drawIntersection() {
    Matrix trans = new Matrix();
    float x, y;
    float w = carImage.getWidth();
    float h = carImage.getHeight();
    float canvasW = canvas.getWidth();
    float canvasH = canvas.getHeight();
    paint = new Paint();
    paint.setAntiAlias(true);

    // Paint the background.
    canvas.drawColor(Color.parseColor("#607D8B"));

    // Center of screen, rotated.
    drawObject(carImage, canvasW / 2, canvasH / 2, 45);
    // Bottom right corner.
    drawObject(carImage, canvasW - w / 2, canvasH - h / 2, 0);
    // Bottom right corner, rotated.
    drawObject(carImage, canvasW - h / 2, canvasH - w / 2 - h, 90);

    // Mark the canvas' center point
    x = canvasW / 2;
    y = canvasH / 2;
    float size = 2;
    trans.setTranslate(x, y);
    paint.setColor(Color.GREEN);
    canvas.drawRect(x - size, y - size, x + size, y + size, paint);

    setImageBitmap(finalBitmap);
  }

  public void clearCanvas() {
    canvas.drawColor(Color.parseColor("#607D8B"));
    //finalBitmap.recycle();
    //finalBitmap = null;
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

            drawIntersection();
        }
        return true;
    }*/
}
