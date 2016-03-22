/*
 * Copyright (c) 2015-2016. Spiracle Studios. All Rights Reserved.
 */

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
     * <p>NOTE: Implement appropriate upgrade code, otherwise you will be resetting the
     * database.</p>
     */
    public static final int DATABASE_VERSION = 7;
    public static final String DATABASE_NAME = "database.db";
    private Context context;

    public DbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    /** TODO: Write this comment :< */
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Timber.d("Upgrading database from version %d to version %d.", oldVersion, newVersion);

        if (oldVersion < 6 && newVersion >= 6) {
            if (oldVersion < 5) {
                DbContract.deleteStaticTables(db);
                onCreate(db);
            } else if (oldVersion == 5) {
                db.execSQL("DROP TABLE IF EXISTS " + DbContract.History.TABLE_NAME);
                db.execSQL(DbContract.SQL_CREATE_HISTORY);
                db.execSQL("DROP TABLE IF EXISTS " + DbContract.Rewards.TABLE_NAME);
            }
        }
        if (oldVersion < 7 && newVersion >= 7) {
            db.execSQL("ALTER TABLE " + DbContract.History.TABLE_NAME + " ADD COLUMN " + DbContract.History.COLUMN_DATE_TIME + " INTEGER");
        } else {
            DbContract.deleteStaticTables(db);
            onCreate(db);
        }
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Timber.d("Downgrading database from version %d to version %d.", oldVersion, newVersion);

        DbContract.deleteStaticTables(db);
        onCreate(db);
    }

    /**
     * What if I don't call DbContract.deleteStaticTables() before calling this? Would it just
     * append a duplicate of the tables to their contents?
     *
     * @param db The target database.
     */
    public void onCreate(SQLiteDatabase db) {
        Timber.d("Executing database onCreate() method.");

        // Create static tables
        db.execSQL(DbContract.SQL_CREATE_TESTS);
        db.execSQL(DbContract.SQL_CREATE_QUESTIONS);
        db.execSQL(DbContract.SQL_CREATE_ROAD_SIGNS);
        // Create dynamic tables
        db.execSQL(DbContract.SQL_CREATE_HISTORY);

        // Populate static tables
        AssetManager assetManager = context.getAssets();

        for (int i = 0; i < 3; i++) {
            String sqlFileName = "";
            switch (i) {
                case 0:
                    sqlFileName = "Tests.sql";
                    break;
                case 1:
                    sqlFileName = "Questions.sql";
                    break;
                case 2:
                    sqlFileName = "RoadSigns.sql";
                    break;
            }

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
                    Timber.e("Error occurred while trying to populate a database table from asset file %s", sqlFileName);
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
}