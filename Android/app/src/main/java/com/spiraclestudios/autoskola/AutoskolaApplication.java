/*
 * Copyright 2015-2016 Spiracle Studios. All Rights Reserved.
 */

package com.spiraclestudios.autoskola;

import android.app.Application;
import android.content.Intent;
import android.os.StrictMode;

import com.crashlytics.android.Crashlytics;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;
import com.spiraclestudios.autoskola.activities.SettingsActivity;
import com.zplesac.connectifty.Connectify;
import com.zplesac.connectifty.ConnectifyConfiguration;

import io.fabric.sdk.android.Fabric;
import io.palaima.debugdrawer.log.data.LumberYard;
import timber.log.Timber;

/**
 * This is a subclass of {@link Application} used to provide shared objects for this app, such as
 * the {@link Tracker}.
 */
public class AutoskolaApplication extends Application {

    public static boolean STRICT_MODE = false;

    @Override
    public void onCreate() {
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

        LumberYard lumberYard = LumberYard.getInstance(this);
        lumberYard.cleanUp();

        Timber.plant(lumberYard.tree());
        Timber.plant(new Timber.DebugTree());

        // Initialize Crashlytics
        final Fabric fabric = new Fabric.Builder(this)
                .kits(new Crashlytics())
                .debuggable(true)
                .build();
        Fabric.with(fabric);

        // Initialize Google Analytics
        AnalyticsTrackers.initialize(this);

        // Initialize Connectify
        ConnectifyConfiguration connectifyConfiguration = new ConnectifyConfiguration.Builder(this)
                .build();
        Connectify.getInstance().init(connectifyConfiguration);
    }

    public void restart() {
        Intent intent = getBaseContext().getPackageManager()
                .getLaunchIntentForPackage(getBaseContext().getPackageName());
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}