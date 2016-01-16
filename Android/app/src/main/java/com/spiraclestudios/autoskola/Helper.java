/*
 * Copyright 2015-2016 Spiracle Studios. All Rights Reserved.
 */

package com.spiraclestudios.autoskola;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.zplesac.connectionbuddy.ConnectionBuddy;
import com.zplesac.connectionbuddy.cache.ConnectionBuddyCache;
import com.zplesac.connectionbuddy.interfaces.ConnectivityChangeListener;
import com.zplesac.connectionbuddy.models.ConnectivityEvent;
import com.zplesac.connectionbuddy.models.ConnectivityState;

import java.util.GregorianCalendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Original created by benji on 14/10/2015.
 */
public class Helper {
    // [Social links]
    public static final String webURL = "http://spiraclestudios.com";
    public static final String facebookURL = "https://facebook.com/spiraclestudios";
    public static final String twitterURL = "https://twitter.com/SpiracleStudios";
    public static final String youtubeURL = "https://youtube.com/channel/UCYF2X1mTodkkRkKTp0ER2aw";
    public static final String googlePlayURL = "http://play.google.com/store/search?q=pub:Spiracle%20Studios";

    public static boolean demoMode = false;

    public enum Groups {
        AB,
        CDT
    }

    // Returns 0 (A,B) if index is 1-35 and 1 (C,D,T) if index is greater than 35
    public static Groups getGroupFromTestIndex(int index) {
        return (index > 35) ? Groups.CDT : Groups.AB;
    }

    public static boolean isOnline(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

    public static boolean isValidEmail(String emailAddress) {
        // Taken from http://howtodoinjava.com/2014/11/11/java-regex-validate-email-address/
        String regex = "^[\\w!#$%&'*+/=?`{|}~^-]+(?:\\.[\\w!#$%&'*+/=?`{|}~^-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,6}$";
        Pattern pattern = Pattern.compile(regex);
        return pattern.matcher(emailAddress).matches();
    }

    /*@Override
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
    }*/

    /*@Override
    public void onCreate(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            ConnectifyCache.clearLastNetworkState(this);
        }
    }*/

    public static void loadAd(final Context context, final AdView adView) {
        // Don't show ads in demo mode.
        if (demoMode) {
            return;
        }

        //Connectify.getInstance().registerForConnectivityEvents(context, helper);

        if (isOnline(context)) {
            SharedPreferences prefs = PreferenceManager
                    .getDefaultSharedPreferences(context.getApplicationContext());

            AdRequest.Builder builder = new AdRequest.Builder()
                    // [Ben's Devices]
                    .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                    .addTestDevice("0D620C4121D0B22F0AF6E438AD25D050") // LG G2
                    .addTestDevice("B0FF4D1DC8FED5463A805EA5860E577C"); // Galaxy S3 Mini
            // TODO: ADD ASUS TABLET
            //.addTestDevice(""); // Asus Memo Pad 10

            // Ad Targeting
            builder.setGender(Integer.parseInt(prefs.getString("user_gender", "0")));

            if (prefs.contains("user_birth_year")) {
                builder.setBirthday(new GregorianCalendar(Integer.parseInt(prefs
                        .getString("user_birth_year", "1998")), 1, 1).getTime());
            }

            adView.loadAd(builder.build());
        } else {
            // TODO: Implement proper network check for ads
            // If there is no internet connection, check every 30 seconds if connection changed
            Handler handler = new Handler();
            Runnable runnable = new Runnable() {
                public void run() {
                    loadAd(context, adView);
                }
            };
            handler.postDelayed(runnable, 30000);
        }
    }

    /**
     * Handle changing of themes.
     */
    public static void setTheme(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        boolean nightMode = prefs.getBoolean("night_mode", false);
        boolean amoledMode = prefs.getBoolean("amoled_mode", false);

        if (nightMode) {
            if (amoledMode) {
                context.setTheme(R.style.MyTheme_Dark_AMOLED);
            } else {
                context.setTheme(R.style.MyTheme_Dark);
            }
        } else {
            context.setTheme(R.style.MyTheme_Light);
        }
    }

    public static void setDemoMode(boolean value) {
        demoMode = value;
    }
}
