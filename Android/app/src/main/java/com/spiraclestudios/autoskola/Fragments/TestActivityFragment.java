package com.spiraclestudios.autoskola.Fragments;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
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
import com.spiraclestudios.autoskola.Activities.ResultsActivity;
import com.spiraclestudios.autoskola.DbContract;
import com.spiraclestudios.autoskola.DbHelper;
import com.spiraclestudios.autoskola.Helper;
import com.spiraclestudios.autoskola.R;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnLongClick;
import timber.log.Timber;

public class TestActivityFragment extends Fragment {
    private static final String TAG = "TestActivityFragment";

    // [Test info]
    public int testId = 1;
    public int testVersion = 1;
    public ArrayList<Integer> allQuestionIds = new ArrayList<>();
    public int currentQuestionIdx = 1;
    public boolean usesQuestions;
    public boolean usesRoadSigns;
    public boolean usesIntersections;
    public int questionsCount;
    public int maxPoints;
    public int amountCorrect;

    // [Internal]
    // Did the user evaluate the test results?
    private boolean finished = false;
    private boolean allQuestionsAnswered = false;
    private long elapsedTime;
    private int amountAnswered;

    // [Test Settings - Internal]
    public boolean allowClickingOnAnswers = true;
    public boolean markCorrectAnswers = false;
    public boolean colorCorrectAnswers = false;

    // Cached data from database
    List<Integer> questionIds;
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
        bundle.putBoolean("usesQuestions", useQuestions);
        bundle.putBoolean("usesRoadSigns", useRoadSigns);
        bundle.putBoolean("usesIntersections", useIntersections);
        bundle.putBoolean("markCorrectAnswers", markCorrectAnswers);
        fragment.setArguments(bundle);
        return fragment;
    }

    /**
     * Copy question text to clipboard
     */
    @OnLongClick(R.id.question_text)
    public boolean question_text_onLongClick() {
        ClipboardManager clipboard = (ClipboardManager) getActivity()
                .getSystemService(Context.CLIPBOARD_SERVICE);

        String label = String.format(getString(R.string.clip_label_question), currentQuestionIdx);
        ClipData clip = ClipData.newPlainText(label, question_text.getText().toString());

        clipboard.setPrimaryClip(clip);

        Toast.makeText(getContext(), R.string.toast_question_was_copied, Toast.LENGTH_SHORT).show();
        return true;
    }

    /**
     * Copy answer text to clipboard
     */
    @OnLongClick({R.id.answer1, R.id.answer2, R.id.answer3})
    public boolean answers_onLongClick(Button button) {
        ClipboardManager clipboard = (ClipboardManager) getActivity()
                .getSystemService(Context.CLIPBOARD_SERVICE);

        String label = getString(R.string.clip_label_answer);
        ClipData clip = ClipData.newPlainText(label, button.getText().toString());

        clipboard.setPrimaryClip(clip);

        Toast.makeText(getContext(), R.string.toast_answer_was_copied, Toast.LENGTH_SHORT).show();
        return true;
    }

    /**
     * Moves to the next question and highlights it.
     */
    @OnClick(R.id.next_question)
    public void nextQuestion() {
        if (currentQuestionIdx < questionsList.size()) {
            changeQuestion(currentQuestionIdx + 1);
        } else {
            highlightAnswer(chosenAnswersList.get(currentQuestionIdx - 1));
        }
    }

    /**
     * Moves to the previous question and highlights it.
     */
    @OnClick(R.id.previous_question)
    public void previousQuestion() {
        if (currentQuestionIdx > 1)
            changeQuestion(currentQuestionIdx - 1);
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

    /**
     * Check or un-check an answer.
     *
     * @param answer The index of the answer button.
     */
    private void answerChosen(int answer) {
        if (!allowClickingOnAnswers) {
            return;
        }

        int currentAnswer = chosenAnswersList.get(currentQuestionIdx - 1);

        // Un-check the answer if the user clicks on the current answer.
        if (currentAnswer == answer) {
            amountAnswered--;
            allQuestionsAnswered = false;
            chosenAnswersList.set(currentQuestionIdx - 1, 0);
            highlightAnswer(0);
        }
        // If there is currently no answer or a different answer than the current one was chosen
        else {
            if (currentAnswer == 0) {
                amountAnswered++;
            }
            chosenAnswersList.set(currentQuestionIdx - 1, answer);
            nextQuestion();
        }

        // If the toast wasn't shown yet, then show it.
        if (!allQuestionsAnswered && amountAnswered == questionsCount) {
            allQuestionsAnswered = true;
            Toast.makeText(getContext(), R.string.toast_all_questions_answered, Toast.LENGTH_SHORT)
                    .show();
        }
    }

    /**
     * Calculate points, handle test review and show the results activity.
     */
    public void evaluateResults() {
        if (!finished) {
            // Calculate scored points
            amountCorrect = 0;
            for (int i = 0; i < questionsCount; i++) {
                if (chosenAnswersList.get(i) == correctAnswersList.get(i)) {
                    addPoints(pointsList.get(i));
                    amountCorrect++;
                }
            }

            // Mark the correct answers for if the user comes back to the test
            // after viewing the results
            markCorrectAnswers = true;
            colorCorrectAnswers = true;
            allowClickingOnAnswers = false;
            pauseTimer();
            highlightAnswer(chosenAnswersList.get(currentQuestionIdx - 1));

            finished = true;
        }

        // Start ResultsActivity
        Intent intent = new Intent(getContext(), ResultsActivity.class);
        intent.putExtra(ResultsActivity.EXTRA_TEST_ID, testId);
        intent.putExtra(ResultsActivity.EXTRA_TEST_VERSION, testVersion);
        intent.putExtra(ResultsActivity.EXTRA_USES_QUESTIONS, usesQuestions);
        intent.putExtra(ResultsActivity.EXTRA_USES_ROAD_SIGNS, usesRoadSigns);
        intent.putExtra(ResultsActivity.EXTRA_USES_INTERSECTIONS, usesIntersections);
        intent.putExtra(ResultsActivity.EXTRA_POINTS, mPoints);
        intent.putExtra(ResultsActivity.EXTRA_MAX_POINTS, maxPoints);
        intent.putExtra(ResultsActivity.EXTRA_ELAPSED_TIME, getElapsedTime());
        intent.putExtra(ResultsActivity.EXTRA_ELAPSED_TIME_TEXT, elapsed_time.getText());
        intent.putIntegerArrayListExtra(ResultsActivity.EXTRA_ANSWERS,
                (ArrayList<Integer>) chosenAnswersList);
        intent.putExtra(ResultsActivity.EXTRA_CORRECT, amountCorrect);
        intent.putExtra(ResultsActivity.EXTRA_INCORRECT, questionsCount - amountCorrect);

        startActivity(intent);
    }

    @Override
    public void onPause() {
        super.onPause();
        pauseTimer();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!finished && !markCorrectAnswers)
            resumeTimer();
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
        usesQuestions = args.getBoolean("usesQuestions");
        usesRoadSigns = args.getBoolean("usesRoadSigns");
        usesIntersections = args.getBoolean("usesIntersections");
        markCorrectAnswers = args.getBoolean("markCorrectAnswers");

        // TODO: Remove after implementing intersections
        if (usesIntersections) {
            Toast.makeText(getContext(), R.string.toast_intersections_not_yet_implemented,
                    Toast.LENGTH_SHORT)
                    .show();
        }

        if (markCorrectAnswers) {
            colorCorrectAnswers = true;
            allowClickingOnAnswers = false;
        }

        setTest(args.getInt("testId"));

        return view;
    }

    /**
     * Retrieves data from db, sets all the text and onClickListeners, restarts everything
     */
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

        // The whole 'questions' string from the Tests table
        String questionsString = cTest.getString(cTest.getColumnIndexOrThrow(
                DbContract.Tests.COLUMN_QUESTIONS));

        // If this test has no questions_checkbox assigned, show a toast and return to MainActivity
        if (questionsString == null || questionsString.isEmpty()) {
            Toast.makeText(getContext(), R.string.toast_test_is_empty, Toast.LENGTH_LONG).show();

            // TODO: Shouldn't this be replaced with simply finish()?
            Intent intent = new Intent(getContext(), MainActivity.class);
            startActivity(intent);
            return;
        }

        // Split test questions
        String[] questionIdsSplit = questionsString.split(",");

        // All questions in the test (every type of question)
        for (String question : questionIdsSplit) {
            allQuestionIds.add(Integer.parseInt(question));
        }

        testVersion = cTest.getInt(cTest.getColumnIndexOrThrow(
                DbContract.Tests.COLUMN_VERSION_CODE));

        cTest.close();


        //// [Questions] ////

        // Selector for question type
        String typeSelector = "";

        if (usesQuestions || usesRoadSigns || usesIntersections) {
            typeSelector += "AND (";
            boolean previousWasSet = false;

            if (usesQuestions) {
                typeSelector += "type=0";
                previousWasSet = true;
            }

            if (usesRoadSigns) {
                if (previousWasSet) {
                    typeSelector += " OR ";
                }
                typeSelector += "type=1";
                previousWasSet = true;
            }

            if (usesIntersections) {
                if (previousWasSet) {
                    typeSelector += " OR ";
                }
                typeSelector += "type=2";
            }

            typeSelector += ")";
        }

        // Get the Filtered Questions for this test version
        String query = "SELECT * FROM " + DbContract.Questions.TABLE_NAME +
                " WHERE " + DbContract.Questions.COLUMN_QUESTION_ID + " IN (" + questionsString + ") AND " + DbContract.Questions.COLUMN_VERSION + " <= ? " + typeSelector;

        Cursor cFilteredQuestions = db.rawQuery(query, new String[]{Integer.toString(testVersion)});

        // Questions after filtering by type
        questionIds = new ArrayList<>();
        for (cFilteredQuestions.moveToFirst(); !cFilteredQuestions.isAfterLast(); cFilteredQuestions.moveToNext()) {
            questionIds.add(cFilteredQuestions.getInt(cFilteredQuestions.
                    getColumnIndexOrThrow(DbContract.Questions.COLUMN_QUESTION_ID)));
        }

        questionsList = new ArrayList<>();
        for (cFilteredQuestions.moveToFirst(); !cFilteredQuestions.isAfterLast(); cFilteredQuestions.moveToNext()) {
            questionsList.add(cFilteredQuestions.getString(cFilteredQuestions.
                    getColumnIndexOrThrow(DbContract.Questions.COLUMN_QUESTION)));
        }

        imagesList = new ArrayList<>();
        for (cFilteredQuestions.moveToFirst(); !cFilteredQuestions.isAfterLast(); cFilteredQuestions.moveToNext()) {
            imagesList.add(cFilteredQuestions.getString(cFilteredQuestions.
                    getColumnIndexOrThrow(DbContract.Questions.COLUMN_IMAGE)));
        }

        correctAnswersList = new ArrayList<>();
        for (cFilteredQuestions.moveToFirst(); !cFilteredQuestions.isAfterLast(); cFilteredQuestions.moveToNext()) {
            correctAnswersList.add(cFilteredQuestions.getInt(cFilteredQuestions.
                    getColumnIndexOrThrow(DbContract.Questions.COLUMN_CORRECT_ANSWER)));
        }

        answer1List = new ArrayList<>();
        for (cFilteredQuestions.moveToFirst(); !cFilteredQuestions.isAfterLast(); cFilteredQuestions.moveToNext()) {
            answer1List.add(cFilteredQuestions.getString(cFilteredQuestions.
                    getColumnIndexOrThrow(DbContract.Questions.COLUMN_ANSWER_1)));
        }

        answer2List = new ArrayList<>();
        for (cFilteredQuestions.moveToFirst(); !cFilteredQuestions.isAfterLast(); cFilteredQuestions.moveToNext()) {
            answer2List.add(cFilteredQuestions.getString(cFilteredQuestions.
                    getColumnIndexOrThrow(DbContract.Questions.COLUMN_ANSWER_2)));
        }

        answer3List = new ArrayList<>();
        for (cFilteredQuestions.moveToFirst(); !cFilteredQuestions.isAfterLast(); cFilteredQuestions.moveToNext()) {
            answer3List.add(cFilteredQuestions.getString(cFilteredQuestions.
                    getColumnIndexOrThrow(DbContract.Questions.COLUMN_ANSWER_3)));
        }

        pointsList = new ArrayList<>();
        for (cFilteredQuestions.moveToFirst(); !cFilteredQuestions.isAfterLast(); cFilteredQuestions.moveToNext()) {
            int points = cFilteredQuestions.getInt(cFilteredQuestions.
                    getColumnIndexOrThrow(DbContract.Questions.COLUMN_POINTS));
            pointsList.add(points);
            maxPoints += points;
        }

        cFilteredQuestions.close();

        // Get count of questions and amount of max points
        questionsCount = questionIds.size();

        // Initialize the chosenAnswersList to the right size
        for (int i = 0; i < questionsCount; i++) {
            chosenAnswersList.add(
                    (markCorrectAnswers) ? correctAnswersList.get(i) : 0);
        }

        db.close();

        // If previewing correct answers, display R.string.correct_answers_caps in elapsed_time
        if (!markCorrectAnswers) {
            restartTimer();
        } else {
            elapsed_time.setText(getResources().getString(R.string.correct_answers_caps));
            // colorSecondaryText dark
            elapsed_time.setTextColor(Color.parseColor("#b2ffffff"));
            elapsed_time.setTextSize(14);
        }

        changeQuestion(1);
    }

    public void changeQuestion(int index) {
        currentQuestionIdx = index;
        int questionId = currentQuestionIdx - 1;

        setQuestionText(questionsList.get(questionId));
        setImage(imagesList.get(questionId));
        setCorrectAnswer(correctAnswersList.get(questionId));
        setPointsValue(pointsList.get(questionId));
        setAnswers(answer1List.get(questionId), answer2List.get(questionId),
                answer3List.get(questionId));
        setQuestionCounter(currentQuestionIdx);
        highlightAnswer(chosenAnswersList.get(questionId));
    }

    public void highlightAnswer(int answer) {
        List<Button> buttons = new ArrayList<>();
        buttons.add(question_answer1);
        buttons.add(question_answer2);
        buttons.add(question_answer3);

        // Tint all buttons with default color
        for (int i = 0; i < buttons.size(); i++) {
            buttons.get(i).getBackground().setColorFilter(Color.LTGRAY, PorterDuff.Mode.MULTIPLY);
        }

        if (answer == 0) return;

        Drawable drawable = buttons.get(answer - 1).getBackground();

        // Color chosen button
        int correctAnswer = correctAnswersList.get(currentQuestionIdx - 1);
        if (colorCorrectAnswers) {
            if (answer == correctAnswer) {
                // Correct answer - Green
                drawable.setColorFilter(Color.parseColor("#4CAF50"), PorterDuff.Mode.MULTIPLY);
            } else {
                // Incorrect answer - Red
                drawable.setColorFilter(Color.parseColor("#F44336"), PorterDuff.Mode.MULTIPLY);

                if (finished) {
                    // Color the correct answer Green
                    Drawable drawable2 = buttons.get(correctAnswer - 1).getBackground();
                    drawable2.setColorFilter(Color.parseColor("#4CAF50"), PorterDuff.Mode.MULTIPLY);
                }
            }
        } else {
            // Correct answer is not revealed - Gray
            drawable.setColorFilter(Color.GRAY, PorterDuff.Mode.MULTIPLY);
        }

        // TODO: Test this
        // Force a redraw on pre-lollipop devices
        // Doesn't help/work?
        for (Button button : buttons) {
            button.invalidateDrawable(button.getBackground());
        }
    }

    public void clearHighlights() {

    }

    public void setQuestionText(String text) {
        mText = text;
        question_text.setText(mText);
    }

    public void setImage(String path) {
        if (path != null && !path.isEmpty()) {
            InputStream inputStream;
            String _sign = "s:";
            String _inter = "i:";
            String _placeholder = "placeholder:";

            // Road Signs
            if (path.startsWith(_sign)) {
                String signIdentifier = path.substring(_sign.length()).toLowerCase();
                String signImage = signIdentifier.toLowerCase();
                String category = "";

                // Get the category from the signIdentifier
                Pattern regex = Pattern.compile("^[^0-9]*");
                Matcher matcher = regex.matcher(signIdentifier);

                if (matcher.find()) {
                    category = matcher.group(0).toUpperCase();
                }

                // Exception for "sp.png" file
                if (category.equals("SP")) {
                    category = "S";
                }

                try {
                    inputStream = getContext().getAssets()
                            .open("images/road_signs/" + category + "/" + signImage + ".png");
                    mImage = Drawable.createFromStream(inputStream, null);
                } catch (IOException ex) {
                    // If file doesn't exist, use the placeholder image
                    mImage = ContextCompat.getDrawable(getContext(),
                            R.drawable.placeholder_small);
                    Timber.d(TAG, "Image \"" + category + "/" + signImage + ".png" +
                            "\" does not exist.");
                }
            }

            // Intersections
            else if (path.startsWith(_inter)) {
                // Use image from the assets folder
                String intersectionName = path.substring(_inter.length());
                try {
                    inputStream = getContext().getAssets()
                            .open("images/intersections/" + intersectionName + ".png");
                    mImage = Drawable.createFromStream(inputStream, null);
                } catch (IOException ex) {
                    // If file doesn't exist, use the placeholder image
                    mImage = ContextCompat.getDrawable(getContext(),
                            R.drawable.placeholder_large);
                    Timber.d(TAG, "Image \"" + intersectionName + ".png" + "\" does not exist.");
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
                    inputStream = getContext().getAssets().open("images/" + path);
                    mImage = Drawable.createFromStream(inputStream, null);
                } catch (IOException ex) {
                    ex.printStackTrace();
                    mImage = null;
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

    public void setQuestionCounter(int current) {
        question_counter.setText(current + "/" + questionsCount);
    }

    public void setPointsValue(int value) {
        Resources res = getResources();
        String sufix = value == 1 ? res.getString(R.string.point) : res.getString(R.string.points);
        points_value.setText(value + " " + sufix);
    }

    public void restartTimer() {
        elapsed_time.setBase(SystemClock.elapsedRealtime());
        elapsed_time.start();
    }

    public void pauseTimer() {
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
