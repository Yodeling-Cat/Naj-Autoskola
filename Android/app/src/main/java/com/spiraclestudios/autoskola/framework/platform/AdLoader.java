// Copyright (c) 2015-2017. Spiracle Software. All Rights Reserved.

package com.spiraclestudios.autoskola.framework.platform;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.View;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdRequest.Builder;
import com.google.android.gms.ads.AdView;
import com.spiraclestudios.autoskola.BuildConfig;
import com.spiraclestudios.autoskola.G;
import com.spiraclestudios.autoskola.Helper;
import java.util.GregorianCalendar;

public class AdLoader {

  public void loadAd(final AdView adView) {
    if (BuildConfig.PREMIUM) {
      hideAdView(adView);
      return;
    }

    AdRequestBuilder builder = new AdRequestBuilder();
    builder.addTestDevices();
    builder.setupAdTargeting();

    adView.loadAd(builder.build());
    showAdView(adView);
  }

  public void showAdView(final AdView adView) {
    adView.setVisibility(View.VISIBLE);
  }

  public void hideAdView(final AdView adView) {
    adView.setVisibility(View.GONE);
  }

  private class AdRequestBuilder {

    private Builder builder;

    AdRequestBuilder() {
      this.builder = new Builder();
    }

    public AdRequest build() {
      return builder.build();
    }

    void addTestDevices() {
      builder.addTestDevice(AdRequest.DEVICE_ID_EMULATOR);
      builder.addTestDevice("3CF9408FED195A254A8CCF7A72623E63"); // LG G5
      builder.addTestDevice("B0FF4D1DC8FED5463A805EA5860E577C"); // Galaxy S3 Mini
      builder.addTestDevice("BD637FC4B0D81AC763666E47BA737F75"); // Asus MemoPad 10
    }

    void setupAdTargeting() {
      SharedPreferences prefs = Helper.getApplicationContext()
          .getSharedPreferences(G.PREFS_GENERIC, Context.MODE_PRIVATE);

      /*if (prefs.contains("user_birth_year")) {
        builder.setBirthday(
            new GregorianCalendar(prefs.getInt("user_birth_year", 1998), 1, 1).getTime());
      }*/
    }
  }
}

