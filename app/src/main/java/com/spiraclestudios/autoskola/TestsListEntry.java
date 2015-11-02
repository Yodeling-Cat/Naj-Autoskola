package com.spiraclestudios.autoskola;

import android.util.Log;

/**
 * Created by benji on 15/10/2015.
 */
public class TestsListEntry {
    private int mIndex;

    TestsListEntry(int index){
        setIndex(index);
    }

    public int getIndex() {
        return mIndex;
    }

    public void setIndex(int index) {
        mIndex = index;
    }
}