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

import com.spiraclestudios.autoskola.R;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by benji on 17/11/2015.
 */
public class AdsSlide extends Fragment {

    @Bind(R.id.gender)
    Spinner gender;
    @Bind(R.id.birth_year)
    EditText birth_year;
    @Bind(R.id.save)
    Button save;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.slide_ads, container, false);
        ButterKnife.bind(this, view);

        // Restore last choices from SharedPreferences
        SharedPreferences prefs = PreferenceManager
                .getDefaultSharedPreferences(getActivity().getApplicationContext());

        gender.setSelection(Integer.parseInt(prefs.getString("user_gender", "0")));

        if (prefs.contains("user_birth_year")) {
            birth_year.setText(prefs.getString("user_birth_year", "1998"));
        }

        return view;
    }

    @OnClick(R.id.save)
    public void save_onClick() {
        Resources res = getResources();
        int Gender = gender.getSelectedItemPosition();
        String BirthYear = birth_year.getText().toString();

        SharedPreferences prefs = PreferenceManager
                .getDefaultSharedPreferences(getActivity().getApplicationContext());

        prefs.edit().putString("user_gender", Integer.toString(Gender)).apply();

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
            prefs.edit().putString("user_birth_year", BirthYear).apply();
        } else {
            // If user chose to provide no year, delete the pref
            prefs.edit().remove("user_birth_year").apply();
        }

        // Hide the keyboard
        InputMethodManager imm = (InputMethodManager) getActivity()
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(save.getWindowToken(), 0);

        Toast.makeText(getContext(), res.getString(R.string.toast_ads_saved),
                Toast.LENGTH_SHORT).show();
    }
}
