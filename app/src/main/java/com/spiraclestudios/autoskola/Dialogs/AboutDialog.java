package com.spiraclestudios.autoskola.Dialogs;

import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.spiraclestudios.autoskola.AutoskolaApplication;
import com.spiraclestudios.autoskola.BuildConfig;
import com.spiraclestudios.autoskola.Helper;
import com.spiraclestudios.autoskola.R;

import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by benji on 5/10/2015.
 */
public class AboutDialog extends DialogFragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_about, container, false);
        getDialog().setTitle(R.string.dialog_about_title);

        // Obtain the shared Tracker instance
        final Tracker mTracker = ((AutoskolaApplication) getActivity().getApplication())
                .getDefaultTracker();

        Resources res = getResources();

        TextView info_values = ButterKnife.findById(view, R.id.info_values);
        ImageView app_icon = ButterKnife.findById(view, R.id.app_icon);
        // Social networks
        ImageView facebook = ButterKnife.findById(view, R.id.facebook);
        ImageView twitter = ButterKnife.findById(view, R.id.twitter);
        ImageView youtube = ButterKnife.findById(view, R.id.youtube);
        ImageView google_play = ButterKnife.findById(view, R.id.google_play);

        info_values.setText(
                String.format(res.getString(R.string.dialog_about_info_values),
                        BuildConfig.VERSION_NAME));

        app_icon.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                mTracker.send(new HitBuilders.EventBuilder()
                        .setCategory("Navigation")
                        .setAction("Developer Tools")
                        .build());

                DialogFragment fragment = new DevToolsDialog();
                fragment.show(getActivity().getSupportFragmentManager(), "DevTools");
                dismiss();
                return true;
            }
        });

        facebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(Helper.facebookURL));
                startActivity(browserIntent);
            }
        });

        twitter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(Helper.twitterURL));
                startActivity(browserIntent);
            }
        });

        youtube.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(Helper.youtubeURL));
                startActivity(browserIntent);
            }
        });

        google_play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(Helper.googlePlayURL));
                startActivity(browserIntent);
            }
        });

        return view;
    }
}
