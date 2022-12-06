package jmail.server.models.actions;

import jmail.lib.helpers.SystemIOHelper;
import jmail.lib.models.ServerResponse;
import jmail.lib.models.ServerResponseBody;
import jmail.lib.models.commands.CommandLogin;

public class ActionLogin implements ActionCommand {
  private final CommandLogin command;

  public ActionLogin(CommandLogin command) {
    this.command = command;
  }

  @Override
  public ServerResponse executeAndGetResult() {
    if (!SystemIOHelper.userExists(command.getParameter().email()))
      return ServerResponse.createErrorResponse("User does not exist");

    var user = SystemIOHelper.getUser(command.getParameter().email());
    if (user.getPasswordSHA256().equals(command.getParameter().hashedPassword())) {
      return ServerResponse.createOkResponse("Login successful");
    } else {
      return ServerResponse.createErrorResponse("Login failed");
    }
  }

  public record ActionLoginServerResponseBody(String token)
      implements ServerResponseBody {} // Token should allow or disallow login maybe
}
