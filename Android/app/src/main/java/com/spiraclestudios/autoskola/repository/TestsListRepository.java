// Copyright (c) 2015-2017. Spiracle Software. All Rights Reserved.

package com.spiraclestudios.autoskola.repository;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.SparseIntArray;
import com.mikepenz.fastadapter.items.AbstractItem;
import com.spiraclestudios.autoskola.DbContract;
import com.spiraclestudios.autoskola.DbHelper;
import com.spiraclestudios.autoskola.FABSpaceListEntry;
import com.spiraclestudios.autoskola.TestListEntry;
import com.spiraclestudios.autoskola.domain.Groups;
import java.util.ArrayList;

public class TestsListRepository {

  private Context ctx;

  public TestsListRepository(Context ctx) {
    this.ctx = ctx;
  }

  public ArrayList<AbstractItem> getList(Groups group) {
    // Get the History for this test version.
    String query = "SELECT " +
        DbContract.History.COLUMN_TEST_ID +
        ", count(" + DbContract.History.COLUMN_TEST_ID +
        ") FROM " + DbContract.History.TABLE_NAME +
        " GROUP by " + DbContract.History.COLUMN_TEST_ID;

    DbHelper dbHelper = new DbHelper(ctx);
    SQLiteDatabase db = dbHelper.getReadableDatabase();

    Cursor cHistory = db.rawQuery(query, new String[] {});
    SparseIntArray timesCompletedMap = new SparseIntArray();

    for (cHistory.moveToFirst(); !cHistory.isAfterLast(); cHistory.moveToNext()) {
      int testId = cHistory.getInt(0);
      int testCount = cHistory.getInt(1);

      if (testId != 0) {
        timesCompletedMap.put(testId, testCount);
      }
    }

    cHistory.close();
    db.close();
    dbHelper.close();

    int start;
    int end;
    if (group == Groups.AB) {
      start = 1;
      end = 36;
    } else {
      start = 36;
      end = 61;
    }

    ArrayList<AbstractItem> results = new ArrayList<>();
    for (int i = start; i < end; i++) {
      int timesCompleted = timesCompletedMap.get(i, 0);
      TestListEntry entry = new TestListEntry();
      entry.index = i;
      entry.timesCompleted = timesCompleted;
      results.add(entry);
    }

    results.add(new FABSpaceListEntry());
    return results;
  }
}
