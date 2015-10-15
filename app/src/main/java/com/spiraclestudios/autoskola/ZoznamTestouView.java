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
import android.widget.TextView;

/**
 * TODO: document your custom view class.
 */
public class ZoznamTestouView extends CardView {
    public TextView title_text;
    public ImageView title_icon;
    public LinearLayout content;
    public ImageButton expand_icon;

    public enum Skupiny {
        AB,
        CDT
    }

    private Skupiny mAttrSkupina;

    public ZoznamTestouView(Context context) {
        super(context);
        //init();
    }

    public ZoznamTestouView(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.ZoznamTestouView,
                0, 0);

        try {
            mAttrSkupina = Skupiny.values()[a.getInteger(R.styleable.ZoznamTestouView_skupina, 1)];
        } finally {
            a.recycle();
        }


        // [Init]
        inflate(getContext(), R.layout.zoznam_testou_view, this);
        title_text = (TextView) findViewById(R.id.title_text);
        title_icon = (ImageView) findViewById(R.id.title_icon);
        content = (LinearLayout) findViewById(R.id.content);
        expand_icon = (ImageButton) findViewById(R.id.expand_icon);

        Resources resources = getResources();
        String title = "Title";
        Drawable icon = null;

        if (mAttrSkupina != null) {
            switch (mAttrSkupina) {
                case AB:
                    title = resources.getString(R.string.skupina_ab);
                    icon = ContextCompat.getDrawable(context, R.drawable.ic_directions_car_black_24dp);
                    break;
                case CDT:
                    title = resources.getString(R.string.skupina_cdt);
                    icon = ContextCompat.getDrawable(context, R.drawable.ic_local_shipping_black_24dp);
                    break;
            }

            title_text.setText(title);
            title_icon.setImageDrawable(icon);
        }

        expand_icon.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                content.setVisibility(content.isShown() ? GONE : VISIBLE);
            }
        });
    }

    // TODO: Implement based on the android studio View template
    public ZoznamTestouView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        //init();
    }

//    private void init() {
//    }
}