package com.spiraclestudios.autoskola;

/**
 * Created by benji on 15/10/2015.
 */
public class DataObject {
    private int mIndex;

    DataObject (int index){
        mIndex = index;
    }

    public int getIndex() {
        return mIndex;
    }

    public void setIndex(int index) {
        mIndex = index;
    }
}