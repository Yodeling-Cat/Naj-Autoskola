// Copyright (c) 2015-2017. Spiracle Software. All Rights Reserved.

package com.spiraclestudios.autoskola;

/**
 * Added by benji on 14/10/2015.
 */
public class Utils {

  public enum Groups {
    AB, CDT
  }

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
}
