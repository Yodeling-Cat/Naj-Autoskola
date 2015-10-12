package com.spiraclestudios.autoskola;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

/**
 * TODO: document your custom view class.
 */
public class CustomTestSelectionView extends CardView {
    public TextView title_text;
    public ImageView title_icon;
    public CheckBox otazky_checkbox;
    public CheckBox dopravne_znacky_checkbox;
    public CheckBox krizovatky_checkbox;
    public Spinner specificky_test_categories;
    public Spinner specificky_test_indexes;
    public Button vlastny_test_start;

    public CustomTestSelectionView(Context context) {
        super(context);
        init();
    }

    public CustomTestSelectionView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CustomTestSelectionView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        inflate(getContext(), R.layout.custom_test_selection_view, this);
        title_text = (TextView) findViewById(R.id.title_text);
        title_icon = (ImageView) findViewById(R.id.title_icon);
        otazky_checkbox = (CheckBox) findViewById(R.id.otazky_checkbox);
        dopravne_znacky_checkbox = (CheckBox) findViewById(R.id.dopravne_znacky_checkbox);
        krizovatky_checkbox = (CheckBox) findViewById(R.id.krizovatky_checkbox);
        specificky_test_categories = (Spinner) findViewById(R.id.specificky_test_categories);
        specificky_test_indexes = (Spinner) findViewById(R.id.specificky_test_indexes);
        vlastny_test_start = (Button) findViewById(R.id.vlastny_test_start);
    }
}