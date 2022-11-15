package jmail.server.handlers;

import com.fasterxml.jackson.core.JsonProcessingException;
import java.io.PrintWriter;
import java.util.Map;
import java.util.function.Supplier;
import jmail.lib.constants.CommandActions;
import jmail.lib.constants.ServerResponseStatuses;
import jmail.lib.helpers.JsonHelper;
import jmail.lib.models.ServerResponse;
import jmail.lib.models.commands.Command;
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

    // TODO: Fare qualcosa di effettivo

    try {
      var resp = new ServerResponse(ServerResponseStatuses.OK, "");

      writer.println(JsonHelper.toJson(resp));
    } catch (JsonProcessingException ex) {
      LOGGER.error(ex.getMessage());
    }

    return null;
  }
}
