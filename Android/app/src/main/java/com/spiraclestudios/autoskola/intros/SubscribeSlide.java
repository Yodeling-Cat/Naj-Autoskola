/*
 * Copyright 2015-2016 Spiracle Studios. All Rights Reserved.
 */

package com.spiraclestudios.autoskola.intros;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.spiraclestudios.autoskola.Helper;
import com.spiraclestudios.autoskola.R;
import com.zplesac.connectionbuddy.ConnectionBuddy;
import com.zplesac.connectionbuddy.cache.ConnectionBuddyCache;
import com.zplesac.connectionbuddy.interfaces.ConnectivityChangeListener;
import com.zplesac.connectionbuddy.models.ConnectivityEvent;
import com.zplesac.connectionbuddy.models.ConnectivityState;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import timber.log.Timber;

/**
 * Original created by benji on 17/11/2015.
 */
public class SubscribeSlide extends Fragment implements ConnectivityChangeListener {

    @Bind(R.id.email_address)
    EditText email_address;
    @Bind(R.id.first_name)
    EditText first_name;
    @Bind(R.id.last_name)
    EditText last_name;
    @Bind(R.id.subscribe)
    Button subscribe;
    @Bind(R.id.connectivity_error)
    TextView connectivity_error;

    String emailAddress;
    String firstName;
    String lastName;

    @Override
    public void onStart() {
        super.onStart();
        ConnectionBuddy.getInstance().registerForConnectivityEvents(this, this);
    }

    @Override
    public void onStop() {
        super.onStop();
        ConnectionBuddy.getInstance().unregisterFromConnectivityEvents(this);
    }

    @Override
    public void onConnectionChange(ConnectivityEvent event) {
        if (event.getState() == ConnectivityState.CONNECTED) {
            connectivity_error.setVisibility(View.GONE);
        } else {
            connectivity_error.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            ConnectionBuddyCache.clearLastNetworkState(this);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.slide_subscribe, container, false);
        ButterKnife.bind(this, view);

        // Restore last choices from SharedPreferences
        SharedPreferences prefs = PreferenceManager
                .getDefaultSharedPreferences(getActivity().getApplicationContext());

        email_address.setText(prefs.getString("user_email_address", ""));
        first_name.setText(prefs.getString("user_first_name", ""));
        last_name.setText(prefs.getString("user_last_name", ""));

        return view;
    }

    class SubscribeUser extends AsyncTask<String, Void, String> {

        protected String doInBackground(String... params) {
            String emailAddress = params[0];
            String firstName = params[1];
            String lastName = params[2];

            // Send POST request to MailChimp
            HttpURLConnection urlConnection;
            String url = "https://us3.api.mailchimp.com/3.0/lists/eb68697832/members/";
            String result = null;
            try {
                // Build json object
                JSONObject json = new JSONObject();
                JSONObject merge_fields = new JSONObject();
                json.put("email_address", emailAddress);
                json.put("status", "subscribed");
                merge_fields.put("FNAME", firstName);
                merge_fields.put("LNAME", lastName);
                json.put("merge_fields", merge_fields);
                // TODO: If you start targeting more countries, change this hard-coded language
                json.put("language", "sk");

                // Connect
                urlConnection = (HttpURLConnection) ((new URL(url).openConnection()));
                urlConnection.setDoOutput(true);
                urlConnection.setRequestProperty("Content-Type", "application/json");
                urlConnection.setRequestProperty("Accept", "application/json");
                urlConnection.setRequestMethod("POST");
                String apiKey = ":" + getResources().getString(R.string.mailchimp_api_key);
                String basicAuth = "Basic " + new String(Base64.encode(apiKey.getBytes(),
                        Base64.NO_WRAP));
                urlConnection.setRequestProperty("Authorization", basicAuth);
                urlConnection.connect();

                // Write
                OutputStream outputStream = urlConnection.getOutputStream();
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(outputStream, "UTF-8"));
                writer.write(json.toString());
                writer.close();
                outputStream.close();

                // Read result
                BufferedReader bufferedReader = new BufferedReader(
                        new InputStreamReader(urlConnection.getInputStream(), "UTF-8"));
                String line;
                StringBuilder sb = new StringBuilder();

                while ((line = bufferedReader.readLine()) != null) {
                    sb.append(line);
                }

                bufferedReader.close();
                result = sb.toString();

            } catch (IOException | JSONException e) {
                e.printStackTrace();
                Crashlytics.logException(e);
            }
            return result;
        }

        protected void onPostExecute(String result) {
            Resources res = getResources();
            Crashlytics.setString("subscribe_result", result);

            if (result != null) {
                Toast.makeText(getContext(), res.getString(R.string.toast_subscribe_success),
                        Toast.LENGTH_SHORT).show();

                // Save the entered values into SharedPreferences
                SharedPreferences prefs = PreferenceManager
                        .getDefaultSharedPreferences(getActivity().getApplicationContext());
                SharedPreferences.Editor prefsEdit = prefs.edit();

                prefsEdit.putString("user_email_address", emailAddress);
                prefsEdit.putString("user_first_name", firstName);
                prefsEdit.putString("user_last_name", lastName);
                prefsEdit.apply();

                // Set Crashlytics user email and name.
                String fullName = Helper.getFullName(firstName, lastName);

                if (!emailAddress.isEmpty()) {
                    Crashlytics.setUserEmail(emailAddress);
                }
                if (!fullName.isEmpty()) {
                    Crashlytics.setUserName(fullName);
                }

                Timber.d("Crashlytics user info:\n->Email: %s\n->Name: %s", emailAddress, fullName);
            } else {
                Toast.makeText(getContext(), res.getString(R.string.toast_subscribe_failure),
                        Toast.LENGTH_LONG)
                        .show();
            }
        }
    }

    @OnClick(R.id.subscribe)
    public void subscribe_onClick() {
        Resources res = getResources();

        if (!ConnectionBuddy.getInstance().hasNetworkConnection()) {
            Toast.makeText(getContext(), res.getString(R.string.error_connect_to_the_internet),
                    Toast.LENGTH_SHORT).show();
            return;
        }

        emailAddress = email_address.getText().toString().trim();
        firstName = first_name.getText().toString().trim();
        lastName = last_name.getText().toString().trim();

        if (TextUtils.isEmpty(emailAddress)) {
            email_address.setError(res.getString(R.string.error_enter_an_email));
            return;
        }

        if (!Helper.isValidEmail(emailAddress)) {
            email_address.setError(res.getString(R.string.error_invalid_email));
            return;
        }

        // Hide the keyboard
        InputMethodManager imm = (InputMethodManager) getActivity()
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(subscribe.getWindowToken(), 0);

        new SubscribeUser().execute(emailAddress, firstName, lastName);
        Toast.makeText(getContext(), res.getString(R.string.toast_subscribe_subscribing),
                Toast.LENGTH_SHORT).show();
    }

    @OnTextChanged(R.id.email_address)
    void email_address_onTextChanged(CharSequence text) {
        if (!TextUtils.isEmpty(text)) {
            email_address.setError(null);
        }
    }
}
