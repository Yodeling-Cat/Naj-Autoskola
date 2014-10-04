package com.spiracle.paymentapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.os.Bundle;

import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.widget.DrawerLayout;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;


public class MainActivity extends ActionBarActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks {

	private static final String TAG = "MainActivity";

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));

		/*
		String[] values = new String[] { "Android", "iPhone", "WindowsMobile",
				"Blackberry", "WebOS", "Ubuntu", "Windows7", "Max OS X",
				"Linux", "OS/2", "Ubuntu", "Windows7", "Max OS X", "Linux",
				"OS/2", "Ubuntu", "Windows7", "Max OS X", "Linux", "OS/2",
				"Android", "iPhone", "WindowsMobile" };

		final ArrayList<String> list = new ArrayList<String>();

		Collections.addAll(list, values);

		final StableArrayAdapter adapter = new StableArrayAdapter(this,
				android.R.layout.simple_list_item_1, list);

		setContentView(R.layout.fragment_main_activity);
		final ListView listview = (ListView) findViewById(R.id.favoritesList);

		listview.setAdapter(adapter);

		listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, final View view,
									int position, long id) {
				final String item = (String) parent.getItemAtPosition(position);
				// Animate the removing of an item from the list (Requires API 12 and 16)
				view.animate().setDuration(2000).alpha(0)
//						.withEndAction(new Runnable() {
//							@Override
//							public void run() {
//								list.remove(item);
//								adapter.notifyDataSetChanged();
//								view.setAlpha(1);
//							}
//						});

				list.remove(item);
				adapter.notifyDataSetChanged();
			}
		});
		*/
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
		switch (position) {
			case 0:
				// update the main content by replacing fragments
				FragmentManager fragmentManager = getSupportFragmentManager();
				fragmentManager.beginTransaction()
						.replace(R.id.container, PlaceholderFragment.newInstance(position + 1))
						.commit();
				break;
			case 1:
				Toast.makeText(this, "Not yet implemented", Toast.LENGTH_SHORT).show();
				break;
			case 2:
				Intent intent = new Intent(this, SettingsActivity.class);
				startActivity(intent);
				break;
		}
	}

    public void onSectionAttached(int number) {
        switch (number) {
            case 1:
                mTitle = getString(R.string.title_section1);
                break;
            case 2:
                mTitle = getString(R.string.title_section2);
                break;
            case 3:
                mTitle = getString(R.string.title_section3);
                break;
        }
    }

    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.main_activity_actions, menu);
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

		switch (item.getItemId()) {
			case R.id.action_add_person:
				Intent intent = new Intent(this, AddPersonActivity.class);
				startActivity(intent);
				return true;

			case R.id.action_done:
				ListView msgList = (ListView) findViewById(R.id.favoritesList);

				for (int i = 0; i < msgList.getChildCount(); i++) {
					CheckBox checkbox = (CheckBox) msgList.getChildAt(i).findViewById(R.id.checkbox);

					if (checkbox != null && checkbox.isChecked()) {
						PersonDetails person = (PersonDetails) msgList.getAdapter().getItem(i);

						DatabaseHelper mDbHelper = new DatabaseHelper(this);
						SQLiteDatabase db = mDbHelper.getReadableDatabase();

						ContentValues values = new ContentValues();
						// TODO: Make a variable for price per ride and replace the float here
						values.put(DatabaseContract.PersonEntry.COLUMN_NAME_FUNDS, person.getFunds() - 30);

						String selection = DatabaseContract.PersonEntry._ID + " = ?";
						String[] selectionArgs = { String.valueOf(person.getId()) };

						db.update(
								DatabaseContract.PersonEntry.TABLE_NAME,
								values,
								selection,
								selectionArgs
						);
					}
				}
				return true;

			default:
				return super.onOptionsItemSelected(item);
		}
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";
		ListView msgList;
		ArrayList<PersonDetails> details;
		AdapterView.AdapterContextMenuInfo info;
		private CustomAdapter mAdapter;

		/**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_main_activity, container, false);

			DatabaseHelper mDbHelper = new DatabaseHelper(getActivity());
			SQLiteDatabase db = mDbHelper.getWritableDatabase();

			// Check if the people table exists
			Cursor tableExistsCursor = db.rawQuery("SELECT DISTINCT tbl_name FROM sqlite_master WHERE tbl_name = '" + DatabaseContract.PersonEntry.TABLE_NAME + "'", null);

			if (tableExistsCursor != null) {
				if (tableExistsCursor.getCount() > 0) {
					msgList = (ListView) rootView.findViewById(R.id.favoritesList);
					registerForContextMenu(msgList);

					details = new ArrayList<PersonDetails>();
					mAdapter = new CustomAdapter(details, getActivity());

					mAdapter.addSectionHeaderItem("Favorites");

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
					String[] selectionArgs = { "%" }; //{ String.valueOf(rowId) };

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

					PersonDetails Detail;

					if (cursor.moveToFirst()){
						while(!cursor.isAfterLast()){
							int id = cursor.getInt(cursor.getColumnIndex("_id"));
							String name = cursor.getString(cursor.getColumnIndex("name"));
							int funds = cursor.getInt(cursor.getColumnIndex("funds"));

							Detail = new PersonDetails();
							Detail.setId(id);
							Detail.setAvatar(R.drawable.ic_launcher);
							Detail.setName(name);
							Detail.setFunds(funds);
							details.add(Detail);

							cursor.moveToNext();
						}
					}
					cursor.close();
					db.close();

					msgList.setAdapter(mAdapter);

					// Respond to clicks on the items in the listview
					msgList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
						public void onItemClick(AdapterView a, View v, int position, long id) {

							if (a.getAdapter().getItemViewType(position) == CustomAdapter.TYPE_ITEM) {
								String s = (String) ((TextView) v.findViewById(R.id.name)).getText();
								Toast.makeText(getActivity(), s, Toast.LENGTH_SHORT).show();
							}
						}
					});

					tableExistsCursor.close();
				}
				else {
					return inflater.inflate(R.layout.fragment_no_entries, container, false);
				}
			}

			return rootView;
        }

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
            ((MainActivity) activity).onSectionAttached(
                    getArguments().getInt(ARG_SECTION_NUMBER));
        }

		@Override
		public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
			super.onCreateContextMenu(menu, v, menuInfo);

			info = (AdapterView.AdapterContextMenuInfo) menuInfo;

			menu.setHeaderTitle(details.get(info.position).getName());
			menu.add(Menu.NONE, v.getId(), 0, "Edit");
			menu.add(Menu.NONE, v.getId(), 1, "Delete");
		}

		@Override
		public boolean onContextItemSelected(MenuItem item) {
			if (item.getTitle() == "Edit") {
				// TODO: Implement
			}

			else if (item.getTitle() == "Delete") {
				AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

				// TODO: We are trying to get() an index of the array that does not exist.
				final String name = "NAME_PLACEHOLDER"; //details.get(item.getItemId()).getName();

				builder.setMessage("Delete user \"" + name + "\"?");

				builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialogInterface, int i) {
						String selection = "_id == ?";

						String[] selectionArgs = { String.valueOf(details.get(i).getId()) };

						DatabaseHelper mDbHelper = new DatabaseHelper(getActivity());
						SQLiteDatabase db = mDbHelper.getWritableDatabase();

						db.delete(DatabaseContract.PersonEntry.TABLE_NAME, selection, selectionArgs);
						db.close();

						Toast.makeText(getActivity(), "\"" + name + "\" was deleted", Toast.LENGTH_SHORT).show();
					}
				});

				builder.setNegativeButton("Cancel", null);

				AlertDialog dialog = builder.create();
				dialog.show();
			}
			else {
				return false;
			}
			return true;
		}
    }
}
