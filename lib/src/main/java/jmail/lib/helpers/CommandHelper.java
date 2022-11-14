package jmail.lib.helpers;

import com.fasterxml.jackson.core.JsonProcessingException;
import jmail.lib.exceptions.CommandNotFoundException;
import jmail.lib.models.commands.*;

import static jmail.lib.constants.CommandActions.*;

public class CommandHelper {

    public static Command getCommandImplementation(String jsonCommand) throws CommandNotFoundException, JsonProcessingException {
        var node = JsonHelper.toJsonNode(jsonCommand);
        var action = node.get("action").asText();
        var parameters = node.get("parameters");

        switch (action) {
            case DELETE -> {
                return new CommandDeleteEmail(
                        JsonHelper.fromJsonNode(parameters, CommandDeleteEmail.CommandDeleteEmailParameter.class)
                );
            }
            case LIST -> {
                return new CommandListEmail(
                        JsonHelper.fromJsonNode(parameters, CommandListEmail.CommandListEmailParameter.class)
                );
            }
            case READ -> {
                return new CommandReadEmail(
                        JsonHelper.fromJsonNode(parameters, CommandReadEmail.CommandReadEmailParameter.class)
                );
            }
            case SEND -> {
                return new CommandSendEmail(
                        JsonHelper.fromJsonNode(parameters, CommandSendEmail.CommandSendEmailParameter.class)
                );
            }
            case RESTORE -> {
                return new CommandRestoreEmail(
                        JsonHelper.fromJsonNode(parameters, CommandRestoreEmail.CommandRestoreEmailParameter.class)
                );
            }
            default -> throw new CommandNotFoundException("Command type not recognized"); // TODO: creare un exc corretta
        }
    }

}
