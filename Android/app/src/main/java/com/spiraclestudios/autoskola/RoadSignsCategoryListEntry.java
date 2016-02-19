/*
 * Copyright 2015-2016 Spiracle Studios. All Rights Reserved.
 */

package com.spiraclestudios.autoskola;

/**
 * Original created by benji on 4/2/2016.
 */
public class RoadSignsCategoryListEntry {

	// Category identifier: A, B, C, E, II, IP, IS, O, P, S, SPEC, V, Z
	private String mCategory;
	// Pretty name, "Výstražné značky", etc.
	private String mCategoryName;
	private String mImagePath;

	public RoadSignsCategoryListEntry ( String category, String categoryName, String imagePath ) {

		setCategory( category );
		setCategoryName( categoryName );
		setImagePath( imagePath );
	}

	public String getCategory ( ) {

		return mCategory;
	}

	public void setCategory ( String category ) {

		mCategory = category;
	}

	public String getCategoryName ( ) {

		return mCategoryName;
	}

	public void setCategoryName ( String categoryName ) {

		mCategoryName = categoryName;
	}

	public String getImagePath ( ) {

		return mImagePath;
	}

	public void setImagePath ( String imagePath ) {

		mImagePath = imagePath;
	}
}