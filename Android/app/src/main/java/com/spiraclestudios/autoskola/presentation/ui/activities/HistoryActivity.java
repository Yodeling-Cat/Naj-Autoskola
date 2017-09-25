// Copyright (c) 2015-2017. Spiracle Software. All Rights Reserved.

package com.spiraclestudios.autoskola.presentation.ui.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import butterknife.BindView;
import com.spiraclestudios.autoskola.DbContract;
import com.spiraclestudios.autoskola.DbContract.History;
import com.spiraclestudios.autoskola.DbHelper;
import com.spiraclestudios.autoskola.HistoryListAdapter;
import com.spiraclestudios.autoskola.HistoryListEntry;
import com.spiraclestudios.autoskola.ListItemDecoration;
import com.spiraclestudios.autoskola.R;
import com.spiraclestudios.autoskola.Utils;
import com.spiraclestudios.autoskola.framework.presentation.ui.BaseActivity;
import com.spiraclestudios.autoskola.framework.presentation.ui.StandardActivity;
import java.util.ArrayList;
import java.util.List;

/**
 * Added by benji on 26/2/2016.
 * <p>
 * If EXTRA_TEST_ID == 0, shows the global history, otherwise shows history for the passed test id.
 * </p>
 */
public class HistoryActivity extends StandardActivity {

  public final static String EXTRA_TEST_ID = "com.spiraclestudios.autoskola.TEST_ID";

  private int testIndex;

  @BindView(R.id.recycler_view) public RecyclerView recycler_view;
  @BindView(R.id.empty_state) public LinearLayout empty_state;

  @Override protected BaseActivity getThis() {
    return this;
  }

  @Override public void setActivityContentView() {
    setContentView(R.layout.history__activity);
  }

  @Override protected void onCreate(Bundle savedInstanceState) {
    Intent intent = getIntent();
    testIndex = intent.getIntExtra(EXTRA_TEST_ID, 0);

    super.onCreate(savedInstanceState);

    // Set up the recycler_view
    ArrayList<HistoryListEntry> dataSet = getDataSet();
    if (dataSet.size() != 0) {
      recycler_view.setHasFixedSize(true);
      RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
      recycler_view.setLayoutManager(layoutManager);
      RecyclerView.Adapter<HistoryListAdapter.ViewHolder> adapter = new HistoryListAdapter(dataSet);
      ((HistoryListAdapter) adapter).setContext(this);
      recycler_view.setAdapter(adapter);
      recycler_view.addItemDecoration(new ListItemDecoration(this));
      //registerForContextMenu(recycler_view);
      //recycler_view.setLongClickable(true);
    } else {
      showEmptyState();
    }
  }

  @Override protected void onCreateFromIntent(Intent intent) {
    super.onCreateFromIntent(intent);
  }

  @Override protected void onCreateFromSavedInstanceState(Bundle savedInstanceState) {
    super.onCreateFromSavedInstanceState(savedInstanceState);
  }

  @Override public boolean backActionInToolbar() {
    return testIndex != 0;
  }

  @Override public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.activity__history, menu);
    return true;
  }

  @Override public boolean onOptionsItemSelected(MenuItem item) {
    int id = item.getItemId();

    switch (id) {
      case R.id.action__delete_all:
        if (!getDataSet().isEmpty()) {
          confirmWantsToDeleteWholeHistoryWithDialog();
        }
        return true;
    }
    return super.onOptionsItemSelected(item);
  }

  private void confirmWantsToDeleteWholeHistoryWithDialog() {
    AlertDialog.Builder builder = new AlertDialog.Builder(this);

    String message;
    if (testIndex != 0) {
      message = getString(R.string.delete_whole_history__text__delete_specific_test, testIndex);
    } else {
      message = getString(R.string.delete_whole_history__text__delete_all_tests);
    }

    builder.setMessage(message)
        .setPositiveButton(R.string.delete_whole_history__action__delete,
            new DialogInterface.OnClickListener() {
              @Override public void onClick(DialogInterface dialog, int which) {
                if (testIndex != 0) {
                  deleteHistoryOfTest(testIndex);
                } else {
                  deleteWholeHistory();
                }
                showEmptyState();
              }
            })
        .setNegativeButton(R.string.delete_whole_history__action__cancel,
            new DialogInterface.OnClickListener() {
              @Override public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
              }
            });

    builder.create().show();
  }

  private void deleteWholeHistory() {
    DbHelper dbHelper = new DbHelper(this);
    SQLiteDatabase db = dbHelper.getWritableDatabase();

    db.delete(DbContract.History.TABLE_NAME, null, null);

    db.close();
    dbHelper.close();
  }

  private void deleteHistoryOfTest(int testIndex) {
    DbHelper dbHelper = new DbHelper(this);
    SQLiteDatabase db = dbHelper.getWritableDatabase();

    db.delete(DbContract.History.TABLE_NAME, History.COLUMN_TEST_ID + "=?",
        new String[] { Integer.toString(testIndex) });

    db.close();
    dbHelper.close();
  }

  public void showEmptyState() {
    recycler_view.setVisibility(View.GONE);
    empty_state.setVisibility(View.VISIBLE);
  }

  public void hideEmptyState() {
    recycler_view.setVisibility(View.VISIBLE);
    empty_state.setVisibility(View.GONE);
  }

  /**
   * @return Data to populate the adapter with.
   */
  private ArrayList<HistoryListEntry> getDataSet() {
    ArrayList<HistoryListEntry> results = new ArrayList<>();

    // Set up the Database
    DbHelper dbHelper = new DbHelper(this);
    SQLiteDatabase db = dbHelper.getReadableDatabase();

    // Get the History for this test version.
    // TODO: On the line " WHERE " + DbContract.History.COLUMN_TEST_ID + " == ?" .. add a version check.
    String[] selectionArgs = new String[] {};
    String query = "SELECT "
        + DbContract.History._ID
        + ", "
        + DbContract.History.COLUMN_TEST_ID
        + ", "
        + DbContract.History.COLUMN_TEST_VERSION
        + ", "
        + DbContract.History.COLUMN_USES_QUESTIONS
        + ", "
        + DbContract.History.COLUMN_USES_ROAD_SIGNS
        + ", "
        + DbContract.History.COLUMN_USES_INTERSECTIONS
        + ", "
        + DbContract.History.COLUMN_POINTS
        + ", "
        + DbContract.History.COLUMN_MAX_POINTS
        + ", "
        + DbContract.History.COLUMN_ELAPSED_TIME
        + ", "
        + DbContract.History.COLUMN_ANSWERS
        + ", "
        + DbContract.History.COLUMN_DATE_TIME
        + " FROM "
        + DbContract.History.TABLE_NAME;

    if (testIndex != 0) {
      query += " WHERE " + DbContract.History.COLUMN_TEST_ID + " == ?";
      selectionArgs = new String[] { Integer.toString(testIndex) };
    }

    query += " ORDER BY " + DbContract.History._ID + " DESC";

    Cursor cHistory = db.rawQuery(query, selectionArgs);

    for (cHistory.moveToFirst(); !cHistory.isAfterLast(); cHistory.moveToNext()) {
      int dbIndex = cHistory.getInt(cHistory.getColumnIndexOrThrow(DbContract.History._ID));
      int testId =
          cHistory.getInt(cHistory.getColumnIndexOrThrow(DbContract.History.COLUMN_TEST_ID));
      int testVersion =
          cHistory.getInt(cHistory.getColumnIndexOrThrow(DbContract.History.COLUMN_TEST_VERSION));

      boolean usesQuestions =
          cHistory.getInt(cHistory.getColumnIndexOrThrow(DbContract.History.COLUMN_USES_QUESTIONS))
              != 0;
      boolean usesRoadSigns =
          cHistory.getInt(cHistory.getColumnIndexOrThrow(DbContract.History.COLUMN_USES_ROAD_SIGNS))
              != 0;
      boolean usesIntersections = cHistory.getInt(
          cHistory.getColumnIndexOrThrow(DbContract.History.COLUMN_USES_INTERSECTIONS)) != 0;

      int points =
          cHistory.getInt(cHistory.getColumnIndexOrThrow(DbContract.History.COLUMN_POINTS));
      int maxPoints =
          cHistory.getInt(cHistory.getColumnIndexOrThrow(DbContract.History.COLUMN_MAX_POINTS));

      long elapsedTime =
          cHistory.getLong(cHistory.getColumnIndexOrThrow(DbContract.History.COLUMN_ELAPSED_TIME));
      long dateTime =
          cHistory.getLong(cHistory.getColumnIndexOrThrow(DbContract.History.COLUMN_DATE_TIME));

      String answersString =
          cHistory.getString(cHistory.getColumnIndexOrThrow(DbContract.History.COLUMN_ANSWERS));

      List<Integer> chosenAnswersList = new ArrayList<>();
      List<Integer> questionIds = new ArrayList<>();
      List<Integer> correctAnswersList = new ArrayList<>();
      int amountCorrect = 0;
      int amountIncorrect;
      int amountUnanswered = 0;

      // Selector for the question type.
      String typeSelector = "AND (";
      List<String> concat = new ArrayList<>();

      if (usesQuestions) {
        concat.add(DbContract.Questions.COLUMN_TYPE + "=0");
      }
      if (usesRoadSigns) {
        concat.add(DbContract.Questions.COLUMN_TYPE + "=1");
      }
      if (usesIntersections) {
        concat.add(DbContract.Questions.COLUMN_TYPE + "=2");
      }

      for (int i = 0; i < concat.size(); i++) {
        String s = concat.get(i);

        typeSelector += s;

        if (i < concat.size() - 1) {
          typeSelector += " OR ";
        }
      }
      typeSelector += ")";

      // Get latest version of this test.
      Cursor cTest = db.rawQuery("SELECT "
          + DbContract.Tests.COLUMN_QUESTIONS
          + ", "
          + DbContract.Tests.COLUMN_VERSION_CODE
          + " FROM "
          + DbContract.Tests.TABLE_NAME
          + " WHERE "
          + DbContract.Tests.COLUMN_TEST_ID
          + " = ?", new String[] { Integer.toString(testId) });

      cTest.moveToFirst();

      // The whole 'questions' string from the Tests table.
      String questionsString =
          cTest.getString(cTest.getColumnIndexOrThrow(DbContract.Tests.COLUMN_QUESTIONS));

      // Get the Filtered Questions for this test version.
      String query2 = "SELECT * FROM "
          + DbContract.Questions.TABLE_NAME
          + " WHERE "
          + DbContract.Questions.COLUMN_QUESTION_ID
          + " IN ("
          + questionsString
          + ") AND "
          + DbContract.Questions.COLUMN_VERSION
          + " <= ? "
          + typeSelector;

      Cursor cFilteredQuestions =
          db.rawQuery(query2, new String[] { Integer.toString(testVersion) });

      for (cFilteredQuestions.moveToFirst(); !cFilteredQuestions.isAfterLast();
          cFilteredQuestions.moveToNext()) {
        questionIds.add(cFilteredQuestions.getInt(cFilteredQuestions.
            getColumnIndexOrThrow(DbContract.Questions.COLUMN_QUESTION_ID)));

        correctAnswersList.add(cFilteredQuestions.getInt(cFilteredQuestions.
            getColumnIndexOrThrow(DbContract.Questions.COLUMN_CORRECT_ANSWER)));
      }

      // Get count of questions and amount of max points.
      int questionsCount = questionIds.size();

      if (!answersString.isEmpty()) {
        for (String answer : answersString.split(",")) {
          int chosenAnswer = Integer.parseInt(answer);
          chosenAnswersList.add(chosenAnswer);
        }
      }
      for (int i = 0; i < questionsCount; i++) {
        if (chosenAnswersList.get(i).equals(correctAnswersList.get(i))) {
          amountCorrect++;
        }
        if (chosenAnswersList.get(i).equals(0)) {
          amountUnanswered++;
        }
      }

      amountIncorrect = questionsCount - amountCorrect;

      cTest.close();
      cFilteredQuestions.close();

      boolean wasSuccessful = Utils.getTestSuccessful(points, elapsedTime);
      results.add(
          new HistoryListEntry(dbIndex, testId, Utils.getGroupFromTestIndex(testId), wasSuccessful,
              usesQuestions, usesRoadSigns, usesIntersections, points, maxPoints, amountCorrect,
              amountIncorrect, amountUnanswered, elapsedTime, answersString, dateTime));
    }

    cHistory.close();
    dbHelper.close();
    db.close();

    return results;
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
