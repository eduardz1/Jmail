package jmail.lib.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jmail.lib.models.commands.*;

import java.io.IOException;

import static jmail.lib.constants.CommandActions.*;

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
          case DELETE -> new CommandDeleteEmail(
              mapper.treeToValue(parameters, CommandDeleteEmail.CommandDeleteEmailParameter.class));
          case LIST -> new CommandListEmail(
              mapper.treeToValue(parameters, CommandListEmail.CommandListEmailParameter.class));
          case READ -> new CommandReadEmail(
              mapper.treeToValue(parameters, CommandReadEmail.CommandReadEmailParameter.class));
          case SEND -> new CommandSendEmail(
              mapper.treeToValue(parameters, CommandSendEmail.CommandSendEmailParameter.class));
          case RESTORE -> new CommandRestoreEmail(
              mapper.treeToValue(
                  parameters, CommandRestoreEmail.CommandRestoreEmailParameter.class));
          case LOGIN -> new CommandLogin(
              mapper.treeToValue(parameters, CommandLogin.CommandLoginParameter.class));
          case PING -> new CommandPing();
          default -> null;
        };
    if (command != null) command.setUserEmail(user);
    return command;
  }
}
