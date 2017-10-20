// Copyright (c) 2015-2017. Spiracle Software. All Rights Reserved.

package com.spiraclestudios.autoskola.presentation.ui.dialogs;

import android.app.Dialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.View;
import butterknife.ButterKnife;
import com.spiraclestudios.autoskola.BuildConfig;
import com.spiraclestudios.autoskola.G;
import com.spiraclestudios.autoskola.R;

import static android.content.Context.MODE_PRIVATE;

public class GoingFreeDialog extends AppCompatDialogFragment {

  public GoingFreeDialog() {
  }

  public static void handleGoingFreeDialog(AppCompatActivity ctx) {
    if (BuildConfig.PREMIUM) return;

    SharedPreferences prefs = ctx.getSharedPreferences(G.PREFS_GENERIC, MODE_PRIVATE);

    if (!prefs.getBoolean("seen_going_free_dialog", false)) {
      prefs.edit().putBoolean("seen_going_free_dialog", true).apply();
      GoingFreeDialog goingFreeDialog = new GoingFreeDialog();
      goingFreeDialog.show(ctx.getSupportFragmentManager(), "GoingFree");
    }
  }

  @NonNull @Override public Dialog onCreateDialog(Bundle savedInstanceState) {
    View view = getActivity().getLayoutInflater().inflate(R.layout.dialog_going_free, null);
    ButterKnife.bind(this, view);

    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
    builder.setView(view)
        .setTitle(getString(R.string.going_free__title))
        .setPositiveButton(R.string.going_free__action__ok, (dialog, id) -> {
        });
    return builder.create();
  }
}
