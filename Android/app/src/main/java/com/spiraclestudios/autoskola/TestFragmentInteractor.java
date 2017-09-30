// Copyright (c) 2015-2017. Spiracle Software. All Rights Reserved.

package com.spiraclestudios.autoskola;

import android.graphics.drawable.Drawable;
import com.spiraclestudios.autoskola.presentation.ui.activities.TestActivity.TestTypes;
import java.util.List;

public interface TestFragmentInteractor {

  TestPagerAdapter getTestPagerAdapter();

  int getQuestionType(int index);

  String getQuestionText(int index);

  Drawable getQuestionImage(int index);

  List<String> getQuestionAnswers(int index);

  int getChosenAnswer(int index);

  TestTypes getTestType();

  void selectAnswer(int answer);

  boolean getCompleted();

  boolean getAllowClickingAnswers();

  boolean getShouldRevealAnswersImmediately();

  int getCorrectAnswer(int index);

  void goToQuestion(int index, boolean smoothScroll);

  void nextQuestion();
}
