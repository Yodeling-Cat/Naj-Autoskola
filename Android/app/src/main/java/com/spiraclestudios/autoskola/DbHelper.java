// Copyright (c) 2015-2017. Spiracle Software. All Rights Reserved.

package com.spiraclestudios.autoskola;

import android.content.Context;
import android.content.res.AssetManager;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.io.InputStream;
import timber.log.Timber;

/**
 * Added by benji on 20/10/2015.
 */
public class DbHelper extends SQLiteOpenHelper {

  /**
   * If you change the database schema, you must increment the database version.
   * <p>NOTE: Implement appropriate upgrade code, otherwise the database will get wiped.</p>
   */
  public static final int DATABASE_VERSION = 9;
  public static final String DATABASE_NAME = "database.db";
  private Context context;

  public DbHelper(Context context) {
    super(context, DATABASE_NAME, null, DATABASE_VERSION);
    this.context = context;
  }

  /**
   * Runs upgrade code for each database version between the old and new version, in order.
   * So if you were upgrading from version 4 to 7, it would first upgrade to version 5 then 6 and
   * then 7.
   *
   * @param db Target database.
   * @param oldVersion Version we're upgrading from.
   * @param newVersion Version we're upgrading to.
   */
  public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    Timber.d("Upgrading database from version %d to version %d.", oldVersion, newVersion);

    for (int version = oldVersion + 1; version <= newVersion; version++) {
      switch (version) {
        case 6:
          db.execSQL("DROP TABLE IF EXISTS " + DbContract.History.TABLE_NAME);
          db.execSQL(DbContract.SQL_CREATE_HISTORY);
          db.execSQL("DROP TABLE IF EXISTS " + DbContract.Rewards.TABLE_NAME);
          break;
        case 7:
          db.execSQL("ALTER TABLE "
              + DbContract.History.TABLE_NAME
              + " ADD COLUMN "
              + DbContract.History.COLUMN_DATE_TIME
              + " INTEGER");
          break;
        case 8:
          db.execSQL("DROP TABLE IF EXISTS " + DbContract.RoadSigns.TABLE_NAME);
          db.execSQL(DbContract.SQL_CREATE_ROAD_SIGNS);
          break;
        case 9:
          db.execSQL("DROP TABLE IF EXISTS " + DbContract.Questions.TABLE_NAME);
          createQuestionsTable(db);
          break;
        default:
          // Wiping the database deletes all the users data.
          // Always implement an appropriate upgrade for new database versions!
          // I don't delete the dynamic tables, like history.
          DbContract.deleteStaticTables(db);
          onCreate(db);
          Timber.d(
              "No migration code defined for database version %d. Deleting and recreating everything.",
              version);
          break;
      }
    }
  }

  public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    Timber.d("Downgrading database from version %d to version %d.", oldVersion, newVersion);

    DbContract.deleteStaticTables(db);
    onCreate(db);
  }

  private void createTestsTable(SQLiteDatabase db) {
    db.execSQL(DbContract.SQL_CREATE_TESTS);
    executeSQLFromFile(db, "Tests.sql");
  }

  private void createQuestionsTable(SQLiteDatabase db) {
    db.execSQL(DbContract.SQL_CREATE_QUESTIONS);
    executeSQLFromFile(db, "Questions.sql");
  }

  private void createRoadSignsTable(SQLiteDatabase db) {
    db.execSQL(DbContract.SQL_CREATE_ROAD_SIGNS);
    executeSQLFromFile(db, "RoadSigns.sql");
  }

  // What if I don't call DbContract.deleteStaticTables() before calling this? Would it just
  // append a duplicate of the tables to their contents?
  public void onCreate(SQLiteDatabase db) {
    Timber.d("Executing database onCreate() method.");

    createTestsTable(db);
    createQuestionsTable(db);
    createRoadSignsTable(db);

    // Create dynamic tables
    db.execSQL(DbContract.SQL_CREATE_HISTORY);
  }

  private void executeSQLFromFile(SQLiteDatabase db, String sqlFileName) {
    AssetManager assetManager = context.getAssets();

    db.beginTransaction();
    try {
      //            for (int i = 1; i < 61; i++) {
      //                ContentValues values = new ContentValues();
      //                values.put(DbContract.Tests._ID, i);
      //                values.put(DbContract.Tests.COLUMN_VERSION_CODE, 1);
      //                values.put(DbContract.Tests.COLUMN_VERSION_NAME, "2015-v1");
      //                db.insert(DbContract.Tests.TABLE_NAME, null, values);
      //            }
      InputStream input;
      try {
        input = assetManager.open(sqlFileName);

        if (input != null) {
          int size = input.available();
          byte[] buffer = new byte[size];
          input.read(buffer);
          input.close();
          // byte buffer into a string
          String text = new String(buffer);
          String[] lines = text.split("\\r?\\n");

          for (String line : lines) {
            if (line.startsWith(("INSERT INTO"))) {
              db.execSQL(line);
            }
          }
        }
      } catch (Exception ex) {
        Timber.e("Error occurred while trying to populate a database table from asset file %s",
            sqlFileName);
        ex.printStackTrace();
      }
      db.setTransactionSuccessful();
    } catch (SQLException ex) {
      ex.printStackTrace();
    } finally {
      db.endTransaction();
    }
  }
}