package com.spiraclestudios.autoskola.Fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.spiraclestudios.autoskola.R;

public class FeedbackActivityFragment extends Fragment {

    public FeedbackActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_feedback, container, false);

        LinearLayout send_a_suggestion = (LinearLayout) view.findViewById(R.id.send_a_suggestion);
        LinearLayout report_a_bug = (LinearLayout) view.findViewById(R.id.report_a_bug);
        LinearLayout ask_for_help = (LinearLayout) view.findViewById(R.id.ask_for_help);

        send_a_suggestion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GoToSendFeedbackFragment(0);
            }
        });

        report_a_bug.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GoToSendFeedbackFragment(1);
            }
        });

        ask_for_help.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "Not yet implemented.", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }

    private void GoToSendFeedbackFragment(int feedbackType) {
        Fragment fragment = SendFeedbackActivityFragment.newInstance(feedbackType);
        FragmentTransaction transaction = getFragmentManager().beginTransaction();

        transaction.replace(R.id.fragment_container, fragment);
        transaction.addToBackStack(null);

        transaction.commit();
    }
}
