/*
 * Copyright 2015-2016 Spiracle Studios. All Rights Reserved.
 */

package com.spiraclestudios.autoskola

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.DisplayMetrics
import android.view.MotionEvent
import android.widget.ImageView

/**
 * Copyright 2015-2016 Spiracle Studios. All Rights Reserved.
 * Original created by benji on 19/12/2015.
 */
class IntersectionCanvas

(c: Context, attrs: AttributeSet) : ImageView(c, attrs) {

    private lateinit var mCanvas: Canvas
    // The image that gets drawn to the screen.
    private lateinit var mFinalBitmap: Bitmap
    private lateinit var mCarImage: Bitmap
    private var mPaint: Paint? = null

    private var rot: Int = 0
    private var oldX: Float = 0f

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

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        init()
        drawMeLikeOneOfYourIntersections()
    }

    protected fun init() {
        mFinalBitmap = Bitmap.createBitmap(480, 270, Bitmap.Config.ARGB_8888)
        mCanvas = Canvas(mFinalBitmap)
        mCanvas.density = DisplayMetrics.DENSITY_HIGH

        val options = BitmapFactory.Options()
        options.inScaled = false
        //options.inDensity = DisplayMetrics.DENSITY_HIGH;
        //options.inTargetDensity = res.getDisplayMetrics().densityDpi;

        // TODO: Cache all the images used by this intersection from assets. Move this into importIntersection()
        //InputStream inputStream = assetManager.open(path);
        mCarImage = BitmapFactory.decodeResource(resources, R.drawable.car, options)
    }

    /*protected fun importIntersection() : Intersection
    {
        return
    }*/

    protected fun drawObject(image: Bitmap, x: Float, y: Float, angle: Float) {
        val trans = Matrix()
        val w = image.width.toFloat()
        val h = image.height.toFloat()

        trans.setTranslate(x - w / 2, y - h / 2)
        trans.postRotate(angle, x, y)
        mCanvas.drawBitmap(image, trans, mPaint)
    }

    protected fun drawMeLikeOneOfYourIntersections() {
        val trans = Matrix()
        val x: Float
        val y: Float
        val w = mCarImage.width.toFloat()
        val h = mCarImage.height.toFloat()
        val canvasW = mCanvas.width.toFloat()
        val canvasH = mCanvas.height.toFloat()
        val mPaint = Paint()
        mPaint.isAntiAlias = true

        // Clear screen
        mCanvas.drawColor(Color.parseColor("#e5e5e5"))

        // Center of screen, rotated
        drawObject(mCarImage, canvasW / 2, canvasH / 2, rot.toFloat())
        // Bottom right corner
        drawObject(mCarImage, canvasW - w / 2, canvasH - h / 2, 0f)
        // Bottom right corner, rotated
        mPaint.setColorFilter(PorterDuffColorFilter(Color.GREEN, PorterDuff.Mode.MULTIPLY))
        drawObject(mCarImage, canvasW - h / 2, canvasH - w / 2 - h, 90f)


        // Mark the canvas' center point
        x = canvasW / 2
        y = canvasH / 2
        val size = 2f
        trans.setTranslate(x, y)
        mPaint.color = Color.GREEN
        mCanvas.drawRect(x - size, y - size, x + size, y + size, mPaint)

        setImageBitmap(mFinalBitmap)
    }

    fun clearCanvas() {
        mCanvas.drawColor(Color.parseColor("#e5e5e5"))
        //mFinalBitmap.recycle();
        //mFinalBitmap = null;
        //invalidate();
    }

    // TODO: Remove onTouchEvent for rotating objects in the canvas.
    override fun onTouchEvent(e: MotionEvent): Boolean {
        if (e.action == MotionEvent.ACTION_DOWN) {
            oldX = e.x
            //oldY = e.getY();
        } else if (e.action == MotionEvent.ACTION_MOVE) {
            val delta = (e.x - oldX) * 0.7f
            rot += delta.toInt()
            oldX = e.x
            //oldY = e.getY();

            drawMeLikeOneOfYourIntersections()
        }
        return true
    }
}