// Copyright (c) 2015-2017. Spiracle Software. All Rights Reserved.

package com.spiraclestudios.autoskola;

import com.spiraclestudios.autoskola.domain.Groups;

/**
 * Added by benji on 14/10/2015.
 */
public class Utils {

  // Returns 0 (A,B) if index is 1-35 and 1 (C,D,T) if index is greater than 35
  public static Groups getGroupFromTestIndex(int index) {
    return (index > 35) ? Groups.CDT : Groups.AB;
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
  public static boolean getTestSuccessful(int points, long elapsedTime) {
    return points >= 50 && (elapsedTime / 1000) / 60 <= 20;
  }

  /**
   * Evaluates the scored points and returns success status.
   * NOTE: You should also pass the elapsed time as a parameter, as time is a factor in real tests.
   *
   * @param points Scored points.
   * @return Would the user with this score pass the test?
   */
  public static boolean getTestSuccessful(int points) {
    return points >= 50;
  }

  public static int clamp(int min, int val, int max) {
    return Math.max(min, Math.min(max, val));
  }

  public static float clamp(float min, float val, float max) {
    return Math.max(min, Math.min(max, val));
  }
}
