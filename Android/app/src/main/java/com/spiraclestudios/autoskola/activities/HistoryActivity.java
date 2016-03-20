/*
 * Copyright (c) 2015-2016. Spiracle Studios. All Rights Reserved.
 */

package com.spiraclestudios.autoskola.activities;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.NavUtils;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.spiraclestudios.autoskola.DbContract;
import com.spiraclestudios.autoskola.DbHelper;
import com.spiraclestudios.autoskola.Helper;
import com.spiraclestudios.autoskola.HistoryListAdapter;
import com.spiraclestudios.autoskola.HistoryListEntry;
import com.spiraclestudios.autoskola.ListItemDecoration;
import com.spiraclestudios.autoskola.R;
import com.spiraclestudios.autoskola.interfaces.IBaseActivity;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Added by benji on 26/2/2016.
 * <p>
 * If EXTRA_TEST_ID == 0, shows the global history, otherwise shows history for the passed test id.
 * </p>
 */
public class HistoryActivity extends BaseActivity
        implements IBaseActivity {

    public final static String EXTRA_TEST_ID = "com.spiraclestudios.autoskola.TEST_ID";

    public String activityName = "HistoryActivity";

    private int testIndex;

    @Bind(R.id.recycler_view)
    public RecyclerView recycler_view;
    @Bind(R.id.empty_state)
    public TextView empty_state;

    public String getActivityName() {
        return activityName;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Helper.setTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        ButterKnife.bind(this);

        Intent intent = getIntent();
        testIndex = intent.getIntExtra(EXTRA_TEST_ID, 0);

        // Set up Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (testIndex != 0) {
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.setSubtitle("Test " + testIndex);
                actionBar.setDisplayHomeAsUpEnabled(true);
            }
            ((DrawerLayout) findViewById(R.id.nav_drawer_layout)).setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        } else {
            // Set up Navigation Drawer
            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.nav_drawer_layout);
            ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar
                    , R.string.cd_navigation_drawer_open,
                    R.string.cd_navigation_drawer_close);
            drawer.addDrawerListener(toggle);
            toggle.syncState();

            NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
            navigationView.setNavigationItemSelectedListener(this);
        }

        // Set up the recycler_view
        ArrayList<HistoryListEntry> dataSet = getDataSet();
        if (dataSet.size() != 0) {
            recycler_view.setHasFixedSize(true);
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
            recycler_view.setLayoutManager(layoutManager);
            RecyclerView.Adapter<HistoryListAdapter.ViewHolder> adapter =
                    new HistoryListAdapter(dataSet);
            ((HistoryListAdapter) adapter).setContext(this);
            recycler_view.setAdapter(adapter);
            recycler_view.addItemDecoration(new ListItemDecoration(this));
            //registerForContextMenu(recycler_view);
            //recycler_view.setLongClickable(true);
        } else {
            recycler_view.setVisibility(View.GONE);
            empty_state.setVisibility(View.VISIBLE);
        }

        Helper.initializeDebugDrawer(this);
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
        String query = "SELECT " +
                DbContract.History._ID + ", " +
                DbContract.History.COLUMN_TEST_ID + ", " +
                DbContract.History.COLUMN_USES_QUESTIONS + ", " +
                DbContract.History.COLUMN_USES_ROAD_SIGNS + ", " +
                DbContract.History.COLUMN_USES_INTERSECTIONS + ", " +
                DbContract.History.COLUMN_POINTS + ", " +
                DbContract.History.COLUMN_MAX_POINTS + ", " +
                DbContract.History.COLUMN_ELAPSED_TIME + ", " +
                DbContract.History.COLUMN_ANSWERS +
                " FROM " + DbContract.History.TABLE_NAME;

        if (testIndex != 0) {
            query += " WHERE " + DbContract.History.COLUMN_TEST_ID + " == ?";
            selectionArgs = new String[] { Integer.toString(testIndex) };
        }

        query += " ORDER BY " + DbContract.History._ID + " DESC";

        Cursor cHistory = db.rawQuery(query, selectionArgs);

        for (cHistory.moveToFirst(); !cHistory.isAfterLast(); cHistory.moveToNext()) {
            int testId = cHistory.getInt(cHistory.getColumnIndexOrThrow(DbContract.History.COLUMN_TEST_ID));
            boolean usesQuestions = cHistory.getInt(cHistory.getColumnIndexOrThrow(DbContract.History.COLUMN_USES_QUESTIONS)) != 0;
            boolean usesRoadSigns = cHistory.getInt(cHistory.getColumnIndexOrThrow(DbContract.History.COLUMN_USES_ROAD_SIGNS)) != 0;
            boolean usesIntersections = cHistory.getInt(cHistory.getColumnIndexOrThrow(DbContract.History.COLUMN_USES_INTERSECTIONS)) != 0;
            int points = cHistory.getInt(cHistory.getColumnIndexOrThrow(DbContract.History.COLUMN_POINTS));
            int maxPoints = cHistory.getInt(cHistory.getColumnIndexOrThrow(DbContract.History.COLUMN_MAX_POINTS));
            long elapsedTime = cHistory.getLong(cHistory.getColumnIndexOrThrow(DbContract.History.COLUMN_ELAPSED_TIME));
            String answersString = cHistory.getString(cHistory.getColumnIndexOrThrow(DbContract.History.COLUMN_ANSWERS));

            // Did the user pass the test?
            boolean wasSuccessful = points >= 50 && (elapsedTime / 1000) / 60 <= 20;

            results.add(new HistoryListEntry(testId, Helper.getGroupFromTestIndex(testId), wasSuccessful,
                    usesQuestions, usesRoadSigns, usesIntersections, points, maxPoints, elapsedTime, answersString, "XX.X.", 2000));
        }

        cHistory.close();
        dbHelper.close();
        db.close();

        return results;
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.nav_drawer_layout);
        if (drawer != null && drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (testIndex != 0)
                super.onBackPressed();
            else
                NavUtils.navigateUpTo(this, new Intent(this, MainActivity.class));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_history, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            //NavUtils.navigateUpTo(this, new Intent(this, MainActivity.class));
            super.onBackPressed();
            return true;
        } else if (id == R.id.action_delete) {
            Toast.makeText(this, R.string.toast_not_yet_implemented, Toast.LENGTH_SHORT).show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
