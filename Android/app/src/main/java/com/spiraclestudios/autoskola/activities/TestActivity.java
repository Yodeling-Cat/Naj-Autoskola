/*
 * Copyright (c) 2015-2016. Spiracle Studios. All Rights Reserved.
 */

package com.spiraclestudios.autoskola.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.google.android.gms.ads.AdView;
import com.spiraclestudios.autoskola.DbContract;
import com.spiraclestudios.autoskola.DbHelper;
import com.spiraclestudios.autoskola.Helper;
import com.spiraclestudios.autoskola.R;
import com.spiraclestudios.autoskola.interfaces.IBaseActivity;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnLongClick;
import io.palaima.debugdrawer.DebugDrawer;
import io.palaima.debugdrawer.actions.ActionsModule;
import io.palaima.debugdrawer.actions.ButtonAction;
import io.palaima.debugdrawer.commons.BuildModule;
import io.palaima.debugdrawer.commons.DeviceModule;
import io.palaima.debugdrawer.commons.SettingsModule;
import io.palaima.debugdrawer.timber.TimberModule;
import timber.log.Timber;

public class TestActivity extends BaseActivity
    implements IBaseActivity
{
  public final static String EXTRA_TEST_TYPE = "com.spiraclestudios.autoskola.TEST_TYPE";
  public final static String EXTRA_TEST_GROUP = "com.spiraclestudios.autoskola.TEST_GROUP";
  public final static String EXTRA_TEST_ID = "com.spiraclestudios.autoskola.TEST_ID";
  public final static String EXTRA_USES_QUESTIONS = "com.spiraclestudios.autoskola.USE_QUESTIONS";
  public final static String EXTRA_USES_ROAD_SIGNS = "com.spiraclestudios.autoskola.USE_ROAD_SIGNS";
  public final static String EXTRA_USES_INTERSECTIONS = "com.spiraclestudios.autoskola.USE_INTERSECTIONS";
  public final static String EXTRA_POINTS = "com.spiraclestudios.autoskola.POINTS";
  public final static String EXTRA_MAX_POINTS = "com.spiraclestudios.autoskola.MAX_POINTS";
  public final static String EXTRA_ELAPSED_TIME = "com.spiraclestudios.autoskola.TIME";
  public final static String EXTRA_ELAPSED_TIME_TEXT = "com.spiraclestudios.autoskola.TIME_TEXT";
  public final static String EXTRA_ANSWERS = "com.spiraclestudios.autoskola.ANSWERS";
  public String mActivityName = "TestActivity";

  public enum TestTypes
  {
    NORMAL,
    CORRECT_ANSWERS,
    HISTORY
  }

  // [Test Info]
  public TestTypes mTestType;
  public int mTestId = 1;
  public int mTestVersion = 1;
  public ArrayList<Integer> mAllQuestionIds = new ArrayList<>();
  public int mCurrentQuestionIdx = 1;
  public boolean mUsesQuestions;
  public boolean mUsesRoadSigns;
  public boolean mUsesIntersections;
  public int mQuestionsCount;
  public int mMaxPoints;
  public int mAmountCorrect;

  // [Test Settings - Internal]
  public boolean mAllowClickingOnAnswers = true;
  public boolean mMarkCorrectAnswers = false;
  public boolean mColorCorrectAnswers = false;
  public String mText;
  public Drawable mImage;
  public int mPoints = 0;
  public int mCorrectAnswer = 0;
  public String mAnswer1;
  public String mAnswer2;
  public String mAnswer3;

  // [Cached data from database]
  /** Questions after filtering by type. */
  List<Integer> mQuestionIds;
  List<Integer> mQuestionTypes;
  List<String> mQuestionsList;
  List<String> mImagesList;
  List<Integer> mCorrectAnswersList;
  List<String> mAnswer1List;
  List<String> mAnswer2List;
  List<String> mAnswer3List;
  List<Integer> mPointsList;

  // [Current data used by the layout views]
  List<Integer> mChosenAnswersList = new ArrayList<>();

  // [Layout views]
  @Bind(R.id.ad_view)
  AdView ad_view;
  @Bind(R.id.question_text)
  TextView question_text;
  //@Bind(R.id.intersection_canvas)
  //IntersectionCanvas intersection_canvas;
  @Bind(R.id.question_image)
  ImageButton question_image;
  @Bind(R.id.expanded_image)
  ImageView expanded_image;
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
  @Bind(R.id.points_value)
  TextView points_value;
  @Bind(R.id.question_counter)
  TextView question_counter;
  @Bind(R.id.elapsed_time)
  Chronometer elapsed_time;

  // [Internal]
  /**
   * Did the user evaluate the test results?
   */
  private boolean mCompleted = false;
  private boolean mAllQuestionsAnswered = false;
  private long mElapsedTime;
  private int mAmountAnswered;

  // [Miscellaneous]
  private Animator mExpandAnimator;
  private int mShortAnimationDuration;

  public String getActivityName()
  {
    return mActivityName;
  }

  @Override
  protected void onCreate(Bundle savedInstanceState)
  {
    Helper.setTheme(this);
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_test);
    ButterKnife.bind(this);

    // Keep the screen on.
    if (PreferenceManager.getDefaultSharedPreferences(this)
        .getBoolean("keep_screen_on", true)) {
      getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    // Retrieve and cache the system's default "short" animation time.
    mShortAnimationDuration = getResources().getInteger(
        android.R.integer.config_shortAnimTime);

    // Read extras from the intent.
    Intent intent = getIntent();
    mTestType = (TestTypes) intent.getSerializableExtra(EXTRA_TEST_TYPE);
    if (mTestType == null) { mTestType = TestTypes.NORMAL; }
    int selectedIndexId = intent.getIntExtra(EXTRA_TEST_ID, 1);
    Helper.Groups selectedGroup = (Helper.Groups) intent.getSerializableExtra(EXTRA_TEST_GROUP);
    mUsesQuestions = intent.getBooleanExtra(EXTRA_USES_QUESTIONS, true);
    mUsesRoadSigns = intent.getBooleanExtra(EXTRA_USES_ROAD_SIGNS, true);
    mUsesIntersections = intent.getBooleanExtra(EXTRA_USES_INTERSECTIONS, true);
    mPoints = intent.getIntExtra(EXTRA_POINTS, 0);
    mMaxPoints = intent.getIntExtra(EXTRA_MAX_POINTS, 0);
    mElapsedTime = intent.getLongExtra(EXTRA_ELAPSED_TIME, 0);
    String elapsedTimeText = intent.getStringExtra(EXTRA_ELAPSED_TIME_TEXT);
    String passedAnswersString = intent.getStringExtra(EXTRA_ANSWERS);

    // Decide which test to open.
    String groupString;
    int testIndexToUse;
    Resources res = getResources();

    // [Index]
    // If random was chosen
    if (selectedGroup != null) {
      if (selectedGroup == Helper.Groups.AB) {
        // Random number in range of 1-35
        testIndexToUse = new Random().nextInt(36 - 1) + 1;
      } else {
        // Random number in range of 36-60
        testIndexToUse = new Random().nextInt(61 - 36) + 36;
      }
    } else {
      // Int between 1-60
      testIndexToUse = selectedIndexId;
    }

    // Create and add the TestActivityFragment to the layout
    /*TestActivityFragment testActivityFragment = TestActivityFragment
        .newInstance(testIndexToUse, usesQuestions, usesRoadSigns, usesIntersections
                        , mMarkCorrectAnswers);
        getSupportFragmentManager().beginTransaction().add(
                R.id.fragment_container, testActivityFragment).commit();*/

    // Returns "Skupina A,B" or "Skupina C,D,T"
    groupString = (Helper.getGroupFromTestIndex(
        testIndexToUse) == Helper.Groups.AB) ? res.getString(R.string.group_ab_long) : res.getString(R.string.group_cdt_long);

    // Setup Toolbar
    Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);

    ActionBar actionBar = getSupportActionBar();
    if (actionBar != null) {
      actionBar.setTitle("Test " + testIndexToUse);
      actionBar.setSubtitle(groupString);
      actionBar.setDisplayHomeAsUpEnabled(true);
    }

    // Setup TabLayout
    //TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
    //tabLayout.addTab(tabLayout.newTab().setText(R.string.title_test));
    //tabLayout.addTab(tabLayout.newTab().setText(R.string.title_vyhlaska));
    //tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        /*final ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
		final TestActivityPagerAdapter adapter = new TestActivityPagerAdapter
                (getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });*/

    switch (mTestType) {
      case NORMAL:
        restartTimer();
        // TODO: Remove after implementing intersections
        if (mUsesIntersections) {
          Toast.makeText(this, R.string.toast_intersections_not_yet_implemented,
              Toast.LENGTH_LONG)
              .show();
        }
        break;
      case CORRECT_ANSWERS:
        mCompleted = true;
        mMarkCorrectAnswers = true;
        mColorCorrectAnswers = true;
        mAllowClickingOnAnswers = false;
        elapsed_time.setText(getString(R.string.correct_answers));
        elapsed_time.setTextColor(Color.parseColor("#b2ffffff"));
        elapsed_time.setTextSize(13);
        break;
      case HISTORY:
        mCompleted = true;
        mMarkCorrectAnswers = true;
        mColorCorrectAnswers = true;
        mAllowClickingOnAnswers = false;
        elapsed_time.setText(mPoints + "/" + mMaxPoints + "\n" + elapsedTimeText);
        elapsed_time.setTextColor(Color.parseColor("#b2ffffff"));
        elapsed_time.setTextSize(13);
        if (!passedAnswersString.isEmpty()) {
          for (String answer : passedAnswersString.split(",")) {
            mChosenAnswersList.add(Integer.parseInt(answer));
          }
        }
        break;
    }

    setTest(testIndexToUse);

    // Load an ad.
    Helper.loadAd(ad_view);

    // Debug Drawer
    ButtonAction buttonAction = new ButtonAction("Successful test", new ButtonAction.Listener()
    {

      @Override
      public void onClick()
      {
        // Start ResultsActivity with max score.
        mChosenAnswersList = mCorrectAnswersList;
        mAmountAnswered = mQuestionsCount;
        evaluateResults();
      }
    });

    new DebugDrawer.Builder(this)
        .modules(
            new ActionsModule(buttonAction),
            new TimberModule(),
            new DeviceModule(this),
            new BuildModule(this),
            new SettingsModule(this)
        ).build();
  }

  @Override
  public void onPause()
  {
    ad_view.pause();
    pauseTimer();
    super.onPause();
  }

  @Override
  public void onResume()
  {
    ad_view.resume();
    if (!mCompleted && !mMarkCorrectAnswers)
      resumeTimer();
    super.onResume();
  }

  @Override
  public void onDestroy()
  {
    ad_view.destroy();
    super.onDestroy();
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu)
  {
    if (mTestType == TestTypes.NORMAL) {
      getMenuInflater().inflate(R.menu.activity_test, menu);
    }
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item)
  {
    int id = item.getItemId();
    if (id == R.id.action_evaluate) {
      evaluateResults();
      return true;
    }/* else if (id == R.id.action_vyhlaska) {
      Toast.makeText(this, R.string.toast_not_yet_implemented, Toast.LENGTH_SHORT).show();
      return true;
    }*/
    return super.onOptionsItemSelected(item);
  }

  /**
   * Expand the question_image on click.
   */
  @OnClick(R.id.question_image)
  public void question_image_onClick()
  {
    // If there's an animation in progress, cancel it
    // immediately and proceed with this one.
    if (mExpandAnimator != null) {
      mExpandAnimator.cancel();
    }

    // Load the high-resolution "zoomed-in" image.
    expanded_image.setImageDrawable(mImage);

    // Calculate the starting and ending bounds for the zoomed-in image.
    // This step involves lots of math. Yay, math.
    final Rect startBounds = new Rect();
    final Rect finalBounds = new Rect();
    final Point globalOffset = new Point();

    // The start bounds are the global visible rectangle of the thumbnail,
    // and the final bounds are the global visible rectangle of the container
    // view. Also set the container view's offset as the origin for the
    // bounds, since that's the origin for the positioning animation
    // properties (X, Y).
    question_image.getGlobalVisibleRect(startBounds);
    findViewById(R.id.content)
        .getGlobalVisibleRect(finalBounds, globalOffset);
    startBounds.offset(-globalOffset.x, -globalOffset.y);
    finalBounds.offset(-globalOffset.x, -globalOffset.y);

    // Adjust the start bounds to be the same aspect ratio as the final
    // bounds using the "center crop" technique. This prevents undesirable
    // stretching during the animation. Also calculate the start scaling
    // factor (the end scaling factor is always 1.0).
    float startScale;
    if ((float) finalBounds.width() / finalBounds.height()
        > (float) startBounds.width() / startBounds.height()) {
      // Extend start bounds horizontally
      startScale = (float) startBounds.height() / finalBounds.height();
      float startWidth = startScale * finalBounds.width();
      float deltaWidth = (startWidth - startBounds.width()) / 2;
      startBounds.left -= deltaWidth;
      startBounds.right += deltaWidth;
    } else {
      // Extend start bounds vertically
      startScale = (float) startBounds.width() / finalBounds.width();
      float startHeight = startScale * finalBounds.height();
      float deltaHeight = (startHeight - startBounds.height()) / 2;
      startBounds.top -= deltaHeight;
      startBounds.bottom += deltaHeight;
    }

    // Hide the thumbnail and show the zoomed-in view. When the animation
    // begins, it will position the zoomed-in view in the place of the
    // thumbnail.
    question_image.setAlpha(0f);
    expanded_image.setVisibility(View.VISIBLE);

    // Set the pivot point for SCALE_X and SCALE_Y transformations
    // to the top-left corner of the zoomed-in view (the default
    // is the center of the view).
    expanded_image.setPivotX(0f);
    expanded_image.setPivotY(0f);

    // Construct and run the parallel animation of the four translation and
    // scale properties (X, Y, SCALE_X, and SCALE_Y).
    AnimatorSet set = new AnimatorSet();
    set
        .play(ObjectAnimator.ofFloat(expanded_image, View.X,
            startBounds.left, finalBounds.left))
        .with(ObjectAnimator.ofFloat(expanded_image, View.Y,
            startBounds.top, finalBounds.top))
        .with(ObjectAnimator.ofFloat(expanded_image, View.SCALE_X,
            startScale, 1f)).with(ObjectAnimator.ofFloat(expanded_image,
        View.SCALE_Y, startScale, 1f));
    set.setDuration(mShortAnimationDuration);
    set.setInterpolator(new DecelerateInterpolator());
    set.addListener(new AnimatorListenerAdapter()
    {
      @Override
      public void onAnimationEnd(Animator animation)
      {
        mExpandAnimator = null;
      }

      @Override
      public void onAnimationCancel(Animator animation)
      {
        mExpandAnimator = null;
      }
    });
    set.start();
    mExpandAnimator = set;

    // Upon clicking the zoomed-in image, it should zoom back down
    // to the original bounds and show the thumbnail instead of
    // the expanded image.
    final float startScaleFinal = startScale;
    expanded_image.setOnClickListener(new View.OnClickListener()
    {
      @Override
      public void onClick(View view)
      {

        if (mExpandAnimator != null) {
          mExpandAnimator.cancel();
        }

        // Animate the four positioning/sizing properties in parallel,
        // back to their original values.
        AnimatorSet set = new AnimatorSet();
        set.play(ObjectAnimator
            .ofFloat(expanded_image, View.X, startBounds.left))
            .with(ObjectAnimator
                .ofFloat(expanded_image,
                    View.Y, startBounds.top))
            .with(ObjectAnimator
                .ofFloat(expanded_image,
                    View.SCALE_X, startScaleFinal))
            .with(ObjectAnimator
                .ofFloat(expanded_image,
                    View.SCALE_Y, startScaleFinal));
        set.setDuration(mShortAnimationDuration);
        set.setInterpolator(new DecelerateInterpolator());
        set.addListener(new AnimatorListenerAdapter()
        {
          @Override
          public void onAnimationEnd(Animator animation)
          {
            question_image.setAlpha(1f);
            expanded_image.setVisibility(View.GONE);
            mExpandAnimator = null;
          }

          @Override
          public void onAnimationCancel(Animator animation)
          {
            question_image.setAlpha(1f);
            expanded_image.setVisibility(View.GONE);
            mExpandAnimator = null;
          }
        });
        set.start();
        mExpandAnimator = set;
      }
    });
  }

  /**
   * Copy question text to clipboard.
   */
  @OnLongClick(R.id.question_text)
  public boolean question_text_onLongClick()
  {
    ClipboardManager clipboard = (ClipboardManager) this.getSystemService(Context.CLIPBOARD_SERVICE);

    String label = String.format(getString(R.string.clip_label_question), mCurrentQuestionIdx);
    ClipData clip = ClipData.newPlainText(label, question_text.getText().toString());

    clipboard.setPrimaryClip(clip);

    Toast.makeText(this, R.string.toast_question_was_copied, Toast.LENGTH_SHORT).show();
    return true;
  }

  /**
   * Copy answer text to clipboard.
   */
  @OnLongClick({ R.id.answer1, R.id.answer2, R.id.answer3 })
  public boolean answers_onLongClick(Button button)
  {
    ClipboardManager clipboard = (ClipboardManager) this.getSystemService(Context.CLIPBOARD_SERVICE);

    String label = getString(R.string.clip_label_answer);
    ClipData clip = ClipData.newPlainText(label, button.getText().toString());

    clipboard.setPrimaryClip(clip);

    Toast.makeText(this, R.string.toast_answer_was_copied, Toast.LENGTH_SHORT).show();
    return true;
  }

  /**
   * Moves to the next question and highlights it.
   */
  @OnClick(R.id.next_question)
  public void nextQuestion()
  {
    if (mCurrentQuestionIdx < mQuestionsList.size()) {
      changeQuestion(mCurrentQuestionIdx + 1);
    } else {
      highlightAnswer(mChosenAnswersList.get(mCurrentQuestionIdx - 1));
    }
  }

  /**
   * Moves to the previous question and highlights it.
   */
  @OnClick(R.id.previous_question)
  public void previousQuestion()
  {
    if (mCurrentQuestionIdx > 1)
      changeQuestion(mCurrentQuestionIdx - 1);
  }

  @OnClick(R.id.answer1)
  public void answer1_onClick()
  {
    answerChosen(1);
  }

  @OnClick(R.id.answer2)
  public void answer2_onClick()
  {
    answerChosen(2);
  }

  @OnClick(R.id.answer3)
  public void answer3_onClick()
  {
    answerChosen(3);
  }

  /**
   * Check or un-check an answer.
   *
   * @param answer The index of the answer button.
   */
  private void answerChosen(int answer)
  {
    if (!mAllowClickingOnAnswers) {
      return;
    }

    int currentAnswer = mChosenAnswersList.get(mCurrentQuestionIdx - 1);

    // Un-check the answer if the user clicks on the current answer.
    if (currentAnswer == answer) {
      mAmountAnswered--;
      mAllQuestionsAnswered = false;
      mChosenAnswersList.set(mCurrentQuestionIdx - 1, 0);
      highlightAnswer(0);
    }
    // If there is currently no answer or a different answer than the current one was chosen
    else {
      if (currentAnswer == 0) {
        mAmountAnswered++;
      }
      mChosenAnswersList.set(mCurrentQuestionIdx - 1, answer);
      nextQuestion();
    }

    // If the toast wasn't shown yet, then show it.
    if (!mAllQuestionsAnswered && mAmountAnswered == mQuestionsCount) {
      mAllQuestionsAnswered = true;
      Toast.makeText(this, R.string.toast_all_questions_answered, Toast.LENGTH_SHORT)
          .show();
    }
  }

  /**
   * Calculate points, handle test review and show the results activity.
   */
  public void evaluateResults()
  {
    if (!mCompleted) {
      // Calculate scored points.
      mAmountCorrect = 0;
      for (int i = 0; i < mQuestionsCount; i++) {
        if (mChosenAnswersList.get(i).equals(mCorrectAnswersList.get(i))) {
          addPoints(mPointsList.get(i));
          mAmountCorrect++;
        }
      }

      // Mark the correct answers for if the user comes back to the test
      // after viewing the results.
      mCompleted = true;
      mMarkCorrectAnswers = true;
      mColorCorrectAnswers = true;
      mAllowClickingOnAnswers = false;
      pauseTimer();
      highlightAnswer(mChosenAnswersList.get(mCurrentQuestionIdx - 1));
    }

    // NOTE: When making changes to this code, also update the DebugDrawer version in onCreate().
    Intent intent = new Intent(this, ResultsActivity.class);
    intent.putExtra(ResultsActivity.EXTRA_TEST_ID, mTestId);
    intent.putExtra(ResultsActivity.EXTRA_TEST_VERSION, mTestVersion);
    intent.putExtra(ResultsActivity.EXTRA_USES_QUESTIONS, mUsesQuestions);
    intent.putExtra(ResultsActivity.EXTRA_USES_ROAD_SIGNS, mUsesRoadSigns);
    intent.putExtra(ResultsActivity.EXTRA_USES_INTERSECTIONS, mUsesIntersections);
    intent.putExtra(ResultsActivity.EXTRA_POINTS, mPoints);
    intent.putExtra(ResultsActivity.EXTRA_MAX_POINTS, mMaxPoints);
    intent.putExtra(ResultsActivity.EXTRA_ELAPSED_TIME, getElapsedTime());
    intent.putExtra(ResultsActivity.EXTRA_ELAPSED_TIME_TEXT, elapsed_time.getText().toString());
    intent.putIntegerArrayListExtra(ResultsActivity.EXTRA_ANSWERS,
        (ArrayList<Integer>) mChosenAnswersList);
    intent.putExtra(ResultsActivity.EXTRA_CORRECT, mAmountCorrect);
    intent.putExtra(ResultsActivity.EXTRA_INCORRECT, mQuestionsCount - mAmountCorrect);
    intent.putExtra(ResultsActivity.EXTRA_ANSWERED, mAmountAnswered);

    startActivity(intent);
  }

  /**
   * Retrieves data from db, sets all the text and onClickListeners, restarts everything.
   */
  public void setTest(int id)
  {
    mTestId = id;

    Crashlytics.getInstance().core.setInt("current_test", mTestId);

    // Set up the Database
    DbHelper dbHelper = new DbHelper(this);
    SQLiteDatabase db = dbHelper.getReadableDatabase();

    //// [Tests] ////

    // Get latest version of this test.
    Cursor cTest = db.rawQuery(
        "SELECT " + DbContract.Tests.COLUMN_QUESTIONS + ", " +
            DbContract.Tests.COLUMN_VERSION_CODE + " FROM " +
            DbContract.Tests.TABLE_NAME + " WHERE " +
            DbContract.Tests.COLUMN_TEST_ID + " = ?", new String[]
            { Integer.toString(mTestId) });

    cTest.moveToFirst();

    // The whole 'questions' string from the Tests table.
    String questionsString = cTest.getString(cTest.getColumnIndexOrThrow(
        DbContract.Tests.COLUMN_QUESTIONS));

    // Split test questions.
    String[] questionIdsSplit = questionsString.split(",");

    // All questions in the test (every type of question).
    for (String question : questionIdsSplit) {
      mAllQuestionIds.add(Integer.parseInt(question));
    }

    mTestVersion = cTest.getInt(cTest.getColumnIndexOrThrow(
        DbContract.Tests.COLUMN_VERSION_CODE));

    cTest.close();


    //// [Questions] ////

    // Selector for the question type.
    String typeSelector = "AND (";
    List<String> concatenation = new ArrayList<>();

    if (mUsesQuestions) {
      concatenation.add(DbContract.Questions.COLUMN_TYPE + "=0");
    }
    if (mUsesRoadSigns) {
      concatenation.add(DbContract.Questions.COLUMN_TYPE + "=1");
    }
    if (mUsesIntersections) {
      concatenation.add(DbContract.Questions.COLUMN_TYPE + "=2");
    }

    for (int i = 0; i < concatenation.size(); i++) {
      String s = concatenation.get(i);

      typeSelector += s;

      if (i < concatenation.size() - 1) {
        typeSelector += " OR ";
      }
    }
    typeSelector += ")";

    // Get the Filtered Questions for this test version.
    String query = "SELECT * FROM " + DbContract.Questions.TABLE_NAME +
        " WHERE " + DbContract.Questions.COLUMN_QUESTION_ID + " IN (" + questionsString + ") AND " + DbContract.Questions.COLUMN_VERSION + " <= ? " + typeSelector;

    Cursor cFilteredQuestions = db.rawQuery(query, new String[] { Integer.toString(mTestVersion) });

    mQuestionIds = new ArrayList<>();
    mQuestionTypes = new ArrayList<>();
    mQuestionsList = new ArrayList<>();
    mImagesList = new ArrayList<>();
    mCorrectAnswersList = new ArrayList<>();
    mAnswer1List = new ArrayList<>();
    mAnswer2List = new ArrayList<>();
    mAnswer3List = new ArrayList<>();
    mPointsList = new ArrayList<>();

    for (cFilteredQuestions.moveToFirst(); !cFilteredQuestions.isAfterLast(); cFilteredQuestions.moveToNext()) {
      mQuestionIds.add(cFilteredQuestions.getInt(cFilteredQuestions.
          getColumnIndexOrThrow(DbContract.Questions.COLUMN_QUESTION_ID)));

      mQuestionTypes.add(cFilteredQuestions.getInt(cFilteredQuestions.
          getColumnIndexOrThrow(DbContract.Questions.COLUMN_TYPE)));

      mQuestionsList.add(cFilteredQuestions.getString(cFilteredQuestions.
          getColumnIndexOrThrow(DbContract.Questions.COLUMN_QUESTION)));

      mImagesList.add(cFilteredQuestions.getString(cFilteredQuestions.
          getColumnIndexOrThrow(DbContract.Questions.COLUMN_IMAGE)));

      mCorrectAnswersList.add(cFilteredQuestions.getInt(cFilteredQuestions.
          getColumnIndexOrThrow(DbContract.Questions.COLUMN_CORRECT_ANSWER)));

      mAnswer1List.add(cFilteredQuestions.getString(cFilteredQuestions.
          getColumnIndexOrThrow(DbContract.Questions.COLUMN_ANSWER_1)));

      mAnswer2List.add(cFilteredQuestions.getString(cFilteredQuestions.
          getColumnIndexOrThrow(DbContract.Questions.COLUMN_ANSWER_2)));

      mAnswer3List.add(cFilteredQuestions.getString(cFilteredQuestions.
          getColumnIndexOrThrow(DbContract.Questions.COLUMN_ANSWER_3)));

      int points = cFilteredQuestions.getInt(cFilteredQuestions.
          getColumnIndexOrThrow(DbContract.Questions.COLUMN_POINTS));
      mPointsList.add(points);
      mMaxPoints += points;
    }

    cFilteredQuestions.close();
    db.close();
    dbHelper.close();

    // Get count of questions and amount of max points.
    mQuestionsCount = mQuestionIds.size();

    // Initialize the mChosenAnswersList to the right size.
    for (int i = 0; i < mQuestionsCount; i++) {
      mChosenAnswersList.add(
          (mMarkCorrectAnswers) ? mCorrectAnswersList.get(i) : 0);
    }

    changeQuestion(1);
  }

  public void changeQuestion(int index)
  {
    mCurrentQuestionIdx = index;
    int questionId = mCurrentQuestionIdx - 1;

    setQuestionText(mQuestionsList.get(questionId));
    setImage(mImagesList.get(questionId));
    setCorrectAnswer(mCorrectAnswersList.get(questionId));
    setPointsValue(mPointsList.get(questionId));
    setAnswers(mAnswer1List.get(questionId), mAnswer2List.get(questionId),
        mAnswer3List.get(questionId));
    setQuestionCounter(mCurrentQuestionIdx);
    highlightAnswer(mChosenAnswersList.get(questionId));

    // Show or hide the image view based on question type.
    if (mQuestionTypes.get(questionId) == 0) {
      question_image.setVisibility(View.GONE);
    } else {
      question_image.setVisibility(View.VISIBLE);
    }

    // [CANVAS-CODE]
    // Show or hide the canvas based on question type.
    /*if (mQuestionTypes.get(questionId) == 2) {
			intersection_canvas.clearCanvas();
            intersection_canvas.setVisibility(View.VISIBLE);
            question_image.setVisibility(View.GONE);
        } else {
            intersection_canvas.setVisibility(View.GONE);
            question_image.setVisibility(View.VISIBLE);
        }*/
  }

  /**
   * Colors the chosen button.
   *
   * @param answer The index of the button that was pressed, from 1 to 3.
   */
  public void highlightAnswer(int answer)
  {
    List<Button> buttons = new ArrayList<>();
    buttons.add(question_answer1);
    buttons.add(question_answer2);
    buttons.add(question_answer3);

    Resources.Theme theme = getTheme();
    TypedValue colorNormal = new TypedValue();
    TypedValue colorSelected = new TypedValue();
    TypedValue colorCorrect = new TypedValue();
    TypedValue colorIncorrect = new TypedValue();

    theme.resolveAttribute(R.attr.colorAnswerNormal, colorNormal, true);
    theme.resolveAttribute(R.attr.colorAnswerSelected, colorSelected, true);
    theme.resolveAttribute(R.attr.colorAnswerCorrect, colorCorrect, true);
    theme.resolveAttribute(R.attr.colorAnswerIncorrect, colorIncorrect, true);

    // Tint all buttons with normal color.
    for (Button button : buttons) {
      button.getBackground().setColorFilter(colorNormal.data, PorterDuff.Mode.MULTIPLY);
      button.setTextColor(Color.parseColor("#212121"));
    }

    if (answer == 0)
      return;

    Button selectedButton = buttons.get(answer - 1);

    // Color the chosen button.
    if (mColorCorrectAnswers) {
      int correctAnswer = mCorrectAnswersList.get(mCurrentQuestionIdx - 1);
      if (answer == correctAnswer || mCompleted) {
        // Correct answer - Green
        Button correctButton = buttons.get(correctAnswer - 1);
        correctButton.getBackground().setColorFilter(colorCorrect.data, PorterDuff.Mode.MULTIPLY);
        correctButton.setTextColor(Color.parseColor("#b2ffffff"));
      }
      if (answer != correctAnswer) {
        // Incorrect answer - Red
        selectedButton.getBackground().setColorFilter(colorIncorrect.data, PorterDuff.Mode.MULTIPLY);
        selectedButton.setTextColor(Color.parseColor("#b2ffffff"));
      }
    } else {
      // Correct answer is not revealed.
      // Just color the selected button - Gray.
      selectedButton.getBackground().setColorFilter(colorSelected.data, PorterDuff.Mode.MULTIPLY);
      selectedButton.setTextColor(Color.parseColor("#212121"));
    }

    // TODO: Check if this code is needed.
    // Force a redraw on pre-lollipop devices.
    for (Button btn : buttons) {
      selectedButton.invalidateDrawable(btn.getBackground());
    }
  }

  public void setQuestionText(String text)
  {
    mText = text;
    question_text.setText(mText);
  }

  public void setImage(String path)
  {
    if (path != null && !path.isEmpty()) {
      InputStream inputStream;
      int type = mQuestionTypes.get(mCurrentQuestionIdx - 1);

      // Road Signs
      if (type == 1) {
        String signImage = path.toLowerCase();
        String category = "";

        // Get the category from the signIdentifier.
        Pattern regex = Pattern.compile("^[^0-9]*");
        Matcher matcher = regex.matcher(signImage);

        if (matcher.find()) {
          category = matcher.group(0).toUpperCase();
        }

        // Exception for "sp.png" file.
        if (category.equals("SP")) {
          category = "S";
        }

        try {
          inputStream = this.getAssets()
              .open("images/road_signs/" + category + "/" + signImage + ".png");
          mImage = Drawable.createFromStream(inputStream, null);
        } catch (IOException ex) {
          // If file doesn't exist, use the placeholder image.
          mImage = ContextCompat.getDrawable(this,
              R.drawable.placeholder_small);
          Timber.d("Image \"images/road_signs/%s/%s.png\" does not exist.", category, signImage);
        }
      }

      // Intersections
      else if (type == 2) {
        // Use image from the assets folder.
        try {
          inputStream = this.getAssets()
              .open("images/intersections/" + path + ".png");
          mImage = Drawable.createFromStream(inputStream, null);
        } catch (IOException ex) {
          // If file doesn't exist, use the placeholder image.
          mImage = ContextCompat.getDrawable(this,
              R.drawable.placeholder_large);
          Timber.d("Image \"images/intersections/%s.png\" does not exist.", path);
        }
      }

      question_image.setImageDrawable(mImage);
      question_image.setVisibility(View.VISIBLE);
    } else {
      mImage = null;
      question_image.setVisibility(View.GONE);
    }
  }

  public void setPoints(int points)
  {
    mPoints = points;
  }

  public void addPoints(int amount)
  {
    setPoints(mPoints + amount);
  }

  public void setCorrectAnswer(int index)
  {
    mCorrectAnswer = index;
  }

  public void setAnswers(String answer1, String answer2, String answer3)
  {
    // Strip the colors from the strings.
    String regex = "red:|green:|blue:";
    answer1 = answer1.replaceFirst(regex, "");
    answer2 = answer2.replaceFirst(regex, "");
    answer3 = answer3.replaceFirst(regex, "");

    mAnswer1 = answer1;
    mAnswer2 = answer2;
    mAnswer3 = answer3;

    // TODO: Try to implement, currently not working, try the tinting code used with buttons.
    // Show a colorful circle in the button, representing the color of the car in the answer.
    /*if (mAnswer1.startsWith("red:")) {
			Drawable drawable = (Drawable) ContextCompat.getDrawable(this, R.drawable.circle);
            //drawable.getPaint().setColor(Color.parseColor("#FF0000FF"));
            question_answer1.setCompoundDrawables(drawable, null, null, null);
        } else if (mAnswer1.startsWith("green:")) {
            Drawable drawable = (Drawable) ContextCompat.getDrawable(this, R.drawable.circle);
            //drawable.getPaint().setColor(Color.parseColor("#FF0000FF"));
            question_answer1.setCompoundDrawables(drawable, null, null, null);
        } else if (mAnswer1.startsWith("blue:")) {
            Drawable drawable = (Drawable) ContextCompat.getDrawable(this, R.drawable.circle);
            //drawable.getPaint().setColor(Color.parseColor("#FF00FF00"));
            question_answer1.setCompoundDrawables(drawable, null, null, null);
        } else {
            question_answer1.setCompoundDrawables(null, null, null, null);
        }*/

    question_answer1.setText(mAnswer1);
    question_answer2.setText(mAnswer2);
    question_answer3.setText(mAnswer3);
  }

  public void setQuestionCounter(int current)
  {
    question_counter.setText(String.format(Locale.ENGLISH, "%d/%d", current, mQuestionsCount));
  }

  public void setPointsValue(int points)
  {
    Resources res = getResources();
    String pointsSuffix;
    if (points == 1) {
      pointsSuffix = res.getString(R.string.point);
    } else if (points > 1 && points < 5) {
      pointsSuffix = res.getString(R.string.points_2to4);
    } else {
      pointsSuffix = res.getString(R.string.points);
    }
    points_value.setText(String.format(Locale.ENGLISH, "%d %s", points, pointsSuffix));
  }

  public void restartTimer()
  {
    elapsed_time.setBase(SystemClock.elapsedRealtime());
    elapsed_time.start();
  }

  public void pauseTimer()
  {
    mElapsedTime = getElapsedTime();
    elapsed_time.stop();
  }

  public void resumeTimer()
  {
    elapsed_time.setBase(SystemClock.elapsedRealtime() - mElapsedTime);
    elapsed_time.start();
  }

  public long getElapsedTime()
  {
    return SystemClock.elapsedRealtime() - elapsed_time.getBase();
  }
}
