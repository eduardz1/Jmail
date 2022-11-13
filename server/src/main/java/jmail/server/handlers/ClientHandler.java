package jmail.server.handlers;

import jmail.lib.helpers.CommandHelper;
import jmail.lib.helpers.JsonHelper;

import java.io.*;
import java.net.Socket;

public class ClientHandler implements Runnable {

//  private final DataInputStream inputStream;

//  private final CommandHandler cmdHandler;
  private BufferedReader reader;
  private PrintWriter writer;
  private final Socket internalSocket;

  public ClientHandler(Socket clientSocket) throws IOException {
    internalSocket = clientSocket;
//    inputStream = new DataInputStream(new BufferedInputStream(clientSocket.getInputStream()));
//    cmdHandler = new CommandHandler()
  }


  @Override
  public void run() {
    try {

      reader = new BufferedReader(new InputStreamReader(internalSocket.getInputStream()));
      writer = new PrintWriter(internalSocket.getOutputStream(), true);
      String request = reader.readLine();

      var node = JsonHelper.toJsonNode(request);
      var action = node.get("action").asText();
      var param = node.get("parameters");
      var cmd = CommandHelper.getCommandImplementation(action, param);

      var commandHandler = new CommandHandler(cmd, writer);
      commandHandler.executeAction();

    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (Exception e) {
      // TODO eccezione comando non riconosciuto

      throw new RuntimeException(e);
    }
  }
}
