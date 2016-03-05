/*
 * Copyright (c) 2015-2016. Spiracle Studios. All Rights Reserved.
 */

package com.spiraclestudios.autoskola;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.view.View;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.analytics.Tracker;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.regex.Pattern;

import io.palaima.debugdrawer.DebugDrawer;
import io.palaima.debugdrawer.commons.BuildModule;
import io.palaima.debugdrawer.commons.DeviceModule;
import io.palaima.debugdrawer.commons.SettingsModule;
import io.palaima.debugdrawer.timber.TimberModule;

/**
 * Original created by benji on 14/10/2015.
 */
public class Helper
{
  // [Social Links]
  // TODO: Update webURL once I change company name / get website.
  public static final String webURL = "http://spiraclestudios.com";
  public static final String facebookURL = "https://facebook.com/spiraclestudios";
  public static final String twitterURL = "https://twitter.com/SpiracleStudios";
  public static final String youtubeURL = "https://youtube.com/channel/UCYF2X1mTodkkRkKTp0ER2aw";
  public static final String googlePlayURL = "http://play.google.com/store/search?q=pub:Spiracle%20Studios";

  public enum Groups
  {
    AB, CDT
  }

  public static boolean demoMode = false;
  public static int themeResId = R.style.MyTheme_Light;
  private static Context mApplicationContext;
  private static Tracker mTracker;

  public static Tracker getTracker()
  {
    if (mTracker == null) {
      mTracker = AnalyticsTrackers.getInstance().get(AnalyticsTrackers.Target.APP);
    }
    return mTracker;
  }

  public static void setDemoMode(boolean value)
  {
    demoMode = value;
  }

  public static DebugDrawer initializeDebugDrawer(Activity context)
  {
    return new DebugDrawer.Builder(context)
        .modules(
            new TimberModule(),
            new DeviceModule(context),
            new BuildModule(context),
            new SettingsModule(context)
        ).build();
  }

  // Returns 0 (A,B) if index is 1-35 and 1 (C,D,T) if index is greater than 35
  public static Groups getGroupFromTestIndex(int index)
  {
    return (index > 35) ? Groups.CDT : Groups.AB;
  }

  public static boolean isOnline()
  {
    ConnectivityManager cm = (ConnectivityManager) getApplicationContext().
        getSystemService(Context.CONNECTIVITY_SERVICE);
    NetworkInfo networkInfo = cm.getActiveNetworkInfo();
    return networkInfo != null && networkInfo.isConnected();
  }

  public static boolean isValidEmail(String emailAddress)
  {
    // Source: http://howtodoinjava.com/2014/11/11/java-regex-validate-email-address/
    String regex = "^[\\w!#$%&'*+/=?`{|}~^-]+(?:\\.[\\w!#$%&'*+/=?`{|}~^-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,6}$";
    Pattern pattern = Pattern.compile(regex);
    return pattern.matcher(emailAddress).matches();
  }

  /**
   * Combines the first name and last name together.
   */
  public static String getFullName(String firstName, String lastName)
  {
    String userFullName = firstName;
    if (!lastName.isEmpty()) {
      if (!firstName.isEmpty()) {
        userFullName += " ";
      }
      userFullName += lastName;
    }

    return userFullName;
  }

  public static String getTranslatedBoolean(boolean bool)
  {
    Resources res = getApplicationContext().getResources();
    return bool ? res.getString(R.string.yes) : res.getString(R.string.no);
  }

  public static Context getApplicationContext()
  {
    return mApplicationContext;
  }

  /**
   * Called by the Application class.
   *
   * @param application The application context.
   */
  public static void setApplicationContext(Application application)
  {
    mApplicationContext = application;
  }

  public static void loadAd(final AdView adView)
  {
    // Don't show ads in demo mode.
    if (demoMode) {
      return;
    }

    //ConnectionBuddy.getInstance().registerForConnectivityEvents(context, helper);

    if (isOnline()) {
      SharedPreferences prefs = PreferenceManager
          .getDefaultSharedPreferences(getApplicationContext());

      AdRequest.Builder builder = new AdRequest.Builder()
          // [Ben's Devices]
          .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
          .addTestDevice("0D620C4121D0B22F0AF6E438AD25D050") // LG G2
          .addTestDevice("B0FF4D1DC8FED5463A805EA5860E577C"); // Galaxy S3 Mini
      // TODO: ADD ASUS TABLET
      //.addTestDevice(""); // Asus Memo Pad 10

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
      Runnable runnable = new Runnable()
      {
        public void run()
        {

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
  public static void setTheme(Context context)
  {
    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
    boolean nightMode = prefs.getBoolean("night_mode", false);
    boolean amoledMode = prefs.getBoolean("amoled_mode", false);

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
