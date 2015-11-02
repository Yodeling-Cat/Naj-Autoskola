package com.spiraclestudios.autoskola;

import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by benji on 5/10/2015.
 */
public class AboutDialog extends DialogFragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_about, container, false);
        getDialog().setTitle(R.string.dialog_about_title);

        Resources res = getResources();
        ((TextView) view.findViewById(R.id.info_values)).setText(
                String.format(res.getString(R.string.dialog_about_info_values),
                        BuildConfig.VERSION_NAME));

        return view;
    }
}
