/*
 * Copyright (c) 2015-2016. Spiracle Studios. All Rights Reserved.
 */

package com.spiraclestudios.autoskola.activities;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.RingtonePreference;
import android.support.design.widget.AppBarLayout;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.analytics.HitBuilders;
import com.spiraclestudios.autoskola.AutoskolaApplication;
import com.spiraclestudios.autoskola.Helper;
import com.spiraclestudios.autoskola.R;
import com.spiraclestudios.autoskola.intros.IntroActivity;

import java.util.List;

import timber.log.Timber;

/**
 * A {@link PreferenceActivity} that presents a set of application settings. On handset devices,
 * settings are presented as a single list. On tablets, settings are split by category, with
 * category headers shown to the left of the list of settings.
 * <p/>
 * See <a href="http://developer.android.com/design/patterns/settings.html"> Android Design:
 * Settings</a> for design guidelines and the <a href="http://developer.android.com/guide/topics/ui/settings.html">Settings
 * API Guide</a> for more information on developing a Settings UI.
 */
public class SettingsActivity extends AppCompatPreferenceActivity
{

  private static boolean needsRestart = false;
  /**
   * A preference value change listener that updates the preference's summary to reflect its new
   * value.
   */
  private static Preference.OnPreferenceChangeListener sOnPreferenceChangeListener = new Preference.OnPreferenceChangeListener()
  {
    @Override
    public boolean onPreferenceChange(Preference preference, Object value)
    {

      String stringValue = value.toString();

      // Set summaries
      if (preference instanceof ListPreference) {
        // For list preferences, look up the correct display value in
        // the preference's 'entries' list.
        ListPreference listPreference = (ListPreference) preference;
        int index = listPreference.findIndexOfValue(stringValue);

        // Set the summary to reflect the new value.
        preference.setSummary(
            index >= 0
                ? listPreference.getEntries()[index]
                : null);

      } else if (preference instanceof RingtonePreference) {
        // For ringtone preferences, look up the correct display value
        // using RingtoneManager.
        if (TextUtils.isEmpty(stringValue)) {
          // Empty values correspond to 'silent' (no ringtone).
          preference.setSummary(R.string.pref_ringtone_silent);

        } else {
          Ringtone ringtone = RingtoneManager.getRingtone(
              preference.getContext(), Uri.parse(stringValue));

          if (ringtone == null) {
            // Clear the summary if there was a lookup error.
            preference.setSummary(null);
          } else {
            // Set the summary to reflect the new ringtone display
            // name.
            String name = ringtone.getTitle(preference.getContext());
            preference.setSummary(name);
          }
        }

      } else {
        // For all other preferences, set the summary to the value's
        // simple string representation.
        preference.setSummary(stringValue);
      }
      return true;
    }
  };
  public String mActivityName = "SettingsActivity";

  /**
   * Helper method to determine if the device has an extra-large screen. For example, 10" tablets
   * are extra-large.
   */
  private static boolean isXLargeTablet(Context context)
  {

    return (context.getResources().getConfiguration().screenLayout
        & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_XLARGE;
  }

  /**
   * Binds a preference's summary to its value. More specifically, when the preference's value is
   * changed, its summary (line of text below the preference title) is updated to reflect the
   * value. The summary is also immediately updated upon calling this method. The exact display
   * format is dependent on the type of preference.
   *
   * @see #sOnPreferenceChangeListener
   */
  private static void sBindPreferenceSummaryToValue(Preference preference, String defaultValue)
  {
    // Set the listener to watch for value changes.
    preference.setOnPreferenceChangeListener(sOnPreferenceChangeListener);

    // Trigger the listener immediately with the preference's
    // current value.
    sOnPreferenceChangeListener.onPreferenceChange(preference,
        PreferenceManager
            .getDefaultSharedPreferences(preference.getContext())
            .getString(preference.getKey(), defaultValue));
  }

  @Override
  protected void onCreate(Bundle savedInstanceState)
  {

    Helper.setTheme(this);
    super.onCreate(savedInstanceState);

    // [SetUp Toolbar]
    LinearLayout root = (LinearLayout) findViewById(android.R.id.list)
        .getParent().getParent().getParent();
    AppBarLayout appBarLayout = (AppBarLayout) LayoutInflater.from(this)
        .inflate(R.layout.toolbar_settings, root, false);
    root.addView(appBarLayout, 0);

    Toolbar toolbar = (Toolbar) appBarLayout.findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);
    getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    Helper.initializeDebugDrawer(this);
  }

  @Override
  public void onResume()
  {

    super.onResume();

    Timber.i("Setting analytics tracker screen name: %s", mActivityName);
    Helper.getTracker().setScreenName(mActivityName);
    Helper.getTracker().send(new HitBuilders.ScreenViewBuilder().build());
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item)
  {

    int id = item.getItemId();
    if (id == android.R.id.home) {
      if (needsRestart) {
        needsRestart = false;
        ((AutoskolaApplication) getApplication()).restart();
      } else {
        finish();
      }
      return true;
    }
    return super.onOptionsItemSelected(item);
  }

  @Override
  public void onBackPressed()
  {

    if (needsRestart) {
      needsRestart = false;
      ((AutoskolaApplication) getApplication()).restart();
      return;
    }
    super.onBackPressed();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean onIsMultiPane()
  {

    return isXLargeTablet(this);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  @TargetApi(Build.VERSION_CODES.HONEYCOMB)
  public void onBuildHeaders(List<Header> target)
  {

    loadHeadersFromResource(R.xml.pref_headers, target);
  }

  @Override
  public void onHeaderClick(Header header, int position)
  {

    if (header.id == R.id.subscription_and_about_you) {
      // Start the IntroActivity
      Intent intent = new Intent(this, IntroActivity.class);
      startActivity(intent);
    } else {
      super.onHeaderClick(header, position);
    }
  }

  /**
   * This method stops fragment injection in malicious applications. Make sure to deny any unknown
   * fragments here. Note: Only runs on API 19 (KitKat) and up.
   */
  @Override
  protected boolean isValidFragment(String fragmentName)
  {

    return PreferenceFragment.class.getName().equals(fragmentName)
        || GeneralPreferenceFragment.class.getName().equals(fragmentName)
        || AppearancePreferenceFragment.class.getName().equals(fragmentName)
        || SubscriptionAndAboutYouPreferenceFragment.class.getName().equals(fragmentName);
  }

  /**
   * This fragment shows general preferences only. It is used when the activity is showing a
   * two-pane settings UI.
   */
  @TargetApi(Build.VERSION_CODES.HONEYCOMB)
  public static class GeneralPreferenceFragment extends PreferenceFragment
  {

    boolean cdtMainGroupOld;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {

      super.onCreate(savedInstanceState);
      addPreferencesFromResource(R.xml.pref_general);
      //setHasOptionsMenu(true);

      Preference cdtMainGroup = findPreference("cdt_main_group");

      SharedPreferences prefs = PreferenceManager
          .getDefaultSharedPreferences(getActivity().getApplicationContext());

      // Cache the state of prefs they had on create.
      cdtMainGroupOld = prefs.getBoolean(cdtMainGroup.getKey(), false);

      // Set onClickListeners
      Preference.OnPreferenceChangeListener listener = new Preference.OnPreferenceChangeListener()
      {
        public boolean onPreferenceChange(Preference preference, Object newValue)
        {

          SharedPreferences prefs = PreferenceManager
              .getDefaultSharedPreferences(getActivity().getApplicationContext());

          boolean cdtMainGroupNew;
          boolean cdtMainGroupChanged = false;

          // Get current values of all variables.
          if (preference.getKey().equals("cdt_main_group")) {
            cdtMainGroupNew = (boolean) newValue;
            cdtMainGroupChanged = cdtMainGroupNew != cdtMainGroupOld;
          }

          needsRestart = cdtMainGroupChanged;
          return true;
        }
      };

      cdtMainGroup.setOnPreferenceChangeListener(listener);
    }
  }

  @TargetApi(Build.VERSION_CODES.HONEYCOMB)
  public static class AppearancePreferenceFragment extends PreferenceFragment
  {

    boolean nightModeOld;
    boolean amoledModeOld;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {

      super.onCreate(savedInstanceState);
      addPreferencesFromResource(R.xml.pref_appearance);
      //setHasOptionsMenu(true);

      Preference nightMode = findPreference("night_mode");
      Preference amoledMode = findPreference("amoled_mode");

      SharedPreferences prefs = PreferenceManager
          .getDefaultSharedPreferences(getActivity().getApplicationContext());

      // Cache the state of prefs they had on create.
      nightModeOld = prefs.getBoolean(nightMode.getKey(), false);
      amoledModeOld = prefs.getBoolean(amoledMode.getKey(), false);

      // Set onClickListeners
      Preference.OnPreferenceChangeListener listener = new Preference.OnPreferenceChangeListener()
      {
        public boolean onPreferenceChange(Preference preference, Object newValue)
        {

          SharedPreferences prefs = PreferenceManager
              .getDefaultSharedPreferences(getActivity().getApplicationContext());

          boolean nightModeNew;
          boolean amoledModeNew;

          // Get current values of all variables.
          // If we clicked on night_mode then we know its value and need to get the other var.
          if (preference.getKey().equals("night_mode")) {
            amoledModeNew = prefs.getBoolean("amoled_mode", false);
            nightModeNew = (boolean) newValue;
          } else {
            nightModeNew = prefs.getBoolean("night_mode", false);
            amoledModeNew = (boolean) newValue;
          }

          boolean nightChanged = nightModeNew != nightModeOld;
          boolean amoledChanged = amoledModeNew != amoledModeOld;
          needsRestart = (nightChanged || amoledChanged);
          return true;
        }
      };

      nightMode.setOnPreferenceChangeListener(listener);
      amoledMode.setOnPreferenceChangeListener(listener);
    }
  }

  @TargetApi(Build.VERSION_CODES.HONEYCOMB)
  public static class SubscriptionAndAboutYouPreferenceFragment extends PreferenceFragment
  {

    @Override
    public void onCreate(Bundle savedInstanceState)
    {

      super.onCreate(savedInstanceState);
      addPreferencesFromResource(R.xml.pref_subscription_and_about_you);
      //setHasOptionsMenu(true);

      Resources res = getResources();
      //SharedPreferences prefs = PreferenceManager
      //        .getDefaultSharedPreferences(this.getActivity().getApplicationContext());

      // Note: Integer prefs are stored as Strings.
      Preference email_address = findPreference("user_email_address");
      Preference first_name = findPreference("user_first_name");
      Preference last_name = findPreference("user_last_name");
      Preference subscribe = findPreference("subscribe");
      Preference gender = findPreference("user_gender");
      Preference birth_year = findPreference("user_birth_year");

      // Set preference summaries
      sBindPreferenceSummaryToValue(email_address, res.getString(R.string.pref_summary_email_address));
      sBindPreferenceSummaryToValue(first_name, res.getString(R.string.pref_summary_first_name));
      sBindPreferenceSummaryToValue(last_name, res.getString(R.string.pref_summary_last_name));
      sBindPreferenceSummaryToValue(gender, res.getString(R.string.hint_dont_want_to_provide));
      sBindPreferenceSummaryToValue(birth_year, res.getString(R.string.hint_dont_want_to_provide));

      // TODO: Set Crashlytics User Info like in SubscribeSlide
      // Preference.OnPreferenceChangeListener ?

      // Set onClickListeners
      Preference.OnPreferenceClickListener subscribe_onClick = new Preference
          .OnPreferenceClickListener()
      {
        public boolean onPreferenceClick(Preference preference)
        {

          if (Helper.isOnline()) {
            Toast.makeText(getActivity(), R.string.toast_not_yet_implemented, Toast.LENGTH_SHORT).show();
          } else {
            Toast.makeText(getActivity(), R.string.toast_connect_to_the_internet, Toast.LENGTH_SHORT).show();
          }
          return true;
        }
      };
      subscribe.setOnPreferenceClickListener(subscribe_onClick);
    }
  }

    /*@TargetApi(Build.VERSION_CODES.HONEYCOMB)
  public static class NotificationPreferenceFragment extends PreferenceFragment {

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_notification);
            setHasOptionsMenu(true);

            // Bind the summaries of EditText/List/Dialog/Ringtone preferences
            // to their values. When their values change, their summaries are
            // updated to reflect the new value, per the Android Design
            // guidelines.
            //bindPreferenceSummaryToValue(findPreference("notifications_new_message_ringtone"));
        }
    }*/

    /*@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	public static class DataSyncPreferenceFragment extends PreferenceFragment {

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_data_sync);
            setHasOptionsMenu(true);

            // Bind the summaries of EditText/List/Dialog/Ringtone preferences
            // to their values. When their values change, their summaries are
            // updated to reflect the new value, per the Android Design
            // guidelines.
            //bindPreferenceSummaryToValue(findPreference("sync_frequency"));
        }
    }*/
}
