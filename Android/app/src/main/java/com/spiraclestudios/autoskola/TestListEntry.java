// Copyright (c) 2015-2017. Spiracle Software. All Rights Reserved.

package com.spiraclestudios.autoskola;

/**
 * Added by benji on 15/10/2015.
 */
public class TestListEntry implements ListEntry {

  private int mIndex;
  private int mTimesCompleted;

  public TestListEntry(int index, int timesCompleted) {

    setIndex(index);
    setTimesCompleted(timesCompleted);
  }

  public int getIndex() {

    return mIndex;
  }

  public void setIndex(int index) {

    mIndex = index;
  }

  public int getTimesCompleted() {

    return mTimesCompleted;
  }

  public void setTimesCompleted(int timesCompleted) {

    mTimesCompleted = timesCompleted;
  }
}
