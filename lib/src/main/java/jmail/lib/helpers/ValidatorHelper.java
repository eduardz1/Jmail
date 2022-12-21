package jmail.lib.helpers;

public class ValidatorHelper {
  public static boolean isEmailValid(String email) {
    return email != null
        && !email.isEmpty()
        && email.matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$");
  }
}
