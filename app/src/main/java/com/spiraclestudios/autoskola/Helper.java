package com.spiraclestudios.autoskola;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

/**
 * Created by benji on 14/10/2015.
 */
public class Helper {
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

    public static void loadAd(Context context, AdView adView) {
        if (isOnline(context)) {
            AdRequest adRequest = new AdRequest.Builder()
                    .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                    .addTestDevice("A053777425A9926103BE02DE879DA5A1") // Galaxy Note
                    .addTestDevice("B0FF4D1DC8FED5463A805EA5860E577C") // Galaxy S3 Mini
                    .build();
            adView.loadAd(adRequest);
        }
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
