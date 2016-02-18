/*
 * Copyright 2015-2016 Spiracle Studios. All Rights Reserved.
 */

package com.spiraclestudios.autoskola;

import android.content.Context;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.io.InputStream;
import java.util.ArrayList;

import timber.log.Timber;

/**
 * Original created by benji on 20/10/2015.
 */
public class DbHelper extends SQLiteOpenHelper {
    private static final String TAG = "DbHelper";
    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 5;
    public static final String DATABASE_NAME = "database.db";
    private Context context;


    public DbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        DbContract.deleteStaticTables(db);
        onCreate(db);
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    public void onCreate(SQLiteDatabase db) {
        Timber.d("Database did not exist, creating.");

        db.execSQL(DbContract.SQL_CREATE_TESTS);
        db.execSQL(DbContract.SQL_CREATE_QUESTIONS);
        db.execSQL(DbContract.SQL_CREATE_ROAD_SIGNS);
        db.execSQL(DbContract.SQL_CREATE_HISTORY);

        // [Populate the static tables]
        // TODO: CLEAN-UP: I use the same code for all of them just different file names

        // Tests table
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
            AssetManager assetManager = context.getAssets();
            try {
                input = assetManager.open("Tests.sql");

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
                            //Timber.d(TAG, "Executing line of SQL: " + line);
                        }
                    }
                }
            } catch (Exception ex) {
                Timber.e("Error occurred while trying to populate database table 'Tests' " +
                        "from asset file Tests.sql");
                ex.printStackTrace();
            }
            db.setTransactionSuccessful();
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            db.endTransaction();
        }

        // Questions table
        db.beginTransaction();
        try {
            InputStream input;
            AssetManager assetManager = context.getAssets();
            try {
                input = assetManager.open("Questions.sql");

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
                            //Timber.d(TAG, "Executing line of SQL: " + line);
                        }
                    }
                }
            } catch (Exception ex) {
                Timber.e("Error occurred while trying to populate database table 'Questions' " +
                        "from asset file Questions.sql");
                ex.printStackTrace();
            }
            db.setTransactionSuccessful();
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            db.endTransaction();
        }

        // RoadSigns table
        db.beginTransaction();
        try {
            InputStream input;
            AssetManager assetManager = context.getAssets();
            try {
                input = assetManager.open("RoadSigns.sql");

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
                            //Timber.d(TAG, "Executing line of SQL: " + line);
                        }
                    }
                }
            } catch (Exception ex) {
                Timber.e("Error occurred while trying to populate database table 'RoadSigns'" +
                        " from asset file RoadSigns.sql");
                ex.printStackTrace();
            }
            db.setTransactionSuccessful();
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            db.endTransaction();
        }
    }


    /**
     * Used by the DatabaseManagerActivity
     */
    public ArrayList<Cursor> getData(String Query) {
        SQLiteDatabase db = this.getWritableDatabase();
        String[] columns = new String[]{"message"};
        // an array list of cursor to save two cursors one has results from the query
        // other cursor stores error message if any errors are triggered
        ArrayList<Cursor> alc = new ArrayList<>(2);
        MatrixCursor Cursor2 = new MatrixCursor(columns);
        alc.add(null);
        alc.add(null);

        try {
            Cursor c = db.rawQuery(Query, null);

            //add value to cursor2
            Cursor2.addRow(new Object[]{"Success"});

            alc.set(1, Cursor2);
            if (null != c && c.getCount() > 0) {
                alc.set(0, c);
                c.moveToFirst();
                return alc;
            }
            return alc;
        } catch (SQLException sqlEx) {
            Timber.d(sqlEx.getMessage());
            // if an exception is thrown, save the error message to cursor and return the ArrayList
            Cursor2.addRow(new Object[]{"" + sqlEx.getMessage()});
            alc.set(1, Cursor2);
            return alc;

        } catch (Exception ex) {
            Timber.d(ex.getMessage());
            // if an exception is thrown, save the error message to cursor and return the ArrayList
            Cursor2.addRow(new Object[]{"" + ex.getMessage()});
            alc.set(1, Cursor2);
            return alc;
        }
    }
}