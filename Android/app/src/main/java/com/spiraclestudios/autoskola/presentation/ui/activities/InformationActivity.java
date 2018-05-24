// Copyright (c) 2015-2017. Spiracle Software. All Rights Reserved.

package com.spiraclestudios.autoskola.presentation.ui.activities;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import butterknife.BindView;
import butterknife.OnClick;
import butterknife.OnLongClick;
import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.PurchaseEvent;
import com.mikepenz.aboutlibraries.Libs.ActivityStyle;
import com.mikepenz.aboutlibraries.LibsBuilder;
import com.spiraclestudios.autoskola.BaseApplication;
import com.spiraclestudios.autoskola.BuildConfig;
import com.spiraclestudios.autoskola.R;
import com.spiraclestudios.autoskola.databinding.InformationActivityBinding;
import com.spiraclestudios.autoskola.databinding.InformationContentBinding;
import com.spiraclestudios.autoskola.framework.platform.RemoveAds;
import com.spiraclestudios.autoskola.framework.platform.StoreRating;
import com.spiraclestudios.autoskola.framework.presentation.ui.BaseActivity;
import com.spiraclestudios.autoskola.framework.presentation.ui.StandardActivity;
import com.spiraclestudios.autoskola.presentation.ui.dialogs.DevToolsDialog;
import com.zplesac.connectionbuddy.ConnectionBuddy;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Currency;
import javax.annotation.Nonnull;
import org.solovyev.android.checkout.ActivityCheckout;
import org.solovyev.android.checkout.Checkout;
import org.solovyev.android.checkout.EmptyRequestListener;
import org.solovyev.android.checkout.Inventory;
import org.solovyev.android.checkout.ProductTypes;
import org.solovyev.android.checkout.Purchase;
import org.solovyev.android.checkout.Sku;

import static com.spiraclestudios.autoskola.framework.platform.AttributeResolver.resolveBooleanAttr;
import static org.solovyev.android.checkout.ResponseCodes.ACCOUNT_ERROR;
import static org.solovyev.android.checkout.ResponseCodes.ITEM_ALREADY_OWNED;
import static org.solovyev.android.checkout.ResponseCodes.USER_CANCELED;

public class InformationActivity extends StandardActivity {

  private static final String STATE_VERSION_NAME = "VERSION_NAME";
  private static final String STATE_VERSION_CODE = "VERSION_CODE";

  private ActivityCheckout checkout;
  private Sku removeAdsSku;
  private String versionName;
  private int versionCode;

  @BindView(R.id.app_version) TextView appVersionView;
  @BindView(R.id.remove_ads_container) View remove_ads_container;
  @BindView(R.id.remove_ads_title) TextView remove_ads_title;
  @BindView(R.id.remove_ads_description) TextView remove_ads_description;
  @BindView(R.id.purchase_remove_ads) Button purchase_remove_ads;

  @Override protected BaseActivity getThis() {
    return this;
  }

  @Override public void setActivityContentView() {
    InformationActivityBinding activityBinding =
        DataBindingUtil.setContentView(this, R.layout.information__activity);

    InformationContentBinding contentBinding = activityBinding.informationContent;
    contentBinding.appVersion.setText("TEST OF DATA BINDING");
  }

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    Toolbar toolbar = findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);

    ActionBar actionBar = getSupportActionBar();
    if (actionBar != null) {
      actionBar.setDisplayHomeAsUpEnabled(true);
    }

    if (savedInstanceState == null) {
      versionName = BuildConfig.VERSION_NAME;
      versionCode = BuildConfig.VERSION_CODE;
    } else {
      versionName = savedInstanceState.getString(STATE_VERSION_NAME);
      versionCode = savedInstanceState.getInt(STATE_VERSION_CODE);
    }
    appVersionView.setText(
        getString(R.string.information__text__version, versionName, versionCode));

    if (!BuildConfig.PREMIUM) {
      checkout = Checkout.forActivity(this, BaseApplication.get().getBilling());
      checkout.start();
      checkout.loadInventory(Inventory.Request.create()
          .loadPurchases(ProductTypes.IN_APP)
          .loadSkus(ProductTypes.IN_APP, RemoveAds.PRODUCT_REMOVE_ADS), new InventoryCallback());
    }
  }

  @Override protected void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);

    outState.putString(STATE_VERSION_NAME, versionName);
    outState.putInt(STATE_VERSION_CODE, versionCode);
  }

  @Override
 protected void onDestroy() {
    if (!BuildConfig.PREMIUM) {
      checkout.stop();
    }
    super.onDestroy();
  }

  private class InventoryCallback implements Inventory.Callback {

    @Override public void onLoaded(@Nonnull Inventory.Products products) {
      final Inventory.Product product = products.get(ProductTypes.IN_APP);

      if (!product.supported) {
        return;
      }

      removeAdsSku = product.getSku(RemoveAds.PRODUCT_REMOVE_ADS);
      boolean isPurchased = removeAdsSku != null && product.isPurchased(removeAdsSku);

      if (removeAdsSku != null) {
        showRemoveAdsProduct(removeAdsSku, isPurchased);
      }
    }

    private void showRemoveAdsProduct(Sku sku, boolean isPurchased) {
      remove_ads_container.setVisibility(View.VISIBLE);
      remove_ads_title.setText(sku.getDisplayTitle());
      remove_ads_description.setText(sku.description);
      purchase_remove_ads.setText(
          isPurchased ? getString(R.string.billing__text__purchased) : sku.price);

      purchase_remove_ads.setOnClickListener(
          view -> checkout.startPurchaseFlow(ProductTypes.IN_APP, sku.id.code, null,
              new PurchaseListener()));
    }
  }

  private class PurchaseListener extends EmptyRequestListener<Purchase> {

    @Override public void onSuccess(@Nonnull Purchase result) {
      logPurchase(removeAdsSku, true);

      purchase_remove_ads.setText(getString(R.string.billing__text__purchased));
    }

    @Override public void onError(int response, @Nonnull Exception e) {
      switch (response) {
        case USER_CANCELED:
          break;
        case ITEM_ALREADY_OWNED:
          Toast.makeText(InformationActivity.this, R.string.billing__toast__item_already_owned,
              Toast.LENGTH_LONG).show();
          break;
        case ACCOUNT_ERROR:
          // If the ACCOUNT_ERROR was caused by the lack of a network connection then do nothing,
          // otherwise show a message and log the exception.
          if (ConnectionBuddy.getInstance().hasNetworkConnection()) {
            Crashlytics.logException(e);

            Toast.makeText(InformationActivity.this,
                R.string.billing__toast__billing_error_occurred, Toast.LENGTH_LONG).show();
          }
          break;
        default:
          Crashlytics.logException(e);
          logPurchase(removeAdsSku, false);

          Toast.makeText(InformationActivity.this, R.string.billing__toast__billing_error_occurred,
              Toast.LENGTH_LONG).show();
          break;
      }
    }

    private void logPurchase(Sku sku, boolean purchaseSucceeded) {
      // The amount is originally in micro-units,
      // where 1,000,000 micro-units equal one unit of the currency.
      BigDecimal itemPrice = new BigDecimal(sku.detailedPrice.amount);
      itemPrice = itemPrice.setScale(2, RoundingMode.HALF_UP);
      itemPrice = itemPrice.multiply(new BigDecimal(0.000001));

      Answers.getInstance()
          .logPurchase(new PurchaseEvent().putItemId(sku.id.code)
              .putItemName(sku.title)
              .putItemPrice(itemPrice)
              .putCurrency(Currency.getInstance(sku.detailedPrice.currency))
              .putSuccess(purchaseSucceeded));
    }
  }

  @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    if (!BuildConfig.PREMIUM) {
      checkout.onActivityResult(requestCode, resultCode, data);
    }
    super.onActivityResult(requestCode, resultCode, data);
  }

  @Override public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case android.R.id.home:
        super.onBackPressed();
        return true;
    }
    return super.onOptionsItemSelected(item);
  }

  @OnClick({
      R.id.web_link, R.id.facebook_link, R.id.google_play_link
  }) public void onClick_SocialLinks(View view) {
    String url = getSocialLinkUrl(view.getId());

    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
    startActivity(browserIntent);
  }

  @OnLongClick({
      R.id.web_link, R.id.facebook_link, R.id.google_play_link
  }) public boolean onLongClick_SocialLinks(View view) {
    String url = getSocialLinkUrl(view.getId());

    ClipboardManager clipboard =
        (ClipboardManager) this.getSystemService(Context.CLIPBOARD_SERVICE);
    ClipData clip =
        ClipData.newPlainText(getString(R.string.information__clipboard_label__social_link), url);
    clipboard.setPrimaryClip(clip);

    Toast.makeText(this, R.string.information__toast__link_was_copied, Toast.LENGTH_SHORT).show();
    return true;
  }

  @OnClick(R.id.show_changelog) public void show_changelog_onClick() {
    startActivity(ChangelogActivity.Companion.createIntentWithAllChangelogs(this));
  }

  @OnClick(R.id.show_about_libraries) public void show_about_libraries_onClick() {
    boolean isLightTheme = resolveBooleanAttr(this, R.attr.isLightTheme);
    new LibsBuilder().withActivityTitle(getString(R.string.screen_title__about_libraries))
        .withActivityStyle(isLightTheme ? ActivityStyle.LIGHT_DARK_TOOLBAR : ActivityStyle.DARK)
        .withLicenseShown(true)
        .start(this);
  }

  @OnClick(R.id.rate_our_app) public void rate_our_app_onClick() {
    StoreRating.rateApp(this);
  }

  @OnLongClick(R.id.app_version) public boolean app_version_onLongClick() {
    if (!BuildConfig.DEBUG) {
      return false;
    }
    showDeveloperTools();
    return true;
  }

  private void showDeveloperTools() {
    Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
    vibrator.vibrate(20);

    DialogFragment dialog = new DevToolsDialog();
    dialog.show(getSupportFragmentManager(), "DevTools");
  }

  private String getSocialLinkUrl(int viewId) {
    switch (viewId) {
      case R.id.web_link:
        return getString(R.string.link__social__web);
      case R.id.facebook_link:
        return getString(R.string.link__social__facebook);
      //case R.id.twitter_link:
      //  return getString(R.string.link__social__twitter);
      //case R.id.youtube_link:
      //  return getString(R.string.link__social__youtube);
      case R.id.google_play_link:
        return getString(R.string.link__social__google_play__publisher_profile);
      default:
        return "";
    }
  }
}
