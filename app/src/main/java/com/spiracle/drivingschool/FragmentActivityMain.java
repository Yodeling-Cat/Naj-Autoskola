package com.spiracle.drivingschool;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class FragmentActivityMain extends Fragment {
	/**
	 * The fragment argument representing the section number for this
	 * fragment.
	 */
	private static final String ARG_SECTION_NUMBER = "section_number";
	ListView favoritesList;
	static PeopleAdapter mAdapter;
	ArrayList<PersonDetails> people;
	AdapterView.AdapterContextMenuInfo info;

	/**
	 * Returns a new instance of this fragment for the given section
	 * number.
	 */
	public static FragmentActivityMain newInstance(int sectionNumber) {
		FragmentActivityMain fragment = new FragmentActivityMain();
		Bundle args = new Bundle();
		args.putInt(ARG_SECTION_NUMBER, sectionNumber);
		fragment.setArguments(args);
		return fragment;
	}

	public FragmentActivityMain() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_main_activity, container, false);

		/*Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
        if (toolbar != null)
        {
            //Toolbar will now take on default actionbar characteristics
            ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
            ((AppCompatActivity) getActivity()).getSupportActionBar().setHomeButtonEnabled(true);
            ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Hello from Appcompat Toolbar");
        }*/

        /*
		DatabaseHelper mDbHelper = new DatabaseHelper(getActivity());
		SQLiteDatabase db = mDbHelper.getWritableDatabase();

		// Check if the people table exists
		Cursor tableExistsCursor = db.rawQuery("SELECT DISTINCT tbl_name FROM sqlite_master WHERE tbl_name = '" + DatabaseContract.PersonEntry.TABLE_NAME + "'", null);

		// If table exists
		if (tableExistsCursor.getCount() > 0) {
			Cursor tableIsEmptyCursor = db.rawQuery("SELECT EXISTS (select 1 FROM " + DatabaseContract.PersonEntry.TABLE_NAME + ")", null);
			tableIsEmptyCursor.moveToFirst();

			// and if its not empty, continue
			if (tableIsEmptyCursor.getInt(0) != 0) {
				favoritesList = (ListView) rootView.findViewById(R.id.favoritesList);
				registerForContextMenu(favoritesList);
				// TODO: Remove and make a custom listview with android:divider="@null"
				favoritesList.setDividerHeight(0);
				favoritesList.setDivider(null);

				people = new ArrayList<>();
				mAdapter = new PeopleAdapter(people, getActivity());

				//mAdapter.addSectionHeaderItem("FAVORITES");

				// Define a projection that specifies which columns from the database
				// you will actually use after this query.
				String[] projection = {
						DatabaseContract.PersonEntry._ID,
						DatabaseContract.PersonEntry.COLUMN_NAME_AVATAR,
						DatabaseContract.PersonEntry.COLUMN_NAME_NAME,
						DatabaseContract.PersonEntry.COLUMN_NAME_FUNDS,
						DatabaseContract.PersonEntry.COLUMN_NAME_FAVORITE
				};

				// Define 'where' part of query.
				// TODO: Doesn't seem to be returning anything.
				String selection = DatabaseContract.PersonEntry.COLUMN_NAME_NAME + " != ?";
				// Specify arguments in placeholder order.
				String[] selectionArgs = {"%"}; //{ String.valueOf(rowId) };

				// TODO: Let the user choose the sorting order in the settings. (Save in SharedPreferences)
				String sortOrder =
						//DatabaseContract.PersonEntry.COLUMN_NAME_UPDATED + " DESC";
						DatabaseContract.PersonEntry._ID;

				Cursor cursor = db.query(
						DatabaseContract.PersonEntry.TABLE_NAME,  // The table to query
						projection,                               // The columns to return
						selection,                                // The columns for the WHERE clause
						selectionArgs,                            // The values for the WHERE clause
						null,                                     // don't group the rows
						null,                                     // don't filter by row groups
						sortOrder                                 // The sort order
				);

				PersonDetails person;

				if (cursor.moveToFirst()) {
					while (!cursor.isAfterLast()) {
						int id = cursor.getInt(cursor.getColumnIndex("_id"));
						String name = cursor.getString(cursor.getColumnIndex("name"));
						int funds = cursor.getInt(cursor.getColumnIndex("funds"));

						person = new PersonDetails();
						person.setId(id);
						person.setAvatar(R.drawable.ic_launcher);
						person.setName(name);
						person.setFunds(funds);
						people.add(person);

						cursor.moveToNext();
					}
				}
				cursor.close();

				// [Add the favorites header text]
				View header = getLayoutInflater(savedInstanceState).inflate(R.layout.list_item_header, null);
				TextView headerText = (TextView) header.findViewById(R.id.headerText);
				headerText.setText(R.string.list_header_favorites);
				favoritesList.addHeaderView(header);

				favoritesList.setAdapter(mAdapter);

				// Respond to clicks on the items in the listview
				favoritesList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
					public void onItemClick(AdapterView a, View v, int position, long id) {
						//String name = (String) ((TextView) v.findViewById(R.id.name)).getText();
						//Toast.makeText(getActivity(), name, Toast.LENGTH_SHORT).show();
					}
				});

				tableExistsCursor.close();
			}
			else {
				return inflater.inflate(R.layout.fragment_no_entries, container, false);
			}
		}
		else {
			return inflater.inflate(R.layout.fragment_no_entries, container, false);
		}

		db.close();
        */
		return rootView;
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		((ActivityMain) activity).onSectionAttached(
				getArguments().getInt(ARG_SECTION_NUMBER));
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		info = (AdapterView.AdapterContextMenuInfo) menuInfo;

		// If the listview item is not a header then create the context menu
		if (favoritesList.getAdapter().getItemViewType(info.position) == 0) {
			menu.setHeaderTitle(people.get(info.position-1).getName());
			menu.add(Menu.NONE, Menu.NONE, 0, "Edit");
			menu.add(Menu.NONE, Menu.NONE, 1, "Delete");
		}

	}

	@Override
	public boolean onContextItemSelected(final MenuItem item) {
		if (item.getTitle() == "Edit") {
			// TODO: Implement editing of users, send extras that contain the user ID to the intent

			Intent intent = new Intent(getActivity(), ActivityEditPerson.class);
			startActivity(intent);
			return true;
		}

		else if (item.getTitle() == "Delete") {
			AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

			final String name = people.get(info.position-1).getName();

			builder.setMessage("Delete user \"" + name + "\"?");

			builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialogInterface, int i) {
					String selection = "_id = ?";

					info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();

					String[] selectionArgs = { String.valueOf(people.get(info.position-1).getId()) };

					DatabaseHelper mDbHelper = new DatabaseHelper(getActivity());
					SQLiteDatabase db = mDbHelper.getWritableDatabase();

					db.delete(DatabaseContract.PersonEntry.TABLE_NAME, selection, selectionArgs);
					db.close();

					people.remove(info.position-1);
					mAdapter.notifyDataSetChanged();
					Toast.makeText(getActivity(), "\"" + name + "\" was deleted", Toast.LENGTH_SHORT).show();

					// If there are no more people left after removing, display the no_entries fragment
					if (people.isEmpty()) {
						FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
						fragmentManager.beginTransaction()
								.replace(R.id.container, new FragmentNoEntries())
								.commit();
					}
				}
			});

			builder.setNegativeButton("Cancel", null);

			AlertDialog dialog = builder.create();
			dialog.show();

			return true;
		}

		return false;
	}
}