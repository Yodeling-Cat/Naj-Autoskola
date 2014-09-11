package com.spiracle.paymentapp;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;

public class MyView2 extends View {

	public MyView2(Context context) {
		super(context);
	}

	public MyView2(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public MyView2(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}


	Rect ourRect = new Rect();
	Paint blue = new Paint();

	@Override
	protected void onDraw(Canvas canvas)
	{
		super.onDraw(canvas);

		ourRect.set(0, 0, canvas.getWidth(), canvas.getHeight()/2);

		blue.setColor(Color.BLUE);
		blue.setStyle(Paint.Style.FILL);

		canvas.drawRect(ourRect, blue);
	}
}
