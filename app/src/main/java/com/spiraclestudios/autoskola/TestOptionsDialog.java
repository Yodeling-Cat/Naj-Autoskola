package com.spiraclestudios.autoskola;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.Toast;

import com.google.android.gms.analytics.Tracker;


public class TestOptionsDialog extends DialogFragment {
    private static final String TAG = "TestOptionsDialog";
    private Tracker mTracker;

    public final static String EXTRA_GROUP =
            "com.spiraclestudios.autoskola.TEST_OPTIONS_GROUP";
    public final static String EXTRA_INDEX =
            "com.spiraclestudios.autoskola.TEST_OPTIONS_INDEX";
    public final static String EXTRA_USE_QUESTIONS =
            "com.spiraclestudios.autoskola.TEST_OPTIONS_QUESTIONS";
    public final static String EXTRA_USE_ROAD_SIGNS =
            "com.spiraclestudios.autoskola.TEST_OPTIONS_ROAD_SIGNS";
    public final static String EXTRA_USE_INTERSECTIONS =
            "com.spiraclestudios.autoskola.TEST_OPTIONS_INTERSECTIONS";

    private static final String ARG_PARAM_GROUP = "group";
    private static final String ARG_PARAM_INDEX = "index";

    private Helper.Groups mParamGroup;
    private int mParamIndex;

    public CheckBox questions_checkbox;
    public CheckBox road_signs_checkbox;
    public CheckBox intersections_checkbox;

    public boolean useQuestions;
    public boolean useRoadSigns;
    public boolean useIntersections;

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
        // Inflate the layout
        Helper.setTheme(getActivity());
        View view = inflater.inflate(R.layout.dialog_test_options, container, false);

        // Store references to views
        questions_checkbox = (CheckBox)view.findViewById(R.id.questions_checkbox);
        road_signs_checkbox = (CheckBox)view.findViewById(R.id.road_signs_checkbox);
        intersections_checkbox = (CheckBox)view.findViewById(R.id.intersections_checkbox);

        // Retrieve last choices from SharedPreferences
        SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
        useQuestions        = sharedPref.getBoolean("TestOptions_useQuestions", true);
        useRoadSigns        = sharedPref.getBoolean("TestOptions_useRoadSigns", true);
        useIntersections    = sharedPref.getBoolean("TestOptions_useIntersections", true);

        // Set checked status of checkboxes
        questions_checkbox.setChecked(useQuestions);
        road_signs_checkbox.setChecked(useRoadSigns);
        intersections_checkbox.setChecked(useIntersections);

        // Set onClickListeners for checkboxes
        questions_checkbox.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                useQuestions = questions_checkbox.isChecked();
            }
        });

        road_signs_checkbox.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                useRoadSigns = road_signs_checkbox.isChecked();
            }
        });

        intersections_checkbox.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                useIntersections = intersections_checkbox.isChecked();
            }
        });

        // setOnClickListener for begin_test
        view.findViewById(R.id.begin_test).setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if (!useQuestions && !useRoadSigns && !useIntersections) {
                    Toast toast = Toast.makeText(getContext(), R.string.toast_select_at_least_one,
                            Toast.LENGTH_SHORT);
                    View toastView = toast.getView();
                    toastView.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.colorToastDuringDialog));
                    toast.show();
                } else {
                    // Save the last choices
                    SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.putBoolean("TestOptions_useQuestions", useQuestions);
                    editor.putBoolean("TestOptions_useRoadSigns", useRoadSigns);
                    editor.putBoolean("TestOptions_useIntersections", useIntersections);
                    editor.apply();

                    // Start TestActivity
                    Intent intent = new Intent(getActivity().getApplicationContext(), TestActivity.class);

                    intent.putExtra(EXTRA_GROUP, mParamGroup);
                    intent.putExtra(EXTRA_INDEX, mParamIndex);
                    intent.putExtra(EXTRA_USE_QUESTIONS, useQuestions);
                    intent.putExtra(EXTRA_USE_ROAD_SIGNS, useRoadSigns);
                    intent.putExtra(EXTRA_USE_INTERSECTIONS, useIntersections);
                    startActivity(intent);
                    getFragmentManager().popBackStackImmediate();

                    dismiss();
                }
            }
        });

        return view;
    }
}
