// Copyright (c) 2015-2017. Spiracle Software. All Rights Reserved.

package com.spiraclestudios.autoskola.presentation.ui.activities;

import android.content.ContentValues;
import android.content.Intent;
import android.content.res.Resources;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.content.res.AppCompatResources;
import android.support.v7.widget.Toolbar;
import android.text.format.DateUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.OnClick;
import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.CustomEvent;
import com.spiraclestudios.autoskola.BaseApplication;
import com.spiraclestudios.autoskola.BuildConfig;
import com.spiraclestudios.autoskola.DbContract;
import com.spiraclestudios.autoskola.DbHelper;
import com.spiraclestudios.autoskola.R;
import com.spiraclestudios.autoskola.Utils;
import com.spiraclestudios.autoskola.billing.remove_ads.RemoveAds;
import com.spiraclestudios.autoskola.billing.remove_ads.RemoveAdsView;
import com.spiraclestudios.autoskola.domain.Groups;
import com.spiraclestudios.autoskola.features.driving_test.DrivingTestInfo;
import com.spiraclestudios.autoskola.features.test_results.DrivingTestResult;
import com.spiraclestudios.autoskola.framework.platform.Sharing;
import com.spiraclestudios.autoskola.framework.platform.StoreRating;
import com.spiraclestudios.autoskola.framework.presentation.ui.BaseActivity;
import com.spiraclestudios.autoskola.framework.presentation.ui.StandardActivity;
import java.util.Locale;
import javax.annotation.Nonnull;
import org.solovyev.android.checkout.ActivityCheckout;
import org.solovyev.android.checkout.Checkout;
import org.solovyev.android.checkout.Inventory;
import org.solovyev.android.checkout.ProductTypes;
import timber.log.Timber;

public class ResultActivity extends StandardActivity {

  public final static String EXTRA_ALREADY_OPENED_RESULTS = "com.spiraclestudios.autoskola.ALREADY_CHECKED_RESULTS";
  public final static String EXTRA_TEST_INFO = "com.spiraclestudios.autoskola.TEST_INFO";
  public final static String EXTRA_TEST_RESULT = "com.spiraclestudios.autoskola.TEST_RESULT";

  private final static String STATE_ALREADY_OPENED_RESULTS = "alreadyOpenedResults";
  private final static String STATE_TEST_INFO = "testInfo";
  private final static String STATE_TEST_RESULT = "testResult";

  private ActivityCheckout checkout;
  private boolean alreadyOpenedResults;
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
  @BindView(R.id.remove_ads_view) RemoveAdsView remove_ads_view;

  @Override protected BaseActivity getThis() {
    return this;
  }

  @Override public void setActivityContentView() {
    setContentView(R.layout.result__activity);
  }

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    // Set compound drawables
    results_points.setCompoundDrawablesWithIntrinsicBounds(
        AppCompatResources.getDrawable(this, R.drawable.ic_scored_points), null, null, null);
    results_elapsed_time.setCompoundDrawablesWithIntrinsicBounds(
        AppCompatResources.getDrawable(this, R.drawable.ic_elapsed_time), null, null, null);
    results_correct.setCompoundDrawablesWithIntrinsicBounds(
        AppCompatResources.getDrawable(this, R.drawable.ic_correct_questions), null, null, null);
    results_incorrect.setCompoundDrawablesWithIntrinsicBounds(
        AppCompatResources.getDrawable(this, R.drawable.ic_incorrect_questions), null, null, null);
    results_unanswered.setCompoundDrawablesWithIntrinsicBounds(
        AppCompatResources.getDrawable(this, R.drawable.ic_unanswered_questions), null, null, null);

    // Show the rate_app button if the user hasn't rated the app before.
    if (StoreRating.hasRatedApp(this)) {
      rate_app.setVisibility(View.GONE);
    }

    if (savedInstanceState == null) {
      // Read the intent.
      Intent intent = getIntent();
      alreadyOpenedResults = intent.getBooleanExtra(EXTRA_ALREADY_OPENED_RESULTS, false);
      testInfo = (DrivingTestInfo) intent.getSerializableExtra(EXTRA_TEST_INFO);
      testResult = (DrivingTestResult) intent.getSerializableExtra(EXTRA_TEST_RESULT);
    } else {
      alreadyOpenedResults = savedInstanceState.getBoolean(STATE_ALREADY_OPENED_RESULTS, false);
      testInfo = (DrivingTestInfo) savedInstanceState.getSerializable(STATE_TEST_INFO);
      testResult = (DrivingTestResult) savedInstanceState.getSerializable(STATE_TEST_RESULT);
    }

    Resources res = getResources();

    setUpToolbar((Toolbar) findViewById(R.id.toolbar));

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
    boolean isPartial = !testInfo.getWithQuestions() || !testInfo.getWithRoadSigns() || !testInfo.getWithIntersections();

    if (isPartial) {
      String questions = testInfo.getWithQuestions() ? res.getString(R.string.text__questions) : "";
      String roadSigns = testInfo.getWithRoadSigns() ? res.getString(R.string.text__road_signs) : "";
      String intersections = testInfo.getWithIntersections() ? res.getString(R.string.text__intersections) : "";

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

    // Code to run only once.
    if (savedInstanceState == null && !alreadyOpenedResults) {
      saveToDatabase();

      Answers.getInstance()
          .logCustom(
              new CustomEvent("Test End").putCustomAttribute("Success", wasSuccessful ? 1 : 0)
                  .putCustomAttribute("Points", testResult.getPoints())
                  .putCustomAttribute("Time", DateUtils.formatElapsedTime(testResult.getElapsedTime() / 1000)));
    }

    // Init checkout for ads removal
    if (!BuildConfig.PREMIUM) {
      checkout = Checkout.forActivity(this, BaseApplication.get().getBilling());
      checkout.start();
      checkout.loadInventory(Inventory.Request.create()
              .loadPurchases(ProductTypes.IN_APP)
              .loadSkus(ProductTypes.IN_APP, RemoveAds.PRODUCT_REMOVE_ADS),
          new InventoryCallback());
    }
  }

  @Override protected void onDestroy() {
    if (!BuildConfig.PREMIUM) {
      checkout.stop();
    }
    super.onDestroy();
  }

  @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    if (!BuildConfig.PREMIUM) {
      checkout.onActivityResult(requestCode, resultCode, data);
    }
    super.onActivityResult(requestCode, resultCode, data);
  }

  private class InventoryCallback implements Inventory.Callback {

    @Override public void onLoaded(@Nonnull Inventory.Products products) {
      final Inventory.Product inAppProduct = products.get(ProductTypes.IN_APP);

      if (inAppProduct.supported) {
        remove_ads_view.initProductView(ResultActivity.this, checkout, inAppProduct);
      }
    }
  }

  @Override protected void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);

    outState.putBoolean(STATE_ALREADY_OPENED_RESULTS, alreadyOpenedResults);
    outState.putSerializable(STATE_TEST_INFO, testInfo);
    outState.putSerializable(STATE_TEST_RESULT, testResult);
  }

  private void setUpToolbar(Toolbar toolbar) {
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
  }

  @OnClick(R.id.rate_our_app) public void rate_our_app_onClick() {
    boolean success = StoreRating.rateApp(this);
    if (success) {
      rate_app.setText(R.string.result__text__thanks_for_rating_the_app);
      rate_app.setEnabled(false);
    }
  }

  /**
   * Store result in database
   */
  private void saveToDatabase() {
    Timber.d("saveToDatabase() called");

    DbHelper dbHelper = new DbHelper(this);
    SQLiteDatabase db = dbHelper.getWritableDatabase();

    ContentValues values = new ContentValues();
    values.put(DbContract.History.COLUMN_TEST_ID, testInfo.getTestId());
    values.put(DbContract.History.COLUMN_TEST_VERSION, testInfo.getTestVersion());
    values.put(DbContract.History.COLUMN_USES_QUESTIONS, testInfo.getWithQuestions());
    values.put(DbContract.History.COLUMN_USES_ROAD_SIGNS, testInfo.getWithRoadSigns());
    values.put(DbContract.History.COLUMN_USES_INTERSECTIONS, testInfo.getWithIntersections());
    values.put(DbContract.History.COLUMN_POINTS, testResult.getPoints());
    values.put(DbContract.History.COLUMN_MAX_POINTS, testResult.getMaxPoints());
    values.put(DbContract.History.COLUMN_ELAPSED_TIME, testResult.getElapsedTime());
    values.put(DbContract.History.COLUMN_ANSWERS,
        testResult.getChosenAnswersList().toString().replace("[", "").replace("]", "").replace(" ", ""));
    values.put(DbContract.History.COLUMN_DATE_TIME, testResult.getDateStarted());

    db.insert(DbContract.History.TABLE_NAME, null, values);

    dbHelper.close();
    db.close();
  }

  @Override public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.activity__results, menu);
    return true;
  }

  @Override public boolean onOptionsItemSelected(MenuItem item) {
    int id = item.getItemId();

    switch (id) {
      case android.R.id.home:
        onBackPressed();
        return true;
      case R.id.action__share:
        new Sharing(this).shareTestResult(testInfo.getTestId(), testResult);
        return true;
    }

    return super.onOptionsItemSelected(item);
  }
}
