package jmail.server.handlers;

import java.net.Socket;

public class ClientHandler implements Runnable {

  private final Socket internalSocket;

  public ClientHandler(Socket clientSocket) {
    internalSocket = clientSocket;
  }

  @Override
  public void run() {}
}
