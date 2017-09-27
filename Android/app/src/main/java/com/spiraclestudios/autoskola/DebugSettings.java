// Copyright (c) 2015-2017. Spiracle Software. All Rights Reserved.

package com.spiraclestudios.autoskola;

public class DebugSettings {

  private static boolean showingCorrectAnswers = true;

  public static boolean isShowingCorrectAnswers() {
    return BuildConfig.DEBUG && showingCorrectAnswers;
  }
}
