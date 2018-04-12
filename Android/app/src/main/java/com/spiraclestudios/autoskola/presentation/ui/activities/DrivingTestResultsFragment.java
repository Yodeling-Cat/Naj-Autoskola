// Copyright (c) 2015-2017. Spiracle Software. All Rights Reserved.

package com.spiraclestudios.autoskola.presentation.ui.activities;

import android.content.ContentValues;
import android.content.res.Resources;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.content.res.AppCompatResources;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.CustomEvent;
import com.spiraclestudios.autoskola.DbContract;
import com.spiraclestudios.autoskola.DbHelper;
import com.spiraclestudios.autoskola.R;
import com.spiraclestudios.autoskola.Utils;
import com.spiraclestudios.autoskola.features.driving_test.DrivingTestInfo;
import com.spiraclestudios.autoskola.features.test_results.DrivingTestResult;
import com.spiraclestudios.autoskola.features.test_results.DrivingTestResultsFragmentInteractor;
import com.spiraclestudios.autoskola.framework.platform.StoreRating;
import java.util.Locale;
import timber.log.Timber;

public class DrivingTestResultsFragment extends Fragment {

  public final static String EXTRA_TEST_INFO = "com.spiraclestudios.autoskola.TEST_INFO";
  public final static String EXTRA_TEST_RESULT = "com.spiraclestudios.autoskola.TEST_RESULT";

  private final static String STATE_TEST_INFO = "testInfo";
  private final static String STATE_TEST_RESULT = "testResult";

  public DrivingTestResultsFragmentInteractor interactor;
  private DrivingTestInfo testInfo;
  private DrivingTestResult testResult;

  @BindView(R.id.results_title) TextView results_title;
  @BindView(R.id.results_summary) TextView results_summary;
  @BindView(R.id.results_points) TextView results_points;
  @BindView(R.id.results_correct) TextView results_correct;
  @BindView(R.id.results_incorrect) TextView results_incorrect;
  @BindView(R.id.results_unanswered) TextView results_unanswered;
  @BindView(R.id.results_unanswered_container) LinearLayout results_unanswered_container;
  @BindView(R.id.results_elapsed_time) TextView results_elapsed_time;
  @BindView(R.id.rate_our_app) Button rate_app;

  public static DrivingTestResultsFragment newInstance(DrivingTestInfo testInfo,
      DrivingTestResult testResult) {
    Bundle bundle = new Bundle();
    bundle.putSerializable(EXTRA_TEST_INFO, testInfo);
    bundle.putSerializable(EXTRA_TEST_RESULT, testResult);

    DrivingTestResultsFragment fragment = new DrivingTestResultsFragment();
    fragment.setArguments(bundle);
    return fragment;
  }

  @Override public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    // TODO: Delete result__activity and result__app_bar layouts
    View view = inflater.inflate(R.layout.test_results__fragment, container, false);
    ButterKnife.bind(this, view);
    initiateFragment(savedInstanceState);
    return view;
  }

  private void initiateFragment(Bundle savedInstanceState) {
    interactor = (DrivingTestResultsFragmentInteractor) getActivity();

    Bundle args = getArguments();
    testInfo = (DrivingTestInfo) args.getSerializable(EXTRA_TEST_INFO);
    testResult = (DrivingTestResult) args.getSerializable(EXTRA_TEST_RESULT);

    // Set compound drawables
    results_points.setCompoundDrawablesWithIntrinsicBounds(
        AppCompatResources.getDrawable(getContext(), R.drawable.ic_scored_points), null, null,
        null);
    results_elapsed_time.setCompoundDrawablesWithIntrinsicBounds(
        AppCompatResources.getDrawable(getContext(), R.drawable.ic_elapsed_time), null, null, null);
    results_correct.setCompoundDrawablesWithIntrinsicBounds(
        AppCompatResources.getDrawable(getContext(), R.drawable.ic_correct_questions), null, null,
        null);
    results_incorrect.setCompoundDrawablesWithIntrinsicBounds(
        AppCompatResources.getDrawable(getContext(), R.drawable.ic_incorrect_questions), null, null,
        null);
    results_unanswered.setCompoundDrawablesWithIntrinsicBounds(
        AppCompatResources.getDrawable(getContext(), R.drawable.ic_unanswered_questions), null,
        null, null);

    // Show the rate_app button if the user hasn't rated the app before.
    if (StoreRating.hasRatedApp(getContext())) {
      rate_app.setVisibility(View.GONE);
    }

    // TODO: Do I need to be saving the instance state or can I always restore from Arguments? Are they present even after killing the activity and restarting?
    if (savedInstanceState != null) {
      testInfo = (DrivingTestInfo) savedInstanceState.getSerializable(STATE_TEST_INFO);
      testResult = (DrivingTestResult) savedInstanceState.getSerializable(STATE_TEST_RESULT);
    }

    Resources res = getResources();

    // Did the user pass the test?
    boolean wasSuccessful = Utils.INSTANCE.getTestSuccessful(testResult.getPoints(), testResult.getElapsedTime());

    String pointsSuffix;
    if (testResult.getPoints() == 1) {
      pointsSuffix = res.getString(R.string.point);
    } else if (testResult.getPoints() > 1 && testResult.getPoints() < 5) {
      pointsSuffix = res.getString(R.string.points_2to4);
    } else {
      pointsSuffix = res.getString(R.string.points);
    }

    String titleText;
    String summaryText;
    boolean isPartial = !testInfo.getWithQuestions()
        || !testInfo.getWithRoadSigns()
        || !testInfo.getWithIntersections();

    if (isPartial) {
      String questions = testInfo.getWithQuestions() ? res.getString(R.string.text__questions) : "";
      String roadSigns =
          testInfo.getWithRoadSigns() ? res.getString(R.string.text__road_signs) : "";
      String intersections =
          testInfo.getWithIntersections() ? res.getString(R.string.text__intersections) : "";

      String titleString = questions;
      if (!roadSigns.isEmpty()) {
        if (!questions.isEmpty()) {
          titleString += " " + res.getString(R.string.conjunction__and) + " ";
        }
        titleString += roadSigns;
      }
      if (!intersections.isEmpty()) {
        if (!questions.isEmpty() || !roadSigns.isEmpty()) {
          titleString += " " + res.getString(R.string.conjunction__and) + " ";
        }
        titleString += intersections;
      }

      titleText = titleString;
      results_summary.setVisibility(View.GONE);
    } else {
      if (wasSuccessful) {
        titleText = res.getString(R.string.result__text__successful);
        summaryText = res.getString(R.string.result__text__summary_successful);
      } else {
        titleText = res.getString(R.string.result__text__unsuccessful);
        summaryText = res.getString(R.string.result__text__summary_unsuccessful);
      }
      results_summary.setText(summaryText);
      results_summary.setVisibility(View.VISIBLE);
    }

    results_title.setText(titleText);

    results_points.setText(
        String.format(Locale.ENGLISH, "%s: %d/%d", res.getString(R.string.result__text__points),
            testResult.getPoints(), testResult.getMaxPoints()));
    results_correct.setText(
        String.format(Locale.ENGLISH, "%s: %d", res.getString(R.string.result__text__correct),
            testResult.getAmountCorrect()));
    results_incorrect.setText(
        String.format(Locale.ENGLISH, "%s: %d", res.getString(R.string.result__text__incorrect),
            testResult.getAmountIncorrect() - testResult.getAmountUnanswered()));
    results_elapsed_time.setText(
        String.format(Locale.ENGLISH, "%s: %s", res.getString(R.string.result__text__time),
            DateUtils.formatElapsedTime(testResult.getElapsedTime() / 1000)));
    if (testResult.getAmountUnanswered() > 0) {
      results_unanswered.setText(
          String.format(Locale.ENGLISH, "%s: %d", res.getString(R.string.result__text__unanswered),
              testResult.getAmountUnanswered()));
    } else {
      results_unanswered.setVisibility(View.GONE);
      results_unanswered_container.setVisibility(View.GONE);
    }
  }

  @Override public void onSaveInstanceState(@NonNull Bundle outState) {
    super.onSaveInstanceState(outState);

    outState.putSerializable(STATE_TEST_INFO, testInfo);
    outState.putSerializable(STATE_TEST_RESULT, testResult);
  }

  // TODO
  /*private void setUpToolbar(Toolbar toolbar) {
    setSupportActionBar(toolbar);
    ActionBar actionBar = getSupportActionBar();

    if (actionBar != null) {
      // Returns "Skupina A,B" or "Skupina C,D,T"
      String groupString =
          (Utils.INSTANCE.getGroupFromTestIndex(testInfo.getTestId()) == Groups.AB) ? getString(R.string.text__group_ab)
              : getString(R.string.text__group_cdt);

      actionBar.setTitle(getString(R.string.screen_title__results, testInfo.getTestId()));
      actionBar.setSubtitle(groupString);
      actionBar.setDisplayHomeAsUpEnabled(true);
      actionBar.setHomeAsUpIndicator(R.drawable.ic_close);
    }
  }*/

  @OnClick(R.id.rate_our_app) public void rate_our_app_onClick() {
    boolean success = StoreRating.rateApp(getContext());
    if (success) {
      rate_app.setText(R.string.result__text__thanks_for_rating_the_app);
      rate_app.setEnabled(false);
    }
  }

  // TODO
  /*@Override public boolean onCreateOptionsMenu(Menu menu) {
  // TODO: Delete the menu resource
    getMenuInflater().inflate(R.menu.activity__results, menu);
    return true;
  }

  // TODO
  @Override public boolean onOptionsItemSelected(MenuItem item) {
    int id = item.getItemId();

    switch (id) {
      case android.R.id.home:
        onBackPressed();
        return true;
      case R.id.action__share:
        new Sharing(getContext()).shareTestResult(testInfo.getTestId(), testResult);
        return true;
    }

    return super.onOptionsItemSelected(item);
  }*/
}
