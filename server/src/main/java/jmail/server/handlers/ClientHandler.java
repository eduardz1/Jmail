package jmail.server.handlers;

import com.fasterxml.jackson.core.JsonProcessingException;
import jmail.lib.constants.ServerResponseStatuses;
import jmail.lib.exceptions.CommandNotFoundException;
import jmail.lib.helpers.CommandHelper;
import jmail.lib.helpers.JsonHelper;
import jmail.lib.models.ServerResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.Socket;

public class ClientHandler implements Runnable {
  private BufferedReader reader;
  private PrintWriter writer;
  private final Socket internalSocket;

  private static final Logger LOGGER = LoggerFactory.getLogger(ClientHandler.class.getName());

  public ClientHandler(Socket clientSocket) throws IOException {
    internalSocket = clientSocket;
  }


  @Override
  public void run() {
    try {

      reader = new BufferedReader(new InputStreamReader(internalSocket.getInputStream()));
      writer = new PrintWriter(internalSocket.getOutputStream(), true);
      String request = reader.readLine();
      LOGGER.info("Message received from client: " + request);

      // TODO: Implementare autenticazione
      // Non so ancora se dovrà essere inviato un comando di login, e registrare la sessione sul socket temporaneamente
      // o se inviare l'id dell'utente come parametro di command

      var cmd = CommandHelper.getCommandImplementation(request);
      var commandHandler = new CommandHandler(cmd, writer);
      commandHandler.executeAction();

    }
    catch (CommandNotFoundException | JsonProcessingException e) {
      LOGGER.error("Message received not valid");
      sendResponse(ServerResponseStatuses.ERROR, "Message invalid");
    }
    catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  private void sendResponse(String status, String errorMessage) {
    var resp = new ServerResponse(status, errorMessage);
    try {
      writer.println(JsonHelper.toJson(resp));
    } catch (JsonProcessingException e) {
      LOGGER.error("Cannot serialize server response: " + e.getLocalizedMessage());
      writer.println("{\"status\": \"error\", \"message\":\"Unable to get response from server\"}");
    }

  }

}
