package com.spiraclestudios.autoskola;

/**
 * Created by benji on 14/10/2015.
 */
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.IOException;

public class MainActivityFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Helper.setTheme(getContext());
        View view = inflater.inflate(R.layout.fragment_main, container, false);


        // [DATABASE TESTING]
        LinearLayout content_main = (LinearLayout)view.findViewById(R.id.content_main);


        DatabaseHelper dbHelper = new DatabaseHelper(getContext());
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        //Cursor cursor = db.query("Otazky", new String[]{"question", "answer1"}, "questionId = ? AND version = ?", new String[]{"3", "2"}, null, null, null);
        Cursor cursor = db.rawQuery("SELECT versionCode, versionName FROM Testy WHERE _id = 35;", null);
        cursor.moveToFirst();

        TextView textView = new TextView(getContext());
        textView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        textView.setText("Version code: " + cursor.getString(cursor.getColumnIndexOrThrow("versionCode")));
        content_main.addView(textView);

        TextView textView2 = new TextView(getContext());
        textView2.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        textView2.setText("Version name: " + cursor.getString(cursor.getColumnIndexOrThrow("versionName")));
        content_main.addView(textView2);

        cursor.close();
        return view;
    }
}