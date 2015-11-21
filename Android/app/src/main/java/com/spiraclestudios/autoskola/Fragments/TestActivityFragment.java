package com.spiraclestudios.autoskola.Fragments;

import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
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

    // [Test info]
    public int testId = 1;
    public int testVersion = 1;
    public ArrayList<Integer> testQuestions = new ArrayList<>();
    public int currentQuestion = 1;
    public boolean useQuestions;
    public boolean useRoadSigns;
    public boolean useIntersections;

    // [Internal]
    private long elapsedTime;

    // [Test Settings - Internal]
    public boolean allowClickingOnAnswers = true;
    public boolean markCorrectAnswers = false;
    public boolean colorCorrectAnswers = false;

    // Cached data from database
    List<String> questionsList;
    List<String> imagesList;
    List<Integer> correctAnswersList;
    List<String> answer1List;
    List<String> answer2List;
    List<String> answer3List;
    List<Integer> pointsList;

    // [Current data used by the layout views]
    List<Integer> chosenAnswersList = new ArrayList<>();
    public String mText;
    public Drawable mImage;
    public int mPoints = 0;
    public int mCorrectAnswer = 0;
    public String mAnswer1;
    public String mAnswer2;
    public String mAnswer3;

    // [Layout views]
    @Bind(R.id.question_text)
    TextView question_text;
    @Bind(R.id.question_image)
    ImageView question_image;
    @Bind(R.id.answer1)
    Button question_answer1;
    @Bind(R.id.answer2)
    Button question_answer2;
    @Bind(R.id.answer3)
    Button question_answer3;
    @Bind(R.id.next_question)
    ImageButton next_question;
    @Bind(R.id.previous_question)
    ImageButton previous_question;
    TextView points_value;
    TextView question_counter;
    Chronometer elapsed_time;

    public TestActivityFragment() {
    }

    public static TestActivityFragment newInstance(
            int testId, boolean useQuestions, boolean useRoadSigns, boolean useIntersections
            , boolean markCorrectAnswers) {
        TestActivityFragment fragment = new TestActivityFragment();
        Bundle bundle = new Bundle();

        bundle.putInt("testId", testId);
        bundle.putBoolean("useQuestions", useQuestions);
        bundle.putBoolean("useRoadSigns", useRoadSigns);
        bundle.putBoolean("useIntersections", useIntersections);
        bundle.putBoolean("markCorrectAnswers", markCorrectAnswers);
        fragment.setArguments(bundle);
        return fragment;
    }

    @OnClick(R.id.next_question)
    public void next_question_onClick() {
        if (currentQuestion < questionsList.size()) {
            changeQuestion(currentQuestion + 1);
        } else {
            highlightAnswer(chosenAnswersList.get(currentQuestion - 1));
        }
    }

    @OnClick(R.id.previous_question)
    public void previous_question_onClick() {
        if (currentQuestion > 1)
            changeQuestion(currentQuestion - 1);
    }

    @OnClick(R.id.answer1)
    public void answer1_onClick() {
        answerChosen(1);
    }

    @OnClick(R.id.answer2)
    public void answer2_onClick() {
        answerChosen(2);
    }

    @OnClick(R.id.answer3)
    public void answer3_onClick() {
        answerChosen(3);
    }

    private void answerChosen(int answer) {
        if (!allowClickingOnAnswers) {
            return;
        }

        // If this question was not answered yet and the answer is correct
        if (chosenAnswersList.get(currentQuestion - 1) == 0 && mCorrectAnswer == answer) {
            // Add the amount of points that this question is worth
            addPoints(pointsList.get(currentQuestion - 1));
        }

        // Mark the chosen answer for this question
        chosenAnswersList.set(currentQuestion - 1, answer);

        // Move to the next question
        next_question_onClick();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_test, container, false);
        ButterKnife.bind(this, view);

        // Store references to container's views
        points_value = ButterKnife.findById(container.getRootView(), R.id.points_value);
        question_counter = ButterKnife.findById(container.getRootView(), R.id.question_counter);
        elapsed_time = ButterKnife.findById(container.getRootView(), R.id.elapsed_time);

        // Load an ad
        Helper.loadAd(getContext(), (AdView) container.getRootView().findViewById(R.id.adView));

        Bundle args = getArguments();
        useQuestions = args.getBoolean("useQuestions");
        useRoadSigns = args.getBoolean("useRoadSigns");
        useIntersections = args.getBoolean("useIntersections");
        markCorrectAnswers = args.getBoolean("markCorrectAnswers");

        if (markCorrectAnswers) {
            colorCorrectAnswers = true;
            allowClickingOnAnswers = false;
        }

        setTest(args.getInt("testId"));

        return view;
    }

    // Retrieves data from db, sets all the text and onClickListeners, restarts everything
    public void setTest(int id) {
        testId = id;

        Crashlytics.getInstance().core.setInt("currect_test", testId);

        // SetUp the Database
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

        // Select the user chosen types of questions from the database
        String selector = "";

        if (useQuestions || useRoadSigns || useIntersections) {
            selector += "AND (";
            boolean previousWasSet = false;

            if (useQuestions) {
                selector += "type=0";
                previousWasSet = true;
            }

            if (useRoadSigns) {
                if (previousWasSet) {
                    selector += " OR ";
                }
                selector += "type=1";
                previousWasSet = true;
            }

            if (useIntersections) {
                if (previousWasSet) {
                    selector += " OR ";
                }
                selector += "type=2";
            }

            selector += ")";
        }

        // Get all from Questions for this test version
        String query = "SELECT * FROM " + DbContract.Questions.TABLE_NAME +
                " WHERE " + DbContract.Questions.COLUMN_VERSION + " <= ? " + selector;

        Cursor cQuestions = db.rawQuery(query, new String[]{Integer.toString(testVersion)});

        questionsList = new ArrayList<>();
        for (cQuestions.moveToFirst(); !cQuestions.isAfterLast(); cQuestions.moveToNext()) {
            questionsList.add(cQuestions.getString(cQuestions.
                    getColumnIndexOrThrow(DbContract.Questions.COLUMN_QUESTION)));
        }

        imagesList = new ArrayList<>();
        for (cQuestions.moveToFirst(); !cQuestions.isAfterLast(); cQuestions.moveToNext()) {
            imagesList.add(cQuestions.getString(cQuestions.
                    getColumnIndexOrThrow(DbContract.Questions.COLUMN_IMAGE)));
        }

        correctAnswersList = new ArrayList<>();
        for (cQuestions.moveToFirst(); !cQuestions.isAfterLast(); cQuestions.moveToNext()) {
            correctAnswersList.add(cQuestions.getInt(cQuestions.
                    getColumnIndexOrThrow(DbContract.Questions.COLUMN_CORRECT_ANSWER)));
        }

        answer1List = new ArrayList<>();
        for (cQuestions.moveToFirst(); !cQuestions.isAfterLast(); cQuestions.moveToNext()) {
            answer1List.add(cQuestions.getString(cQuestions.
                    getColumnIndexOrThrow(DbContract.Questions.COLUMN_ANSWER_1)));
        }

        answer2List = new ArrayList<>();
        for (cQuestions.moveToFirst(); !cQuestions.isAfterLast(); cQuestions.moveToNext()) {
            answer2List.add(cQuestions.getString(cQuestions.
                    getColumnIndexOrThrow(DbContract.Questions.COLUMN_ANSWER_2)));
        }

        answer3List = new ArrayList<>();
        for (cQuestions.moveToFirst(); !cQuestions.isAfterLast(); cQuestions.moveToNext()) {
            answer3List.add(cQuestions.getString(cQuestions.
                    getColumnIndexOrThrow(DbContract.Questions.COLUMN_ANSWER_3)));
        }

        pointsList = new ArrayList<>();
        for (cQuestions.moveToFirst(); !cQuestions.isAfterLast(); cQuestions.moveToNext()) {
            pointsList.add(cQuestions.getInt(cQuestions.
                    getColumnIndexOrThrow(DbContract.Questions.COLUMN_POINTS)));
        }

        cQuestions.close();

        // Initialize the chosenAnswersList to the right size
        for (int i = 0; i < testQuestions.size(); i++) {
            chosenAnswersList.add((markCorrectAnswers) ? correctAnswersList
                    .get(testQuestions.get(i)) : 0);
        }

        db.close();

        // If previewing correct answers, display R.string.correct_answers_caps in elapsed_time
        if (!markCorrectAnswers) {
            startTimer();
        } else {
            elapsed_time.setText(getResources().getString(R.string.correct_answers_caps));
            elapsed_time.setTextColor(Color.parseColor("#ffffff"));
        }

        changeQuestion(1);
    }

    public void changeQuestion(int index) {
        currentQuestion = index;
        int questionId = testQuestions.get(currentQuestion - 1);

        setQuestion(questionsList.get(questionId));
        setImage(imagesList.get(questionId));
        setCorrectAnswer(correctAnswersList.get(questionId));
        setPointsValue(pointsList.get(currentQuestion - 1));
        setAnswers(answer1List.get(questionId), answer2List.get(questionId),
                answer3List.get(questionId));
        setQuestionCounter(currentQuestion, questionsList.size());
        highlightAnswer(chosenAnswersList.get(currentQuestion - 1));
    }

    public void highlightAnswer(int answer) {
        List<Button> buttons = new ArrayList<>();
        buttons.add(question_answer1);
        buttons.add(question_answer2);
        buttons.add(question_answer3);

        // If no answer was chosen for this question, just tint all buttons gray
        if (answer == 0) {
            for (Button button : buttons) {
                Drawable drawable = DrawableCompat.wrap(button.getBackground());
                DrawableCompat.setTint(drawable, Color.LTGRAY);
            }
            return;
        }

        // Wrap the drawable so that future tinting calls work
        // on pre-v21 devices. Always use the returned drawable.
        Drawable drawable = DrawableCompat.wrap(buttons.get(answer - 1).getBackground());

        // Color chosen button
        if (colorCorrectAnswers) {
            if (answer == correctAnswersList.get(currentQuestion - 1)) {
                // Correct answer - Green
                DrawableCompat.setTint(drawable, Color.parseColor("#4CAF50"));
            } else {
                // Incorrect answer - Red
                DrawableCompat.setTint(drawable, Color.parseColor("#F44336"));
            }
        } else {
            // Correct answer is not revealed - Gray
            DrawableCompat.setTint(drawable, Color.GRAY);
        }

        // TODO: move somewhere else, check SDK version
        // Doesn't work?
        // Force a redraw on pre-lollipop devices
        for (Button button : buttons) {
            drawable = DrawableCompat.wrap(button.getBackground());
            button.invalidateDrawable(drawable);
        }

        // Tint remaining buttons with default color
        buttons.remove(answer - 1);
        for (Button button : buttons) {
            drawable = DrawableCompat.wrap(button.getBackground());
            DrawableCompat.setTint(drawable, Color.LTGRAY);
        }
    }

    public void setQuestion(String text) {
        mText = text;
        question_text.setText(mText);
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
                    mImage = Drawable.createFromStream(inputStream, null);
                } catch (IOException ex) {
                    // If file doesn't exist, use the placeholder image
                    mImage = ContextCompat.getDrawable(getContext(),
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
                    mImage = Drawable.createFromStream(inputStream, null);
                } catch (IOException ex) {
                    // If file doesn't exist, use the placeholder image
                    mImage = ContextCompat.getDrawable(getContext(),
                            R.drawable.placeholder_large);
                }
            }

            // Placeholders
            else if (path.startsWith(_placeholder)) {
                String image = path.substring(_placeholder.length());
                switch (image) {
                    case "small":
                        mImage = ContextCompat.getDrawable(getContext(),
                                R.drawable.placeholder_small);
                        break;
                    case "large":
                        mImage = ContextCompat.getDrawable(getContext(),
                                R.drawable.placeholder_large);
                        break;
                }
            }

            // Custom Images
            else {
                try {
                    inputStream = getContext().getAssets().open("Images/" + path);
                    mImage = Drawable.createFromStream(inputStream, null);
                } catch (IOException ex) {
                    ex.printStackTrace();
                    return;
                }
            }

            question_image.setImageDrawable(mImage);
            question_image.setVisibility(View.VISIBLE);
        } else {
            mImage = null;
            question_image.setVisibility(View.GONE);
        }
    }

    public void setPoints(int points) {
        mPoints = points;
    }

    public void addPoints(int amount) {
        setPoints(mPoints + amount);
    }

    public void setCorrectAnswer(int index) {
        mCorrectAnswer = index;
    }

    public void setAnswers(String answer1, String answer2, String answer3) {
        // Strip the colors from the strings
        String regex = "red:|green:|blue:";
        answer1 = answer1.replaceFirst(regex, "");
        answer2 = answer2.replaceFirst(regex, "");
        answer3 = answer3.replaceFirst(regex, "");

        mAnswer1 = answer1;
        mAnswer2 = answer2;
        mAnswer3 = answer3;

        // TODO: Try to implement, currently not working, try the tinting code used with buttons
        // Show a colorful circle in the button, representing the color of the car in the answer
        /*if (mAnswer1.startsWith("red:")) {
            Drawable drawable = (Drawable) ContextCompat.getDrawable(getContext(), R.drawable.circle);
            //drawable.getPaint().setColor(Color.parseColor("#FF0000FF"));
            question_answer1.setCompoundDrawables(drawable, null, null, null);
        } else if (mAnswer1.startsWith("green:")) {
            Drawable drawable = (Drawable) ContextCompat.getDrawable(getContext(), R.drawable.circle);
            //drawable.getPaint().setColor(Color.parseColor("#FF0000FF"));
            question_answer1.setCompoundDrawables(drawable, null, null, null);
        } else if (mAnswer1.startsWith("blue:")) {
            Drawable drawable = (Drawable) ContextCompat.getDrawable(getContext(), R.drawable.circle);
            //drawable.getPaint().setColor(Color.parseColor("#FF00FF00"));
            question_answer1.setCompoundDrawables(drawable, null, null, null);
        } else {
            question_answer1.setCompoundDrawables(null, null, null, null);
        }*/

        question_answer1.setText(mAnswer1);
        question_answer2.setText(mAnswer2);
        question_answer3.setText(mAnswer3);
    }

    public void setQuestionCounter(int current, int max) {
        question_counter.setText(current + "/" + max);
    }

    public void setPointsValue(int value) {
        Resources res = getResources();
        String sufix = value == 1 ? res.getString(R.string.point) : res.getString(R.string.points);
        points_value.setText(value + " " + sufix);
    }

    public void startTimer() {
        elapsed_time.setBase(SystemClock.elapsedRealtime());
        elapsed_time.start();
    }

    public void stopTimer() {
        elapsedTime = getElapsedTime();
        elapsed_time.stop();
    }

    public void resumeTimer() {
        elapsed_time.setBase(SystemClock.elapsedRealtime() - elapsedTime);
        elapsed_time.start();
    }

    public long getElapsedTime() {
        return SystemClock.elapsedRealtime() - elapsed_time.getBase();
    }
}
