package com.spiracle.paymentapp;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;


public class ActivityAddPerson extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_person);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.add_person_actions, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

		switch (item.getItemId()) {
			case R.id.action_add_person:

				DatabaseHelper mDbHelper = new DatabaseHelper(this);
				SQLiteDatabase db = mDbHelper.getWritableDatabase();

				// Create the people table if it doesn't exist.
				db.execSQL(DatabaseContract.getSqlCreateEntries());

				// TODO: Check for empty values in text boxes!
				// Create a new map of values, where column names are the keys
				ContentValues values = new ContentValues();
				//values.put(DatabaseContract.PersonEntry.COLUMN_NAME_ENTRY_ID, id);
				values.put(DatabaseContract.PersonEntry.COLUMN_NAME_AVATAR, "ic_launcher");
				values.put(DatabaseContract.PersonEntry.COLUMN_NAME_NAME, ((EditText) findViewById(R.id.name)).getText().toString());
				values.put(DatabaseContract.PersonEntry.COLUMN_NAME_FUNDS, String.valueOf(Float.valueOf( ((EditText) findViewById(R.id.funds)).getText().toString() ) * 100.0f)); // Multiply by 100 to get cents
				values.put(DatabaseContract.PersonEntry.COLUMN_NAME_FAVORITE, false);
				// TODO: SAVE THE FAVORITE STATUS FROM THE MAIN ACTIVITY

				db.insert(
						DatabaseContract.PersonEntry.TABLE_NAME,
						null,
						values);

				db.close();

				// Go back to the main activity
				NavUtils.navigateUpFromSameTask(this);
				return true;

			/*case R.id.action_create_table:

				db = new DatabaseHelper(this).getWritableDatabase();
				db.execSQL(DatabaseContract.getSqlCreateEntries());
				db.close();

				Toast.makeText(this, "Creating a new People table", Toast.LENGTH_SHORT).show();
				return true;
			*/

			case R.id.action_drop_table:

				db = new DatabaseHelper(this).getWritableDatabase();
				db.execSQL(DatabaseContract.getSqlDeleteEntries());
				db.close();

				Toast.makeText(this, "Dropping the People table", Toast.LENGTH_SHORT).show();
				return true;

			default:

				return super.onOptionsItemSelected(item);
		}
    }
}
