package com.spiraclestudios.autoskola;

import android.content.Context;
import android.preference.PreferenceManager;
import android.view.View;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.analytics.Tracker;

/**
 * Created by benji on 14/10/2015.
 */
public class Helper {

    public static void loadAd(AdView adView) {
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .addTestDevice("A053777425A9926103BE02DE879DA5A1") // Galaxy Note
                .addTestDevice("B0FF4D1DC8FED5463A805EA5860E577C") // Galaxy S3 Mini
                .build();
        adView.loadAd(adRequest);
    }

    // Handle setting the night theme
    public static void setTheme(Context context) {
        if (PreferenceManager.getDefaultSharedPreferences(context)
                .getBoolean("night_theme_switch", false)) {
            if (PreferenceManager.getDefaultSharedPreferences(context)
                    .getBoolean("amoled_mode_switch", false))
                context.setTheme(R.style.MyTheme_Dark_AMOLED);
            else
                context.setTheme(R.style.MyTheme_Dark);
        } else
            context.setTheme(R.style.MyTheme_Light);
    }
}
