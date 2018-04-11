// Copyright (c) 2015-2017. Spiracle Software. All Rights Reserved.

package com.spiraclestudios.autoskola.framework.platform

import android.content.Context
import android.text.format.DateUtils
import com.crashlytics.android.answers.Answers
import com.crashlytics.android.answers.ShareEvent
import com.spiraclestudios.autoskola.R
import com.spiraclestudios.autoskola.features.test_results.DrivingTestResult
import org.jetbrains.anko.share
import timber.log.Timber

class Sharing(internal val context: Context) {

  fun shareTestResult(testId: Int, testResult: DrivingTestResult) {
    Timber.d("Sharing test result: %s", testResult.toString())

    shareTestResult(testId, testResult.points, testResult.maxPoints,
        testResult.amountCorrect, testResult.amountIncorrect,
        testResult.elapsedTime)
  }

  fun shareTestResult(testId: Int, points: Int, maxPoints: Int, amountCorrect: Int,
      amountIncorrect: Int, elapsedTime: Long) {
    Timber.d("Sharing test result")
    Answers.getInstance().logShare(ShareEvent().putMethod("Results"))

    val res = context.resources
    val textToShare = res.getString(R.string.share_test__text__message, testId, points,
        maxPoints, amountCorrect, amountIncorrect,
        DateUtils.formatElapsedTime(elapsedTime / 1000),
        res.getString(R.string.link__app__google_play__short))

    context.share(textToShare, res.getString(R.string.share_test__text__subject))
  }
}
