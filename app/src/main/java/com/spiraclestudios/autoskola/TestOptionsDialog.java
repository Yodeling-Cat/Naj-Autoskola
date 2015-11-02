package com.spiraclestudios.autoskola;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import com.google.android.gms.analytics.Tracker;


public class TestOptionsDialog extends DialogFragment {
    private static final String TAG = "TestOptionsDialog";
    private Tracker mTracker;

    public final static String EXTRA_GROUP =
            "com.spiraclestudios.autoskola.TEST_OPTIONS_GROUP";
    public final static String EXTRA_INDEX =
            "com.spiraclestudios.autoskola.TEST_OPTIONS_INDEX";

    private static final String ARG_PARAM_GROUP = "group";
    private static final String ARG_PARAM_INDEX = "index";

    private Helper.Groups mParamGroup;
    private int mParamIndex;

    public static TestOptionsDialog newInstance(int index) {
        TestOptionsDialog fragment = new TestOptionsDialog();
        Bundle args = new Bundle();

        args.putInt(ARG_PARAM_INDEX, index);

        fragment.setArguments(args);
        return fragment;
    }

    public static TestOptionsDialog newInstance(Helper.Groups group) {
        TestOptionsDialog fragment = new TestOptionsDialog();
        Bundle args = new Bundle();

        args.putSerializable(ARG_PARAM_GROUP, group);

        fragment.setArguments(args);
        return fragment;
    }

    public TestOptionsDialog() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // [SetUp Activity]
        super.onCreate(savedInstanceState);

        mTracker = ((AutoskolaApplication) getActivity().getApplication()).getDefaultTracker();


        if (getArguments().containsKey(ARG_PARAM_GROUP)) {
            mParamGroup = (Helper.Groups) getArguments().getSerializable(ARG_PARAM_GROUP);
        }

        if (getArguments().containsKey(ARG_PARAM_INDEX)) {
            mParamIndex = getArguments().getInt(ARG_PARAM_INDEX);
            Log.d(TAG, "onCreate got this index: " + mParamIndex);
        }
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
        View view = inflater.inflate(R.layout.dialog_moznosti_testu, container, false);


        // setOnClickListener for zacat_test
        view.findViewById(R.id.zacat_test).setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent intent = new Intent(getActivity().getApplicationContext(), TestActivity.class);

                intent.putExtra(EXTRA_GROUP, mParamGroup);
                intent.putExtra(EXTRA_INDEX, mParamIndex);
                startActivity(intent);
                getFragmentManager().popBackStackImmediate();

                dismiss();
            }
        });

        return view;
    }
}
