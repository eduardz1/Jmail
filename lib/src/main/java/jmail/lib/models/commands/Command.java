package jmail.lib.models.commands;

import lombok.Data;
import lombok.NonNull;

@Data
public class Command {
  @NonNull private String action;
  private String userID;

  public boolean hasUser() {
    return userID != null && !userID.isEmpty();
  }
}
