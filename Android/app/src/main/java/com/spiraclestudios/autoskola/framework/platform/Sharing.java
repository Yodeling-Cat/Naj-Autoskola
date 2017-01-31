// Copyright (c) 2015-2017. Spiracle Software. All Rights Reserved.

package com.spiraclestudios.autoskola.framework.platform;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.text.format.DateUtils;
import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.ShareEvent;
import com.spiraclestudios.autoskola.R;
import com.spiraclestudios.autoskola.domain.TestResult;
import timber.log.Timber;

public class Sharing {

  private Activity activity;

  public Sharing(Activity activity) {
    this.activity = activity;
  }

  public void shareTestResult(TestResult testResult) {
    Resources res = activity.getResources();

    Timber.d("Sharing test result: %s", testResult.toString());

    String shareText =
        res.getString(R.string.share_test__text__message, testResult.testId, testResult.points,
            testResult.maxPoints, testResult.amountCorrect, testResult.amountIncorrect,
            DateUtils.formatElapsedTime(testResult.elapsedTime / 1000),
            res.getString(R.string.link__app__google_play__short));

    Intent sendIntent = new Intent(Intent.ACTION_SEND);
    sendIntent.setType("text/plain");
    sendIntent.putExtra(Intent.EXTRA_SUBJECT, res.getString(R.string.share_test__text__subject));
    sendIntent.putExtra(Intent.EXTRA_TEXT, shareText);
    activity.startActivity(sendIntent);

    Answers.getInstance().logShare(new ShareEvent().putMethod("Results"));
  }
}
