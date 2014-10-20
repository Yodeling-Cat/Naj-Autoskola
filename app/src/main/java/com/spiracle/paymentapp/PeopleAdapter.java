package com.spiracle.paymentapp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.TreeSet;

public class PeopleAdapter extends BaseAdapter {

	private ArrayList<PersonDetails> _data;
	Context _c;

	private LayoutInflater mInflater;

	public PeopleAdapter(ArrayList<PersonDetails> data, Context c){
		_data = data;
		_c = c;
		mInflater = (LayoutInflater) c
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public int getCount() {
		return _data.size();
	}

	@Override
	public Object getItem(int position) {
		return _data.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, final ViewGroup parent) {
		View v = convertView;

		if (v == null) {
			v = mInflater.inflate(R.layout.list_item_person, null);
		}

		ImageView image = (ImageView) v.findViewById(R.id.avatar);
		TextView nameView = (TextView) v.findViewById(R.id.name);
		TextView fundsView = (TextView)v.findViewById(R.id.funds);

		PersonDetails person = _data.get(position);
		image.setImageResource(person.getAvatar());
		nameView.setText(person.getName());
		// TODO: Add the currency symbol to the settings
		DecimalFormat df = new DecimalFormat("€#,##0.00;(€#,##0.00)");
		fundsView.setText(df.format( person.getFunds() / 100.0f ));

		//@Override
		image.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				AlertDialog.Builder adb = new AlertDialog.Builder(parent.getContext());
				adb.setMessage("Add To Contacts?");
				adb.setNegativeButton("Cancel", null);
				//final int selectedid = position;
				//final String itemname = (String) _data.get(position).getName();

				adb.setPositiveButton("OK", new AlertDialog.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {

					}
				});

				adb.show();
			}
		});

		return v;
	}

	//public void addItem(final String item) {
	//	mData.add(item);
	//	notifyDataSetChanged();
	//}
}