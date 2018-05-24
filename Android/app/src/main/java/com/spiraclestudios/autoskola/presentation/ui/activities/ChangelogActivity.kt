// Copyright (c) 2015-2017. Spiracle Software. All Rights Reserved.

package com.spiraclestudios.autoskola.presentation.ui.activities

import android.content.Context
import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.MenuItem
import com.mikepenz.fastadapter.commons.adapters.FastItemAdapter
import com.mikepenz.fastadapter.items.AbstractItem
import com.spiraclestudios.autoskola.ListItemDecoration
import com.spiraclestudios.autoskola.R
import com.spiraclestudios.autoskola.databinding.ChangelogActivityBinding
import com.spiraclestudios.autoskola.framework.presentation.ui.BaseActivity
import com.spiraclestudios.autoskola.presentation.ui.activities.ChangelogActivity.IntentType.*
import com.spiraclestudios.autoskola.repository.ChangelogListRepository
import com.spiraclestudios.autoskola.repository.ChangelogListRepository.CHANGELOG_VERSION
import java.util.*

class ChangelogActivity : BaseActivity() {

  private lateinit var activityBinding: ChangelogActivityBinding
  private var intentType: IntentType? = null
  private var specificVersion: Int = 0
  private var fromVersion: Int = 0
  private var tillVersion: Int = 0

  override fun getThis(): BaseActivity = this

  override fun setActivityContentView() {
    activityBinding = DataBindingUtil.setContentView(this, R.layout.changelog__activity)
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    val repository = ChangelogListRepository(this)

    val listItems: List<AbstractItem<*, *>> = when (intentType) {
      ALL_CHANGELOGS -> repository.getChangelogForRangeOfVersions(0, CHANGELOG_VERSION)
      RANGE_OF_CHANGELOGS -> repository.getChangelogForRangeOfVersions(fromVersion, tillVersion)
      SPECIFIC_CHANGELOG -> repository.getChangelogForVersion(specificVersion)
      else -> ArrayList()
    }

    if (listItems.isEmpty()) {
      Log.d("ChangelogActivity", "Finishing ChangelogActivity because no changelogs were found.")
      finish()
    }

    with(activityBinding) {
      recyclerView.layoutManager = LinearLayoutManager(this@ChangelogActivity)
      recyclerView.addItemDecoration(ListItemDecoration(this@ChangelogActivity))

      recyclerView.adapter = FastItemAdapter<AbstractItem<*, *>>().apply {
        add(listItems)
      }
    }
  }

  override fun setUpToolbar() {
    super.setUpToolbar()

    supportActionBar?.apply {
      setDisplayHomeAsUpEnabled(true)
      setHomeAsUpIndicator(R.drawable.ic_close)

      title = when (intentType) {
        ALL_CHANGELOGS -> getString(R.string.screen_title__changelog__all_changes)
        RANGE_OF_CHANGELOGS -> getString(R.string.screen_title__changelog__range_of_versions)
        SPECIFIC_CHANGELOG -> getString(R.string.screen_title__changelog__specific_version)
        else -> null
      }
    }
  }

  override fun onOptionsItemSelected(item: MenuItem): Boolean =
      when (item.itemId) {
        android.R.id.home -> {
          super.onBackPressed()
          true
        }
        else -> super.onOptionsItemSelected(item)
      }

  override fun onCreateFromIntent(intent: Intent) = with(intent) {
    intentType = getSerializableExtra(EXTRA_INTENT_TYPE) as IntentType
    specificVersion = getIntExtra(EXTRA_SPECIFIC_VERSION, 0)
    fromVersion = getIntExtra(EXTRA_FROM_VERSION, 0)
    tillVersion = getIntExtra(EXTRA_TILL_VERSION, 0)
  }

  override fun onCreateFromSavedInstanceState(state: Bundle) = with(state) {
    intentType = getSerializable(STATE_INTENT_TYPE) as IntentType
    specificVersion = getInt(STATE_SPECIFIC_VERSION, 0)
    fromVersion = getInt(STATE_FROM_VERSION, 0)
    tillVersion = getInt(STATE_TILL_VERSION, 0)
  }

  override fun onSaveInstanceState(outState: Bundle) {
    super.onSaveInstanceState(outState)
    outState.putSerializable(STATE_INTENT_TYPE, intentType)
    outState.putInt(STATE_SPECIFIC_VERSION, specificVersion)
    outState.putInt(STATE_FROM_VERSION, fromVersion)
    outState.putInt(STATE_TILL_VERSION, tillVersion)
  }

  private enum class IntentType {
    ALL_CHANGELOGS, RANGE_OF_CHANGELOGS, SPECIFIC_CHANGELOG
  }

  companion object {

    const val EXTRA_INTENT_TYPE = "com.spiraclestudios.autoskola.INTENT_TYPE"
    const val EXTRA_SPECIFIC_VERSION = "com.spiraclestudios.autoskola.SPECIFIC_VERSION"
    const val EXTRA_FROM_VERSION = "com.spiraclestudios.autoskola.FROM_VERSION"
    const val EXTRA_TILL_VERSION = "com.spiraclestudios.autoskola.TILL_VERSION"

    private const val STATE_INTENT_TYPE = "intentType"
    private const val STATE_SPECIFIC_VERSION = "specificVersion"
    private const val STATE_FROM_VERSION = "fromVersion"
    private const val STATE_TILL_VERSION = "tillVersion"

    fun createIntentWithAllChangelogs(context: Context): Intent =
        Intent(context, ChangelogActivity::class.java).apply {
          putExtra(EXTRA_INTENT_TYPE, ALL_CHANGELOGS)
        }

    fun createIntentWithRangeOfChangelogs(context: Context, fromVersion: Int, tillVersion: Int): Intent =
        Intent(context, ChangelogActivity::class.java).apply {
          putExtra(EXTRA_INTENT_TYPE, RANGE_OF_CHANGELOGS)
          putExtra(EXTRA_FROM_VERSION, fromVersion)
          putExtra(EXTRA_TILL_VERSION, tillVersion)
        }
  }
}
