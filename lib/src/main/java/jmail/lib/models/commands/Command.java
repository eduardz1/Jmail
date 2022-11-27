package jmail.lib.models.commands;

import lombok.Data;
import lombok.NonNull;

@Data

public class Command {
  @NonNull private String action;
  private String userEmail;

  public boolean hasEmail() {
    return userEmail != null && !userEmail.isEmpty();
  }
}
