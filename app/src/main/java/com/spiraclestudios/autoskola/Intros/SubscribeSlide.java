package com.spiraclestudios.autoskola.Intros;

import android.content.Context;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.spiraclestudios.autoskola.R;
import com.zplesac.connectifty.Connectify;
import com.zplesac.connectifty.ConnectifyConfiguration;
import com.zplesac.connectifty.cache.ConnectifyCache;
import com.zplesac.connectifty.interfaces.ConnectivityChangeListener;
import com.zplesac.connectifty.models.ConnectifyEvent;
import com.zplesac.connectifty.models.ConnectifyState;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.Authenticator;
import java.net.HttpURLConnection;
import java.net.PasswordAuthentication;
import java.net.URL;
import java.util.ArrayList;
import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;

/**
 * Created by benji on 17/11/2015.
 */
public class SubscribeSlide extends Fragment implements ConnectivityChangeListener {

    @Bind(R.id.email)
    EditText email;
    @Bind(R.id.first_name)
    EditText first_name;
    @Bind(R.id.last_name)
    EditText last_name;
    @Bind(R.id.subscribe)
    Button subscribe;
    @Bind(R.id.connectivity_error)
    TextView connectivity_error;

    @Override
    public void onStart() {
        super.onStart();
        Connectify.getInstance().registerForConnectivityEvents(this, this);
    }

    @Override
    public void onStop() {
        super.onStop();
        Connectify.getInstance().unregisterFromConnectivityEvents(this);
    }

    @Override
    public void onConnectionChange(ConnectifyEvent event) {
        if (event.getState() == ConnectifyState.CONNECTED) {
            subscribe.setEnabled(true);
            connectivity_error.setVisibility(View.GONE);
        } else {
            subscribe.setEnabled(false);
            connectivity_error.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            ConnectifyCache.clearLastNetworkState(this);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.slide_subscribe, container, false);
        ButterKnife.bind(this, view);
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
                // TODO: Hide the api-key
                String apikey = ":6a61d22ab226526f497bbcb890c0aa46-us3";
                String basicAuth = "Basic " + new String(Base64.encode(apikey.getBytes(),
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
                String line = null;
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
            if (result != null) {
                Toast.makeText(getContext(), res.getString(R.string.toast_subscribe_success),
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(), res.getString(R.string.toast_subscribe_failure),
                        Toast.LENGTH_LONG).show();
            }
        }
    }

    @OnClick(R.id.subscribe)
    public void subscribe_onClick() {
        Resources res = getResources();
        String emailAddress = email.getText().toString();
        String firstName = first_name.getText().toString();
        String lastName = last_name.getText().toString();

        if (TextUtils.isEmpty(emailAddress)) {
            email.setError(res.getString(R.string.error_enter_an_email));
            return;
        }

        // Hide the keyboard
        InputMethodManager imm = (InputMethodManager) getActivity()
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(subscribe.getWindowToken(), 0);

        new SubscribeUser().execute(emailAddress, firstName, lastName);
        Toast.makeText(getContext(), res.getString(R.string.toast_subscribe_subscribing), Toast.LENGTH_SHORT).show();
    }

    @OnTextChanged(R.id.email)
    void email_onTextChanged(CharSequence text) {
        if (TextUtils.isEmpty(text)) {
            email.setError(getResources().getString(R.string.error_enter_an_email));
        } else {
            email.setError(null);
        }
    }
}
