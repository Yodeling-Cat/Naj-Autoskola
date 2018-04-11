package com.spiraclestudios.autoskola.features.test_results

import java.io.Serializable

data class DrivingTestResult(val points: Int, val maxPoints: Int,
                             val elapsedTime: Long,
                             val chosenAnswersList: List<Int>,
                             val amountCorrect: Int, val amountIncorrect: Int, val amountAnswered: Int,
                             val dateStarted: Long) : Serializable {

    val amountUnanswered: Int
        get() = chosenAnswersList.size - amountAnswered
}