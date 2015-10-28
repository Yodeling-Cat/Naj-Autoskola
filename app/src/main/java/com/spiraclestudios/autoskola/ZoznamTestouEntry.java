package com.spiraclestudios.autoskola;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class ZoznamTestouEntry extends RelativeLayout {
    public TextView text;
    public ImageButton view_answers_icon;
    public ImageButton toggle_history_icon;
    public LinearLayout history;

    public int index;

    public ZoznamTestouEntry(Context context) {
        this(context, null);
    }

    public ZoznamTestouEntry(Context context, AttributeSet attrs) {
        this(context, attrs, 0);

    }

    public ZoznamTestouEntry(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        inflate(getContext(), R.layout.zoznam_testou_entry, this);
        text = (TextView) findViewById(R.id.text);
        view_answers_icon = (ImageButton) findViewById(R.id.view_answers_icon);
        toggle_history_icon = (ImageButton) findViewById(R.id.toggle_history_icon);
        history = (LinearLayout) findViewById(R.id.history);


        // [Set onClickListener's]
        toggle_history_icon.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (history.isShown()) {
                    Effects.slide_up(getContext(), history);
                    history.setVisibility(GONE);
                }
                else {
                    history.setVisibility(VISIBLE);
                    Effects.slide_down(getContext(), history);
                }
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