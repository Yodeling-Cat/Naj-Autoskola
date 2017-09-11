// Copyright (c) 2015-2017. Spiracle Software. All Rights Reserved.

package com.spiraclestudios.autoskola.presentation.ui.activities;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView.LayoutManager;
import android.view.MenuItem;
import com.mikepenz.fastadapter.commons.adapters.FastItemAdapter;
import com.mikepenz.fastadapter.items.AbstractItem;
import com.spiraclestudios.autoskola.ListItemDecoration;
import com.spiraclestudios.autoskola.R;
import com.spiraclestudios.autoskola.databinding.ChangelogActivityBinding;
import com.spiraclestudios.autoskola.framework.presentation.ui.BaseActivity;
import com.spiraclestudios.autoskola.repository.ChangelogListRepository;
import java.util.List;

public class ChangelogActivity extends BaseActivity {

  public final static String EXTRA_SPECIFIC_APPLICATION_VERSION =
      "com.spiraclestudios.autoskola.SPECIFIC_APPLICATION_VERSION";
  public final static String EXTRA_LAST_APPLICATION_VERSION =
      "com.spiraclestudios.autoskola.LAST_APPLICATION_VERSION";
  public final static String EXTRA_CURRENT_APPLICATION_VERSION =
      "com.spiraclestudios.autoskola.CURRENT_APPLICATION_VERSION";

  private final static String STATE_SPECIFIC_APPLICATION_VERSION = "specificApplicationVersion";
  private final static String STATE_LAST_APPLICATION_VERSION = "lastApplicationVersion";
  private final static String STATE_CURRENT_APPLICATION_VERSION = "currentApplicationVersion";

  private ChangelogActivityBinding activityBinding;
  private int specificApplicationVersion;
  private int lastApplicationVersion;
  private int currentApplicationVersion;

  @Override protected BaseActivity getThis() {
    return this;
  }

  @Override public void setActivityContentView() {
    activityBinding = DataBindingUtil.setContentView(this, R.layout.changelog__activity);
  }

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    LayoutManager layoutManager = new LinearLayoutManager(this);
    activityBinding.recyclerView.setLayoutManager(layoutManager);
    activityBinding.recyclerView.addItemDecoration(new ListItemDecoration(this));

    FastItemAdapter<AbstractItem> fastAdapter = new FastItemAdapter<>();
    activityBinding.recyclerView.setAdapter(fastAdapter);
    List<AbstractItem> listItems;
    if (isForSpecificVersion()) {
      listItems = new ChangelogListRepository(this).getList(specificApplicationVersion);
    } else {
      // TODO: lastApplicationVersion
      listItems = new ChangelogListRepository(this).getList(9, currentApplicationVersion);
    }
    fastAdapter.add(listItems);

    // TODO
    if (fastAdapter.getItemCount() == 0) {
      //launchHomeActivity();
    }
  }

  // TODO: Simplify state restoration
  @Override protected void onCreateFromIntent(Intent intent) {
    specificApplicationVersion = intent.getIntExtra(EXTRA_SPECIFIC_APPLICATION_VERSION, 0);
    lastApplicationVersion = intent.getIntExtra(EXTRA_LAST_APPLICATION_VERSION, 0);
    currentApplicationVersion = intent.getIntExtra(EXTRA_CURRENT_APPLICATION_VERSION, 0);
  }

  @Override protected void onCreateFromSavedInstanceState(Bundle savedInstanceState) {
    specificApplicationVersion = savedInstanceState.getInt(STATE_SPECIFIC_APPLICATION_VERSION, 0);
    lastApplicationVersion = savedInstanceState.getInt(STATE_LAST_APPLICATION_VERSION, 0);
    currentApplicationVersion = savedInstanceState.getInt(STATE_CURRENT_APPLICATION_VERSION, 0);
  }

  @Override protected void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
    outState.putInt(STATE_SPECIFIC_APPLICATION_VERSION, specificApplicationVersion);
    outState.putInt(STATE_LAST_APPLICATION_VERSION, lastApplicationVersion);
    outState.putInt(STATE_CURRENT_APPLICATION_VERSION, currentApplicationVersion);
  }

  @Override protected void setUpToolbar() {
    super.setUpToolbar();
    ActionBar actionBar = getSupportActionBar();

    if (actionBar != null) {
      actionBar.setDisplayHomeAsUpEnabled(true);
      actionBar.setHomeAsUpIndicator(R.drawable.ic_close);
      // TODO: It's false when it shouldn't
      if (isForSpecificVersion()) {
        actionBar.setTitle(null);
      }
    }
  }

  @Override public boolean onOptionsItemSelected(@NonNull MenuItem item) {
    if (item.getItemId() == android.R.id.home) {
      super.onBackPressed();
      return true;
    }
    return super.onOptionsItemSelected(item);
  }

  private boolean isForSpecificVersion() {
    return specificApplicationVersion > 0;
  }
}
