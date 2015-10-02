package com.spiracle.drivingschool;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.support.v4.app.FragmentManager;
import android.os.Bundle;

import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.support.v4.widget.DrawerLayout;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.Toast;


public class ActivityMain extends AppCompatActivity
        implements FragmentNavigationDrawer.NavigationDrawerCallbacks {

	private static final String TAG = "ActivityMain";

    private Toolbar toolbar;

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private FragmentNavigationDrawer mNavigationDrawerFragment;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null)
        {
            setSupportActionBar(toolbar);
            // TODO: Is this needed?
            // getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setTitle("Driving school");
            getSupportActionBar().setSubtitle("Tests");
        }

        mNavigationDrawerFragment = (FragmentNavigationDrawer)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
		switch (position) {
			case 0:
				// update the main content by replacing fragments
				FragmentManager fragmentManager = getSupportFragmentManager();
				fragmentManager.beginTransaction()
						.replace(R.id.container, FragmentActivityMain.newInstance(position + 1))
						.commit();
				break;
			case 1:
				Toast.makeText(this, "Not yet implemented", Toast.LENGTH_SHORT).show();
				break;
			case 2:
				//Intent intent = new Intent(this, ActivitySettings.class);
				//startActivity(intent);
				break;
		}
	}

    public void onSectionAttached(int number) {
        switch (number) {
            case 1:
                mTitle = getString(R.string.title_tests);
                break;
            case 2:
                mTitle = getString(R.string.title_news);
                break;
            case 3:
                mTitle = getString(R.string.title_road_signs);
                break;
            case 4:
                mTitle = getString(R.string.title_vyhlaska);
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
            //getMenuInflater().inflate(R.menu.main_activity_actions, menu);
            //restoreActionBar();
            //return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			/*case R.id.action_add_person:
				Intent intent = new Intent(this, ActivityAddPerson.class);
				startActivity(intent);
				return true;

			case R.id.action_minus:
				ListView favoritesList = (ListView) findViewById(R.id.favoritesList);

				if (favoritesList == null)
					return true;

				for (int i = 0; i < favoritesList.getChildCount(); i++) {
					CheckBox checkbox = (CheckBox) favoritesList.getChildAt(i).findViewById(R.id.checkbox);

					if (checkbox != null && checkbox.isChecked()) {
						PersonDetails person = (PersonDetails) favoritesList.getAdapter().getItem(i);

						DatabaseHelper mDbHelper = new DatabaseHelper(this);
						SQLiteDatabase db = mDbHelper.getReadableDatabase();

						ContentValues values = new ContentValues();
						// TODO: Make a variable for price per ride and replace the float here
						int newFunds = person.getFunds() - 40;
						values.put(DatabaseContract.PersonEntry.COLUMN_NAME_FUNDS, newFunds);

						String selection = DatabaseContract.PersonEntry._ID + " = ?";
						String[] selectionArgs = { String.valueOf(person.getId()) };

						db.update(
								DatabaseContract.PersonEntry.TABLE_NAME,
								values,
								selection,
								selectionArgs
						);

						person.setFunds(newFunds);
						// TODO: Getting the adapter like this might be a problem when we add the Underdogs list
						PeopleAdapter adapter = FragmentActivityMain.mAdapter;
						adapter.notifyDataSetChanged();
					}
				}
				return true;

			case R.id.action_plus:
				favoritesList = (ListView) findViewById(R.id.favoritesList);

				if (favoritesList == null)
					return true;

				for (int i = 0; i < favoritesList.getChildCount(); i++) {
					CheckBox checkbox = (CheckBox) favoritesList.getChildAt(i).findViewById(R.id.checkbox);

					if (checkbox != null && checkbox.isChecked()) {
						PersonDetails person = (PersonDetails) favoritesList.getAdapter().getItem(i);

						DatabaseHelper mDbHelper = new DatabaseHelper(this);
						SQLiteDatabase db = mDbHelper.getReadableDatabase();

						ContentValues values = new ContentValues();
						// TODO: Make a variable for price per ride and replace the float here
						int newFunds = person.getFunds() + 40;
						values.put(DatabaseContract.PersonEntry.COLUMN_NAME_FUNDS, newFunds);

						String selection = DatabaseContract.PersonEntry._ID + " = ?";
						String[] selectionArgs = { String.valueOf(person.getId()) };

						db.update(
								DatabaseContract.PersonEntry.TABLE_NAME,
								values,
								selection,
								selectionArgs
						);

						person.setFunds(newFunds);
						// TODO: Getting the adapter like this might be a problem when we add the Underdogs list
						PeopleAdapter adapter = FragmentActivityMain.mAdapter;
						adapter.notifyDataSetChanged();
					}
				}
				return true;
			*/
			default:
				return super.onOptionsItemSelected(item);
		}
    }
}
