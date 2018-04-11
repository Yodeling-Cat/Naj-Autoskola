package com.spiraclestudios.autoskola.features.driving_test

import java.io.Serializable

data class DrivingTestInfo(val testId: Int, val testVersion: Int,
                           val withQuestions: Boolean, val withRoadSigns: Boolean, val withIntersections: Boolean) : Serializable
