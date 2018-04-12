// Copyright (c) 2015-2017. Spiracle Software. All Rights Reserved.

package com.spiraclestudios.autoskola.presentation.ui.activities

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.spiraclestudios.autoskola.HistoryListAdapter
import com.spiraclestudios.autoskola.ListItemDecoration
import com.spiraclestudios.autoskola.R
import com.spiraclestudios.autoskola.databinding.HistoryActivityBinding
import com.spiraclestudios.autoskola.databinding.HistoryContentBinding
import com.spiraclestudios.autoskola.framework.presentation.ui.BaseActivity
import com.spiraclestudios.autoskola.framework.presentation.ui.StandardActivity
import com.spiraclestudios.autoskola.presentation.ui.viewmodels.HistoryActivityViewModel

/**
 * Added by benji on 26/2/2016.
 *
 * If EXTRA_TEST_ID == 0, shows the global history, otherwise shows history for the passed test id.
 */
class HistoryActivity : StandardActivity() {

  private var testIndex: Int = 0

  private lateinit var contentBinding: HistoryContentBinding
  private lateinit var viewModel: HistoryActivityViewModel

  override fun getThis(): BaseActivity = this

  override fun setActivityContentView() {
    val activityBinding = DataBindingUtil.setContentView<HistoryActivityBinding>(this, R.layout.history__activity)
    contentBinding = activityBinding.contentBinding
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    viewModel = ViewModelProviders.of(this).get(HistoryActivityViewModel::class.java)
    super.onCreate(savedInstanceState)

    setupViewModel()
    setupView()
  }

  private fun setupViewModel() {
    with (viewModel) {
      getHistory().observe(this@HistoryActivity, Observer {historyEntries ->
        if (historyEntries == null || historyEntries.isEmpty()) {
          showEmptyState()
          invalidateOptionsMenu()
        } else {
          hideEmptyState()

          val adapter = HistoryListAdapter(historyEntries)
          adapter.setContext(this@HistoryActivity)
          contentBinding.recyclerView.adapter = adapter
        }
      })
    }
  }

  private fun setupView() {
    with(contentBinding) {
      recyclerView.layoutManager = LinearLayoutManager(this@HistoryActivity)
      recyclerView.setHasFixedSize(true)
      recyclerView.addItemDecoration(ListItemDecoration(this@HistoryActivity))
    }
  }

  override fun onCreateFromIntent(intent: Intent) {
    super.onCreateFromIntent(intent)

    with(intent) {
      testIndex = getIntExtra(EXTRA_TEST_ID, 0)
      viewModel.loadHistoryOfTest(testIndex)
    }
  }

  override fun backActionInToolbar(): Boolean = testIndex != 0

  override fun onCreateOptionsMenu(menu: Menu): Boolean {
    menuInflater.inflate(R.menu.activity__history, menu)
    return true
  }

  override fun onPrepareOptionsMenu(menu: Menu): Boolean {
    menu.findItem(R.id.action__delete_all).isVisible = !viewModel.getHistory().value!!.isEmpty()
    return super.onPrepareOptionsMenu(menu)
  }

  override fun onOptionsItemSelected(item: MenuItem): Boolean {
    val id = item.itemId

    when (id) {
      R.id.action__delete_all -> {
        if (!viewModel.getHistory().value!!.isEmpty()) {
          confirmWantsToDeleteWholeHistoryWithDialog()
        }
        return true
      }
    }
    return super.onOptionsItemSelected(item)
  }

  private fun confirmWantsToDeleteWholeHistoryWithDialog() {
    val builder = AlertDialog.Builder(this)

    val message = if (testIndex != 0) {
      getString(R.string.delete_whole_history__text__delete_specific_test, testIndex)
    } else {
      getString(R.string.delete_whole_history__text__delete_all_tests)
    }

    builder.setMessage(message).setPositiveButton(R.string.delete_whole_history__action__delete) { _, _ ->
      if (testIndex != 0) {
        viewModel.deleteHistoryOfTest(testIndex)
      } else {
        viewModel.deleteWholeHistory()
      }
      showEmptyState()
      invalidateOptionsMenu()
    }.setNegativeButton(R.string.delete_whole_history__action__cancel) { dialog, _ -> dialog.dismiss() }

    builder.create().show()
  }

  fun showEmptyState() {
    contentBinding.recyclerViewCard.visibility = View.GONE
    contentBinding.recyclerView.visibility = View.GONE
    contentBinding.emptyState.visibility = View.VISIBLE
  }

  fun hideEmptyState() {
    contentBinding.recyclerViewCard.visibility = View.VISIBLE
    contentBinding.recyclerView.visibility = View.VISIBLE
    contentBinding.emptyState.visibility = View.GONE
  }

  companion object {

    const val EXTRA_TEST_ID = "com.spiraclestudios.autoskola.TEST_ID"
  }

  /*@Override public void onBackPressed() {
    DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
    if (drawer != null && drawer.isDrawerOpen(GravityCompat.START)) {
      drawer.closeDrawer(GravityCompat.START);
    } else {
      // If we opened history from the tests list, looking at the history of a specific test.
      //if (testIndex != 0) {
        super.onBackPressed();
      //} else {
      //  NavUtils.navigateUpTo(this, new Intent(this, HomeActivity.class));
      //}
    }
  }*/
}
