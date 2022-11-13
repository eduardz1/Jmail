package jmail.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.*;
import jmail.server.handlers.ClientHandler;

public class Server extends Thread {

  private final ServerSocket internalServerSocket;
  private final ThreadPoolExecutor threadPool;

  /**
   * Creates a new server instance.
   *
   * @param port The port to listen on.
   * @throws IOException If an I/O error occurs when opening the socket.
   */
  public Server(int port) throws IOException {
    threadPool = new ThreadPoolExecutor(10, 10, 60L, TimeUnit.SECONDS, new LinkedBlockingQueue<>());
    threadPool.allowCoreThreadTimeOut(true);
    internalServerSocket = new ServerSocket(port);
    System.out.println(internalServerSocket.getLocalSocketAddress().toString());
  }

  @Override
  public void run() {
    try {
      internalServerSocket.setReuseAddress(true);


      while (!Thread.interrupted()) {
        Socket clientSocket = internalServerSocket.accept();
        threadPool.execute(new ClientHandler(clientSocket));
      }
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      if (internalServerSocket != null) {
        try {
          internalServerSocket.close();
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    }
  }
}
