package com.spiraclestudios.autoskola;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * TODO: document your custom view class.
 */
public class ZoznamTestouEntry extends RelativeLayout {
    public TextView text;
    public ImageButton view_answers_icon;
    public ImageButton expand_history_icon;

    public int index;

    public ZoznamTestouEntry(Context context) {
        super(context);
        init();
    }

    public ZoznamTestouEntry(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    // TODO: Implement based on the android studio View template
    public ZoznamTestouEntry(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        inflate(getContext(), R.layout.zoznam_testou_entry, this);
        text = (TextView) findViewById(R.id.text);
        view_answers_icon = (ImageButton) findViewById(R.id.view_answers_icon);
        expand_history_icon = (ImageButton) findViewById(R.id.expand_history_icon);

        text.setText("#" + index + " - " + "x3");


        // [Set onClickListener's]
        expand_history_icon.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                //content.setVisibility(content.isShown() ? GONE : VISIBLE);
            }
        });

        view_answers_icon.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                //content.setVisibility(content.isShown() ? GONE : VISIBLE);
            }
        });
    }
}