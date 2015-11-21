package com.spiraclestudios.autoskola;

import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

/**
 * Created by benji on 19/10/2015.
 */
public final class DbContract {
    // To prevent someone from accidentally instantiating the contract class,
    // give it an empty constructor.
    public DbContract() {}

    public static abstract class Tests implements BaseColumns {
        public static final String TABLE_NAME = "Tests";
        public static final String COLUMN_TEST_ID = "testId";
        public static final String COLUMN_VERSION_CODE = "versionCode";
        public static final String COLUMN_VERSION_NAME = "versionName";
        public static final String COLUMN_QUESTIONS = "questions";
    }

    public static abstract class Questions implements BaseColumns {
        public static final String TABLE_NAME = "Questions";
        public static final String COLUMN_QUESTION_ID = "questionId";
        public static final String COLUMN_TYPE = "type";
        public static final String COLUMN_VERSION = "version";
        public static final String COLUMN_QUESTION = "question";
        public static final String COLUMN_IMAGE = "image";
        public static final String COLUMN_POINTS = "points";
        public static final String COLUMN_CORRECT_ANSWER = "correctAnswer";
        public static final String COLUMN_ANSWER_1 = "answer1";
        public static final String COLUMN_ANSWER_2 = "answer2";
        public static final String COLUMN_ANSWER_3 = "answer3";
    }

    public static abstract class RoadSigns implements BaseColumns {
        public static final String TABLE_NAME = "RoadSigns";
        public static final String COLUMN_CATEGORY = "category";
        public static final String COLUMN_IDENTIFIER = "identifier";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_IMAGE = "image";
        public static final String COLUMN_DESCRIPTION = "description";
    }

    // SQL Queries
    public static final String SQL_CREATE_TESTY =
            "CREATE TABLE IF NOT EXISTS " + Tests.TABLE_NAME + " (" +
                    Tests._ID + " INTEGER PRIMARY KEY," +
                    Tests.COLUMN_TEST_ID + " INTEGER NOT NULL, " +
                    Tests.COLUMN_VERSION_CODE + " INTEGER NOT NULL, " +
                    Tests.COLUMN_VERSION_NAME + " TEXT NOT NULL, " +
                    Tests.COLUMN_QUESTIONS + " TEXT);";

    public static final String SQL_CREATE_OTAZKY =
            "CREATE TABLE IF NOT EXISTS " + Questions.TABLE_NAME + " (" +
                    Questions._ID + " INTEGER PRIMARY KEY," +
                    Questions.COLUMN_QUESTION_ID + " INTEGER, " +
                    Questions.COLUMN_TYPE + " INTEGER, " +
                    Questions.COLUMN_VERSION + " INTEGER, " +
                    Questions.COLUMN_QUESTION + " TEXT, " +
                    Questions.COLUMN_IMAGE + " TEXT, " +
                    Questions.COLUMN_POINTS + " INTEGER, " +
                    Questions.COLUMN_CORRECT_ANSWER + " INTEGER, " +
                    Questions.COLUMN_ANSWER_1 + " TEXT, " +
                    Questions.COLUMN_ANSWER_2 + " TEXT, " +
                    Questions.COLUMN_ANSWER_3 + " TEXT);";

    public static final String SQL_CREATE_ZNACKY =
            "CREATE TABLE IF NOT EXISTS " + RoadSigns.TABLE_NAME + " (" +
                    RoadSigns._ID + " INTEGER PRIMARY KEY," +
                    RoadSigns.COLUMN_CATEGORY + " INTEGER, " +
                    RoadSigns.COLUMN_IDENTIFIER + " TEXT, " +
                    RoadSigns.COLUMN_NAME + " TEXT, " +
                    RoadSigns.COLUMN_IMAGE + " TEXT, " +
                    RoadSigns.COLUMN_DESCRIPTION + " TEXT);";


    public static void deleteStaticTables(SQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS " + Tests.TABLE_NAME + ";");
        db.execSQL("DROP TABLE IF EXISTS " + Questions.TABLE_NAME + ";");
        db.execSQL("DROP TABLE IF EXISTS " + RoadSigns.TABLE_NAME + ";");
    }
}








