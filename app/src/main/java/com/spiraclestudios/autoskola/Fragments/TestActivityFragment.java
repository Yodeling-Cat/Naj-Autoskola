package com.spiraclestudios.autoskola.Fragments;

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
import com.spiraclestudios.autoskola.Activities.MainActivity;
import com.spiraclestudios.autoskola.DbContract;
import com.spiraclestudios.autoskola.DbHelper;
import com.spiraclestudios.autoskola.Helper;
import com.spiraclestudios.autoskola.R;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import hugo.weaving.DebugLog;

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
    @Bind(R.id.question_text) TextView question_text;
    @Bind(R.id.question_image) ImageView question_image;
    @Bind(R.id.answer1) Button question_answer1;
    @Bind(R.id.answer2) Button question_answer2;
    @Bind(R.id.answer3) Button question_answer3;
    @Bind(R.id.next_question) ImageButton next_question;
    @Bind(R.id.previous_question) ImageButton previous_question;
    TextView points_counter;
    TextView question_counter;
    TextView elapsed_time;

    public TestActivityFragment() {}

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

    @OnClick(R.id.next_question)
    public void onClickNextQuestion() {
        if (currentQuestion < questionsList.size() - 1)
            changeQuestion(currentQuestion + 1);
    }

    @OnClick(R.id.previous_question)
    public void onClickPreviousQuestion() {
        if (currentQuestion > 0)
            changeQuestion(currentQuestion - 1);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_test, container, false);
        ButterKnife.bind(this, view);

        // Store references to container's views
        points_counter = ButterKnife.findById(container.getRootView(), R.id.points_counter);
        question_counter = ButterKnife.findById(container.getRootView(), R.id.question_counter);
        elapsed_time = ButterKnife.findById(container.getRootView(), R.id.elapsed_time);

        // Load an ad
        Helper.loadAd(getContext(), (AdView) view.findViewById(R.id.adView));

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
        DbHelper dbHelper = new DbHelper(getContext());
        SQLiteDatabase db = dbHelper.getReadableDatabase();


        //// [Tests] ////

        // Get latest version of this test
        Cursor cTest = db.rawQuery(
                "SELECT " + DbContract.Tests.COLUMN_QUESTIONS + ", " +
                        DbContract.Tests.COLUMN_VERSION_CODE + " FROM " +
                        DbContract.Tests.TABLE_NAME + " WHERE " +
                        DbContract.Tests.COLUMN_TEST_ID + " = ?", new String[]
                        {Integer.toString(testId)});

        cTest.moveToFirst();

        String questions = cTest.getString(cTest.getColumnIndexOrThrow(
                DbContract.Tests.COLUMN_QUESTIONS));

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
                DbContract.Tests.COLUMN_VERSION_CODE));

        cTest.close();


        //// [Questions] ////

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

        String query = "SELECT * FROM " + DbContract.Questions.TABLE_NAME +
                " WHERE " + DbContract.Questions.COLUMN_VERSION + " <= ? " + selector;

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
            String _sign = "sign:";
            String _inter = "inter:";
            String _placeholder = "placeholder:";

            // Road Signs
            if (path.startsWith(_sign)) {
                // Use image from the assets folder
                String subPath = path.substring(_sign.length());
                try {
                    inputStream = getContext().getAssets()
                            .open("Images/road_signs/" + subPath + ".png");
                    questionImage = Drawable.createFromStream(inputStream, null);
                } catch (IOException ex) {
                    // If file doesn't exist, use the placeholder image
                    questionImage = ContextCompat.getDrawable(getContext(),
                            R.drawable.placeholder_small);
                }
            }

            // Intersections
            else if (path.startsWith(_inter)) {
                // Use image from the assets folder
                String subPath = path.substring(_inter.length());
                try {
                    inputStream = getContext().getAssets()
                            .open("Images/intersections/" + subPath + ".png");
                    questionImage = Drawable.createFromStream(inputStream, null);
                } catch (IOException ex) {
                    // If file doesn't exist, use the placeholder image
                    questionImage = ContextCompat.getDrawable(getContext(),
                            R.drawable.placeholder_large);
                }
            }

            // Placeholders
            else if (path.startsWith(_placeholder)) {
                String image = path.substring(_placeholder.length());
                switch (image) {
                    case "small":
                        questionImage = ContextCompat.getDrawable(getContext(),
                                R.drawable.placeholder_small);
                        break;
                    case "large":
                        questionImage = ContextCompat.getDrawable(getContext(),
                                R.drawable.placeholder_large);
                        break;
                }
            }

            // Custom Images
            else {
                try {
                    inputStream = getContext().getAssets().open("Images/" + path);
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
