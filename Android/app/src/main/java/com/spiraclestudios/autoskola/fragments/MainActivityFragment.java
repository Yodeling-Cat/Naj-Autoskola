/*
 * Copyright 2015-2016 Spiracle Studios. All Rights Reserved.
 */

package com.spiraclestudios.autoskola.fragments;

/**
 * Original created by benji on 14/10/2015.
 */

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.spiraclestudios.autoskola.Helper;
import com.spiraclestudios.autoskola.R;
import com.spiraclestudios.autoskola.TestsListAdapter;
import com.spiraclestudios.autoskola.TestsListEntry;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MainActivityFragment extends Fragment {

	private static final String TAG = "MainActivityFragment";

	public Helper.Groups mGroup = Helper.Groups.AB;

	@Bind( R.id.recycler_view )
	public RecyclerView recycler_view;

	/**
	 * Mandatory empty constructor for the fragment manager to instantiate the fragment (e.g. upon
	 * screen orientation changes).
	 */
	public MainActivityFragment ( ) {

	}

	public static MainActivityFragment newInstance ( Helper.Groups group ) {

		MainActivityFragment fragment = new MainActivityFragment();
		Bundle bundle = new Bundle();

		bundle.putSerializable( "group", group );

		fragment.setArguments( bundle );
		return fragment;
	}

	@Override
	public View onCreateView ( LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState ) {

		mGroup = (Helper.Groups) getArguments().getSerializable( "group" );

		Helper.setTheme( getContext() );
		View view = inflater.inflate( R.layout.tests_list, container, false );
		ButterKnife.bind( this, view );

		if ( view != null ) {
			recycler_view.setHasFixedSize( true );
			RecyclerView.LayoutManager layoutManager = new LinearLayoutManager( getContext() );
			recycler_view.setLayoutManager( layoutManager );
			RecyclerView.Adapter<TestsListAdapter.ViewHolder> adapter = new TestsListAdapter( getDataSet() );
			recycler_view.setAdapter( adapter );

			//RecyclerView.ItemDecoration itemDecoration =
			//        new DividerItemDecoration(this, LinearLayoutManager.VERTICAL);
			//recycler_view.addItemDecoration(itemDecoration);
		}

		return view;
	}

	// Returns data to populate the adapter with.
	private ArrayList<TestsListEntry> getDataSet ( ) {

		ArrayList<TestsListEntry> results = new ArrayList<>();
		int start;
		int end;

		if ( mGroup == Helper.Groups.AB ) {
			start = 1;
			end = 36;
		} else {
			start = 36;
			end = 61;
		}

		for ( int i = start; i < end; i++ ) {
			TestsListEntry entry = new TestsListEntry( i, 0 );
			results.add( entry );
		}

		return results;
	}
}