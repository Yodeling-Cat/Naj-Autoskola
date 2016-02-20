/*
 * Copyright 2015-2016 Spiracle Studios. All Rights Reserved.
 */

package com.spiraclestudios.autoskola.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.View;
import android.widget.CheckBox;

import com.spiraclestudios.autoskola.Helper;
import com.spiraclestudios.autoskola.R;
import com.spiraclestudios.autoskola.activities.TestActivity;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;


public class TestOptionsDialog extends AppCompatDialogFragment
		implements DialogInterface.OnDismissListener {

	private static final String ARG_PARAM_INDEX = "index";
	private static final String ARG_PARAM_GROUP = "group";

	private Helper.Groups mTestGroup;
	private int mTestIndex;

	private boolean mUseQuestions;
	private boolean mUseRoadSigns;
	private boolean mUseIntersections;

	@Bind( R.id.questions_checkbox )
	CheckBox questions_checkbox;
	@Bind( R.id.road_signs_checkbox )
	CheckBox road_signs_checkbox;
	@Bind( R.id.intersections_checkbox )
	CheckBox intersections_checkbox;

	public TestOptionsDialog ( ) {

	}

	public static TestOptionsDialog newInstance ( int index ) {

		TestOptionsDialog fragment = new TestOptionsDialog();
		Bundle args = new Bundle();

		args.putInt( ARG_PARAM_INDEX, index );

		fragment.setArguments( args );
		return fragment;
	}

	// When starting a random test.
	public static TestOptionsDialog newInstance ( Helper.Groups group ) {

		TestOptionsDialog fragment = new TestOptionsDialog();
		Bundle args = new Bundle();

		args.putSerializable( ARG_PARAM_GROUP, group );

		fragment.setArguments( args );
		return fragment;
	}

	@Override
	public void onCreate ( Bundle savedInstanceState ) {

		super.onCreate( savedInstanceState );

		if ( getArguments().containsKey( ARG_PARAM_GROUP ) ) {
			mTestGroup = (Helper.Groups) getArguments().getSerializable( ARG_PARAM_GROUP );
		}

		if ( getArguments().containsKey( ARG_PARAM_INDEX ) ) {
			mTestIndex = getArguments().getInt( ARG_PARAM_INDEX );
		}
	}

	/**
	 * The system calls this only when creating the layout in a dialog.
	 */
	@NonNull
	@Override
	public Dialog onCreateDialog ( Bundle savedInstanceState ) {

		Helper.setTheme( getActivity() );
		View view = getActivity().getLayoutInflater().inflate( R.layout.dialog_test_options, null );
		ButterKnife.bind( this, view );

		AlertDialog.Builder builder = new AlertDialog.Builder( getActivity() );
		builder.setTitle( R.string.dialog_test_options_title )
				.setView( view )
				.setOnDismissListener( this )
				.setPositiveButton( R.string.begin_test, new DialogInterface.OnClickListener() {
					public void onClick ( DialogInterface dialog, int id ) {

						saveChoices();

						// Start TestActivity.
						Intent intent = new Intent( getActivity().getApplicationContext(),
								TestActivity.class );
						intent.putExtra( TestActivity.EXTRA_GROUP, mTestGroup );
						intent.putExtra( TestActivity.EXTRA_INDEX, mTestIndex );
						intent.putExtra( TestActivity.EXTRA_USES_QUESTIONS, mUseQuestions );
						intent.putExtra( TestActivity.EXTRA_USES_ROAD_SIGNS, mUseRoadSigns );
						intent.putExtra( TestActivity.EXTRA_USES_INTERSECTIONS, mUseIntersections );
						startActivity( intent );
						getFragmentManager().popBackStackImmediate();
					}
				} );

		AlertDialog dialog = builder.create();

		dialog.setOnShowListener( new DialogInterface.OnShowListener() {
			@Override
			public void onShow ( DialogInterface dialog ) {
				// Restore last choices from SharedPreferences.
				SharedPreferences prefs = getActivity().getPreferences( Context.MODE_PRIVATE );
				mUseQuestions = prefs.getBoolean( "TestOptions_useQuestions", true );
				mUseRoadSigns = prefs.getBoolean( "TestOptions_useRoadSigns", true );
				mUseIntersections = prefs.getBoolean( "TestOptions_useIntersections", true );

				questions_checkbox.setChecked( mUseQuestions );
				road_signs_checkbox.setChecked( mUseRoadSigns );
				intersections_checkbox.setChecked( mUseIntersections );

				questions_checkbox.jumpDrawablesToCurrentState();
				road_signs_checkbox.jumpDrawablesToCurrentState();
				intersections_checkbox.jumpDrawablesToCurrentState();
			}
		} );

		return dialog;
	}

	@Override
	public void onDismiss ( DialogInterface dialog ) {

		super.onDismiss( dialog );
		saveChoices();
	}

	// Save the state of checkboxes.
	private void saveChoices ( ) {

		SharedPreferences prefs = getActivity().getPreferences( Context
				.MODE_PRIVATE );
		SharedPreferences.Editor editor = prefs.edit();
		editor.putBoolean( "TestOptions_useQuestions", mUseQuestions );
		editor.putBoolean( "TestOptions_useRoadSigns", mUseRoadSigns );
		editor.putBoolean( "TestOptions_useIntersections", mUseIntersections );
		editor.apply();
	}

	private void setBeginTestEnabled ( boolean enabled ) {

		( (AlertDialog) this.getDialog() ).getButton( AlertDialog.BUTTON_POSITIVE ).setEnabled( enabled );
	}

	private boolean canBeginTest ( ) {
		// If all of them are unchecked, return false.
		return !( !mUseQuestions && !mUseRoadSigns && !mUseIntersections );
	}

	@OnCheckedChanged( { R.id.questions_checkbox, R.id.road_signs_checkbox,
			R.id.intersections_checkbox } )
	public void questions_checkbox_onChanged ( CheckBox view, boolean isChecked ) {

		switch ( view.getId() ) {
			case R.id.questions_checkbox:
				mUseQuestions = isChecked;
				break;
			case R.id.road_signs_checkbox:
				mUseRoadSigns = isChecked;
				break;
			case R.id.intersections_checkbox:
				mUseIntersections = isChecked;
				break;
		}
		setBeginTestEnabled( canBeginTest() );
	}
}
