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
import com.spiraclestudios.autoskola.activities.RoadSignsCategoryListActivity;

public class RoadSignsDetailFragment extends Fragment {

    public static final String ARG_ROAD_SIGN_NAME = "road_sign_name";
    public static final String ARG_ROAD_SIGN_DESC = "road_sign_desc";

    private String roadSignName;
    private String roadSignDesc;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public RoadSignsDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        roadSignName = getArguments().getString(ARG_ROAD_SIGN_NAME);
        roadSignDesc = getArguments().getString(ARG_ROAD_SIGN_DESC);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_road_signs_detail, container, false);

        ((TextView) view.findViewById(R.id.road_sign_name)).setText(roadSignName);
        ((TextView) view.findViewById(R.id.road_sign_desc)).setText(roadSignDesc);
        //((TextView) view.findViewById(R.id.road_sign_image)).setText(category);

        return view;
    }
}
