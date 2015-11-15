package com.spiraclestudios.autoskola.Dialogs;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.spiraclestudios.autoskola.AutoskolaApplication;
import com.spiraclestudios.autoskola.BuildConfig;
import com.spiraclestudios.autoskola.Helper;
import com.spiraclestudios.autoskola.R;

import butterknife.ButterKnife;

/**
 * Created by benji on 5/10/2015.
 */
public class AboutDialog extends AppCompatDialogFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE, 0);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_about, container, false);

        // Obtain the shared Tracker instance
        final Tracker mTracker = ((AutoskolaApplication) getActivity().getApplication())
                .getDefaultTracker();

        ImageView app_icon = ButterKnife.findById(view, R.id.app_icon);
        TextView app_version = ButterKnife.findById(view, R.id.app_version);

        // Social networks
        ImageButton web_icon = ButterKnife.findById(view, R.id.web_icon);
        ImageButton facebook_icon = ButterKnife.findById(view, R.id.facebook_icon);
        ImageButton twitter_icon = ButterKnife.findById(view, R.id.twitter_icon);
        ImageButton youtube_icon = ButterKnife.findById(view, R.id.youtube_icon);
        ImageButton google_play_icon = ButterKnife.findById(view, R.id.google_play_icon);

        app_version.setText(
                String.format(getResources().getString(R.string.dialog_about_version),
                        BuildConfig.VERSION_NAME));

        app_icon.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                mTracker.send(new HitBuilders.EventBuilder()
                        .setCategory("Navigation")
                        .setAction("Developer Tools")
                        .build());

                DialogFragment dialog = new DevToolsDialog();

                dialog.show(getActivity().getSupportFragmentManager(), "DevTools");
                dismiss();
                return true;
            }
        });

        web_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(Helper.webURL));
                startActivity(browserIntent);
            }
        });

        web_icon.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                ClipboardManager clipboard = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("Link to social website", Helper.webURL);
                clipboard.setPrimaryClip(clip);

                // TODO: Use string resource
                Toast.makeText(getContext(), "Odkaz bol skopírovaný", Toast.LENGTH_SHORT).show();
                return true;
            }
        });

        facebook_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(Helper.facebookURL));
                startActivity(browserIntent);
            }
        });

        twitter_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(Helper.twitterURL));
                startActivity(browserIntent);
            }
        });

        youtube_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(Helper.youtubeURL));
                startActivity(browserIntent);
            }
        });

        google_play_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(Helper.googlePlayURL));
                startActivity(browserIntent);
            }
        });

        return view;
    }
}
