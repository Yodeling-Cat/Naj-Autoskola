package com.spiraclestudios.autoskola;

import android.content.Context;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.InputStream;
import java.util.ArrayList;

/**
 * Created by benji on 20/10/2015.
 */
public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String TAG = "DatabaseHelper";
    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "database.db";
    private Context context;


    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    public void onCreate(SQLiteDatabase db) {
        Log.d(TAG, "Database did not exist, creating.");

        db.execSQL(DatabaseContract.SQL_CREATE_TESTY);
        db.execSQL(DatabaseContract.SQL_CREATE_OTAZKY);
        db.execSQL(DatabaseContract.SQL_CREATE_ZNACKY);

        // [Populate the static tables]
        // TODO: I use the same code for all of them just different file names
        // TODO: but if I am going to switch to .CSV format then they will be different so it's ok

        // Testy table
        db.beginTransaction();
        try
        {
//            for (int i = 1; i < 61; i++) {
//                ContentValues values = new ContentValues();
//                values.put(DatabaseContract.Testy._ID, i);
//                values.put(DatabaseContract.Testy.COLUMN_VERSION_CODE, 1);
//                values.put(DatabaseContract.Testy.COLUMN_VERSION_NAME, "2015-v1");
//                db.insert(DatabaseContract.Testy.TABLE_NAME, null, values);
//            }
            InputStream input;
            AssetManager assetManager = context.getAssets();
            try {
                input = assetManager.open("Testy.sql");

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
                            //Log.d(TAG, "Executing line of SQL: " + line);
                        }
                    }
                }
            } catch (Exception ex) {
                Log.e(TAG, "Error occurred while trying to populate database table 'Testy' " +
                        "from asset file Testy.sql");
                ex.printStackTrace();
            }
            db.setTransactionSuccessful();
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            db.endTransaction();
        }

        // Otazky table
        db.beginTransaction();
        try
        {
            InputStream input;
            AssetManager assetManager = context.getAssets();
            try {
                input = assetManager.open("Otazky.sql");

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
                            //Log.d(TAG, "Executing line of SQL: " + line);
                        }
                    }
                }
            } catch (Exception ex) {
                Log.e(TAG, "Error occurred while trying to populate database table 'Otazky' " +
                        "from asset file Otazky.sql");
                ex.printStackTrace();
            }
            db.setTransactionSuccessful();
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            db.endTransaction();
        }

        // Znacky table
        db.beginTransaction();
        try
        {
            InputStream input;
            AssetManager assetManager = context.getAssets();
            try {
                input = assetManager.open("Znacky.sql");

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
                            //Log.d(TAG, "Executing line of SQL: " + line);
                        }
                    }
                }
            } catch (Exception ex) {
                Log.e(TAG, "Error occurred while trying to populate database table 'Znacky'" +
                        " from asset file Znacky.sql");
                ex.printStackTrace();
            }
            db.setTransactionSuccessful();
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            db.endTransaction();
        }
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        DatabaseContract.deleteStaticTables(db);
        onCreate(db);
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    // Used by the DatabaseManager Activity
    public ArrayList<Cursor> getData(String Query){
        SQLiteDatabase db = this.getWritableDatabase();
        String[] columns = new String[] { "message" };
        // an array list of cursor to save two cursors one has results from the query
        // other cursor stores error message if any errors are triggered
        ArrayList<Cursor> alc = new ArrayList<Cursor>(2);
        MatrixCursor Cursor2= new MatrixCursor(columns);
        alc.add(null);
        alc.add(null);

        try{
            Cursor c = db.rawQuery(Query, null);

            //add value to cursor2
            Cursor2.addRow(new Object[] { "Success" });

            alc.set(1,Cursor2);
            if (null != c && c.getCount() > 0) {

                alc.set(0,c);
                c.moveToFirst();

                return alc ;
            }
            return alc;
        } catch(SQLException sqlEx){
            Log.d(TAG, sqlEx.getMessage());
            // if an exception is thrown, save the error message to cursor and return the ArrayList
            Cursor2.addRow(new Object[] { ""+sqlEx.getMessage() });
            alc.set(1,Cursor2);
            return alc;

        } catch(Exception ex){
            Log.d(TAG, ex.getMessage());
            // if an exception is thrown, save the error message to cursor and return the ArrayList
            Cursor2.addRow(new Object[] { ""+ex.getMessage() });
            alc.set(1,Cursor2);
            return alc;
        }
    }
}