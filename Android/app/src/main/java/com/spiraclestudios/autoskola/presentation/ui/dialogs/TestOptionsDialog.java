// Copyright (c) 2015-2017. Spiracle Software. All Rights Reserved.

package com.spiraclestudios.autoskola.presentation.ui.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.View;
import android.widget.CheckBox;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import com.spiraclestudios.autoskola.R;
import com.spiraclestudios.autoskola.domain.Groups;
import com.spiraclestudios.autoskola.presentation.ui.activities.TestActivity;

public class TestOptionsDialog extends AppCompatDialogFragment
    implements DialogInterface.OnDismissListener {

  private static final String ARG_INDEX = "index";
  private static final String ARG_GROUP = "group";

  private Groups testGroup;
  private int testIndex;

  private boolean useQuestions;
  private boolean useRoadSigns;
  private boolean useIntersections;

  @BindView(R.id.questions_checkbox) CheckBox questions_checkbox;
  @BindView(R.id.road_signs_checkbox) CheckBox road_signs_checkbox;
  @BindView(R.id.intersections_checkbox) CheckBox intersections_checkbox;

  public TestOptionsDialog() {
  }

  /**
   * Starting a specific test.
   */
  public static TestOptionsDialog newInstance(int index) {
    TestOptionsDialog fragment = new TestOptionsDialog();
    Bundle args = new Bundle();

    args.putInt(ARG_INDEX, index);

    fragment.setArguments(args);
    return fragment;
  }

  /**
   * Starting a random test.
   */
  public static TestOptionsDialog newInstance(Groups group) {
    TestOptionsDialog fragment = new TestOptionsDialog();
    Bundle args = new Bundle();

    args.putSerializable(ARG_GROUP, group);

    fragment.setArguments(args);
    return fragment;
  }

  @Override public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    if (getArguments().containsKey(ARG_GROUP)) {
      testGroup = (Groups) getArguments().getSerializable(ARG_GROUP);
    }

    if (getArguments().containsKey(ARG_INDEX)) {
      testIndex = getArguments().getInt(ARG_INDEX);
    }
  }

  /**
   * The system calls this only when creating the layout in a dialog.
   */
  @NonNull @Override public Dialog onCreateDialog(Bundle savedInstanceState) {
    View view = getActivity().getLayoutInflater().inflate(R.layout.dialog_test_options, null);
    ButterKnife.bind(this, view);

    String title;
    if (testGroup != null) {
      String groupString = "";
      switch (testGroup) {
        case AB:
          groupString = getString(R.string.text__group_ab__short);
          break;
        case CDT:
          groupString = getString(R.string.text__group_cdt__short);
          break;
      }
      title = getString(R.string.test_options__title__random, groupString);
    } else {
      title = getString(R.string.test_options__title__specific);
    }

    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
    builder.setTitle(title)
        .setView(view)
        .setOnDismissListener(this)
        .setNegativeButton(R.string.test_options__action__close,
            new DialogInterface.OnClickListener() {
              @Override public void onClick(DialogInterface dialogInterface, int i) {
                dismiss();
              }
            })
        .setPositiveButton(R.string.test_options__action__begin_test,
            new DialogInterface.OnClickListener() {
              public void onClick(DialogInterface dialog, int id) {
                saveChoices();

                // Start TestActivity.
                Intent intent =
                    new Intent(getActivity().getApplicationContext(), TestActivity.class);
                intent.putExtra(TestActivity.EXTRA_TEST_GROUP, testGroup);
                intent.putExtra(TestActivity.EXTRA_TEST_ID, testIndex);
                intent.putExtra(TestActivity.EXTRA_USES_QUESTIONS, useQuestions);
                intent.putExtra(TestActivity.EXTRA_USES_ROAD_SIGNS, useRoadSigns);
                intent.putExtra(TestActivity.EXTRA_USES_INTERSECTIONS, useIntersections);
                startActivity(intent);
                getFragmentManager().popBackStackImmediate();
              }
            });

    AlertDialog dialog = builder.create();

    dialog.setOnShowListener(new DialogInterface.OnShowListener() {
      @Override public void onShow(DialogInterface dialog) {
        // Restore last choices from SharedPreferences.
        SharedPreferences prefs = getActivity().getPreferences(Context.MODE_PRIVATE);
        useQuestions = prefs.getBoolean("TestOptions_useQuestions", true);
        useRoadSigns = prefs.getBoolean("TestOptions_useRoadSigns", true);
        useIntersections = prefs.getBoolean("TestOptions_useIntersections", true);

        questions_checkbox.setChecked(useQuestions);
        road_signs_checkbox.setChecked(useRoadSigns);
        intersections_checkbox.setChecked(useIntersections);

        questions_checkbox.jumpDrawablesToCurrentState();
        road_signs_checkbox.jumpDrawablesToCurrentState();
        intersections_checkbox.jumpDrawablesToCurrentState();
      }
    });

    return dialog;
  }

  @Override public void onDismiss(DialogInterface dialog) {
    super.onDismiss(dialog);
    saveChoices();
  }

  /**
   * Save the state of checkboxes in preferences.
   */
  private void saveChoices() {
    if (getActivity() == null) {
      return;
    }
    SharedPreferences prefs = getActivity().getPreferences(Context.MODE_PRIVATE);
    SharedPreferences.Editor editor = prefs.edit();
    editor.putBoolean("TestOptions_useQuestions", useQuestions);
    editor.putBoolean("TestOptions_useRoadSigns", useRoadSigns);
    editor.putBoolean("TestOptions_useIntersections", useIntersections);
    editor.apply();
  }

  private void setBeginTestEnabled(boolean enabled) {
    ((AlertDialog) this.getDialog()).getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(enabled);
  }

  private boolean canBeginTest() {
    // If all of them are unchecked, return false.
    return !(!useQuestions && !useRoadSigns && !useIntersections);
  }

  @OnCheckedChanged({
      R.id.questions_checkbox, R.id.road_signs_checkbox, R.id.intersections_checkbox
  }) public void checkboxes_onChanged(CheckBox view, boolean isChecked) {
    switch (view.getId()) {
      case R.id.questions_checkbox:
        useQuestions = isChecked;
        break;
      case R.id.road_signs_checkbox:
        useRoadSigns = isChecked;
        break;
      case R.id.intersections_checkbox:
        useIntersections = isChecked;
        break;
    }
    setBeginTestEnabled(canBeginTest());
  }
}
