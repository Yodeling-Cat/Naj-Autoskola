// Copyright (c) 2015-2017. Spiracle Software. All Rights Reserved.

package com.spiraclestudios.autoskola.presentation.ui.dialogs;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.View;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.spiraclestudios.autoskola.R;

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
    builder.setTitle(R.string.dev_tools__title).setView(view);

    return builder.create();
  }

  @OnClick(R.id.force_crash) public void force_crash_onClick() {
    throw new RuntimeException("User forced crash from developer menu.");
  }
}
