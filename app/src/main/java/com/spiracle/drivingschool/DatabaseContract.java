package com.spiracle.drivingschool;

import android.provider.BaseColumns;

public final class DatabaseContract {
	public DatabaseContract() {}

	/* Inner class that defines the table contents */
	public static abstract class PersonEntry implements BaseColumns {
		public static final String TABLE_NAME = "people";
		public static final String COLUMN_NAME_AVATAR = "avatar";
		public static final String COLUMN_NAME_NAME = "name";
		public static final String COLUMN_NAME_FUNDS = "funds";
		public static final String COLUMN_NAME_FAVORITE = "favorite";
	}

	// [Private]
	private static final String COMMA_SEP = ",";
	private static final String TEXT_TYPE = " TEXT";
	private static final String VARCHAR_TYPE = " VARCHAR(64)";
	private static final String INT_TYPE = " INTEGER";
	private static final String FLOAT_TYPE = " DECIMAL(10,2)";
	private static final String BOOL_TYPE = " BOOLEAN";

	private static final String SQL_CREATE_ENTRIES =
			"CREATE TABLE IF NOT EXISTS " + PersonEntry.TABLE_NAME + " (" +
					PersonEntry._ID + " INTEGER PRIMARY KEY," +
					PersonEntry.COLUMN_NAME_AVATAR + TEXT_TYPE + COMMA_SEP +
					PersonEntry.COLUMN_NAME_NAME + VARCHAR_TYPE + COMMA_SEP +
					PersonEntry.COLUMN_NAME_FUNDS + INT_TYPE + COMMA_SEP +
					PersonEntry.COLUMN_NAME_FAVORITE + BOOL_TYPE +
					" )";

	private static final String SQL_DELETE_ENTRIES =
			"DROP TABLE IF EXISTS " + PersonEntry.TABLE_NAME;

	// [Getters]
	public static String getSqlCreateEntries() {
		return SQL_CREATE_ENTRIES;
	}

	public static String getSqlDeleteEntries() {
		return SQL_DELETE_ENTRIES;
	}
}