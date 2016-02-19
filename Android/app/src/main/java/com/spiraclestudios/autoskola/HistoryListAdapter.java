/*
 * Copyright 2015-2016 Spiracle Studios. All Rights Reserved.
 */

package com.spiraclestudios.autoskola;

/**
 * Original created by benji on 19/2/2016.
 */

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class HistoryListAdapter extends RecyclerView.Adapter<HistoryListAdapter.ViewHolder> {

	private Context mContext;

	private ArrayList<HistoryListEntry> mDataSet;

	public HistoryListAdapter ( ArrayList<HistoryListEntry> dataSet ) {

		mDataSet = dataSet;
	}

	@Override
	public HistoryListAdapter.ViewHolder onCreateViewHolder ( final ViewGroup parent, int viewType ) {

		mContext = parent.getContext();

		View view = LayoutInflater.from( mContext )
				.inflate( R.layout.history_list_entry, parent, false );

		return new ViewHolder( view, new ViewHolder.IViewOnClickListener() {
			public void onItemClick ( View view ) {

				int index = mDataSet.get( ( (RecyclerView) parent.findViewById( R.id.recycler_view ) )
						.getChildAdapterPosition( view ) ).getIndex();

				Toast.makeText( mContext, "Clicked index " + index, Toast.LENGTH_SHORT ).show();
				//TestOptionsDialog dialog = TestOptionsDialog.newInstance(index);
				//dialog.show(((MainActivity) view.getContext()).getSupportFragmentManager(),
				//        "MoznostiTestu");
			}
		} );
	}

	@Override
	public void onBindViewHolder ( final ViewHolder holder, final int position ) {

		HistoryListEntry item = getItem( position );
		holder.test_id.setText( "Test " + item.getIndex() );
		holder.test_group.setText( "X, X" );
		holder.results_points.setText( "XX/XX" );
		holder.results_time.setText( "XX:XX" );
		holder.results_date.setText( "XX.X." );
		holder.results_date_year.setText( "XXXX" );

        /*holder.overflow_button.setOnClickListener(new View.OnClickListener() {
			@Override
            public void onClick(final View view) {
                PopupMenu popupMenu = new PopupMenu(view.getContext(), view);
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        Intent intent;

                        switch (item.getItemId()) {
                            case R.id.item_correct_answers:
                                // Start TestActivity with the EXTRA_MARK_CORRECT_ANSWERS flag.
                                intent = new Intent(view.getContext(), TestActivity.class);

                                intent.putExtra(TestActivity.EXTRA_INDEX,
                                        ((HistoryListEntry) item).getIndex());
                                intent.putExtra(TestActivity.EXTRA_MARK_CORRECT_ANSWERS, true);
                                view.getContext().startActivity(intent);
                                return true;

                            case R.id.item_history:
                                // TODO: Open history dialog.
                                // Start TestActivity.
                                *//*intent = new Intent(view.getContext(), TestActivity.class);

                                intent.putExtra(TestActivity.EXTRA_INDEX, getItem(position)
                                        .getIndex());
                                intent.putExtra(TestActivity.EXTRA_MARK_CORRECT_ANSWERS, true);
                                view.getContext().startActivity(intent);*//*

                                Toast.makeText(view.getContext(),
                                        R.string.toast_not_yet_implemented,
                                        Toast.LENGTH_SHORT)
                                        .show();
                                return true;
                        }
                        return true;
                    }
                });
                popupMenu.inflate(R.menu.tests_list);
                popupMenu.show();
            }
        });*/
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
		public TextView results_date_year;

		public ViewHolder ( View view, IViewOnClickListener listener ) {

			super( view );
			mListener = listener;
			test_id = (TextView) view.findViewById( R.id.test_id );
			test_group = (TextView) view.findViewById( R.id.test_category );
			results_points = (TextView) view.findViewById( R.id.results_points );
			results_time = (TextView) view.findViewById( R.id.results_time );
			results_date = (TextView) view.findViewById( R.id.results_date );
			results_date_year = (TextView) view.findViewById( R.id.results_date_year );

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