package com.spiraclestudios.autoskola.billing.remove_ads

import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import com.crashlytics.android.Crashlytics
import com.crashlytics.android.answers.Answers
import com.crashlytics.android.answers.PurchaseEvent
import com.spiraclestudios.autoskola.R
import com.zplesac.connectionbuddy.ConnectionBuddy
import kotlinx.android.synthetic.main.remove_ads__product.view.*
import org.solovyev.android.checkout.EmptyRequestListener
import org.solovyev.android.checkout.Purchase
import org.solovyev.android.checkout.ResponseCodes.*
import org.solovyev.android.checkout.Sku
import java.math.BigDecimal
import java.math.RoundingMode
import java.util.*

class RemoveAdsPurchaseListener(val activity: AppCompatActivity, val removeAdsSku: Sku,
    val removeAdsView: RemoveAdsView) : EmptyRequestListener<Purchase>() {

  override fun onSuccess(result: Purchase) {
    logPurchase(removeAdsSku, true)

    removeAdsView.purchase_remove_ads.text = activity.getString(R.string.billing__text__purchased)
  }

  override fun onError(response: Int, e: Exception) {
    when (response) {
      USER_CANCELED -> {
      }
      ITEM_ALREADY_OWNED -> Toast.makeText(activity,
          R.string.billing__toast__item_already_owned,
          Toast.LENGTH_LONG).show()
      ACCOUNT_ERROR ->
        // If the ACCOUNT_ERROR was caused by the lack of a network connection then do nothing,
        // otherwise show a message and log the exception.
        if (ConnectionBuddy.getInstance().hasNetworkConnection()) {
          Crashlytics.logException(e)

          Toast.makeText(activity,
              R.string.billing__toast__billing_error_occurred, Toast.LENGTH_LONG).show()
        }
      else -> {
        Crashlytics.logException(e)
        logPurchase(removeAdsSku, false)

        Toast.makeText(activity, R.string.billing__toast__billing_error_occurred,
            Toast.LENGTH_LONG).show()
      }
    }
  }

  private fun logPurchase(sku: Sku, purchaseSucceeded: Boolean) {
    // The amount is originally in micro-units,
    // where 1,000,000 micro-units equal one unit of the currency.
    var itemPrice = BigDecimal(sku.detailedPrice.amount)
    itemPrice = itemPrice.setScale(2, RoundingMode.HALF_UP)
    itemPrice = itemPrice.multiply(BigDecimal(0.000001))

    Answers.getInstance()
        .logPurchase(PurchaseEvent().putItemId(sku.id.code)
            .putItemName(sku.title)
            .putItemPrice(itemPrice)
            .putCurrency(Currency.getInstance(sku.detailedPrice.currency))
            .putSuccess(purchaseSucceeded))
  }
}
