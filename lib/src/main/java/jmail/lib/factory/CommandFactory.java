package jmail.lib.factory;

import com.fasterxml.jackson.core.JsonProcessingException;
import jmail.lib.exceptions.CommandNotFoundException;
import jmail.lib.helpers.JsonHelper;
import jmail.lib.models.commands.*;

import static jmail.lib.constants.CommandActions.*;

public class CommandFactory {
    public static Command getCommand(String jsonCommand)
            throws CommandNotFoundException, JsonProcessingException {
        var node = JsonHelper.toJsonNode(jsonCommand);
        var action = node.get("action").asText();
        var parameters = node.get("parameters");

        return switch (action) {
            case DELETE -> new CommandDeleteEmail(
                    JsonHelper.fromJsonNode(parameters, CommandDeleteEmail.CommandDeleteEmailParameter.class));
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
    }


}
