/*
 * Copyright (c) 2015-2016. Spiracle Studios. All Rights Reserved.
 */

package com.spiraclestudios.autoskola.fragments;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.spiraclestudios.autoskola.R;

import java.io.IOException;
import java.io.InputStream;

import timber.log.Timber;

public class RoadSignsDetailFragment extends Fragment
{

  public static final String ARG_ROAD_SIGN_NAME = "road_sign_name";
  public static final String ARG_ROAD_SIGN_DESC = "road_sign_desc";
  public static final String ARG_ROAD_SIGN_IMAGE_PATH = "road_sign_image_path";

  private String roadSignName;
  private String roadSignDesc;
  private String roadSignImagePath;

  /**
   * Mandatory empty constructor for the fragment manager to instantiate the fragment (e.g. upon
   * screen orientation changes).
   */
  public RoadSignsDetailFragment() {}

  @Override
  public void onCreate(Bundle savedInstanceState)
  {
    super.onCreate(savedInstanceState);

    Bundle bundle = getArguments();
    roadSignName = bundle.getString(ARG_ROAD_SIGN_NAME);
    roadSignDesc = bundle.getString(ARG_ROAD_SIGN_DESC);
    roadSignImagePath = bundle.getString(ARG_ROAD_SIGN_IMAGE_PATH);
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState)
  {
    View view = inflater.inflate(R.layout.fragment_road_signs_detail, container, false);

    ((TextView) view.findViewById(R.id.road_sign_name)).setText(roadSignName);
    ((TextView) view.findViewById(R.id.road_sign_desc)).setText(roadSignDesc);

    Drawable roadSignImage;
    try {
      InputStream inputStream = getContext().getAssets()
          .open("images/road_signs/" + roadSignImagePath + ".png");
      roadSignImage = Drawable.createFromStream(inputStream, null);
    } catch (IOException ex) {
      // If file doesn't exist, use the placeholder image.
      roadSignImage = ContextCompat.getDrawable(getContext(),
          R.drawable.placeholder_small);
      Timber.d("Image \"images/road_signs/%s.png\" does not exist.", roadSignImagePath);
    }

    ((ImageView) view.findViewById(R.id.road_sign_image)).setImageDrawable(roadSignImage);

    return view;
  }
}
