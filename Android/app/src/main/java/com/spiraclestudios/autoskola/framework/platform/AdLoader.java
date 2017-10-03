// Copyright (c) 2015-2017. Spiracle Software. All Rights Reserved.

package com.spiraclestudios.autoskola.framework.platform;

import android.view.View;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdRequest.Builder;
import com.google.android.gms.ads.AdView;

public class AdLoader {

  public AdLoader() {
  }

  public void loadAd(final AdView adView) {
    /*if (BuildConfig.PREMIUM) {
      hideAdView(adView);
      return;
    }*/

    AdRequestBuilder builder = new AdRequestBuilder();
    builder.addTestDevices();

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
    }
  }
}

