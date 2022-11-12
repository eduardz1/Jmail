package jmail.server.handlers;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;

public class ClientHandler implements Runnable {

  private final DataInputStream inputStream;

  private final Socket internalSocket;

  public ClientHandler(Socket clientSocket) throws IOException {
    internalSocket = clientSocket;
    inputStream = new DataInputStream(new BufferedInputStream(clientSocket.getInputStream()));
  }

  @Override
  public void run() {
    try {
      var commandHandler = new CommandHandler(inputStream.readInt());
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }
}
