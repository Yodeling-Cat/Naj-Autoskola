/*
 * Copyright 2015-2016 Spiracle Studios. All Rights Reserved.
 */

package com.spiraclestudios.autoskola;

/**
 * Original created by benji on 19/2/2016.
 */

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.support.v7.view.ContextThemeWrapper;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.spiraclestudios.autoskola.activities.TestActivity;

import java.util.ArrayList;
import java.util.Locale;

public class HistoryListAdapter extends RecyclerView.Adapter<HistoryListAdapter.ViewHolder> {

	private Context mContext;
	private ArrayList<HistoryListEntry> mDataSet;

	public HistoryListAdapter ( ArrayList<HistoryListEntry> dataSet ) {

		mDataSet = dataSet;
	}

	public void setContext ( Context context ) {

		mContext = context;
	}

	@Override
	public HistoryListAdapter.ViewHolder onCreateViewHolder ( final ViewGroup parent, int viewType ) {

		View view = LayoutInflater.from( mContext ).inflate( R.layout.history_list_entry, parent, false );

		return new ViewHolder( view, new ViewHolder.IViewOnClickListener() {
			public void onItemClick ( View view ) {

				HistoryListEntry entry = getItem( ( (RecyclerView) parent.findViewById( R.id.recycler_view ) )
						.getChildAdapterPosition( view ) );

				// Start TestActivity.
				Intent intent = new Intent( mContext.getApplicationContext(),
						TestActivity.class );
				intent.putExtra( TestActivity.EXTRA_INDEX, entry.getIndex() );
				intent.putExtra( TestActivity.EXTRA_USES_QUESTIONS, entry.getUsesQuestions() );
				intent.putExtra( TestActivity.EXTRA_USES_ROAD_SIGNS, entry.getUsesRoadSigns() );
				intent.putExtra( TestActivity.EXTRA_USES_INTERSECTIONS, entry.getUsesIntersections() );
				intent.putExtra( TestActivity.EXTRA_ANSWERS, entry.getAnswers() );
				intent.putExtra( TestActivity.EXTRA_MARK_CORRECT_ANSWERS, true );
				mContext.startActivity( intent );
				( (Activity) view.getContext() ).getFragmentManager().popBackStackImmediate();
			}
		} );
	}

	@Override
	public void onBindViewHolder ( final ViewHolder holder, final int position ) {

		Resources res = mContext.getResources();
		HistoryListEntry entry = getItem( position );

		// Returns "Skupina A,B" or "Skupina C,D,T"
		String groupString = ( Helper.getGroupFromTestIndex(
				entry.getIndex() ) == Helper.Groups.AB ) ? res.getString( R.string.group_ab ) : res.getString( R.string.group_cdt );

		holder.test_id.setText( String.format( res.getString( R.string.test_number_of ), entry.getIndex() ) );
		holder.test_group.setText( groupString );
		holder.results_points.setText( String.format( Locale.ENGLISH, "%d/%d", entry.getPoints(), entry.getMaxPoints() ) );
		holder.results_time.setText( String.format( Locale.ENGLISH, "%s", entry.getTime() ) );
		holder.results_date.setText( String.format( Locale.ENGLISH, "%s", entry.getDate() ) );
		holder.results_year.setText( String.format( Locale.ENGLISH, "%d", entry.getYear() ) );
	}

	public void addItem ( HistoryListEntry dataObj, int index ) {

		mDataSet.add( dataObj );
		notifyItemInserted( index );
	}

	public void deleteItem ( int index ) {

		mDataSet.remove( index );
		notifyItemRemoved( index );
	}

	public HistoryListEntry getItem ( int position ) {

		return mDataSet.get( position );
	}

	@Override
	public int getItemCount ( ) {

		return mDataSet.size();
	}

	public static class ViewHolder extends RecyclerView.ViewHolder
			implements View.OnClickListener {

		public IViewOnClickListener mListener;

		public TextView test_id;
		public TextView test_group;
		public TextView results_points;
		public TextView results_time;
		public TextView results_date;
		public TextView results_year;

		public ViewHolder ( View view, IViewOnClickListener listener ) {

			super( view );
			mListener = listener;
			test_id = (TextView) view.findViewById( R.id.test_id );
			test_group = (TextView) view.findViewById( R.id.test_group );
			results_points = (TextView) view.findViewById( R.id.results_points );
			results_time = (TextView) view.findViewById( R.id.results_time );
			results_date = (TextView) view.findViewById( R.id.results_date );
			results_year = (TextView) view.findViewById( R.id.results_year );

			view.setOnClickListener( this );
		}

		@Override
		public void onClick ( View view ) {

			mListener.onItemClick( view );
		}

		public interface IViewOnClickListener {

			void onItemClick ( View view );
		}
	}
}