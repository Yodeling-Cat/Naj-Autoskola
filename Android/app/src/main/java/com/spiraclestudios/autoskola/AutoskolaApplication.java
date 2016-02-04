/*
 * Copyright 2015-2016 Spiracle Studios. All Rights Reserved.
 */

package com.spiraclestudios.autoskola;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.StrictMode;
import android.preference.PreferenceManager;

import com.crashlytics.android.Crashlytics;
import com.google.android.gms.analytics.Tracker;
import com.squareup.leakcanary.LeakCanary;
import com.squareup.leakcanary.RefWatcher;
import com.zplesac.connectionbuddy.ConnectionBuddy;
import com.zplesac.connectionbuddy.ConnectionBuddyConfiguration;

import java.util.Map;
import java.util.Objects;

import io.fabric.sdk.android.Fabric;
import io.palaima.debugdrawer.timber.data.LumberYard;
import timber.log.Timber;

/**
 * This is a subclass of {@link Application} used to provide shared objects for this app, such as
 * the {@link Tracker}.
 */
public class AutoskolaApplication extends Application {

    public static final boolean STRICT_MODE = false;
    private RefWatcher refWatcher;

    @Override
    public void onCreate() {
        // Enable Strict Mode
        if (BuildConfig.DEBUG && STRICT_MODE) {
            StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                    .detectDiskReads()
                    .detectDiskWrites()
                    .detectNetwork()   // or .detectAll() for all detectable problems
                    .penaltyLog()
                    .penaltyFlashScreen()
                    .build());
            StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
                    .detectLeakedSqlLiteObjects()
                    .detectLeakedClosableObjects()
                    .penaltyLog()
                    .penaltyDeath()
                    .build());
        }

        super.onCreate();

        // Initialize Timber
        LumberYard lumberYard = LumberYard.getInstance(this);
        lumberYard.cleanUp();
        Timber.plant(lumberYard.tree());
        Timber.plant(new Timber.DebugTree());

        // Initialize Leak Canary
        //refWatcher = LeakCanary.install(this);

        // Initialize Crashlytics
        final Fabric fabric = new Fabric.Builder(this)
                .kits(new Crashlytics())
                .debuggable(true)
                .build();
        Fabric.with(fabric);

        // Initialize Google Analytics
        AnalyticsTrackers.initialize(this);

        // Initialize ConnectionBuddy
        ConnectionBuddyConfiguration connectionBuddyConfiguration = new ConnectionBuddyConfiguration.Builder(this)
                .build();
        ConnectionBuddy.getInstance().init(connectionBuddyConfiguration);

        // TODO: Remove the bad preferences fix at some point in the future.
        // Fix bad preferences.
        SharedPreferences prefs = PreferenceManager
                .getDefaultSharedPreferences(this);
        SharedPreferences.Editor prefsEdit = prefs.edit();

        Map<String, ?> prefsAll = prefs.getAll();
        Object userGender = prefsAll.get("user_gender");
        Object userBirthYear = prefsAll.get("user_birth_year");
        if (userGender != null) {
            if (userGender.getClass().getSimpleName().equals("String")) {
                prefsEdit.remove("user_gender");
                prefsEdit.putInt("user_gender", Integer.parseInt((String) userGender));
            }
        }
        if (userBirthYear != null) {
            if (userBirthYear.getClass().getSimpleName().equals("String")) {
                prefsEdit.remove("user_birth_year");
                prefsEdit.putInt("user_birth_year", Integer.parseInt((String) userBirthYear));
            }
        }
        prefsEdit.apply();
    }

    public void restart() {
        Intent intent = getBaseContext().getPackageManager()
                .getLaunchIntentForPackage(getBaseContext().getPackageName());
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    public static RefWatcher getRefWatcher(Context context) {
        AutoskolaApplication application = (AutoskolaApplication) context.getApplicationContext();
        return application.refWatcher;
    }
}