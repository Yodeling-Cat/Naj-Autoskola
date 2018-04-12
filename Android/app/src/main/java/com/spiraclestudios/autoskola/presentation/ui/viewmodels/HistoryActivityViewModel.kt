// Copyright (c) 2015-2017. Spiracle Software. All Rights Reserved.

package com.spiraclestudios.autoskola.presentation.ui.viewmodels

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import com.spiraclestudios.autoskola.DbContract.*
import com.spiraclestudios.autoskola.DbHelper
import com.spiraclestudios.autoskola.HistoryEntry
import com.spiraclestudios.autoskola.Utils
import androidx.core.database.getInt
import androidx.core.database.getLong
import androidx.core.database.getString
import java.util.*

class HistoryActivityViewModel(app: Application) : AndroidViewModel(app) {

    private val dbHelper: DbHelper = DbHelper(getApplication())

    private var historyEntries: ArrayList<HistoryEntry> = ArrayList()
    private val liveDataHistoryEntries: MutableLiveData<ArrayList<HistoryEntry>> = MutableLiveData()

    fun getHistory(): LiveData<ArrayList<HistoryEntry>> = liveDataHistoryEntries

    fun deleteWholeHistory() {
        dbHelper.writableDatabase.use {
            it.delete(History.TABLE_NAME, null, null)
        }
        historyEntries.clear()
    }

    fun deleteHistoryOfTest(testIndex: Int) {
        dbHelper.writableDatabase.use {
            it.delete(History.TABLE_NAME,
                    "${History.COLUMN_TEST_ID}=?", arrayOf(Integer.toString(testIndex)))
        }
        historyEntries.clear()
    }

    fun loadHistoryOfTest(testIndex: Int) {
        dbHelper.readableDatabase.use {
            val (query, selectionArgs) = getQueryForHistory(testIndex)
            val history = it.rawQuery(query, selectionArgs)
            processHistory(history, it)
        }

        liveDataHistoryEntries.postValue(historyEntries)
    }

    private fun processHistory(cursor: Cursor, db: SQLiteDatabase) {
        fun getQuestionTypeSelector(usesQuestions: Boolean, usesRoadSigns: Boolean, usesIntersections: Boolean): String {
            val concat = ArrayList<String>()
            if (usesQuestions) concat.add("${Questions.COLUMN_TYPE}=0")
            if (usesRoadSigns) concat.add("${Questions.COLUMN_TYPE}=1")
            if (usesIntersections) concat.add("${Questions.COLUMN_TYPE}=2")

            var typeSelector = "AND ("
            for (i in concat.indices) {
                typeSelector += concat[i]

                if (i < concat.size - 1) {
                    typeSelector += " OR "
                }
            }
            typeSelector += ")"
            return typeSelector
        }

        fun getQuestionsString(testId: Int): String {
            // Get latest version of this test.
            val cTest = db.rawQuery("SELECT ${Tests.COLUMN_QUESTIONS}, " +
                    "${Tests.COLUMN_VERSION_CODE} " +
                    "FROM ${Tests.TABLE_NAME} " +
                    "WHERE ${Tests.COLUMN_TEST_ID} = ?", arrayOf(Integer.toString(testId)))
            // The whole 'questions' string from the Tests table.
            cTest.moveToFirst()
            val questionsString = cTest.getString(Tests.COLUMN_QUESTIONS)
            cTest.close()
            return questionsString
        }

        historyEntries.clear()
        cursor.moveToFirst()
        while (!cursor.isAfterLast) {
            val dbIndex = cursor.getInt(History._ID)
            val testId = cursor.getInt(History.COLUMN_TEST_ID)
            val testVersion = cursor.getInt(History.COLUMN_TEST_VERSION)
            val usesQuestions = cursor.getInt(History.COLUMN_USES_QUESTIONS) != 0
            val usesRoadSigns = cursor.getInt(History.COLUMN_USES_ROAD_SIGNS) != 0
            val usesIntersections = cursor.getInt(History.COLUMN_USES_INTERSECTIONS) != 0
            val points = cursor.getInt(History.COLUMN_POINTS)
            val maxPoints = cursor.getInt(History.COLUMN_MAX_POINTS)
            val elapsedTime = cursor.getLong(History.COLUMN_ELAPSED_TIME)
            val dateTime = cursor.getLong(History.COLUMN_DATE_TIME)
            val answersString = cursor.getString(History.COLUMN_ANSWERS)


            // Get the Filtered Questions for this test version.
            val filteredQuestionsQuery = ("SELECT * FROM ${Questions.TABLE_NAME} " +
                    "WHERE ${Questions.COLUMN_QUESTION_ID} IN (${getQuestionsString(testId)}) " +
                    "AND ${Questions.COLUMN_VERSION} <= ? ${getQuestionTypeSelector(usesQuestions, usesRoadSigns, usesIntersections)}")

            val cFilteredQuestions = db.rawQuery(filteredQuestionsQuery, arrayOf(Integer.toString(testVersion)))

            val questionIds = ArrayList<Int>()
            val correctAnswersList = ArrayList<Int>()
            cFilteredQuestions.moveToFirst()
            while (!cFilteredQuestions.isAfterLast) {
                questionIds.add(cFilteredQuestions.getInt(Questions.COLUMN_QUESTION_ID))

                correctAnswersList.add(cFilteredQuestions.getInt(Questions.COLUMN_CORRECT_ANSWER))
                cFilteredQuestions.moveToNext()
            }
            cFilteredQuestions.close()


            val chosenAnswersList = ArrayList<Int>()
            if (!answersString.isEmpty()) {
                for (answer in answersString.split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()) {
                    val chosenAnswer = Integer.parseInt(answer)
                    chosenAnswersList.add(chosenAnswer)
                }
            }

            // Get count of questions and amount of max points.
            val questionsCount = questionIds.size
            var amountCorrect = 0
            var amountUnanswered = 0

            for (i in 0 until questionsCount) {
                if (chosenAnswersList[i] == correctAnswersList[i]) {
                    amountCorrect++
                }
                if (chosenAnswersList[i] == 0) {
                    amountUnanswered++
                }
            }

            // TODO: Could I create HistoryEntry at the top and fill it out immediately from the cursor?
            val wasSuccessful = Utils.getTestSuccessful(points, elapsedTime)
            val amountIncorrect = questionsCount - amountCorrect
            historyEntries.add(HistoryEntry(dbIndex, testId, Utils.getGroupFromTestIndex(testId), wasSuccessful,
                    usesQuestions, usesRoadSigns, usesIntersections, points, maxPoints, amountCorrect,
                    amountIncorrect, amountUnanswered, elapsedTime, answersString, dateTime))

            cursor.moveToNext()
        }
        cursor.close()
    }

    private fun getQueryForHistory(testIndex: Int): Pair<String, Array<String>> {
        // Get the History for this test version.
        // TODO: On the line " WHERE " + DbContract.History.COLUMN_TEST_ID + " == ?" .. add a version check.
        var query = ("SELECT ${History._ID}, " +
                "${History.COLUMN_TEST_ID}, " +
                "${History.COLUMN_TEST_VERSION}, " +
                "${History.COLUMN_USES_QUESTIONS}, " +
                "${History.COLUMN_USES_ROAD_SIGNS}, " +
                "${History.COLUMN_USES_INTERSECTIONS}, " +
                "${History.COLUMN_POINTS}, " +
                "${History.COLUMN_MAX_POINTS}, " +
                "${History.COLUMN_ELAPSED_TIME}, " +
                "${History.COLUMN_ANSWERS}, " +
                "${History.COLUMN_DATE_TIME} FROM ${History.TABLE_NAME}")

        var selectionArgs = arrayOf<String>()
        if (testIndex != 0) {
            query += " WHERE ${History.COLUMN_TEST_ID} == ?"
            selectionArgs = arrayOf(Integer.toString(testIndex))
        }

        query += " ORDER BY ${History._ID} DESC"

        return query to selectionArgs
    }
}
