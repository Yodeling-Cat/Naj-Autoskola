package com.spiraclestudios.autoskola;

import android.provider.BaseColumns;

/**
 * Created by benji on 19/10/2015.
 */
public final class DatabaseContract {
    // To prevent someone from accidentally instantiating the contract class,
    // give it an empty constructor.
    public DatabaseContract() {}

    /* Inner class that defines the table contents */
    public static abstract class Testy implements BaseColumns {
        public static final String TABLE_NAME = "Testy";
        public static final String COLUMN_NAME_VERSION_CODE = "versionCode";
        public static final String COLUMN_NAME_VERSION_NAME = "versionName";
    }

    public static abstract class Otazky implements BaseColumns {
        public static final String TABLE_NAME = "Otazky";
        public static final String COLUMN_NAME_VERSION_CODE = "versionCode";
        public static final String COLUMN_NAME_VERSION_NAME = "versionName";
    }

    public static abstract class Znacky implements BaseColumns {
        public static final String TABLE_NAME = "Znacky";
        public static final String COLUMN_NAME_VERSION_CODE = "versionCode";
        public static final String COLUMN_NAME_VERSION_NAME = "versionName";
    }

    private static final String SQL_CREATE =
            "CREATE TABLE IF NOT EXISTS " + Testy.TABLE_NAME + " (" +
                    Testy._ID + " INTEGER PRIMARY KEY," +
                    Testy.COLUMN_NAME_VERSION_CODE + " INTEGER, " +
                    Testy.COLUMN_NAME_VERSION_NAME + " TEXT)";

    private static final String SQL_DELETE_STATIC_TABLES =
            "DROP TABLE IF EXISTS " + Testy.TABLE_NAME + "," + Otazky.TABLE_NAME + "," + Znacky.TABLE_NAME + ";";

    // [Getters]
    public static String getSqlCreate() {
        return SQL_CREATE;
    }

    public static String getSqlDeleteStaticTables() {
        return SQL_DELETE_STATIC_TABLES;
    }
}