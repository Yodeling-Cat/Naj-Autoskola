package com.spiraclestudios.autoskola.Intros;

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
import android.widget.TextView;
import android.widget.Toast;

import com.google.ads.AdRequest;
import com.spiraclestudios.autoskola.R;
import com.zplesac.connectifty.Connectify;
import com.zplesac.connectifty.cache.ConnectifyCache;
import com.zplesac.connectifty.interfaces.ConnectivityChangeListener;
import com.zplesac.connectifty.models.ConnectifyEvent;
import com.zplesac.connectifty.models.ConnectifyState;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;

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
        return view;
    }

    // TODO: Add a EditText onChanged listener and verify if the date is correct
    @OnClick(R.id.save)
    public void save_onClick() {
        Resources res = getResources();
        int Gender = gender.getSelectedItemPosition();
        String BirthYear = birth_year.getText().toString();

        SharedPreferences prefs = PreferenceManager
                .getDefaultSharedPreferences(getActivity().getApplicationContext());

        prefs.edit().putInt("user_gender", Gender).apply();

        if (!TextUtils.isEmpty(BirthYear)) {
            if (birth_year.getText().length() < 4) {
                birth_year.setError("Too short");
                return;
            }
            prefs.edit().putInt("user_birth_year", Integer.parseInt(BirthYear)).apply();
        }

        // Hide the keyboard
        InputMethodManager imm = (InputMethodManager) getActivity()
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(save.getWindowToken(), 0);

        // Toast.makeText(getContext(), res.getString(R.string.toast_subscribe_subscribing),
        // Toast.LENGTH_SHORT).show();
        Toast.makeText(getContext(), "Gender: " + Gender + ", Birth Year: " + BirthYear,
                Toast.LENGTH_LONG).show();
    }

    // TODO: MOVE ALL THIS LOGIC INTO THE SAVE BUTTON
    @OnTextChanged(R.id.birth_year)
    void birth_year_onTextChanged(CharSequence text) {
        if (!TextUtils.isEmpty(text)) {
            int BirthYear = Integer.parseInt(text.toString());
            if (BirthYear > 2015) {
                birth_year.setError("Too high");
                //birth_year.setError(getResources().getString(R.string.error_enter_an_email));
            } else if (BirthYear < 1942) {
                birth_year.setError("Too low");
            } else if (text.length() > 4) {
                birth_year.setError("Too long");
            } else {
                birth_year.setError(null);
            }
        }
    }
}
