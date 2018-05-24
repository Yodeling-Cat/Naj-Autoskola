// Copyright (c) 2015-2017. Spiracle Software. All Rights Reserved.

package com.spiraclestudios.autoskola

import android.app.Activity
import android.content.Context
import android.view.inputmethod.InputMethodManager
import com.spiraclestudios.autoskola.domain.Groups

object Utils {

  fun hideSoftKeyboard(activity: Activity) {
    if (activity.currentFocus == null) return
    val manager = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    manager.hideSoftInputFromWindow(activity.currentFocus!!.windowToken, 0)
  }

  fun getGroupFromTestIndex(index: Int): Groups =
      when (index) {
        in 1..35 -> Groups.AB
        else -> Groups.CDT
      }

  /*public static boolean isValidEmail(String emailAddress) {
    // Source: http://howtodoinjava.com/2014/11/11/java-regex-validate-email-address/
    String regex =
        "^[\\w!#$%&'*+/=?`{|}~^-]+(?:\\.[\\w!#$%&'*+/=?`{|}~^-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,6}$";
    Pattern pattern = Pattern.compile(regex);
    return pattern.matcher(emailAddress).matches();
  }*/

  /**
   * Evaluates the scored points and elapsed time and returns success status.
   *
   * @param points Scored points.
   * @param elapsedTime Time taken to complete the test.
   * @return Would the user with this score and time pass the test?
   */
  fun getTestSuccessful(points: Int, elapsedTime: Long): Boolean =
      points >= 50 && elapsedTime / 1000 / 60 <= 20

  /**
   * Evaluates the scored points and returns success status.
   * NOTE: You should also pass the elapsed time as a parameter, as time is a factor in real tests.
   *
   * @param points Scored points.
   * @return Would the user with this score pass the test?
   */
  fun getTestSuccessful(points: Int): Boolean = points >= 50

  fun clamp(min: Int, value: Int, max: Int): Int = Math.max(min, Math.min(max, value))

  fun clamp(min: Float, value: Float, max: Float): Float = Math.max(min, Math.min(max, value))
}
