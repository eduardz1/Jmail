package jmail.server.models.actions;

import jmail.lib.models.Response;
import jmail.lib.models.commands.CommandLogin;

public class ActionLogin implements ActionCommand {
  private final CommandLogin command;

  public ActionLogin(CommandLogin command) {
    this.command = command;
  }

  @Override
  public void execute() {
    // TODO: implement
  }

  public record ActionLoginResponse(String token)
      implements Response {} // Token should allow or disallow login maybe
}
