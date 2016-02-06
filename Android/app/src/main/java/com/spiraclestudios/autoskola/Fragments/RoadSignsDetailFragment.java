/*
 * Copyright 2015-2016 Spiracle Studios. All Rights Reserved.
 */

package com.spiraclestudios.autoskola.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.spiraclestudios.autoskola.R;
import com.spiraclestudios.autoskola.activities.RoadSignsDetailActivity;
import com.spiraclestudios.autoskola.activities.RoadSignsListActivity;
import com.spiraclestudios.autoskola.dummy.DummyContent;

/**
 * A fragment representing a single Znacka detail screen.
 * This fragment is either contained in a {@link RoadSignsListActivity}
 * in two-pane mode (on tablets) or a {@link RoadSignsDetailActivity}
 * on handsets.
 */
public class RoadSignsDetailFragment extends Fragment {
    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public static final String ARG_CATEGORY = "category";
    public static final String ARG_CATEGORY_NAME = "category_name";

    private String category;
    private String categoryName;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public RoadSignsDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        category = getArguments().getString(ARG_CATEGORY);
        categoryName = getArguments().getString(ARG_CATEGORY_NAME);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_road_sign_detail, container, false);

        ((TextView) view.findViewById(R.id.road_sign_name)).setText(categoryName);
        //((TextView) view.findViewById(R.id.road_sign_image)).setText(category);

        return view;
    }
}
