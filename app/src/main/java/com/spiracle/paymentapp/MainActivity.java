package com.spiracle.paymentapp;

import android.app.Activity;
import android.content.Context;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.os.Bundle;

import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.widget.DrawerLayout;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.internal.CardHeader;
import it.gmariotti.cardslib.library.view.CardView;


public class MainActivity extends ActionBarActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks {

	private static final String TAG = "MainActivity";
	public final static String EXTRA_MESSAGE = "com.spiracle.paymentapp.MESSAGE";

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
        setContentView(R.layout.main_activity);

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
        // update the main content by replacing fragments
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, PlaceholderFragment.newInstance(position + 1))
                .commit();
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
				//Intent intent = new Intent(getActivity(), MyActivity.class);
				//startActivity(intent);

				Toast.makeText(this, "Adding person.", Toast.LENGTH_SHORT).show();
				return true;

			case R.id.action_done:
				Toast.makeText(this, "Getting shit done.", Toast.LENGTH_SHORT).show();
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
		ArrayList<MessageDetails> details;
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

			DatabaseContract.DatabaseHelper mDbHelper = new DatabaseContract.DatabaseHelper(getActivity());

			msgList = (ListView) rootView.findViewById(R.id.favoritesList);
			registerForContextMenu(msgList);

			details = new ArrayList<MessageDetails>();
			mAdapter = new CustomAdapter(details, getActivity());

			mAdapter.addSectionHeaderItem("Favorites");

			MessageDetails Detail;
			Detail = new MessageDetails();
			Detail.setIcon(R.drawable.ic_launcher);
			Detail.setName("Some Guy");
			Detail.setSub("Dinner");
			Detail.setDesc("Lorem ipsum dolor sit amet, consectetur adipiscing elit. Nulla auctor.");
			Detail.setTime("12/12/2012 12:12");
			details.add(Detail);

			Detail = new MessageDetails();
			Detail.setIcon(R.drawable.ic_launcher);
			Detail.setName("Rob");
			Detail.setSub("Party");
			Detail.setDesc("Dolor sit amet, consectetur adipiscing elit. Nulla auctor.");
			Detail.setTime("13/12/2012 10:12");
			details.add(Detail);

			Detail = new MessageDetails();
			Detail.setIcon(R.drawable.ic_launcher);
			Detail.setName("Mike");
			Detail.setSub("Mail");
			Detail.setDesc("Lorem ipsum dolor sit amet, consectetur adipiscing elit.");
			Detail.setTime("13/12/2012 02:12");
			details.add(Detail);

			msgList.setAdapter(mAdapter);

			msgList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
				public void onItemClick(AdapterView a, View v, int position, long id) {

					if (a.getAdapter().getItemViewType(position) == CustomAdapter.TYPE_ITEM) {
						String s = (String) ((TextView) v.findViewById(R.id.name)).getText();
						Toast.makeText(getActivity(), s, Toast.LENGTH_SHORT).show();
					}
				}
			});

			//Create a Card
			//Card card = new Card(getActivity());

			//Create a CardHeader
			//CardHeader header = new CardHeader(getActivity());

			//Add Header to card
			//card.addCardHeader(header);

			//Set card in the cardView
			//CardView cardView = (CardView) rootView.findViewById(R.id.carddemo);

			//cardView.setCard(card);

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
			menu.add(Menu.NONE, v.getId(), 0, "Reply");
			menu.add(Menu.NONE, v.getId(), 0, "Reply All");
			menu.add(Menu.NONE, v.getId(), 0, "Forward");
		}

		@Override
		public boolean onContextItemSelected(MenuItem item) {
			if (item.getTitle() == "Reply") {
				//Do your working
			}
			else if (item.getTitle() == "Reply All") {
				//Do your working
			}
			else if (item.getTitle() == "Reply All") {
				//Do your working
			}
			else     {
				return false;
			}
			return true;
		}
    }

	/** Called when the user clicks the Send button */
	public void sendMessage(View view)
	{
		//Intent intent = new Intent(this, MyActivity.class);
		/*EditText editText = (EditText) findViewById(R.id.edit_message);
		String message = editText.getText().toString();
		intent.putExtra(EXTRA_MESSAGE, message);*/
		//startActivity(intent);
	}

}
