package jmail.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.Instant;
import java.util.Date;
import java.util.concurrent.*;
import jmail.server.handlers.ClientHandler;
import jmail.server.helpers.SystemIOHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Server extends Thread {
  private ServerSocket internalServerSocket;
  private final ThreadPoolExecutor threadPool;

  private static final Logger LOGGER = LoggerFactory.getLogger(Server.class.getName());
  private final int port;

  /**
   * Creates a new server instance.
   *
   * @param port The port to listen on.
   * @throws IOException If an I/O error occurs when opening the socket.
   */
  public Server(int port) {
    threadPool = new ThreadPoolExecutor(10, 10, 60L, TimeUnit.SECONDS, new LinkedBlockingQueue<>());
    threadPool.allowCoreThreadTimeOut(true);
    this.port = port;
  }

  @Override
  public void start() {
    try {
      internalServerSocket = new ServerSocket(port);
      SystemIOHelper.createBaseFoldersIfNotExists();

      // FIXME: creati solo come prova
      SystemIOHelper.createUserFolderIfNotExists("occhipinti.eduard@gmail.com");
      SystemIOHelper.createUserFolderIfNotExists("emmedeveloper@gmail.com");
      SystemIOHelper.createUserFolderIfNotExists("marcofrattarola@gmail.com");
      SystemIOHelper.createUserFolderIfNotExists("eduard.occhipinti@edu.unito.it");

      LOGGER.info("Folders created with success");

    } catch (IOException e) {
      LOGGER.error("SocketServer exception on starting: " + e.getLocalizedMessage());
      e.printStackTrace();
    }
    super.start();
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
      LOGGER.error("SocketServer exception on running: " + e.getLocalizedMessage());
      e.printStackTrace();
    } finally {
      close();
    }
  }

  private void close() {
    if (internalServerSocket != null) {
      try {
        if (!internalServerSocket.isClosed()) internalServerSocket.close();
      } catch (IOException e) {
        LOGGER.error("SocketServer exception on closing: " + e.getLocalizedMessage());
        e.printStackTrace();
      }
      LOGGER.info("Server stopped!");
    }
  }

  @Override
  public void interrupt() {
    super.interrupt();
    close();
  }
}
