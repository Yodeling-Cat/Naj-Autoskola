package com.spiraclestudios.autoskola.Dialogs;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.spiraclestudios.autoskola.Activities.TestActivity;
import com.spiraclestudios.autoskola.AnalyticsTrackers;
import com.spiraclestudios.autoskola.AutoskolaApplication;
import com.spiraclestudios.autoskola.DatabaseManager;
import com.spiraclestudios.autoskola.Helper;
import com.spiraclestudios.autoskola.R;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;


public class DevToolsDialog extends AppCompatDialogFragment {
    private static final String TAG = "DevToolsDialog";

    @Bind(R.id.database_manager)
    Button database_manager;
    @Bind(R.id.force_crash)
    Button force_crash;
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
        Intent intent = new Intent(getActivity().getApplicationContext(), DatabaseManager.class);
        startActivity(intent);
        getFragmentManager().popBackStackImmediate();

        dismiss();
    }

    @OnClick(R.id.force_crash)
    public void force_crash_onClick() {
        throw new RuntimeException("Crashing with the Force Crash developer button");
    }

    @OnCheckedChanged(R.id.demo_mode)
    public void demo_mode_onChanged(boolean isChecked) {
        Helper.setDemoMode(isChecked);
    }
}
