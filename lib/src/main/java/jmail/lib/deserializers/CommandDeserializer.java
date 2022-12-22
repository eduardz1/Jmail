package jmail.lib.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;

import jmail.lib.constants.CommandActions;
import jmail.lib.models.commands.*;

public class CommandDeserializer extends JsonDeserializer<Command> {
  @Override
  public Command deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
    JsonNode node = jp.readValueAsTree();
    var action = node.get("action").asText();
    var parameters = node.get("parameter");
    var user = node.get("userEmail").asText();

    ObjectMapper mapper = new ObjectMapper();
    var command =
        switch (action) {
          case CommandActions.DELETE -> new CommandDeleteEmail(
              mapper.treeToValue(parameters, CommandDeleteEmail.CommandDeleteEmailParameter.class));
          case CommandActions.LIST -> new CommandListEmail(
              mapper.treeToValue(parameters, CommandListEmail.CommandListEmailParameter.class));
          case CommandActions.READ -> new CommandReadEmail(
              mapper.treeToValue(parameters, CommandReadEmail.CommandReadEmailParameter.class));
          case CommandActions.SEND -> new CommandSendEmail(
              mapper.treeToValue(parameters, CommandSendEmail.CommandSendEmailParameter.class));
          case CommandActions.RESTORE -> new CommandRestoreEmail(
              mapper.treeToValue(
                  parameters, CommandRestoreEmail.CommandRestoreEmailParameter.class));
          case CommandActions.LOGIN -> new CommandLogin(
              mapper.treeToValue(parameters, CommandLogin.CommandLoginParameter.class));
          default -> null;
        };
    if (command != null) command.setUserEmail(user);
    return command;
  }
}
