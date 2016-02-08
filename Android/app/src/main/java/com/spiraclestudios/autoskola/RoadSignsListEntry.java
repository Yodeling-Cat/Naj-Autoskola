/*
 * Copyright 2015-2016 Spiracle Studios. All Rights Reserved.
 */

package com.spiraclestudios.autoskola;

/**
 * Original created by benji on 4/2/2016.
 */
public class RoadSignsListEntry {

    // Pretty name, "Výstražné značky", etc.
    private String mRoadSignName;
    private String mRoadSignDesc;
    private String mImagePath;


    public RoadSignsListEntry(String roadSignName, String roadSignDesc, String imagePath) {
        setRoadSignName(roadSignName);
        setRoadSignDesc(roadSignDesc);
        setImagePath(imagePath);
    }

    public String getRoadSignName() {
        return mRoadSignName;
    }

    public void setRoadSignName(String roadSignName) {
        mRoadSignName = roadSignName;
    }

    public String getRoadSignDesc() {
        return mRoadSignDesc;
    }

    public void setRoadSignDesc(String roadSignDesc) {
        mRoadSignDesc = roadSignDesc;
    }

    public String getImagePath() {
        return mImagePath;
    }

    public void setImagePath(String imagePath) {
        mImagePath = imagePath;
    }
}