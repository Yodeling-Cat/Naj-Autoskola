// Copyright (c) 2015-2017. Spiracle Software. All Rights Reserved.

package com.spiraclestudios.autoskola.repository;

import android.content.Context;
import android.util.SparseIntArray;
import com.mikepenz.fastadapter.items.AbstractItem;
import com.spiraclestudios.autoskola.ChangelogListEntry;
import com.spiraclestudios.autoskola.R;
import java.util.ArrayList;

public class ChangelogListRepository {

  private final static SparseIntArray changelogMap = new SparseIntArray();

  static {
    changelogMap.put(9, R.string.changelog__text__changes_in_v12);
    changelogMap.put(10, R.string.changelog__text__changes_in_v12);
    changelogMap.put(11, R.string.changelog__text__changes_in_v12);
    changelogMap.put(12, R.string.changelog__text__changes_in_v12);
  }

  private Context ctx;

  public ChangelogListRepository(Context ctx) {
    this.ctx = ctx;
  }

  public ArrayList<AbstractItem> getList(int appVersion) {
    ArrayList<AbstractItem> results = new ArrayList<>();

    ChangelogListEntry entry = getChangelogEntry(appVersion);
    if (entry != null) {
      results.add(entry);
    }

    return results;
  }

  public ArrayList<AbstractItem> getList(int appVersionFrom, int appVersionTill) {
    ArrayList<AbstractItem> results = new ArrayList<>();

    for (int appVersion = appVersionFrom + 1; appVersion <= appVersionTill; appVersion++) {
      ChangelogListEntry entry = getChangelogEntry(appVersion);
      if (entry != null) {
        results.add(entry);
      }
    }

    return results;
  }

  private ChangelogListEntry getChangelogEntry(int appVersion) {
    if (changelogMap.indexOfKey(appVersion) >= 0) {
      String header = ctx.getString(R.string.changelog__text__changelog_header, appVersion);
      String changelog = ctx.getString(changelogMap.get(appVersion));

      ChangelogListEntry entry = new ChangelogListEntry();
      entry.setHeaderText(header);
      entry.setChangelogText(changelog);
      return entry;
    }
    return null;
  }
}
