// Copyright (c) 2015-2017. Spiracle Software. All Rights Reserved.

package com.spiraclestudios.autoskola.billing.remove_ads;

import android.os.Handler;
import android.os.Looper;
import com.spiraclestudios.autoskola.BuildConfig;
import javax.annotation.Nonnull;
import org.solovyev.android.checkout.Checkout;
import org.solovyev.android.checkout.Inventory;
import org.solovyev.android.checkout.ProductTypes;

import static org.solovyev.android.checkout.ProductTypes.IN_APP;

public class RemoveAds {

  public static final String PRODUCT_REMOVE_ADS = "remove_ads";

  private final Handler handler;

  public RemoveAds() {
    this.handler = new Handler();
  }

  public void hasPurchasedRemoveAds(Checkout checkout, RemoveAdsCallback callback) {
    if (BuildConfig.PREMIUM) {
      callback.onRemoveAdsLoaded(true);
      return;
    }

    checkout.loadInventory(Inventory.Request.create().loadPurchases(ProductTypes.IN_APP),
        onMainThread(products -> callback.onRemoveAdsLoaded(
            products.get(IN_APP).isPurchased(PRODUCT_REMOVE_ADS))));
  }

  @Nonnull private Inventory.Callback onMainThread(@Nonnull final Inventory.Callback callback) {
    return products -> {
      if (handler.getLooper() == Looper.myLooper()) {
        callback.onLoaded(products);
        return;
      }
      handler.post(() -> callback.onLoaded(products));
    };
  }

  public interface RemoveAdsCallback {

    void onRemoveAdsLoaded(boolean isRemoveAdsPurchased);
  }
}
