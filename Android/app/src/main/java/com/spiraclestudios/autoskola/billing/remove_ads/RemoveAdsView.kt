package com.spiraclestudios.autoskola.billing.remove_ads

import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import com.spiraclestudios.autoskola.R
import kotlinx.android.synthetic.main.remove_ads__product.view.*
import org.solovyev.android.checkout.Inventory
import org.solovyev.android.checkout.ProductTypes
import org.solovyev.android.checkout.UiCheckout

class RemoveAdsView : FrameLayout {

  constructor(context: Context) : super(context) {
    init(context, null)
  }

  constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
    init(context, attrs)
  }

  constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int)
      : super(context, attrs, defStyleAttr) {
    init(context, attrs)
  }

  private fun init(context: Context, attrs: AttributeSet?) {
    View.inflate(context, R.layout.remove_ads__product, this)

    if (!isInEditMode) {
      visibility = View.GONE
    }
  }

  fun initProductView(activity: AppCompatActivity, checkout: UiCheckout,
      product: Inventory.Product) {
    val removeAdsSku = product.getSku(RemoveAds.PRODUCT_REMOVE_ADS)
    val isPurchased = removeAdsSku != null && product.isPurchased(removeAdsSku)

    if (removeAdsSku == null || isPurchased) {
      visibility = View.GONE
    } else {
      visibility = View.VISIBLE
      remove_ads_title.text = removeAdsSku.displayTitle
      remove_ads_description.text = removeAdsSku.description
      purchase_remove_ads.text = if (isPurchased) context.getString(
          R.string.billing__text__purchased) else removeAdsSku.price

      purchase_remove_ads.setOnClickListener {
        checkout.startPurchaseFlow(ProductTypes.IN_APP, removeAdsSku.id.code, null,
            RemoveAdsPurchaseListener(activity, removeAdsSku, this))
      }
    }
  }
}