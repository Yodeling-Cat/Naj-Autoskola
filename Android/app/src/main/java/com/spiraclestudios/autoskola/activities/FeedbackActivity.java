/*
 * Copyright 2015-2016 Spiracle Studios. All Rights Reserved.
 */

package com.spiraclestudios.autoskola.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.Toast;

import com.spiraclestudios.autoskola.Helper;
import com.spiraclestudios.autoskola.R;
import com.spiraclestudios.autoskola.interfaces.IBaseActivity;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;

@EActivity( R.layout.activity_feedback )
public class FeedbackActivity extends BaseActivity
		implements IBaseActivity {

	public String mActivityName = "FeedbackActivity";

	public String getActivityName ( ) {

		return mActivityName;
	}

	@Override
	protected void onCreate ( Bundle savedInstanceState ) {

		Helper.setTheme( this );
		super.onCreate( savedInstanceState );
	}

	@AfterViews void afterViews ( ) {
		// SetUp Toolbar
		Toolbar toolbar = (Toolbar) findViewById( R.id.toolbar );
		setSupportActionBar( toolbar );

		Helper.initializeDebugDrawer( this );
	}

	@Override
	public boolean onOptionsItemSelected ( MenuItem item ) {

		int id = item.getItemId();

		if ( id == android.R.id.home ) {
			onBackPressed();
			return true;
		}

		return super.onOptionsItemSelected( item );
	}

	@Click
	public void send_a_suggestion ( ) {

		Intent intent = new Intent( getApplicationContext(), SendFeedbackActivity.class );
		intent.putExtra( SendFeedbackActivity.EXTRA_FEEDBACK_TYPE, 0 );
		startActivity( intent );
	}

	@Click
	public void report_a_bug ( ) {

		Intent intent = new Intent( getApplicationContext(), SendFeedbackActivity.class );
		intent.putExtra( SendFeedbackActivity.EXTRA_FEEDBACK_TYPE, 1 );
		startActivity( intent );
	}

	@Click
	public void ask_for_help ( ) {

		Toast.makeText( FeedbackActivity.this, R.string.toast_not_yet_implemented,
				Toast.LENGTH_SHORT ).show();
	}
}
