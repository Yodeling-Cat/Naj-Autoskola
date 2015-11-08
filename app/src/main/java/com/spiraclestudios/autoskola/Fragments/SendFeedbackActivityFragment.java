package com.spiraclestudios.autoskola.Fragments;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.spiraclestudios.autoskola.R;

public class SendFeedbackActivityFragment extends Fragment {
    int mFeedbackType;

    public SendFeedbackActivityFragment() {
    }

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

        EditText email = (EditText) view.findViewById(R.id.email);
        EditText feedback_text = (EditText) view.findViewById(R.id.feedback_text);
        CheckBox attach_screenshot = (CheckBox) view.findViewById(R.id.attach_screenshot);
        CheckBox send_system_info = (CheckBox) view.findViewById(R.id.send_system_info);

        // setOnClickListener for the Floating Action Button
        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.floating_action_button);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO: Send the feedback
            }
        });

        // Get the args
        mFeedbackType = getArguments().getInt("feedbackType");

        return view;
    }
}
