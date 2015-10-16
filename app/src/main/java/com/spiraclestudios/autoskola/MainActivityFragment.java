package com.spiraclestudios.autoskola;

/**
 * Created by benji on 14/10/2015.
 */
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

public class MainActivityFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Helper.setTheme(getContext());
        View view = inflater.inflate(R.layout.content_main, container, false);

        return view;
    }
}