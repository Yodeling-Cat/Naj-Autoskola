/*
 * Copyright 2015-2016 Spiracle Studios. All Rights Reserved.
 */

package com.spiraclestudios.autoskola.dialogs;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.View;
import android.widget.Switch;

import com.google.android.gms.analytics.HitBuilders;
import com.spiraclestudios.autoskola.AnalyticsTrackers;
import com.spiraclestudios.autoskola.activities.DatabaseManagerActivity;
import com.spiraclestudios.autoskola.Helper;
import com.spiraclestudios.autoskola.intros.IntroActivity;
import com.spiraclestudios.autoskola.R;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;


public class DevToolsDialog extends AppCompatDialogFragment {
    private static final String TAG = "DevToolsDialog";

    @Bind(R.id.demo_mode)
    Switch demo_mode;

    public DevToolsDialog() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        AnalyticsTrackers.getInstance().get(AnalyticsTrackers.Target.APP)
                .send(new HitBuilders.EventBuilder()
                        .setCategory("Navigation")
                        .setAction("Developer Tools")
                        .build());
    }

    /**
     * The system calls this only when creating the layout in a dialog.
     */
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Helper.setTheme(getActivity());
        View view = getActivity().getLayoutInflater().inflate(R.layout.dialog_dev_tools, null);
        ButterKnife.bind(this, view);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.title_dev_tools)
                .setView(view);

        AlertDialog dialog = builder.create();

        // Restore state
        demo_mode.setChecked(Helper.demoMode);

        return dialog;
    }

    @OnClick(R.id.database_manager)
    public void database_manager_OnClick() {
        Intent intent = new Intent(getActivity().getApplicationContext(), DatabaseManagerActivity.class);
        startActivity(intent);
        getFragmentManager().popBackStackImmediate();

        dismiss();
    }

    @OnClick(R.id.intro_activity)
    public void intro_activity_onClick() {
        // Start the IntroActivity
        Intent intent = new Intent(getContext(), IntroActivity.class);
        startActivity(intent);
    }

    @OnCheckedChanged(R.id.demo_mode)
    public void demo_mode_onChanged(boolean isChecked) {
        Helper.setDemoMode(isChecked);
    }
}
