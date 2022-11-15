package jmail.lib.models.commands;

import lombok.Data;
import lombok.NonNull;

@Data
public class Command {
  @NonNull private String action;
  private String userID; // TODO: Capire se mettere qui per averlo in ogni messaggio, o se usare un
  // logincommand
}
