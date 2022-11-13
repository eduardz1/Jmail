package jmail.lib.helpers;

import com.fasterxml.jackson.databind.JsonNode;
import jmail.lib.models.commands.Command;
import jmail.lib.models.commands.CommandDeleteEmail;
import jmail.lib.constants.*;

public class CommandHelper {

    public static Command getCommandImplementation(String action, JsonNode parameters) throws Exception {
        switch (action) {
            case CommandActions.DELETE -> {
                return new CommandDeleteEmail(
                                JsonHelper.fromJsonNode(parameters, CommandDeleteEmail.CommandDeleteEmailParameter.class)
                );
            }
//            case LIST -> {
//                return null;
//            }
//            case READ -> {
//                return null;
//            }
//            case SEND -> {
//                return null;
//            }
            default -> throw new Exception("Command type not recognized"); // TODO: creare un exc corretta
        }
    }

}
