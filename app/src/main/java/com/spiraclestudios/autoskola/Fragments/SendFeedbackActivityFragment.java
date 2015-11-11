package com.spiraclestudios.autoskola.Fragments;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;

import com.spiraclestudios.autoskola.BuildConfig;
import com.spiraclestudios.autoskola.Dialogs.PreviewSystemInfoDialog;
import com.spiraclestudios.autoskola.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class SendFeedbackActivityFragment extends Fragment {
    int mFeedbackType;

    public SendFeedbackActivityFragment() {
    }

    /* feedbackType:
     * 0 - Feedback
     * 1 - Bug
     */
    public static SendFeedbackActivityFragment newInstance(int feedbackType) {
        SendFeedbackActivityFragment fragment = new SendFeedbackActivityFragment();
        Bundle bundle = new Bundle();

        bundle.putInt("feedbackType", feedbackType);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_send_feedback, container, false);

        final EditText feedback_text = (EditText) view.findViewById(R.id.feedback_text);
        final CheckBox send_system_info = (CheckBox) view.findViewById(R.id.send_system_info);
        ImageButton preview_system_info = (ImageButton) view.findViewById(
                R.id.preview_system_info);

        // setOnClickListener for Preview System Info
        preview_system_info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: Show a dialog displaying the system info and give the user a button to disable or toggle the checkbox for sending it
                PreviewSystemInfoDialog dialog = new PreviewSystemInfoDialog();
                dialog.show(getActivity().getSupportFragmentManager(), "PreviewSystemInfo");
            }
        });

        // Retrieve arguments
        mFeedbackType = getArguments().getInt("feedbackType");

        // Hide the Send System Info checkbox if not reporting a bug
        if (mFeedbackType != 1) {
            send_system_info.setVisibility(View.GONE);
        }

        // setOnClickListener for the Floating Action Button
        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fab_send);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO: If you stop using email in the future, remember to ask the user for his email so you can contact him back, currently he uses his email to send the feedback.

                String subject = "[Autoškola] ";
                String text = feedback_text.getText().toString();

                // Modify the subject
                if (mFeedbackType == 0) {
                    subject += getResources().getString(R.string.send_feedback_subject_feedback);
                } else {
                    subject += getResources().getString(R.string.send_feedback_subject_bug);
                }

                // Modify the text
                if (send_system_info.isChecked()) {
                    text += getSystemInfo();
                }

                // Send the feedback
                Intent Email = new Intent(Intent.ACTION_SEND);
                Email.setType("text/email");
                Email.putExtra(Intent.EXTRA_EMAIL, new String[]{"spiraclestudios@gmail.com"});
                Email.putExtra(Intent.EXTRA_SUBJECT, subject);
                Email.putExtra(Intent.EXTRA_TEXT, text);
                startActivity(Intent.createChooser(Email, getResources().
                        getString(R.string.send_feedback_chooser_title)));
            }
        });

        return view;
    }

    public String getSystemInfo() {
        String text = "";
        text += "\n\n----------- SYSTEM -----------\n";
        text += "\n-- APPLICATION --\n";
        text += "Package: " + BuildConfig.APPLICATION_ID + "\n";
        text += "Build type: " + BuildConfig.BUILD_TYPE + "\n";
        text += "Flavor: " + ((BuildConfig.FLAVOR != "") ? BuildConfig.FLAVOR : "none") + "\n";
        text += "Version name: " + BuildConfig.VERSION_NAME + "\n";
        text += "Version code: " + BuildConfig.VERSION_CODE + "\n";

        text += "\n-- OS --\n";
        text += "SDK version: " + Build.VERSION.SDK_INT + "\n";
        text += "Incremental: " + Build.VERSION.INCREMENTAL + "\n";
        text += "Changelist ID: " + Build.ID + "\n";

        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss dd/MM/yyyy"
                , Locale.ENGLISH);
        text += "\n-- DEVICE --\n";
        text += "Device: " + Build.DEVICE + "\n";
        text += "Model: " + Build.MODEL + "\n";
        text += "Product: " + Build.PRODUCT + "\n";
        text += "Brand: " + Build.BRAND + "\n";
        text += "Manufacturer: " + Build.MANUFACTURER + "\n";
        text += "Time: " + dateFormat.format(new Date()) + "\n";

        Display display = getActivity().getWindowManager().getDefaultDisplay();
        DisplayMetrics metrics = new DisplayMetrics();
        display.getMetrics(metrics);
        text += "\n-- DISPLAY --\n";
        text += "Width: " + metrics.widthPixels + "\n";
        text += "Height: " + metrics.heightPixels + "\n";
        text += "Density DPI: " + metrics.densityDpi + "\n";
        text += "Scaled density: " + metrics.scaledDensity;

        return text;
    }
}
