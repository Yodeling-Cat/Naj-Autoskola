package com.spiraclestudios.autoskola;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.CardView;
import android.util.AttributeSet;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

/**
 * TODO: document your custom view class.
 */
public class TestSelectionView extends CardView {
    public TextView title_text;
    public ImageView title_icon;

    private String mAttrTitle;
    private Drawable mAttrIcon;

    public TestSelectionView(Context context) {
        super(context);
        init();
    }

    public TestSelectionView(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.TestSelectionView,
                0, 0);

        try {
            mAttrTitle = a.getString(R.styleable.TestSelectionView_title_text);
            mAttrIcon = a.getDrawable(R.styleable.TestSelectionView_title_icon);
        } finally {
            a.recycle();
        }

        init();
    }

    // TODO: Implement based on the android studio View template
    public TestSelectionView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        inflate(getContext(), R.layout.test_selection_view, this);
        title_text = (TextView) findViewById(R.id.title_text);
        title_icon = (ImageView) findViewById(R.id.title_icon);

        if (mAttrTitle != null) {
            title_text.setText(mAttrTitle);
        }

        if (mAttrIcon != null) {
            title_icon.setImageDrawable(mAttrIcon);
        }
    }
}