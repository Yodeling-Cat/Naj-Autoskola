package com.spiraclestudios.autoskola;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ImageView;

import java.io.InputStream;

import timber.log.Timber;

/**
 * Created by benji on 19/12/2015.
 */
public class IntersectionCanvas extends ImageView {

    private Canvas mCanvas;
    // The image that gets drawn to the screen.
    private Bitmap mFinalBitmap;

    public IntersectionCanvas(Context c, AttributeSet attrs) {
        super(c, attrs);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        drawIntersection(w, h);
        setImageBitmap(mFinalBitmap);
    }

    // TODO: How often is this called and how can I make it draw only once (or when orientation changes)
    // TODO: Could just check if the bitmap is empty and draw only then. Clear the bitmap's variable in clearCanvas().
    protected void drawIntersection(int canvasWidth, int canvasHeight) {
        Resources res = getResources();
        mFinalBitmap = Bitmap.createBitmap(480, 270, Bitmap.Config.ARGB_8888);
        mCanvas = new Canvas(mFinalBitmap);
        mCanvas.setDensity(DisplayMetrics.DENSITY_HIGH);
        Matrix trans = new Matrix();
        Paint mPaint = new Paint();
        mPaint.setAntiAlias(true);

        // TODO: Get all the images from assets.
        //InputStream inputStream = assetManager.open(path);

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = false;
        //options.inDensity = DisplayMetrics.DENSITY_HIGH;
        //options.inTargetDensity = res.getDisplayMetrics().densityDpi;
        Bitmap mImage = BitmapFactory.decodeResource(res, R.drawable.car, options);

        float x, y;
        float w = mImage.getWidth();
        float h = mImage.getHeight();

        // Clear screen
        mCanvas.drawColor(Color.parseColor("#e5e5e5"));

        x = 0;
        y = 0;
        trans.setTranslate(x, y);
        mCanvas.drawBitmap(mImage, trans, mPaint);

        x = 0;
        y = h;
        trans.setTranslate(x, y);
        trans.preRotate(45, w / 2, h / 2);
        mCanvas.drawBitmap(mImage, trans, mPaint);

        x = 480 - w;
        y = 270 - h;
        trans.reset();
        trans.setTranslate(x, y);
        mCanvas.drawBitmap(mImage, trans, mPaint);

        x = 480 - h;
        y = 270 - w - h;
        trans.setTranslate(x, y);
        trans.preRotate(90, w / 2, h / 2);
        mCanvas.drawBitmap(mImage, trans, mPaint);
    }

    public void clearCanvas() {
        mCanvas.drawColor(Color.parseColor("#e5e5e5"));
        //mFinalBitmap.recycle();
        //mFinalBitmap = null;
        //invalidate();
    }
}
