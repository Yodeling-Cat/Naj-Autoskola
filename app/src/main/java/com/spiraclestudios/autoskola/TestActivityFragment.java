package com.spiraclestudios.autoskola;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdView;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import hugo.weaving.DebugLog;

/**
 * A placeholder fragment containing a simple view.
 */
public class TestActivityFragment extends Fragment {
    private static final String TAG = "TestActivityFragment";

    // Test info
    public int testId = 1;
    public int testVersion = 1;
    public ArrayList<Integer> testQuestions = new ArrayList<>();
    public int currentQuestion = 0;
    public int currentPoints = 0;
    public boolean useQuestions;
    public boolean useRoadSigns;
    public boolean useIntersections;

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
    public ImageButton next_question;
    public ImageButton previous_question;
    public TextView points_counter;
    public TextView question_counter;
    public TextView elapsed_time;

    public TestActivityFragment() {
    }

    public static TestActivityFragment newInstance(
            int testId, boolean useQuestions, boolean useRoadSigns, boolean useIntersections) {
        TestActivityFragment fragment = new TestActivityFragment();
        Bundle bundle = new Bundle();

        bundle.putInt("testId", testId);
        bundle.putBoolean("useQuestions", useQuestions);
        bundle.putBoolean("useRoadSigns", useRoadSigns);
        bundle.putBoolean("useIntersections", useIntersections);
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
        next_question = (ImageButton) view.findViewById(R.id.next_question);
        previous_question = (ImageButton) view.findViewById(R.id.previous_question);
        points_counter = (TextView) container.getRootView().findViewById(R.id.points_counter);
        question_counter = (TextView) container.getRootView().findViewById(R.id.question_counter);
        elapsed_time = (TextView) container.getRootView().findViewById(R.id.elapsed_time);

        next_question.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (currentQuestion < questionsList.size() - 1)
                    changeQuestion(currentQuestion + 1);
            }
        });

        previous_question.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (currentQuestion > 0)
                    changeQuestion(currentQuestion - 1);
            }
        });

        Bundle args = getArguments();
        useQuestions = args.getBoolean("useQuestions");
        useRoadSigns = args.getBoolean("useRoadSigns");
        useIntersections = args.getBoolean("useIntersections");

        Log.d(TAG, "useQuestions: " + useQuestions);
        Log.d(TAG, "useRoadSigns: " + useRoadSigns);
        Log.d(TAG, "useIntersections: " + useIntersections);

        setTest(args.getInt("testId"));
        return view;
    }

    // Retrieves data from db, sets all the text and onClickListeners, restarts everything
    @DebugLog
    public void setTest(int id) {
        testId = id;

        // [SetUp the Database]
        DatabaseHelper dbHelper = new DatabaseHelper(getContext());
        SQLiteDatabase db = dbHelper.getReadableDatabase();


        //// [Testy] ////

        // Get latest version of this test
        Cursor cTest = db.rawQuery(
                "SELECT " + DatabaseContract.Testy.COLUMN_QUESTIONS + ", " +
                        DatabaseContract.Testy.COLUMN_VERSION_CODE + " FROM Testy WHERE " +
                        DatabaseContract.Testy.COLUMN_TEST_ID + " = ?", new String[]
                        {Integer.toString(testId)});

        cTest.moveToFirst();

        String questions = cTest.getString(cTest.getColumnIndexOrThrow(
                DatabaseContract.Testy.COLUMN_QUESTIONS));

        // If this test has no questions_checkbox assigned, show a toast and return to MainActivity
        if (questions == null || questions.isEmpty()) {
            Toast.makeText(getContext(), R.string.toast_test_is_empty, Toast.LENGTH_LONG).show();

            Intent intent = new Intent(getContext(), MainActivity.class);
            startActivity(intent);
            return;
        }

        String[] questionsArray = questions.split(",");

        for (String question : questionsArray) {
            testQuestions.add(Integer.parseInt(question));
        }

        testVersion = cTest.getInt(cTest.getColumnIndexOrThrow(
                DatabaseContract.Testy.COLUMN_VERSION_CODE));

        cTest.close();


        //// [Otazky] ////

        // Get all questions_checkbox for this test version, then pick the ones we need later
        String selector = "";

        if (useQuestions && useRoadSigns && useIntersections) {
            selector = "AND (type=0 OR type=1 OR type=2)";
        } else if (useQuestions && useRoadSigns && !useIntersections) {
            selector = "AND (type=0 OR type=1)";
        } else if (useQuestions && !useRoadSigns && !useIntersections) {
            selector = "AND type=0";
        } else if (useQuestions && !useRoadSigns && useIntersections) {
            selector = "AND (type=0 OR type=2)";
        } else if (!useQuestions && useRoadSigns && useIntersections) {
            selector = "AND (type=1 OR type=2)";
        } else if (!useQuestions && !useRoadSigns && useIntersections) {
            selector = "AND type=2";
        }

        String query = "SELECT * FROM Otazky WHERE version <= ? " + selector;
        Log.d(TAG, "selector: " + selector);
        Log.d(TAG, "query: " + query);

        Cursor cOtazky = db.rawQuery(query, new String[]{Integer.toString(testVersion)});

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
        changeQuestion(0);
    }

    // param id takes an int starting from 1 and the function handles matching it with the correct
    // 0-based array indexes
    public void changeQuestion(int index) {
        currentQuestion = index;
        int questionId = testQuestions.get(currentQuestion);

        setPoints(pointsList.get(questionId));
        setQuestion(questionsList.get(questionId) + " (" + questionPoints + " body)");
        setImage(imagesList.get(questionId));
        setAnswers(answer1List.get(questionId), answer2List.get(questionId),
                answer3List.get(questionId));
        setQuestionCounter(currentQuestion + 1, questionsList.size());
    }

    public void setQuestion(String text) {
        questionText = text;
        question_text.setText(questionText);
    }

    public void setImage(String path) {
        if (path != null && !path.isEmpty()) {
            InputStream inputStream;

            // Znacky
            if (path.startsWith("znacka:")) {
                // Use image from the assets folder
                String subPath = path.substring(7);
                try {
                    inputStream = getContext().getAssets()
                            .open("images/znacky/" + subPath + ".png");
                    questionImage = Drawable.createFromStream(inputStream, null);
                } catch (IOException ex) {
                    // If file doesn't exist, use the placeholder image
                    questionImage = ContextCompat.getDrawable(getContext(),
                            R.drawable.placeholder_znacka);
                }
            }

            // Krizovatky
            else if (path.startsWith("krizovatka:")) {
                // Use image from the assets folder
                String subPath = path.substring(11);
                try {
                    inputStream = getContext().getAssets()
                            .open("images/krizovatky/" + subPath + ".png");
                    questionImage = Drawable.createFromStream(inputStream, null);
                } catch (IOException ex) {
                    // If file doesn't exist, use the placeholder image
                    questionImage = ContextCompat.getDrawable(getContext(),
                            R.drawable.placeholder_krizovatka);
                }
            }

            // Custom image
            else {
                try {
                    inputStream = getContext().getAssets().open("images/" + path);
                    questionImage = Drawable.createFromStream(inputStream, null);
                } catch (IOException ex) {
                    ex.printStackTrace();
                    return;
                }
            }

            question_image.setImageDrawable(questionImage);
            question_image.setVisibility(View.VISIBLE);
        } else {
            questionImage = null;
            question_image.setVisibility(View.GONE);
        }
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

    public void setQuestionCounter(int current, int max) {
        question_counter.setText(current + "/" + max);
    }

    public void setPointsCounter(int current, int max) {
        points_counter.setText(current + "/" + max);
    }
}
