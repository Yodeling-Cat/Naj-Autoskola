// Copyright (c) 2015-2017. Spiracle Software. All Rights Reserved.

package com.spiraclestudios.autoskola.repository;

import android.content.Context;
import android.util.SparseArray;
import com.mikepenz.fastadapter.items.AbstractItem;
import com.spiraclestudios.autoskola.ChangelogListEntry;
import com.spiraclestudios.autoskola.ChangelogListHeader;
import com.spiraclestudios.autoskola.R;
import com.spiraclestudios.autoskola.domain.Changelog;
import java.util.ArrayList;
import java.util.List;

import static com.spiraclestudios.autoskola.domain.ChangelogType.ADDED;
import static com.spiraclestudios.autoskola.domain.ChangelogType.CHANGED;
import static com.spiraclestudios.autoskola.domain.ChangelogType.FIXED;

public class ChangelogListRepository {

  /**
   * Says which mapping in the changelogMap to use with the current release of the application.
   * The number doesn't actually correspond to anything, like the build version or version name.
   * It's just stored in the shared preferences to know if the app was updated
   * and what version it was updated from.
   * If you want to add a changelog for the next release that you're doing,
   * then just increment this number and map it to a list of changelogs below.
   */
  public static final int CHANGELOG_VERSION = 13;

  private final static SparseArray<List<Changelog>> changelogMap = new SparseArray<>();

  static {
    ArrayList<Changelog> changelog_12 = new ArrayList<>();
    changelog_12.add(new Changelog(ADDED, R.string.changelog__show_most_points_gained_on_test));
    changelog_12.add(new Changelog(ADDED, R.string.changelog__tests_with_enough_points_are_highlighted));
    changelog_12.add(new Changelog(ADDED, R.string.changelog__display_points_when_immediately_scoring_answers));
    changelog_12.add(new Changelog(FIXED, R.string.changelog__correct_mistakes_in_tests_31_and_34));
    changelog_12.add(new Changelog(CHANGED, R.string.changelog__improved_look));
    changelog_12.add(new Changelog(ADDED, R.string.changelog__smooth_scroll_to_next_question));
    changelog_12.add(new Changelog(FIXED, R.string.changelog__delete_all_action_in_history_deletes_specific_test));
    changelog_12.add(new Changelog(CHANGED, R.string.changelog__smoother_theme_switching));
    changelogMap.put(12, changelog_12);

    ArrayList<Changelog> changelog_13 = new ArrayList<>();
    changelog_13.add(new Changelog(CHANGED, R.string.changelog__your_car_in_intersections_is_black));
    changelog_13.add(new Changelog(CHANGED, R.string.changelog__road_signs_are_now_free));
    changelog_13.add(new Changelog(FIXED, R.string.changelog__correct_typo_in_test_32_q_4));
    changelog_13.add(new Changelog(ADDED, R.string.changelog__alert_after_reaching_test_time_limit));
    changelog_13.add(new Changelog(ADDED, R.string.changelog__real_time_theme_change));
    changelogMap.put(13, changelog_13);
  }

  private final Context ctx;

  public ChangelogListRepository(Context context) {
    this.ctx = context;
  }

  public ArrayList<AbstractItem> getChangelogForVersion(int changelogVersion) {
    ArrayList<AbstractItem> results = new ArrayList<>();
    if (changelogMap.indexOfKey(changelogVersion) < 0) return results;

    ChangelogListHeader header = getChangelogHeader(changelogVersion);
    List<ChangelogListEntry> entries = getChangelogEntries(changelogVersion);
    results.add(header);
    results.addAll(entries);
    return results;
  }

  public ArrayList<AbstractItem> getChangelogForRangeOfVersions(int versionFrom, int versionTill) {
    ArrayList<AbstractItem> results = new ArrayList<>();

    for (int version = versionTill; version >= versionFrom; version--) {
      List<AbstractItem> changelog = getChangelogForVersion(version);
      if (!changelog.isEmpty()) {
        results.addAll(changelog);
      }
    }
    return results;
  }

  private List<ChangelogListEntry> getChangelogEntries(int changelogVersion) {
    ArrayList<ChangelogListEntry> results = new ArrayList<>();
    if (changelogMap.indexOfKey(changelogVersion) < 0) return results;

    List<Changelog> changelogs = changelogMap.get(changelogVersion);
    for (Changelog changelog : changelogs) {
      ChangelogListEntry entry = new ChangelogListEntry(changelog);
      results.add(entry);
    }
    return results;
  }

  private ChangelogListHeader getChangelogHeader(int changelogVersion) {
    ChangelogListHeader entry = new ChangelogListHeader();
    entry.setHeaderText(
        ctx.getString(R.string.changelog__text__changelog_header, changelogVersion));
    return entry;
  }
}
