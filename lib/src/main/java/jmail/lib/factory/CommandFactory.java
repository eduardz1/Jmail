package jmail.lib.factory;

import static jmail.lib.constants.CommandActions.*;

import com.fasterxml.jackson.core.JsonProcessingException;
import jmail.lib.exceptions.CommandNotFoundException;
import jmail.lib.helpers.JsonHelper;
import jmail.lib.models.commands.*;
public class CommandFactory {
  public static Command getCommand(String jsonCommand)
      throws CommandNotFoundException, JsonProcessingException {
    var node = JsonHelper.toJsonNode(jsonCommand);
    var action = node.get("action").asText();
    var parameters = node.get("parameter");
    var user = node.get("userEmail").asText();

    var command = switch (action) {
      case DELETE -> new CommandDeleteEmail(
          JsonHelper.fromJsonNode(
              parameters, CommandDeleteEmail.CommandDeleteEmailParameter.class));
      case LIST -> new CommandListEmail(
          JsonHelper.fromJsonNode(parameters, CommandListEmail.CommandListEmailParameter.class));
      case READ -> new CommandReadEmail(
          JsonHelper.fromJsonNode(parameters, CommandReadEmail.CommandReadEmailParameter.class));
      case SEND -> new CommandSendEmail(
          JsonHelper.fromJsonNode(parameters, CommandSendEmail.CommandSendEmailParameter.class));
      case RESTORE -> new CommandRestoreEmail(
          JsonHelper.fromJsonNode(
              parameters, CommandRestoreEmail.CommandRestoreEmailParameter.class));
      default -> throw new CommandNotFoundException("Command type not recognized");
    };

    command.setUserEmail(user);
    return command;
  }


}
