package com.spiraclestudios.autoskola;

import android.database.sqlite.SQLiteDatabase;
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
        public static final String COLUMN_TEST_ID = "testId";
        public static final String COLUMN_VERSION_CODE = "versionCode";
        public static final String COLUMN_VERSION_NAME = "versionName";
        public static final String COLUMN_QUESTIONS = "questions";
    }

    public static abstract class Otazky implements BaseColumns {
        public static final String TABLE_NAME = "Otazky";
        public static final String COLUMN_QUESTION_ID = "questionId";
        public static final String COLUMN_VERSION = "version";
        public static final String COLUMN_QUESTION = "question";
        public static final String COLUMN_IMAGE = "image";
        public static final String COLUMN_POINTS = "points";
        public static final String COLUMN_CORRECT_ANSWER = "correctAnswer";
        public static final String COLUMN_ANSWER_1 = "answer1";
        public static final String COLUMN_ANSWER_2 = "answer2";
        public static final String COLUMN_ANSWER_3 = "answer3";
    }

    public static abstract class Znacky implements BaseColumns {
        public static final String TABLE_NAME = "Znacky";
        public static final String COLUMN_CATEGORY = "category";
        public static final String COLUMN_IDENTIFIER = "identifier";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_IMAGE = "image";
        public static final String COLUMN_DESCRIPTION = "description";
    }

    public static final String SQL_CREATE_TESTY =
            "CREATE TABLE IF NOT EXISTS " + Testy.TABLE_NAME + " (" +
                    Testy._ID + " INTEGER PRIMARY KEY," +
                    Testy.COLUMN_TEST_ID + " INTEGER NOT NULL, " +
                    Testy.COLUMN_VERSION_CODE + " INTEGER NOT NULL, " +
                    Testy.COLUMN_VERSION_NAME + " TEXT NOT NULL, " +
                    Testy.COLUMN_QUESTIONS + " TEXT);";

    public static final String SQL_CREATE_OTAZKY =
            "CREATE TABLE IF NOT EXISTS " + Otazky.TABLE_NAME + " (" +
                    Otazky._ID + " INTEGER PRIMARY KEY," +
                    Otazky.COLUMN_QUESTION_ID + " INTEGER, " +
                    Otazky.COLUMN_VERSION + " INTEGER, " +
                    Otazky.COLUMN_QUESTION + " TEXT, " +
                    Otazky.COLUMN_IMAGE + " TEXT, " +
                    Otazky.COLUMN_POINTS + " INTEGER, " +
                    Otazky.COLUMN_CORRECT_ANSWER + " INTEGER, " +
                    Otazky.COLUMN_ANSWER_1 + " TEXT, " +
                    Otazky.COLUMN_ANSWER_2 + " TEXT, " +
                    Otazky.COLUMN_ANSWER_3 + " TEXT);";

    public static final String SQL_CREATE_ZNACKY =
            "CREATE TABLE IF NOT EXISTS " + Znacky.TABLE_NAME + " (" +
                    Znacky._ID + " INTEGER PRIMARY KEY," +
                    Znacky.COLUMN_CATEGORY + " INTEGER, " +
                    Znacky.COLUMN_IDENTIFIER + " TEXT, " +
                    Znacky.COLUMN_NAME + " TEXT, " +
                    Znacky.COLUMN_IMAGE + " TEXT, " +
                    Znacky.COLUMN_DESCRIPTION + " TEXT);";

    // [Getters]
    //public static String getSqlCreate() {
    //    return SQL_CREATE;
    //}


    public static void deleteStaticTables(SQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS " + DatabaseContract.Testy.TABLE_NAME + ";");
        db.execSQL("DROP TABLE IF EXISTS " + DatabaseContract.Otazky.TABLE_NAME + ";");
        db.execSQL("DROP TABLE IF EXISTS " + DatabaseContract.Znacky.TABLE_NAME + ";");
    }
}








