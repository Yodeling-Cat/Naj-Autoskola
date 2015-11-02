package com.spiraclestudios.autoskola;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import com.google.android.gms.analytics.Tracker;


public class DevToolsDialog extends DialogFragment {
    private static final String TAG = "DevToolsDialog";
    private Tracker mTracker;

    public DevToolsDialog() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // [SetUp Activity]
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

    /**
     * The system calls this to get the DialogFragment's layout, regardless
     * of whether it's being displayed as a dialog or an embedded fragment.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // [Inflate the layout]
        Helper.setTheme(getActivity());
        View view = inflater.inflate(R.layout.dialog_dev_tools, container, false);


        // setOnClickListener for zacat_test
        view.findViewById(R.id.database_manager).setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent intent = new Intent(getActivity().getApplicationContext(), DatabaseManager.class);
                startActivity(intent);
                getFragmentManager().popBackStackImmediate();

                dismiss();
            }
        });

        return view;
    }
}
