package com.spiraclestudios.autoskola.features.test_results

import android.content.ContentValues
import android.content.Context
import com.spiraclestudios.autoskola.DbContract
import com.spiraclestudios.autoskola.DbHelper
import com.spiraclestudios.autoskola.features.driving_test.DrivingTestInfo
import timber.log.Timber

class DrivingTestResultDatabase {

  companion object {

    @JvmStatic
    fun saveTestResult(context: Context, testInfo: DrivingTestInfo, testResult: DrivingTestResult) {
      Timber.d("Saving test result to database.")

      val dbHelper = DbHelper(context)
      val db = dbHelper.writableDatabase

      val values = ContentValues()
      values.apply {
        put(DbContract.History.COLUMN_TEST_ID, testInfo.testId)
        put(DbContract.History.COLUMN_TEST_VERSION, testInfo.testVersion)
        put(DbContract.History.COLUMN_USES_QUESTIONS, testInfo.withQuestions)
        put(DbContract.History.COLUMN_USES_ROAD_SIGNS, testInfo.withRoadSigns)
        put(DbContract.History.COLUMN_USES_INTERSECTIONS, testInfo.withIntersections)
        put(DbContract.History.COLUMN_POINTS, testResult.points)
        put(DbContract.History.COLUMN_MAX_POINTS, testResult.maxPoints)
        put(DbContract.History.COLUMN_ELAPSED_TIME, testResult.elapsedTime)
        val chosenAnswers = testResult.chosenAnswersList.toString()
            .replace("[", "")
            .replace("]", "")
            .replace(" ", "")
        put(DbContract.History.COLUMN_ANSWERS, chosenAnswers)
        put(DbContract.History.COLUMN_DATE_TIME, testResult.dateStarted)
      }

      db.insert(DbContract.History.TABLE_NAME, null, values)

      dbHelper.close()
      db.close()
    }
  }
}