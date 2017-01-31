// Copyright (c) 2015-2017. Spiracle Software. All Rights Reserved.

package com.spiraclestudios.autoskola.domain;

import static com.google.common.base.Preconditions.checkNotNull;

public class TestResult {

  public final int testId;
  public final int points;
  public final int maxPoints;
  public final int amountCorrect;
  public final int amountIncorrect;
  public final int amountUnanswered;
  public final long elapsedTime;

  public TestResult(int testId, int points, int maxPoints, int amountCorrect, int amountIncorrect,
      int amountUnanswered, long elapsedTime) {
    this.testId = checkNotNull(testId);
    this.points = checkNotNull(points);
    this.maxPoints = checkNotNull(maxPoints);
    this.amountCorrect = checkNotNull(amountCorrect);
    this.amountIncorrect = checkNotNull(amountIncorrect);
    this.amountUnanswered = checkNotNull(amountUnanswered);
    this.elapsedTime = checkNotNull(elapsedTime);
  }
}
