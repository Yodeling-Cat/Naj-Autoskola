/*
 * Copyright (c) 2015-2017. Spiracle Studios. All Rights Reserved.
 */

package com.spiraclestudios.autoskola;

/**
 * Added by benji on 19/2/2016.
 */
public class HistoryListEntry {

  private int mDbIndex;
  private int mIndex;
  private Helper.Groups mGroup;
  private boolean mWasSuccessful;
  private int mPoints;
  private int mMaxPoints;
  private int mAmountCorrect;
  private int mAmountIncorrect;
  private long mElapsedTime;
  private String mAnswers;
  private long mDateTime;
  private boolean mUsesQuestions;
  private boolean mUsesRoadSigns;
  private boolean mUsesIntersections;

  public HistoryListEntry(int dbIndex, int index, Helper.Groups group, boolean wasSuccessful,
      boolean usesQuestions, boolean usesRoadSigns, boolean usesIntersections, int points,
      int maxPoints, int amountCorrect, int amountIncorrect, long elapsedTime, String answersString,
      long dateTime) {
    setDbIndex(dbIndex);
    setIndex(index);
    setGroup(group);
    setWasSuccessful(wasSuccessful);
    setUsesQuestions(usesQuestions);
    setUsesRoadSigns(usesRoadSigns);
    setUsesIntersections(usesIntersections);
    setPoints(points);
    setMaxPoints(maxPoints);
    setAmountCorrect(amountCorrect);
    setAmountIncorrect(amountIncorrect);
    setElapsedTime(elapsedTime);
    setAnswers(answersString);
    setDateTime(dateTime);
  }

  public int getDbIndex() {
    return mDbIndex;
  }

  public void setDbIndex(int dbIndex) {
    mDbIndex = dbIndex;
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

  public int getAmountCorrect() {
    return mAmountCorrect;
  }

  public void setAmountCorrect(int amountCorrect) {
    mAmountCorrect = amountCorrect;
  }

  public int getAmountIncorrect() {
    return mAmountIncorrect;
  }

  public void setAmountIncorrect(int amountIncorrect) {
    mAmountIncorrect = amountIncorrect;
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

  public long getDateTime() {
    return mDateTime;
  }

  public void setDateTime(long dateTime) {
    mDateTime = dateTime;
  }
}
