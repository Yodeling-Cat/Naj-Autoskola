package com.spiraclestudios.autoskola;

/**
 * Created by benji on 14/10/2015.
 */

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

public class MainActivityFragment extends Fragment {
    private static final String TAG = "MainActivityFragment";

    public int mGroup;

    public RecyclerView recycler_view;

    private RecyclerView.Adapter<ZoznamTestouAdapter.ViewHolder> adapter;
    private RecyclerView.LayoutManager layoutManager;

    public MainActivityFragment() {
    }

    public static MainActivityFragment newInstance(int group) {
        MainActivityFragment fragment = new MainActivityFragment();
        Bundle bundle = new Bundle();

        bundle.putInt("group", group);
        fragment.setArguments(bundle);
        Log.d(TAG, "[NewInstance] Group is: " + group);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Helper.setTheme(getContext());
        View view = inflater.inflate(R.layout.fragment_main, container, false);

        recycler_view = (RecyclerView) view.findViewById(R.id.recycler_view);

        recycler_view.setHasFixedSize(false);
        layoutManager = new LinearLayoutManager(getContext());
        recycler_view.setLayoutManager(layoutManager);
        adapter = new ZoznamTestouAdapter(getDataSet());
        recycler_view.setAdapter(adapter);

        //RecyclerView.ItemDecoration itemDecoration =
        //        new DividerItemDecoration(this, LinearLayoutManager.VERTICAL);
        //recycler_view.addItemDecoration(itemDecoration);

        mGroup = getArguments().getInt("group");
        Log.d(TAG, "[OnCreateView] Group is: " + mGroup);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        // SetOnClickListener for the adapter entries
        ((ZoznamTestouAdapter) adapter).setOnItemClickListener(
                new ZoznamTestouAdapter.TestEntryClickListener() {
                    @Override
                    public void onItemClick(int position, View view) {
                        // TODO: Change the index param, it has to use the entry's testId
                        MoznostiTestuDialog dialog = MoznostiTestuDialog.newInstance(position);
                        Log.d(TAG, "[onResume] Group is: " + mGroup);

                        dialog.show(((MainActivity)getContext()).getSupportFragmentManager(),
                                "MoznostiTestu");
                    }
                });
    }

    // Returns data to populate the adapter with
    private ArrayList<DataObject> getDataSet() {
        ArrayList<DataObject> results = new ArrayList<>();
        for (int index = 0; index < 35; index++) {
            DataObject obj = new DataObject(index);
            results.add(index, obj);
        }
        return results;
    }
}