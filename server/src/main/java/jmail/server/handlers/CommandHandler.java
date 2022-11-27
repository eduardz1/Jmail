package jmail.server.handlers;

import com.fasterxml.jackson.core.JsonProcessingException;
import java.io.PrintWriter;
import java.util.Map;
import jmail.lib.constants.CommandActions;
import jmail.lib.helpers.JsonHelper;
import jmail.lib.models.ServerResponse;
import jmail.lib.models.commands.*;
import jmail.server.exceptions.ActionExecutionException;
import jmail.server.factory.ActionCommandFactory;
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
  private final Map<String, Runnable> commandMap =
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
    commandMap.get(internalCommand.getAction()).run();
  }

  private void sendOk() {
    this.sendOk("");
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

  private void sendError() {
    this.sendError("");
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

  private void sendEmail() {
    var sendCmd = (CommandSendEmail) internalCommand;
    var param = sendCmd.getParameter();

    try {
      if (param == null || param.getEmail() == null) {
        throw new IllegalArgumentException("Command send: email cannot be null");
      }
      var action = ActionCommandFactory.getActionCommand(internalCommand);
      action.execute();
      sendOk();
    } catch (ActionExecutionException ex) {
      System.out.println(ex.getInnerMessage());
      var msg = "Cannot execute send mail action: " + ex.getMessage();
      LOGGER.error(msg);
      sendError(msg);
    }
  }

  private void listEmails() {
    var listCmd = (CommandListEmail) internalCommand;
    var param = listCmd.getParameter();

    try {
      var action = ActionCommandFactory.getActionCommand(internalCommand);
      action.execute();
      sendOk();
    } catch (ActionExecutionException ex) {
      System.out.println(ex.getInnerMessage());
      var msg = "Cannot execute list mail action: " + ex.getMessage();
      LOGGER.error(msg);
      sendError(msg);
    }
  }

  private void markEmailAsRead() {
    var readCmd = (CommandReadEmail) internalCommand;
    var param = readCmd.getParameter();

    try {
      if (param == null || param.getEmailID() == null || param.getEmailID().isEmpty()) {
        throw new IllegalArgumentException("Command read: email id cannot be null");
      }
      var action = ActionCommandFactory.getActionCommand(internalCommand);

      action.execute();
      sendOk();
    } catch (ActionExecutionException ex) {
      System.out.println(ex.getInnerMessage());
      var msg = "Cannot execute read mail action: " + ex.getMessage();
      LOGGER.error(msg);
      sendError(msg);
    }
  }

  private void deleteEmail() {
    var delCmd = (CommandDeleteEmail) internalCommand;
    var param = delCmd.getParameter();

    try {
      if (param == null || param.getEmailID() == null || param.getEmailID().isEmpty()) {
        throw new IllegalArgumentException("Command delete: email id cannot be null or empty");
      }
      var action = ActionCommandFactory.getActionCommand(internalCommand);
      action.execute();
      sendOk();
    } catch (ActionExecutionException ex) {
      System.out.println(ex.getInnerMessage());
      var msg = "Cannot execute delete mail action: " + ex.getMessage();
      LOGGER.error(msg);
      sendError(msg);
    }
  }
}
