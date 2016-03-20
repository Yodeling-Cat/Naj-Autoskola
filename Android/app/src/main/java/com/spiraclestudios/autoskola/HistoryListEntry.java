/*
 * Copyright (c) 2015-2016. Spiracle Studios. All Rights Reserved.
 */

package com.spiraclestudios.autoskola;

/**
 * Added by benji on 19/2/2016.
 */
public class HistoryListEntry {

    private int mIndex;
    private Helper.Groups mGroup;
    private boolean mWasSuccessful;
    private int mPoints;
    private int mMaxPoints;
    private String mAnswers;
    private long mElapsedTime;
    private String mDate;
    private int mYear;
    private boolean mUsesQuestions;
    private boolean mUsesRoadSigns;
    private boolean mUsesIntersections;

    public HistoryListEntry(int index, Helper.Groups group, boolean wasSuccessful, boolean usesQuestions,
            boolean usesRoadSigns, boolean usesIntersections, int points, int maxPoints, long elapsedTime, String answersString, String date, int year) {

        setIndex(index);
        setGroup(group);
        setWasSuccessful(wasSuccessful);
        setUsesQuestions(usesQuestions);
        setUsesRoadSigns(usesRoadSigns);
        setUsesIntersections(usesIntersections);
        setPoints(points);
        setMaxPoints(maxPoints);
        setElapsedTime(elapsedTime);
        setAnswers(answersString);
        setDate(date);
        setYear(year);
    }

    public int getIndex() {

        return mIndex;
    }

    public void setIndex(int index) {

        mIndex = index;
    }

    public Helper.Groups getGroup() {

        return mGroup;
    }

    public void setGroup(Helper.Groups group) {

        mGroup = group;
    }

    public boolean getWasSuccessful() {

        return mWasSuccessful;
    }

    public void setWasSuccessful(boolean wasSuccessful) {

        mWasSuccessful = wasSuccessful;
    }

    public boolean getUsesQuestions() {

        return mUsesQuestions;
    }

    public void setUsesQuestions(boolean usesQuestions) {

        mUsesQuestions = usesQuestions;
    }

    public boolean getUsesRoadSigns() {

        return mUsesRoadSigns;
    }

    public void setUsesRoadSigns(boolean usesRoadSigns) {

        mUsesRoadSigns = usesRoadSigns;
    }

    public boolean getUsesIntersections() {

        return mUsesIntersections;
    }

    public void setUsesIntersections(boolean usesIntersections) {

        mUsesIntersections = usesIntersections;
    }

    public int getPoints() {

        return mPoints;
    }

    public void setPoints(int points) {

        mPoints = points;
    }

    public int getMaxPoints() {

        return mMaxPoints;
    }

    public void setMaxPoints(int maxPoints) {

        mMaxPoints = maxPoints;
    }

    public String getAnswers() {

        return mAnswers;
    }

    public void setAnswers(String answersString) {

        mAnswers = answersString;
    }

    public long getElapsedTime() {

        return mElapsedTime;
    }

    public void setElapsedTime(long elapsedTime) {

        mElapsedTime = elapsedTime;
    }

    public String getDate() {

        return mDate;
    }

    public void setDate(String date) {

        mDate = date;
    }

    public int getYear() {

        return mYear;
    }

    public void setYear(int year) {

        mYear = year;
    }
}
