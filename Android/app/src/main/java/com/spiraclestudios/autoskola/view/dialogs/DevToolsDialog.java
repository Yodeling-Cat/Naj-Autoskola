/*
 * Copyright (c) 2015-2017. Spiracle Studios. All Rights Reserved.
 */

package com.spiraclestudios.autoskola.view.dialogs;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.View;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.spiraclestudios.autoskola.R;
import com.spiraclestudios.autoskola.view.activities.DatabaseManagerActivity;

public class DevToolsDialog extends AppCompatDialogFragment {

  public DevToolsDialog() {
  }

  @Override public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    FirebaseAnalytics firebaseAnalytics = FirebaseAnalytics.getInstance(getActivity());
    Bundle bundle = new Bundle();
    firebaseAnalytics.logEvent("nav_dev_tools", bundle);
  }

  /**
   * The system calls this only when creating the layout in a dialog.
   */
  @NonNull @Override public Dialog onCreateDialog(Bundle savedInstanceState) {
    View view = getActivity().getLayoutInflater().inflate(R.layout.dialog_dev_tools, null);
    ButterKnife.bind(this, view);

    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
    builder.setTitle(R.string.title__dev_tools).setView(view);

    AlertDialog dialog = builder.create();

    return dialog;
  }

  @OnClick(R.id.database_manager) public void database_manager_OnClick() {
    Intent intent =
        new Intent(getActivity().getApplicationContext(), DatabaseManagerActivity.class);
    startActivity(intent);
    getFragmentManager().popBackStackImmediate();

    dismiss();
  }

  @OnClick(R.id.force_crash) public void force_crash_onClick() {
    throw new RuntimeException("User forced crash from developer menu.");
  }
}
