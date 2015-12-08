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
        public static final String COLUMN_TEST_ID = "test_id";
        public static final String COLUMN_VERSION_CODE = "version_code";
        public static final String COLUMN_VERSION_NAME = "version_name";
        public static final String COLUMN_QUESTIONS = "questions";
    }

    public static abstract class Questions implements BaseColumns {
        public static final String TABLE_NAME = "Questions";
        public static final String COLUMN_QUESTION_ID = "question_id";
        public static final String COLUMN_TYPE = "type";
        public static final String COLUMN_VERSION = "version";
        public static final String COLUMN_QUESTION = "question";
        public static final String COLUMN_IMAGE = "image";
        public static final String COLUMN_POINTS = "points";
        public static final String COLUMN_CORRECT_ANSWER = "correct_answer";
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

    public static abstract class History implements BaseColumns {
        public static final String TABLE_NAME = "History";
        public static final String COLUMN_TEST_ID = "test_id";
        public static final String COLUMN_TEST_VERSION = "test_version";
        public static final String COLUMN_USES_QUESTIONS = "uses_questions";
        public static final String COLUMN_USES_ROAD_SIGNS = "uses_road_signs";
        public static final String COLUMN_USES_INTERSECTIONS = "uses_intersections";
        public static final String COLUMN_POINTS = "points";
        public static final String COLUMN_MAX_POINTS = "max_points";
        public static final String COLUMN_ELAPSED_TIME = "elapsed_time";
        public static final String COLUMN_ANSWERS = "answers";
    }

    public static abstract class Rewards implements BaseColumns {
        public static final String TABLE_NAME = "Rewards";
        public static final String COLUMN_STARS = "stars";
        public static final String COLUMN_THEMES = "themes";
    }

    // SQL Queries
    public static final String SQL_CREATE_TESTS =
            "CREATE TABLE IF NOT EXISTS " + Tests.TABLE_NAME + " (" +
                    Tests._ID + " INTEGER PRIMARY KEY," +
                    Tests.COLUMN_TEST_ID + " INTEGER DEFAULT 1 NOT NULL, " +
                    Tests.COLUMN_VERSION_CODE + " INTEGER DEFAULT 1 NOT NULL, " +
                    Tests.COLUMN_VERSION_NAME + " TEXT DEFAULT '2015' NOT NULL, " +
                    Tests.COLUMN_QUESTIONS + " TEXT);";

    public static final String SQL_CREATE_QUESTIONS =
            "CREATE TABLE IF NOT EXISTS " + Questions.TABLE_NAME + " (" +
                    Questions._ID + " INTEGER PRIMARY KEY," +
                    Questions.COLUMN_QUESTION_ID + " INTEGER DEFAULT 1 NOT NULL, " +
                    Questions.COLUMN_TYPE + " INTEGER DEFAULT 0 NOT NULL, " +
                    Questions.COLUMN_VERSION + " INTEGER DEFAULT 1 NOT NULL, " +
                    Questions.COLUMN_QUESTION + " TEXT NOT NULL, " +
                    Questions.COLUMN_IMAGE + " TEXT, " +
                    Questions.COLUMN_POINTS + " INTEGER NOT NULL, " +
                    Questions.COLUMN_CORRECT_ANSWER + " INTEGER NOT NULL, " +
                    Questions.COLUMN_ANSWER_1 + " TEXT, " +
                    Questions.COLUMN_ANSWER_2 + " TEXT, " +
                    Questions.COLUMN_ANSWER_3 + " TEXT);";

    public static final String SQL_CREATE_ROAD_SIGNS =
            "CREATE TABLE IF NOT EXISTS " + RoadSigns.TABLE_NAME + " (" +
                    RoadSigns._ID + " INTEGER PRIMARY KEY," +
                    RoadSigns.COLUMN_CATEGORY + " TEXT NOT NULL, " +
                    RoadSigns.COLUMN_IDENTIFIER + " TEXT NOT NULL, " +
                    RoadSigns.COLUMN_NAME + " TEXT NOT NULL, " +
                    RoadSigns.COLUMN_IMAGE + " TEXT NOT NULL, " +
                    RoadSigns.COLUMN_DESCRIPTION + " TEXT);";

    public static final String SQL_CREATE_HISTORY =
            "CREATE TABLE IF NOT EXISTS " + History.TABLE_NAME + " (" +
                    History._ID + " INTEGER PRIMARY KEY," +
                    History.COLUMN_TEST_ID + " INTEGER NOT NULL, " +
                    History.COLUMN_TEST_VERSION + " INTEGER NOT NULL, " +
                    History.COLUMN_USES_QUESTIONS + " INTEGER DEFAULT 1 NOT NULL, " +
                    History.COLUMN_USES_ROAD_SIGNS + " INTEGER DEFAULT 1 NOT NULL, " +
                    History.COLUMN_USES_INTERSECTIONS + " INTEGER DEFAULT 1 NOT NULL, " +
                    History.COLUMN_POINTS + " INTEGER DEFAULT 0 NOT NULL, " +
                    History.COLUMN_MAX_POINTS + " INTEGER DEFAULT 55 NOT NULL, " +
                    History.COLUMN_ELAPSED_TIME + " INTEGER DEFAULT 0 NOT NULL, " +
                    History.COLUMN_ANSWERS + " TEXT);";

    public static final String SQL_CREATE_REWARDS =
            "CREATE TABLE IF NOT EXISTS " + Rewards.TABLE_NAME + " (" +
                    Rewards._ID + " INTEGER PRIMARY KEY," +
                    Rewards.COLUMN_STARS + " INTEGER DEFAULT 0 NOT NULL, " +
                    Rewards.COLUMN_THEMES + " TEXT);";


    public static void deleteStaticTables(SQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS " + Tests.TABLE_NAME + ";");
        db.execSQL("DROP TABLE IF EXISTS " + Questions.TABLE_NAME + ";");
        db.execSQL("DROP TABLE IF EXISTS " + RoadSigns.TABLE_NAME + ";");
    }
}








