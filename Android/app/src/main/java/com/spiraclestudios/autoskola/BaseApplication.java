// Copyright (c) 2015-2017. Spiracle Software. All Rights Reserved.

package com.spiraclestudios.autoskola;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.StrictMode;
import com.bumptech.glide.Glide;
import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.core.CrashlyticsCore;
import com.facebook.stetho.Stetho;
import com.google.android.gms.ads.MobileAds;
import com.squareup.leakcanary.LeakCanary;
import com.squareup.leakcanary.RefWatcher;
import com.zplesac.connectionbuddy.ConnectionBuddy;
import com.zplesac.connectionbuddy.ConnectionBuddyConfiguration;
import io.fabric.sdk.android.Fabric;
import timber.log.Timber;

public class BaseApplication extends Application {

  public static boolean STRICT_MODE = false;
  private RefWatcher mRefWatcher;

  public static RefWatcher getRefWatcher(Context context) {
    BaseApplication application = (BaseApplication) context.getApplicationContext();
    return application.mRefWatcher;
  }

  @Override public void onCreate() {
    if (STRICT_MODE && BuildConfig.DEBUG) {
      enableStrictMode();
    }

    super.onCreate();
    Helper.setApplicationContext(this);
    initializeDependencies();

    clearGlideDiskCacheIfNeeded();
  }

  // TODO: Try starting main activity instead of splash activity?
  public void restart() {
    Intent intent = getBaseContext().getPackageManager()
        .getLaunchIntentForPackage(getBaseContext().getPackageName());
    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK
        | Intent.FLAG_ACTIVITY_NEW_TASK
        | Intent.FLAG_ACTIVITY_NO_ANIMATION);
    startActivity(intent);
  }

  private void clearGlideDiskCacheIfNeeded() {
    SharedPreferences prefs = getSharedPreferences(G.PREFS_GENERIC, MODE_PRIVATE);
    SharedPreferences.Editor prefsEdit = prefs.edit();

    if (prefs.getInt("glide_last_disk_cache_version", 0) != Helper.GLIDE_DISK_CACHE_VERSION) {
      Timber.d("Clearing Glide disk cache because GLIDE_DISK_CACHE_VERSION has changed.");

      Runnable runnable = new Runnable() {
        @Override public void run() {
          Glide.get(getApplicationContext()).clearDiskCache();
          //Glide.get(this).clearMemory();
        }
      };
      new Thread(runnable).start();

      prefsEdit.putInt("glide_last_disk_cache_version", Helper.GLIDE_DISK_CACHE_VERSION);
      prefsEdit.apply();
    }
  }

  private void enableStrictMode() {
    StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectDiskReads()
        .detectDiskWrites()
        .detectNetwork() // or .detectAll() for all detectable problems
        .penaltyLog()
        .penaltyFlashScreen()
        .build());
    StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder().detectLeakedSqlLiteObjects()
        .detectLeakedClosableObjects()
        .penaltyLog()
        .penaltyDeath()
        .build());
  }

  private void initializeDependencies() {
    initializeStetho();
    initializeTimber();
    initializeFabric();
    //initializeLeakCanary();
    initializeConnectionBuddy();
    initializeAdMob();
  }

  private void initializeFabric() {
    CrashlyticsCore core = new CrashlyticsCore.Builder().disabled(BuildConfig.DEBUG).build();
    Fabric.with(this, new Crashlytics.Builder().core(core).build(), new Answers());
  }

  private void initializeStetho() {
    Stetho.initializeWithDefaults(this);
  }

  private void initializeTimber() {
    if (timber.log.BuildConfig.DEBUG) {
      Timber.plant(new Timber.DebugTree());
    }
  }

  private void initializeConnectionBuddy() {
    ConnectionBuddyConfiguration conf = new ConnectionBuddyConfiguration.Builder(this).build();
    ConnectionBuddy.getInstance().init(conf);
  }

  private void initializeAdMob() {
    MobileAds.initialize(getApplicationContext(), getString(R.string.banner_ad_unit_id));
  }

  private void initializeLeakCanary() {
    mRefWatcher = LeakCanary.install(this);
  }
}