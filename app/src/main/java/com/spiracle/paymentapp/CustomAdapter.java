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

import java.util.ArrayList;
import java.util.TreeSet;

public class CustomAdapter extends BaseAdapter {

	static final int VIEW_TYPE_COUNT = 2;
	static final int TYPE_ITEM = 0;
	static final int TYPE_SEPARATOR = 1;

	private ArrayList<PersonDetails> _data;
	//private ArrayList<String> mData = new ArrayList<String>();
	private TreeSet<Integer> sectionHeader = new TreeSet<Integer>();
	Context _c;

	private LayoutInflater mInflater;

	public CustomAdapter (ArrayList<PersonDetails> data, Context c){
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
		//ViewHolder holder = null;
		View v = convertView;
		int rowType = getItemViewType(position);


		if (v == null) {
			//holder = new ViewHolder();
			switch (rowType) {
				case TYPE_ITEM:
					v = mInflater.inflate(R.layout.list_item_person, null);
					//holder.textView = (TextView) convertView.findViewById(R.id.text);
					break;
				case TYPE_SEPARATOR:
					v = mInflater.inflate(R.layout.list_item_header, null);
					//holder.textView = (TextView) convertView.findViewById(R.id.textSeparator);
					break;
			}
			//convertView.setTag(holder);
		} else {
			//holder = (ViewHolder) convertView.getTag();
		}
		//holder.textView.setText(mData.get(position));

		if (rowType == TYPE_ITEM) {
			ImageView image = (ImageView) v.findViewById(R.id.avatar);
			TextView nameView = (TextView) v.findViewById(R.id.name);
			TextView fundsView = (TextView)v.findViewById(R.id.funds);

			PersonDetails msg = _data.get(position);
			image.setImageResource(msg.getAvatar());
			nameView.setText(msg.getName());
			// TODO: Add the currency symbol to the settings
			fundsView.setText("€" + String.valueOf(msg.getFunds() / 100.0f));

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

							//Your working
						}
					});

					adb.show();
				}
			});
		}
		else if (rowType == TYPE_SEPARATOR) {
			TextView headerView = (TextView) v.findViewById(R.id.textSeparator);

			PersonDetails msg = _data.get(position);
			headerView.setText(msg.header);
		}

		return v;
	}

	//public void addItem(final String item) {
	//	mData.add(item);
	//	notifyDataSetChanged();
	//}

	public void addSectionHeaderItem(final String headerText) {
		PersonDetails item = new PersonDetails();
		item.setHeader(headerText);
		_data.add(item);
		sectionHeader.add(_data.size() - 1);
		notifyDataSetChanged();
	}

	@Override
	public int getItemViewType(int position) {
		return sectionHeader.contains(position) ? TYPE_SEPARATOR : TYPE_ITEM;
	}

	@Override
	public int getViewTypeCount() {
		return VIEW_TYPE_COUNT;
	}

	//public static class ViewHolder {
	//	public TextView textView;
	//}
}