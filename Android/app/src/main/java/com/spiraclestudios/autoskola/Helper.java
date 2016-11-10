/*
 * Copyright (c) 2015-2016. Spiracle Studios. All Rights Reserved.
 */

package com.spiraclestudios.autoskola;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.provider.Settings;
import android.text.format.DateUtils;
import android.view.View;

import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.ShareEvent;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.regex.Pattern;

import io.palaima.debugdrawer.DebugDrawer;
import io.palaima.debugdrawer.commons.BuildModule;
import io.palaima.debugdrawer.commons.DeviceModule;
import io.palaima.debugdrawer.commons.SettingsModule;
import io.palaima.debugdrawer.timber.TimberModule;
import io.palaima.debugdrawer.timber.util.Intents;
import timber.log.Timber;

import static java.security.AccessController.getContext;

/**
 * Added by benji on 14/10/2015.
 */
public class Helper {

    // [Social Links]
    // TODO: Update webURL once I change company name / get website.
    public static final String webURL = "http://spiraclestudios.com";
    public static final String facebookURL = "https://facebook.com/spiraclestudios";
    public static final String twitterURL = "https://twitter.com/SpiracleStudios";
    public static final String youtubeURL = "https://youtube.com/channel/UCYF2X1mTodkkRkKTp0ER2aw";
    public static final String googlePlayPublisherURL = "http://play.google.com/store/search?q=pub:Spiracle%20Studios";
    public static final String googlePlayShortURL = "http://goo.gl/5lv9Gv";
    public static final String googlePlayURL = "http://play.google.com/store/apps/details?id=com.spiraclestudios.autoskola";
    public static final String googlePlayMarketURL = "market://details?id=com.spiraclestudios.autoskola";
    public static final String googlePlayPremiumMarketURL = "market://details?id=com.spiraclestudios.autoskola.premium";
    public static final String googlePlayPremiumURL = "http://play.google.com/store/apps/details?id=com.spiraclestudios.autoskola.premium";

    public enum Groups {
        AB, CDT
    }

    public static boolean demoMode = false;
    public static int themeResId = R.style.MyTheme_Light;
    private static Context mApplicationContext;

    public static void setDemoMode(boolean value) {
        demoMode = value;
    }

    public static DebugDrawer initializeDebugDrawer(Activity context) {
        return new DebugDrawer.Builder(context)
                .modules(
                        new TimberModule(),
                        new DeviceModule(context),
                        new BuildModule(context),
                        new SettingsModule(context)
                ).build();
    }

    // Returns 0 (A,B) if index is 1-35 and 1 (C,D,T) if index is greater than 35
    public static Groups getGroupFromTestIndex(int index) {
        return (index > 35) ? Groups.CDT : Groups.AB;
    }

    public static boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getApplicationContext().
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

    public static boolean isValidEmail(String emailAddress) {
        // Source: http://howtodoinjava.com/2014/11/11/java-regex-validate-email-address/
        String regex = "^[\\w!#$%&'*+/=?`{|}~^-]+(?:\\.[\\w!#$%&'*+/=?`{|}~^-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,6}$";
        Pattern pattern = Pattern.compile(regex);
        return pattern.matcher(emailAddress).matches();
    }

    /**
     * Combines the first name and last name together.
     */
    public static String getFullName(String firstName, String lastName) {
        String userFullName = firstName;
        if (!lastName.isEmpty()) {
            if (!firstName.isEmpty()) {
                userFullName += " ";
            }
            userFullName += lastName;
        }

        return userFullName;
    }

    public static void ShareTest(Context activityContext, int testId, int points, int maxPoints, int amountCorrect, int amountIncorrect, int amountUnanswered, long elapsedTime) {
        Resources res = getApplicationContext().getResources();

        Timber.d("ShareTest: amountIncorrect %d, amountUnanswered %d", amountIncorrect, amountUnanswered);

        String shareText = String.format(Locale.ENGLISH, res.getString(R.string.results_share_action_text), testId) + "\n\n" +
                String.format(Locale.ENGLISH, "%s: %d/%d", res.getString(R.string.results_points), points, maxPoints) + "\n" +
                String.format(Locale.ENGLISH, "%s: %d", res.getString(R.string.results_correct), amountCorrect) + "\n" +
                String.format(Locale.ENGLISH, "%s: %s", res.getString(R.string.results_incorrect), amountIncorrect) + "\n";
                /*if (amountUnanswered > 0) {
                    shareText += String.format(Locale.ENGLISH, "%s: %d", res.getString(R.string.results_unanswered), amountUnanswered) + "\n";
                }*/

        shareText += String.format(Locale.ENGLISH, "%s: %s", res.getString(R.string.results_time), DateUtils.formatElapsedTime(elapsedTime / 1000));
        shareText += String.format(Locale.ENGLISH, "\n\n" + res.getString(R.string.results_download_link), Helper.googlePlayShortURL);

        Intent sendIntent = new Intent(Intent.ACTION_SEND);
        sendIntent.setType("text/plain");
        sendIntent.putExtra(Intent.EXTRA_SUBJECT, res.getString(R.string.results_share_action_subject));
        sendIntent.putExtra(Intent.EXTRA_TEXT, shareText);
        Intents.maybeStartActivity(activityContext, sendIntent);

        Answers.getInstance().logShare(new ShareEvent().putMethod("Results"));
    }

    /**
     * Evaluates the scored points and elapsed time and returns success status.
     * @param points Scored points.
     * @param elapsedTime Time taken to complete the test.
     * @return Would the user with this score and time pass the test?
     */
    public static boolean getTestSuccessful(int points, long elapsedTime) {
        return points >= 50 && (elapsedTime / 1000) / 60 <= 20;
    }

    public static String getTranslatedBoolean(boolean bool) {
        Resources res = getApplicationContext().getResources();
        return bool ? res.getString(R.string.yes) : res.getString(R.string.no);
    }

    public static Context getApplicationContext() {
        return mApplicationContext;
    }

    /**
     * Called by the Application class.
     *
     * @param application The application context.
     */
    public static void setApplicationContext(Application application) {
        mApplicationContext = application;
    }

    public static void loadAd(final AdView adView) {
        // Don't show ads in premium builds and in demo mode.
        if (BuildConfig.PREMIUM || demoMode) {
            adView.setVisibility(View.GONE);
            return;
        }

        //ConnectionBuddy.getInstance().registerForConnectivityEvents(context, helper);

        if (isOnline()) {
            SharedPreferences prefs = getApplicationContext().getSharedPreferences(G.PREFS_GENERIC, Context.MODE_PRIVATE);

            AdRequest.Builder builder = new AdRequest.Builder()
                    .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                    .addTestDevice("3CF9408FED195A254A8CCF7A72623E63") // LG G5
                    .addTestDevice("B0FF4D1DC8FED5463A805EA5860E577C") // Galaxy S3 Mini
                    .addTestDevice("BD637FC4B0D81AC763666E47BA737F75"); // Asus MemoPad 10

            // Ad Targeting
            builder.setGender(prefs.getInt("user_gender", 0));

            if (prefs.contains("user_birth_year")) {
                builder.setBirthday(new GregorianCalendar(
                        prefs.getInt("user_birth_year", 1998), 1, 1).getTime());
            }

            adView.loadAd(builder.build());
            adView.setVisibility(View.VISIBLE);
        } else {
            // TODO: Implement proper network check for ads
            // If there is no internet connection, check every 30 seconds if connection changed
            Handler handler = new Handler();
            Runnable runnable = new Runnable() {
                public void run() {

                    loadAd(adView);
                }
            };
            handler.postDelayed(runnable, 30000);
            adView.setVisibility(View.GONE);
        }
    }

    /*@Override
  public void onStop() {
        super.onStop();
        ConnectionBuddy.getInstance().unregisterFromConnectivityEvents(this);
    }

    @Override
    public void onConnectionChange(ConnectifyEvent event) {
        if (event.getState() == ConnectionBuddy.CONNECTED) {
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
            ConnectionBuddyCache.clearLastNetworkState(this);
        }
    }*/

    /**
     * Handle changing of themes.
     */
    public static void setTheme(Context context) {
        SharedPreferences prefsSettings = context.getSharedPreferences(G.PREFS_SETTINGS, Context.MODE_PRIVATE);
        boolean nightMode = prefsSettings.getBoolean("night_mode", false);
        boolean amoledMode = prefsSettings.getBoolean("amoled_mode", false);

        if (nightMode) {
            if (amoledMode) {
                context.setTheme(R.style.MyTheme_Dark_AMOLED);
                themeResId = R.style.MyTheme_Dark_AMOLED;
            } else {
                context.setTheme(R.style.MyTheme_Dark);
                themeResId = R.style.MyTheme_Dark;
            }
        } else {
            context.setTheme(R.style.MyTheme_Light);
            themeResId = R.style.MyTheme_Light;
        }
    }
}
