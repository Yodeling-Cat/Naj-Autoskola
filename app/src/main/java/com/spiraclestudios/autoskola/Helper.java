package com.spiraclestudios.autoskola;

import android.content.Context;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.view.ViewConfiguration;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

/**
 * Created by benji on 14/10/2015.
 */
public class Helper {
    public static final String webURL = "http://spiraclestudios.com";
    public static final String facebookURL = "https://facebook.com/spiraclestudios";
    public static final String twitterURL = "https://twitter.com/SpiracleStudios";
    public static final String youtubeURL = "https://youtube.com/channel/UCYF2X1mTodkkRkKTp0ER2aw";
    public static final String googlePlayURL = "https://play.google.com/store/apps/developer?id=Spiracle+Studios";

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

    public static void loadAd(final Context context, final AdView adView) {
        if (demoMode) {
            return;
        }

        if (isOnline(context)) {
            AdRequest adRequest = new AdRequest.Builder()
                    // [Ben's Devices]
                    .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                    .addTestDevice("A053777425A9926103BE02DE879DA5A1") // Galaxy Note
                    .addTestDevice("B0FF4D1DC8FED5463A805EA5860E577C") // Galaxy S3 Mini
                    //TODO: .addTestDevice("") // Asus Memo Pad 10

                    // [Genymotion]
                    .addTestDevice("E4FAF36C23D3DD95FF1C53E4D55E81C7") // Google Nexus 5
                    .addTestDevice("205C906BD72C2FA820456260F0BE0FA5") // Samsung Galaxy S2
                    .addTestDevice("AF16642C9258EC370AE63F311FF12DCF") // Samsung Galaxy S3
                    .addTestDevice("A27D5C846DF77ECC008E8DC2A84D5DBA") // Custom Tablet
                    .build();
            adView.loadAd(adRequest);
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

    public static void setDemoMode(boolean value) {
        demoMode = value;
    }
}
