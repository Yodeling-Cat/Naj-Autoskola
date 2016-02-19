/*
 * Copyright 2015-2016 Spiracle Studios. All Rights Reserved.
 */

package com.spiraclestudios.autoskola.dialogs;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.romainpiel.shimmer.Shimmer;
import com.romainpiel.shimmer.ShimmerTextView;
import com.spiraclestudios.autoskola.BuildConfig;
import com.spiraclestudios.autoskola.Helper;
import com.spiraclestudios.autoskola.R;

import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnLongClick;

/**
 * Original created by benji on 5/10/2015.
 */
public class AboutDialog extends AppCompatDialogFragment {

	@Override
	public void onCreate ( Bundle savedInstanceState ) {

		super.onCreate( savedInstanceState );
		setStyle( DialogFragment.STYLE_NO_TITLE, 0 );
	}

	@Override
	public View onCreateView ( LayoutInflater inflater, ViewGroup container,
							   Bundle savedInstanceState ) {

		View view = inflater.inflate( R.layout.dialog_about, container, false );
		ButterKnife.bind( this, view );

		// Format the version text.
		TextView app_version = ButterKnife.findById( view, R.id.app_version );
		app_version.setText( String.format( getResources().getString( R.string.dialog_about_version ), BuildConfig.VERSION_NAME ) );

		// Shimmer effect on the dialog title.
		if ( Helper.themeResId != R.style.MyTheme_Dark && Helper.themeResId != R.style.MyTheme_Dark_AMOLED ) {
			Shimmer shimmer = new Shimmer();
			shimmer.start( (ShimmerTextView) ButterKnife.findById( view, R.id.app_title ) );
			shimmer.setRepeatCount( 0 )
					.setDuration( 500 )
					.setStartDelay( 750 );
		}
		return view;
	}

	@OnClick( { R.id.web_icon, R.id.facebook_icon, R.id.twitter_icon, R.id.youtube_icon,
				R.id.google_play_icon } )
	public void socialLinks_onClick ( View view ) {

		String url = getSocialLinkUrl( view.getId() );

		Intent browserIntent = new Intent( Intent.ACTION_VIEW, Uri.parse( url ) );
		startActivity( browserIntent );
	}

	@OnLongClick( { R.id.web_icon, R.id.facebook_icon, R.id.twitter_icon, R.id.youtube_icon,
					R.id.google_play_icon } )
	public boolean socialLinks_onLongClick ( View view ) {

		String url = getSocialLinkUrl( view.getId() );

		ClipboardManager clipboard = (ClipboardManager) getActivity()
				.getSystemService( Context.CLIPBOARD_SERVICE );

		ClipData clip = ClipData.newPlainText( getString( R.string.clip_label_social_link ), url );
		clipboard.setPrimaryClip( clip );

		Toast.makeText( getContext(), R.string.toast_link_was_copied, Toast.LENGTH_SHORT ).show();
		return true;
	}

	@OnLongClick( R.id.app_icon )
	public boolean app_icon_onLongClick ( ) {

		if ( !BuildConfig.DEBUG ) {
			return false;
		}

		Vibrator vibrator = (Vibrator) getContext().getSystemService( Context.VIBRATOR_SERVICE );
		vibrator.vibrate( 20 );

		DialogFragment dialog = new DevToolsDialog();
		dialog.show( getActivity().getSupportFragmentManager(), "DevTools" );
		dismiss();
		return true;
	}

	private String getSocialLinkUrl ( int viewId ) {

		switch ( viewId ) {
			case R.id.web_icon:
				return Helper.webURL;
			case R.id.facebook_icon:
				return Helper.facebookURL;
			case R.id.twitter_icon:
				return Helper.twitterURL;
			case R.id.youtube_icon:
				return Helper.youtubeURL;
			case R.id.google_play_icon:
				return Helper.googlePlayURL;
			default:
				return "";
		}
	}
}
