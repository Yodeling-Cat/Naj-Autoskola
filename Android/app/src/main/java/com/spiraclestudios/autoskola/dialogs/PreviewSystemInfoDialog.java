/*
 * Copyright 2015-2016 Spiracle Studios. All Rights Reserved.
 */

package com.spiraclestudios.autoskola.dialogs;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDialogFragment;

import com.spiraclestudios.autoskola.R;

/**
 * Original created by benji on 10/11/2015.
 */
public class PreviewSystemInfoDialog extends AppCompatDialogFragment {

	public static PreviewSystemInfoDialog newInstance ( String message ) {

		PreviewSystemInfoDialog fragment = new PreviewSystemInfoDialog();

		Bundle args = new Bundle();
		args.putString( "message", message );
		fragment.setArguments( args );

		return fragment;
	}

	@NonNull
	@Override
	public Dialog onCreateDialog ( Bundle savedInstanceState ) {

		String message = getArguments().getString( "message" );

		AlertDialog.Builder builder = new AlertDialog.Builder( getActivity() );
		builder.setTitle( R.string.dialog_system_info_preview_title )
				.setMessage( message )
				.setPositiveButton( "OK", new DialogInterface.OnClickListener() {
					public void onClick ( DialogInterface dialog, int id ) {
						// Simply close the dialog
					}
				} );
		return builder.create();
	}
}
