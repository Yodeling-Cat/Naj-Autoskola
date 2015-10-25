package com.spiraclestudios.autoskola;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.ads.AdView;

import java.util.ArrayList;
import java.util.List;

/**
 * A placeholder fragment containing a simple view.
 */
public class TestActivityFragment extends Fragment {
    // Test info
    public int testId = 1;
    public int testVersion = 1;

    // Cached data from database
    List<String> questionsList;
    List<String> imagesList;
    List<String> answer1List;
    List<String> answer2List;
    List<String> answer3List;
    List<Integer> pointsList;

    // Current data used by the layout views
    public String questionText;
    public Drawable questionImage;
    public int questionPoints;
    public int questionCorrectAnswer;
    public String questionAnswer1;
    public String questionAnswer2;
    public String questionAnswer3;

    // Layout views
    public TextView question_text;
    public ImageView question_image;
    public Button question_answer1;
    public Button question_answer2;
    public Button question_answer3;
    public TextView question_counter;
    public ImageButton next_question;
    public ImageButton previous_question;

    public TestActivityFragment() {
    }

    public static TestActivityFragment newInstance(int testId) {
        TestActivityFragment fragment = new TestActivityFragment();
        Bundle bundle = new Bundle();

        bundle.putInt("testId", testId);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_test, container, false);

        // Load an ad
        Helper.loadAd(getContext(), (AdView) view.findViewById(R.id.adView));

        // Store references to all the layout views
        question_text = (TextView) view.findViewById(R.id.question_text);
        question_image = (ImageView) view.findViewById(R.id.question_image);
        question_answer1 = (Button) view.findViewById(R.id.answer1);
        question_answer2 = (Button) view.findViewById(R.id.answer2);
        question_answer3 = (Button) view.findViewById(R.id.answer3);
        question_counter = (TextView) view.findViewById(R.id.question_counter);
        next_question = (ImageButton) view.findViewById(R.id.next_question);
        previous_question = (ImageButton) view.findViewById(R.id.previous_question);

        setTest(getArguments().getInt("testId"));

        return view;
    }

    // Retrieves data from db, sets all the text and onClickListeners, restarts everything
    public void setTest(int id) {
        testId = id;

        // SetUp database
        DatabaseHelper dbHelper = new DatabaseHelper(getContext());
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        // Get latest version of this test
        Cursor cTestVersion = db.rawQuery(
                "SELECT versionCode FROM Testy WHERE _id = ?", new String[]
                        {Integer.toString(testId)});
        cTestVersion.moveToFirst();
        testVersion = cTestVersion.getInt(0);
        cTestVersion.close();

        // Get all the question data for this test version from database and store them
        Cursor cOtazky = db.rawQuery(
                "SELECT question, image, points, correctAnswer, answer1, answer2, answer3" +
                        " FROM Otazky WHERE version <= ?", new String[]
                        {Integer.toString(testVersion)});

        questionsList = new ArrayList<>();
        for (cOtazky.moveToFirst(); !cOtazky.isAfterLast(); cOtazky.moveToNext()) {
            questionsList.add(cOtazky.getString(cOtazky.getColumnIndexOrThrow("question")));
        }

        imagesList = new ArrayList<>();
        for (cOtazky.moveToFirst(); !cOtazky.isAfterLast(); cOtazky.moveToNext()) {
            imagesList.add(cOtazky.getString(cOtazky.getColumnIndexOrThrow("image")));
        }

        answer1List = new ArrayList<>();
        for (cOtazky.moveToFirst(); !cOtazky.isAfterLast(); cOtazky.moveToNext()) {
            answer1List.add(cOtazky.getString(cOtazky.getColumnIndexOrThrow("answer1")));
        }

        answer2List = new ArrayList<>();
        for (cOtazky.moveToFirst(); !cOtazky.isAfterLast(); cOtazky.moveToNext()) {
            answer2List.add(cOtazky.getString(cOtazky.getColumnIndexOrThrow("answer2")));
        }

        answer3List = new ArrayList<>();
        for (cOtazky.moveToFirst(); !cOtazky.isAfterLast(); cOtazky.moveToNext()) {
            answer3List.add(cOtazky.getString(cOtazky.getColumnIndexOrThrow("answer3")));
        }

        pointsList = new ArrayList<>();
        for (cOtazky.moveToFirst(); !cOtazky.isAfterLast(); cOtazky.moveToNext()) {
            pointsList.add(cOtazky.getInt(cOtazky.getColumnIndexOrThrow("points")));
        }

        cOtazky.moveToFirst();
        questionCorrectAnswer = cOtazky.getInt(cOtazky.getColumnIndexOrThrow("correctAnswer"));

        cOtazky.close();
        db.close();

        changeQuestion(3);
    }

    // param id takes an int starting from 1 and the function handles matching it with the correct
    // 0-based array indexes
    public void changeQuestion(int questionId) {
        setPoints(pointsList.get(questionId - 1));
        setQuestion(questionsList.get(questionId - 1) + " (" + questionPoints + " body)");
        setImage(imagesList.get(questionId - 1));
        setAnswers(answer1List.get(questionId - 1), answer2List.get(questionId - 1), answer3List.get(questionId - 1));
    }

    public void setQuestion(String text) {
        questionText = text;
        question_text.setText(questionText);
    }

    public void setImage(String path) {
        if (!path.isEmpty()) {
            if (path.startsWith("znacka:")) {
                // TODO: Get road sign image from database using API that you are going to write right now
                //questionImage = ;
            } else {
                // Get image from the assets folder
                //questionImage = ;
            }
            question_image.setVisibility(View.VISIBLE);
        }
        else {
            questionImage = null;
            question_image.setVisibility(View.GONE);
        }

        question_image.setImageDrawable(questionImage);
        // TODO: REMOVE THIS LINE AFTER IMPLEMENTING THIS FUNCTION PROPERLY
        question_image.setVisibility(View.GONE);
    }

    public void setPoints(int points) {
        questionPoints = points;
    }

    public void setAnswers(String answer1, String answer2, String answer3) {
        questionAnswer1 = answer1;
        questionAnswer2 = answer2;
        questionAnswer3 = answer3;

        question_answer1.setText(questionAnswer1);
        question_answer2.setText(questionAnswer2);
        question_answer3.setText(questionAnswer3);
    }
}
