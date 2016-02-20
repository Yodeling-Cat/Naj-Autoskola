/*
 * Copyright 2015-2016 Spiracle Studios. All Rights Reserved.
 */

package com.spiraclestudios.autoskola;

/**
 * Original created by benji on 19/2/2016.
 */
public class HistoryListEntry {

	private int mIndex;
	private Helper.Groups mGroup;
	private int mPoints;
	private int mMaxPoints;
	private String mTime;
	private String mDate;
	private int mYear;

	public HistoryListEntry ( int index, int points, int maxPoints, String time, String date, int year ) {

		setIndex( index );
		setGroup( Helper.getGroupFromTestIndex( index ) );
		setPoints( points );
		setMaxPoints( maxPoints );
		setTime( time );
		setDate( date );
		setYear( year );
	}

	public int getIndex ( ) {

		return mIndex;
	}

	public void setIndex ( int index ) {

		mIndex = index;
	}

	public Helper.Groups getGroup ( ) {

		return mGroup;
	}

	public void setGroup ( Helper.Groups group ) {

		mGroup = group;
	}

	public int getPoints ( ) {

		return mPoints;
	}

	public void setPoints ( int points ) {

		mPoints = points;
	}

	public int getMaxPoints ( ) {

		return mMaxPoints;
	}

	public void setMaxPoints ( int maxPoints ) {

		mMaxPoints = maxPoints;
	}

	public String getTime ( ) {

		return mTime;
	}

	public void setTime ( String time ) {

		mTime = time;
	}

	public String getDate ( ) {

		return mDate;
	}

	public void setDate ( String date ) {

		mDate = date;
	}

	public int getYear ( ) {

		return mYear;
	}

	public void setYear ( int year ) {

		mYear = year;
	}
}
