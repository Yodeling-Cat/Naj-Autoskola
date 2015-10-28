package com.spiraclestudios.autoskola;

/**
 * Created by benji on 14/10/2015.
 */
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class MainActivityFragment extends Fragment {
    private static final String TAG = "MainActivityFragment";

    public MainActivityFragment() {
    }

    public static MainActivityFragment newInstance(Helper.Groups group) {
        MainActivityFragment fragment = new MainActivityFragment();
        Bundle bundle = new Bundle();

        bundle.putInt("group", group.ordinal());
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Helper.setTheme(getContext());
        View view = inflater.inflate(R.layout.fragment_main, container, false);

        //setTest(getArguments().getInt("testId"));
        return view;
    }
}