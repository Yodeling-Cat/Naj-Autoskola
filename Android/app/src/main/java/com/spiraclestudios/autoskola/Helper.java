// Copyright (c) 2015-2017. Spiracle Software. All Rights Reserved.

package com.spiraclestudios.autoskola;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.format.DateUtils;
import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.ShareEvent;
import java.util.Locale;
import java.util.regex.Pattern;
import timber.log.Timber;

/**
 * Added by benji on 14/10/2015.
 */
public class Helper {

  // Increment when the Glide disk cache needs to be invalidated after image assets were updated.
  public static final int GLIDE_DISK_CACHE_VERSION = 1;

  // [Social Links]
  // TODO: Update webURL once I change company name / get website.
  public static final String webURL = "http://spiraclestudios.com";
  public static final String facebookURL = "https://facebook.com/spiraclestudios";
  public static final String twitterURL = "https://twitter.com/SpiracleStudios";
  public static final String youtubeURL = "https://youtube.com/channel/UCYF2X1mTodkkRkKTp0ER2aw";
  public static final String googlePlayPublisherURL =
      "http://play.google.com/store/search?q=pub:Spiracle%20Studios";
  public static final String googlePlayShortURL = "http://goo.gl/5lv9Gv";
  public static final String googlePlayURL =
      "http://play.google.com/store/apps/details?id=com.spiraclestudios.autoskola";
  public static final String googlePlayMarketURL =
      "market://details?id=com.spiraclestudios.autoskola";
  public static final String googlePlayPremiumMarketURL =
      "market://details?id=com.spiraclestudios.autoskola.premium";
  public static final String googlePlayPremiumURL =
      "http://play.google.com/store/apps/details?id=com.spiraclestudios.autoskola.premium";

  public enum Groups {
    AB, CDT
  }

  private static Context mApplicationContext;

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
    String regex =
        "^[\\w!#$%&'*+/=?`{|}~^-]+(?:\\.[\\w!#$%&'*+/=?`{|}~^-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,6}$";
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

  public static void shareTest(Context activityContext, int testId, int points, int maxPoints,
      int amountCorrect, int amountIncorrect, int amountUnanswered, long elapsedTime) {
    Resources res = getApplicationContext().getResources();

    Timber.d("ShareTest: amountIncorrect %d, amountUnanswered %d", amountIncorrect,
        amountUnanswered);

    String shareText =
        String.format(Locale.ENGLISH, res.getString(R.string.results_share_action_text), testId)
            + "\n\n"
            +
            String.format(Locale.ENGLISH, "%s: %d/%d", res.getString(R.string.results_points),
                points, maxPoints)
            + "\n"
            +
            String.format(Locale.ENGLISH, "%s: %d", res.getString(R.string.results_correct),
                amountCorrect)
            + "\n"
            +
            String.format(Locale.ENGLISH, "%s: %s", res.getString(R.string.results_incorrect),
                amountIncorrect)
            + "\n";
                /*if (amountUnanswered > 0) {
                    shareText += String.format(Locale.ENGLISH, "%s: %d", res.getString(R.string.results_unanswered), amountUnanswered) + "\n";
                }*/

    shareText += String.format(Locale.ENGLISH, "%s: %s", res.getString(R.string.results_time),
        DateUtils.formatElapsedTime(elapsedTime / 1000));
    shareText +=
        String.format(Locale.ENGLISH, "\n\n" + res.getString(R.string.results_download_link),
            Helper.googlePlayShortURL);

    Intent sendIntent = new Intent(Intent.ACTION_SEND);
    sendIntent.setType("text/plain");
    sendIntent.putExtra(Intent.EXTRA_SUBJECT, res.getString(R.string.results_share_action_subject));
    sendIntent.putExtra(Intent.EXTRA_TEXT, shareText);
    activityContext.startActivity(sendIntent);

    Answers.getInstance().logShare(new ShareEvent().putMethod("Results"));
  }

  /**
   * Evaluates the scored points and elapsed time and returns success status.
   *
   * @param points Scored points.
   * @param elapsedTime Time taken to complete the test.
   * @return Would the user with this score and time pass the test?
   */
  public static boolean getTestSuccessful(int points, long elapsedTime) {
    return points >= 50 && (elapsedTime / 1000) / 60 <= 20;
  }

  public static String getTranslatedBoolean(boolean bool) {
    Resources res = getApplicationContext().getResources();
    return bool ? res.getString(R.string.generic_phrase__yes) : res.getString(R.string.generic_phrase__no);
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
}
