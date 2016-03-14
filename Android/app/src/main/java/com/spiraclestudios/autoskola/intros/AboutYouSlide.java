/*
 * Copyright (c) 2015-2016. Spiracle Studios. All Rights Reserved.
 */

package com.spiraclestudios.autoskola.intros;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.spiraclestudios.autoskola.G;
import com.spiraclestudios.autoskola.R;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Added by benji on 17/11/2015.
 */
public class AboutYouSlide extends Fragment {

    @Bind(R.id.gender)
    Spinner gender;
    @Bind(R.id.birth_year)
    EditText birth_year;
    @Bind(R.id.save)
    Button save;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.slide_about_you, container, false);
        ButterKnife.bind(this, view);

        // Restore last choices from SharedPreferences.
        SharedPreferences prefs = getActivity().getApplicationContext()
                .getSharedPreferences(G.PREFS_GENERIC, Context.MODE_PRIVATE);

        gender.setSelection(prefs.getInt("user_gender", 0));

        if (prefs.contains("user_birth_year")) {
            birth_year.setText(Integer.toString(prefs.getInt("user_birth_year", 1998)));
        }

        return view;
    }

    @OnClick(R.id.save)
    public void save_onClick() {
        Resources res = getResources();
        int Gender = gender.getSelectedItemPosition();
        String BirthYear = birth_year.getText().toString();

        SharedPreferences prefs = getActivity().getApplicationContext()
                .getSharedPreferences(G.PREFS_GENERIC, Context.MODE_PRIVATE);
        SharedPreferences.Editor prefsEdit = prefs.edit();

        prefsEdit.putInt("user_gender", Gender);

        // Check if year is valid
        if (!TextUtils.isEmpty(BirthYear)) {
            int year = Integer.parseInt(BirthYear);
            if (BirthYear.length() < 4
                    || year > 2010
                    || year < 1942) {
                birth_year.setError(res.getString(R.string.error_wrong_date));
                return;
            }
            birth_year.setError(null);
            prefsEdit.putInt("user_birth_year", year);
        } else {
            // If user chose to provide no year, delete the pref.
            prefsEdit.remove("user_birth_year");
        }
        prefsEdit.apply();

        // Hide the keyboard.
        InputMethodManager imm = (InputMethodManager) getActivity()
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(save.getWindowToken(), 0);

        Toast.makeText(getContext(), res.getString(R.string.toast_about_you_saved),
                Toast.LENGTH_SHORT).show();
    }
}
