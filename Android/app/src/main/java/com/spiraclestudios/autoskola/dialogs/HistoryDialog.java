/*
 * Copyright 2015-2016 Spiracle Studios. All Rights Reserved.
 */

package com.spiraclestudios.autoskola.dialogs;

import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatDialogFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.spiraclestudios.autoskola.DbContract;
import com.spiraclestudios.autoskola.DbHelper;
import com.spiraclestudios.autoskola.Helper;
import com.spiraclestudios.autoskola.HistoryListAdapter;
import com.spiraclestudios.autoskola.HistoryListEntry;
import com.spiraclestudios.autoskola.R;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Original created by benji on 19/2/2016.
 */
public class HistoryDialog extends AppCompatDialogFragment {

	private static final String ARG_PARAM_INDEX = "index";
	private int mTestIndex;

	@Bind( R.id.title )
	public TextView title;
	@Bind( R.id.recycler_view )
	public RecyclerView recycler_view;
	@Bind( R.id.empty_state )
	public TextView empty_state;

	public static HistoryDialog newInstance ( int index ) {

		HistoryDialog fragment = new HistoryDialog();
		Bundle args = new Bundle();

		args.putInt( ARG_PARAM_INDEX, index );

		fragment.setArguments( args );
		return fragment;
	}

	@Override
	public void onCreate ( Bundle savedInstanceState ) {

		super.onCreate( savedInstanceState );
		setStyle( DialogFragment.STYLE_NO_TITLE, 0 );

		if ( getArguments().containsKey( ARG_PARAM_INDEX ) ) {
			mTestIndex = getArguments().getInt( ARG_PARAM_INDEX );
		}
	}

	@Override
	public View onCreateView ( LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState ) {

		View view = inflater.inflate( R.layout.dialog_history, container, false );
		ButterKnife.bind( this, view );

		if ( view != null ) {
			title.setText( String.format( getString( R.string.dialog_history_title ), mTestIndex ) );

			ArrayList<HistoryListEntry> dataset = getDataSet();

			if ( dataset.size() != 0 ) {
				recycler_view.setHasFixedSize( true );
				RecyclerView.LayoutManager layoutManager = new LinearLayoutManager( getContext() );
				recycler_view.setLayoutManager( layoutManager );
				RecyclerView.Adapter<HistoryListAdapter.ViewHolder> adapter =
						new HistoryListAdapter( dataset );
				( (HistoryListAdapter) adapter ).setContext( getActivity() );
				recycler_view.setAdapter( adapter );

				// TODO: Enable decorations.
				//RecyclerView.ItemDecoration itemDecoration =
				//        new DividerItemDecoration(this, LinearLayoutManager.VERTICAL);
				//recycler_view.addItemDecoration(itemDecoration);
			} else {
				recycler_view.setVisibility( View.GONE );
				empty_state.setVisibility( View.VISIBLE );
			}
		}
		return view;
	}

	/**
	 * @return Data to populate the adapter with.
	 */
	private ArrayList<HistoryListEntry> getDataSet ( ) {

		ArrayList<HistoryListEntry> results = new ArrayList<>();

		// Set up the Database.
		DbHelper dbHelper = new DbHelper( getContext() );
		SQLiteDatabase db = dbHelper.getReadableDatabase();

		// Get the History for this test version.
		String query = "SELECT " +
				DbContract.History.COLUMN_USES_QUESTIONS + ", " +
				DbContract.History.COLUMN_USES_ROAD_SIGNS + ", " +
				DbContract.History.COLUMN_USES_INTERSECTIONS + ", " +
				DbContract.History.COLUMN_POINTS + ", " +
				DbContract.History.COLUMN_MAX_POINTS + ", " +
				DbContract.History.COLUMN_ELAPSED_TIME_TEXT + ", " +
				DbContract.History.COLUMN_ANSWERS +
				" FROM " + DbContract.History.TABLE_NAME +
				" WHERE " + DbContract.History.COLUMN_TEST_ID + " == ?;";

		Cursor cHistory = db.rawQuery( query, new String[] { Integer.toString( mTestIndex ) } );

		for ( cHistory.moveToFirst(); !cHistory.isAfterLast(); cHistory.moveToNext() ) {
			boolean usesQuestions = cHistory.getInt( cHistory.getColumnIndexOrThrow( DbContract.History.COLUMN_USES_QUESTIONS ) ) == 1;
			boolean usesRoadSigns = cHistory.getInt( cHistory.getColumnIndexOrThrow( DbContract.History.COLUMN_USES_ROAD_SIGNS ) ) == 1;
			boolean usesIntersections = cHistory.getInt( cHistory.getColumnIndexOrThrow( DbContract.History.COLUMN_USES_INTERSECTIONS ) ) == 1;
			int points = cHistory.getInt( cHistory.getColumnIndexOrThrow( DbContract.History.COLUMN_POINTS ) );
			int maxPoints = cHistory.getInt( cHistory.getColumnIndexOrThrow( DbContract.History.COLUMN_MAX_POINTS ) );
			String timeText = cHistory.getString( cHistory.getColumnIndexOrThrow( DbContract.History.COLUMN_ELAPSED_TIME_TEXT ) );
			String answersString = cHistory.getString( cHistory.getColumnIndexOrThrow( DbContract.History.COLUMN_ANSWERS ) );

			results.add( new HistoryListEntry( mTestIndex, Helper.getGroupFromTestIndex( mTestIndex ),
					usesQuestions, usesRoadSigns, usesIntersections, points, maxPoints, answersString, timeText, "XX.X.", 2000 ) );
		}

		cHistory.close();
		dbHelper.close();
		db.close();

		return results;
	}
}
