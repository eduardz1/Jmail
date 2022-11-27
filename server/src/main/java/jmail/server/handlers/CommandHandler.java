package jmail.server.handlers;

import com.fasterxml.jackson.core.JsonProcessingException;

import java.io.PrintWriter;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.function.Supplier;

import jmail.lib.constants.CommandActions;
import jmail.lib.constants.ServerResponseStatuses;
import jmail.lib.helpers.JsonHelper;
import jmail.lib.models.ServerResponse;
import jmail.lib.models.commands.Command;
import jmail.lib.models.commands.CommandDeleteEmail;
import jmail.server.exceptions.ActionExecutionException;
import jmail.server.factory.ActionCommandFactory;
import jmail.server.models.actions.ActionCommand;
import jmail.server.models.actions.ActionDeleteMail;
import lombok.extern.java.Log;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CommandHandler {
    private final Command internalCommand;
    private static final Logger LOGGER = LoggerFactory.getLogger(CommandHandler.class.getName());

    private final PrintWriter
            writer; // TODO Cambiare assolutamente con qualcosa di meno specifico per gestire le risposte
    // da inviare al client

    /* For reference, in case we need to change signature of the methods:
     *
     *   in\out:     0            1
     *   0     |  Runnable     Consumer
     *   1     |  Supplier     Function
     */
    private final Map<String, Supplier<Void>> commandMap =
            Map.of(
                    CommandActions.SEND, this::sendEmail,
                    CommandActions.LIST, this::listEmails,
                    CommandActions.READ, this::markEmailAsRead,
                    CommandActions.DELETE, this::deleteEmail);

    public CommandHandler(Command cmd, PrintWriter writer) {
        internalCommand = cmd;
        this.writer = writer;
    }

    public void executeAction() {
        commandMap.get(internalCommand.getAction()).get();
    }

    private Void sendEmail() {
        return null;
    }

    private Void listEmails() {
        return null;
    }

    private Void markEmailAsRead() {
        return null;
    }

    private Void deleteEmail() {

        var delCmd = (CommandDeleteEmail) internalCommand;
        var param = delCmd.getParameter();

        try {
            if (param == null || param.getId() == null || param.getId().isEmpty()) {
                throw new IllegalArgumentException("Command delete: email id cannot be null or empty");
            }
            var action = ActionCommandFactory.getActionCommand(internalCommand);
            action.execute();
            sendOk("");

        } catch (ActionExecutionException ex) {
            System.out.println(ex.getInnerMessage());
            var msg = "Cannot execute delete mail action: " + ex.getMessage();
            LOGGER.error(msg);
            sendError(msg);
        }
        return null;
    }

    private void sendOk(String msg) {
        try {
            var resp = ServerResponse.CreateOkResponse(msg);
            writer.println(JsonHelper.toJson(resp));
        } catch (JsonProcessingException ex) {
            LOGGER.error("Cannot serialize server response: " + ex.getLocalizedMessage());
            writer.println("{\"status\": \"error\", \"message\":\"Unable to get response from server\"}");
        }
    }

    private void sendError(String msg) {
        try {
            var resp = ServerResponse.CreateErrorResponse(msg);
            writer.println(JsonHelper.toJson(resp));
        } catch (JsonProcessingException ex) {
            LOGGER.error("Cannot serialize server response: " + ex.getLocalizedMessage());
            writer.println("{\"status\": \"error\", \"message\":\"Unable to get response from server\"}");
        }
    }
}
