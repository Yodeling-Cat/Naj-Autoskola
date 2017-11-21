// Copyright (c) 2015-2017. Spiracle Software. All Rights Reserved.

package com.spiraclestudios.autoskola.presentation.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView.LayoutManager;
import android.util.Log;
import android.view.MenuItem;
import com.mikepenz.fastadapter.commons.adapters.FastItemAdapter;
import com.mikepenz.fastadapter.items.AbstractItem;
import com.spiraclestudios.autoskola.BuildConfig;
import com.spiraclestudios.autoskola.ListItemDecoration;
import com.spiraclestudios.autoskola.R;
import com.spiraclestudios.autoskola.databinding.ChangelogActivityBinding;
import com.spiraclestudios.autoskola.framework.presentation.ui.BaseActivity;
import com.spiraclestudios.autoskola.repository.ChangelogListRepository;
import java.util.ArrayList;
import java.util.List;

public class ChangelogActivity extends BaseActivity {

  public final static String EXTRA_INTENT_TYPE = "com.spiraclestudios.autoskola.INTENT_TYPE";
  public final static String EXTRA_SPECIFIC_VERSION =
      "com.spiraclestudios.autoskola.SPECIFIC_VERSION";
  public final static String EXTRA_FROM_VERSION = "com.spiraclestudios.autoskola.FROM_VERSION";
  public final static String EXTRA_TILL_VERSION = "com.spiraclestudios.autoskola.TILL_VERSION";

  private final static String STATE_INTENT_TYPE = "intentType";
  private final static String STATE_SPECIFIC_VERSION = "specificVersion";
  private final static String STATE_FROM_VERSION = "fromVersion";
  private final static String STATE_TILL_VERSION = "tillVersion";

  private ChangelogActivityBinding activityBinding;
  private IntentType intentType;
  private int specificVersion;
  private int fromVersion;
  private int tillVersion;

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
    List<AbstractItem> listItems = new ArrayList<>();
    switch (intentType) {
      case ALL_CHANGELOGS:
        listItems = new ChangelogListRepository(this).getChangelogForRangeOfVersions(0,
            BuildConfig.VERSION_CODE);
        break;
      case RANGE_OF_CHANGELOGS:
        listItems = new ChangelogListRepository(this).getChangelogForRangeOfVersions(fromVersion,
            tillVersion);
        break;
      case SPECIFIC_CHANGELOG:
        listItems = new ChangelogListRepository(this).getChangelogForVersion(specificVersion);
        break;
    }

    if (listItems.isEmpty()) {
      Log.d("ChangelogActivity", "Finishing ChangelogActivity because no changelogs were found.");
      finish();
    }

    fastAdapter.add(listItems);
  }

  // TODO: Simplify state restoration
  @Override protected void onCreateFromIntent(Intent intent) {
    intentType = (IntentType) intent.getSerializableExtra(EXTRA_INTENT_TYPE);
    specificVersion = intent.getIntExtra(EXTRA_SPECIFIC_VERSION, 0);
    fromVersion = intent.getIntExtra(EXTRA_FROM_VERSION, 0);
    tillVersion = intent.getIntExtra(EXTRA_TILL_VERSION, 0);
  }

  @Override protected void onCreateFromSavedInstanceState(Bundle savedInstanceState) {
    intentType = (IntentType) savedInstanceState.getSerializable(STATE_INTENT_TYPE);
    specificVersion = savedInstanceState.getInt(STATE_SPECIFIC_VERSION, 0);
    fromVersion = savedInstanceState.getInt(STATE_FROM_VERSION, 0);
    tillVersion = savedInstanceState.getInt(STATE_TILL_VERSION, 0);
  }

  @Override protected void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
    outState.putSerializable(STATE_INTENT_TYPE, intentType);
    outState.putInt(STATE_SPECIFIC_VERSION, specificVersion);
    outState.putInt(STATE_FROM_VERSION, fromVersion);
    outState.putInt(STATE_TILL_VERSION, tillVersion);
  }

  private enum IntentType {
    ALL_CHANGELOGS, RANGE_OF_CHANGELOGS, SPECIFIC_CHANGELOG
  }

  public static Intent createIntentWithAllChangelogs(Context context) {
    Intent intent = new Intent(context, ChangelogActivity.class);
    intent.putExtra(EXTRA_INTENT_TYPE, IntentType.ALL_CHANGELOGS);
    return intent;
  }

  public static Intent createIntentWithRangeOfChangelogs(Context context, int fromVersion,
      int tillVersion) {
    Intent intent = new Intent(context, ChangelogActivity.class);
    intent.putExtra(EXTRA_INTENT_TYPE, IntentType.RANGE_OF_CHANGELOGS);
    intent.putExtra(EXTRA_FROM_VERSION, fromVersion);
    intent.putExtra(EXTRA_TILL_VERSION, tillVersion);
    return intent;
  }

  @Override protected void setUpToolbar() {
    super.setUpToolbar();
    ActionBar actionBar = getSupportActionBar();

    if (actionBar != null) {
      actionBar.setDisplayHomeAsUpEnabled(true);
      actionBar.setHomeAsUpIndicator(R.drawable.ic_close);

      String title = null;
      switch (intentType) {
        case ALL_CHANGELOGS:
          title = getString(R.string.screen_title__changelog__all_changes);
          break;
        case RANGE_OF_CHANGELOGS:
          title = getString(R.string.screen_title__changelog__range_of_versions);
          break;
        case SPECIFIC_CHANGELOG:
          title = getString(R.string.screen_title__changelog__specific_version);
          break;
      }
      actionBar.setTitle(title);
    }
  }

  @Override public boolean onOptionsItemSelected(@NonNull MenuItem item) {
    if (item.getItemId() == android.R.id.home) {
      super.onBackPressed();
      return true;
    }
    return super.onOptionsItemSelected(item);
  }
}
