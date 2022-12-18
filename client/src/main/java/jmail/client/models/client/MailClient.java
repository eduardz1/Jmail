package jmail.client.models.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import jmail.client.models.model.DataModel;
import jmail.lib.helpers.JsonHelper;
import jmail.lib.models.ServerResponse;
import jmail.lib.models.commands.Command;

public class MailClient {
  private Socket internalServerSocket;
  private final ThreadPoolExecutor threadPool;

  static final MailClient instance = new MailClient();

  /**
   * Creates a new server instance.
   *
   * @throws IOException If an I/O error occurs when opening the socket.
   */
  protected MailClient() {
    threadPool = new ThreadPoolExecutor(10, 10, 60L, TimeUnit.SECONDS, new LinkedBlockingQueue<>());
    threadPool.allowCoreThreadTimeOut(true);
  }

  public void connect(String address, int port) {
    try {
      internalServerSocket = new Socket(address, port);
    } catch (Exception e) {
      syncConnectionState();
    }
  }

  public static MailClient getInstance() {
    return instance;
  }

  public boolean isConnected() {
    return internalServerSocket != null && internalServerSocket.isConnected();
  }

  public <T extends ServerResponse> void sendCommand(
      Command cmd, ResponseFunction responseFunc, Class<T> responseClass) {
    threadPool.execute(
        () -> {
          try (BufferedReader reader =
                  new BufferedReader(new InputStreamReader(internalServerSocket.getInputStream()));
              PrintWriter writer = new PrintWriter(internalServerSocket.getOutputStream(), true)) {
            writer.println(JsonHelper.toJson(cmd));

            // Await for response
            String response = reader.readLine();
            try {
              var resp = JsonHelper.fromJson(response, responseClass);
              responseFunc.run(resp);
            } catch (JsonProcessingException ex) {
              System.out.println(
                  "Error: [Response:'"
                      + response
                      + "', Error: '"
                      + ex.getLocalizedMessage()
                      + "']");
            }
          } catch (IOException e) {
            throw new RuntimeException(e);
          }
        });
  }

  private void syncConnectionState() {
    var data = DataModel.getInstance();
    data.setServerStatusConnected(this.isConnected());
  }

  @FunctionalInterface
  public interface ResponseFunction {
    void run(ServerResponse response);
  }

  public void close() {}
}
