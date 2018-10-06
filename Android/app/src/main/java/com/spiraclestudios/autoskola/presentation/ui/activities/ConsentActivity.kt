// Copyright (c) 2015-2017. Spiracle Software. All Rights Reserved.

package com.spiraclestudios.autoskola.presentation.ui.activities

import android.content.Context
import android.content.Intent
import android.databinding.DataBindingUtil
import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.content.res.AppCompatResources
import butterknife.OnClick
import com.spiraclestudios.autoskola.BaseApplication
import com.spiraclestudios.autoskola.G
import com.spiraclestudios.autoskola.R
import com.spiraclestudios.autoskola.Utils.fromHtml
import com.spiraclestudios.autoskola.databinding.ConsentActivityBinding
import com.spiraclestudios.autoskola.framework.presentation.ui.BaseActivity

class ConsentActivity : BaseActivity() {

  private lateinit var binding: ConsentActivityBinding

  override fun getThis(): BaseActivity = this

  override fun setActivityContentView() {
    binding = DataBindingUtil.setContentView(this, R.layout.consent__activity)

    // Set texts from html
    binding.content.informationCollection.informationCollectionText.fromHtml(
        R.string.consent__text__information_collection)

    // Set compound drawables
    val documentsBinding = binding.content.documents
    val openInBrowserIcon = AppCompatResources.getDrawable(this, R.drawable.ic_open_in_browser)

    documentsBinding.openPrivacyPolicy.setCompoundDrawablesWithIntrinsicBounds(
        openInBrowserIcon, null, null, null)
    documentsBinding.openTermsAndConditions.setCompoundDrawablesWithIntrinsicBounds(
        openInBrowserIcon, null, null, null)
  }

  @OnClick(R.id.confirm_consent)
  fun confirmConsent() {
    val prefs = getSharedPreferences(G.PREFS_GENERIC, Context.MODE_PRIVATE)
    val editor = prefs.edit()

    editor.putBoolean(HAS_AGREED_TO_TERMS_KEY, binding.hasAgreedToTerms)
    editor.putBoolean(HAS_AGREED_TO_PRIVACY_POLICY_KEY, binding.hasAgreedToPrivacyPolicy)
    editor.apply()

    val intent = Intent(this, SplashActivity::class.java)
    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
    startActivity(intent)
    finish()
  }

  @OnClick(R.id.refuse_consent)
  fun refuseConsent() {
    confirmRefuseConsentWithDialog()
  }

  @OnClick(R.id.open_privacy_policy)
  fun openPrivacyPolicy() {
    val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(PRIVACY_POLICY_URL))
    startActivity(browserIntent)
  }

  @OnClick(R.id.open_terms_and_conditions)
  fun openTermsAndConditions() {
    val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(TERMS_AND_CONDITIONS_URL))
    startActivity(browserIntent)
  }

  private fun confirmRefuseConsentWithDialog() {
    with(AlertDialog.Builder(this)) {
      setTitle(R.string.consent_exit__title)
      setMessage(getString(R.string.consent_exit__text))

      setPositiveButton(R.string.consent_exit__action__exit_to_home) { _, _ ->
        binding.hasAgreedToTerms = false
        binding.hasAgreedToPrivacyPolicy = false
        BaseApplication.get().exitToLauncher()
      }

      setNegativeButton(R.string.consent_exit__action__dismiss) { dialog, _ ->
        dialog.dismiss()
      }

      create().show()
    }
  }

  override fun onBackPressed() {
    confirmRefuseConsentWithDialog()
  }

  override fun onSaveInstanceState(outState: Bundle) {
    super.onSaveInstanceState(outState)
    outState.putBoolean("hasAgreedToTerms", binding.hasAgreedToTerms)
    outState.putBoolean("hasAgreedToPrivacyPolicy", binding.hasAgreedToPrivacyPolicy)
  }

  override fun onRestoreInstanceState(savedInstanceState: Bundle) {
    super.onRestoreInstanceState(savedInstanceState)
    binding.hasAgreedToTerms = savedInstanceState.getBoolean("hasAgreedToTerms")
    binding.hasAgreedToPrivacyPolicy = savedInstanceState.getBoolean("hasAgreedToPrivacyPolicy")
  }

  companion object {
    private const val PRIVACY_POLICY_URL = "https://benjiko99.github.io/spiracle/privacy_policy"
    private const val TERMS_AND_CONDITIONS_URL = "https://benjiko99.github.io/spiracle/terms_and_conditions"

    const val HAS_AGREED_TO_TERMS_KEY = "has_agreed_to_terms"
    const val HAS_AGREED_TO_PRIVACY_POLICY_KEY = "has_agreed_to_privacy_policy"
  }
}
