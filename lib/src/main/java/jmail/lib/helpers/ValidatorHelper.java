package jmail.lib.helpers;

import jmail.lib.models.Email;
import javafx.util.Pair;

public class ValidatorHelper {

  public static Pair<Boolean, String> isEmailValid(Email email) {
    if (email == null) {
      return new Pair<>(false, "Email is null");
    }

    if (email.getSender() == null || email.getSender().isEmpty()) {
      return new Pair<>(false, "Sender is null or empty");
    }

    if (email.getRecipients() == null || email.getRecipients().isEmpty()) {
      return new Pair<>(false, "Recipients is null or empty");
    }

    if (email.getRecipients().stream().anyMatch(rec -> !isEmailAddressValid(rec))) {
      return new Pair<>(false, "Recipients contains invalid email address");
    }
    return new Pair<>(true, "");
  }

  public static boolean isEmailAddressValid(String address) {
    return address != null
        && !address.isEmpty()
        && address.matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$");
  }
}
