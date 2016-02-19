/*
 * Copyright 2015-2016 Spiracle Studios. All Rights Reserved.
 */

package com.spiraclestudios.autoskola;

/**
 * Original created by benji on 15/10/2015.
 */
public class HistoryListEntry {
    private int mIndex;
    private Helper.Groups mCategory;
    private int mPoints;

    public HistoryListEntry(int index) {
        setIndex(index);
    }

    public int getIndex() {
        return mIndex;
    }

    public void setIndex(int index) {
        mIndex = index;
    }
}
