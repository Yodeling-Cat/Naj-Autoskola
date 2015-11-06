package com.spiraclestudios.autoskola.Dialogs;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.google.android.gms.analytics.Tracker;
import com.spiraclestudios.autoskola.AutoskolaApplication;
import com.spiraclestudios.autoskola.DatabaseManager;
import com.spiraclestudios.autoskola.Helper;
import com.spiraclestudios.autoskola.R;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;


public class DevToolsDialog extends DialogFragment {
    private static final String TAG = "DevToolsDialog";
    private Tracker mTracker;

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
        // SetUp Activity
        super.onCreate(savedInstanceState);

        mTracker = ((AutoskolaApplication) getActivity().getApplication()).getDefaultTracker();
    }

    /**
     * The system calls this only when creating the layout in a dialog.
     */
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    @OnClick(R.id.database_manager)
    public void onClickDatabaseManager() {
        Intent intent = new Intent(getActivity().getApplicationContext(), DatabaseManager.class);
        startActivity(intent);
        getFragmentManager().popBackStackImmediate();

        dismiss();
    }

    @OnClick(R.id.force_crash)
    public void onClickForceCrash() {
        throw new RuntimeException("Crashing with the Force Crash developer button");
    }

    @OnCheckedChanged(R.id.demo_mode)
    public void onChangedDemoMode(CompoundButton view, boolean isChecked) {
        Helper.setDemoMode(isChecked);
    }

    /**
     * The system calls this to get the DialogFragment's layout, regardless
     * of whether it's being displayed as a dialog or an embedded fragment.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout
        Helper.setTheme(getActivity());
        View view = inflater.inflate(R.layout.dialog_dev_tools, container, false);
        ButterKnife.bind(this, view);

        // Restore state
        demo_mode.setChecked(Helper.demoMode);

        return view;
    }
}
