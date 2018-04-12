// Copyright (c) 2015-2017. Spiracle Software. All Rights Reserved.

package com.spiraclestudios.autoskola

import com.spiraclestudios.autoskola.domain.Groups

class HistoryEntry(
        val dbIndex: Int,
        val index: Int,
        val group: Groups,
        val wasSuccessful: Boolean,
        val usesQuestions: Boolean,
        val usesRoadSigns: Boolean,
        val usesIntersections: Boolean,
        val points: Int,
        val maxPoints: Int,
        val amountCorrect: Int,
        val amountIncorrect: Int,
        val amountUnanswered: Int,
        val elapsedTime: Long,
        val answers: String,
        val dateTime: Long
)
