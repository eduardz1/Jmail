package jmail.client.models.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import jmail.client.models.model.DataModel;
import jmail.lib.constants.ServerResponseStatuses;
import jmail.lib.helpers.JsonHelper;
import jmail.lib.models.ServerResponse;
import jmail.lib.models.commands.Command;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.concurrent.*;

public class MailClient {
  //  private final ThreadPoolExecutor threadPool;
  private final ExecutorService threadPool;
  static final MailClient instance = new MailClient();

  /**
   * Creates a new server instance.
   *
   * @throws IOException If an I/O error occurs when opening the socket.
   */
  protected MailClient() {
    threadPool = Executors.newFixedThreadPool(10);
    // FIXME: Utilizzare un custom executor ci sta portando problemi, i thread non vengono chiusi
    // e i messaggi non vengono più inviati

    //    threadPool = new ThreadPoolExecutor(10, 10, 60L, TimeUnit.SECONDS, new LinkedBlockingQueue<>());
    //    threadPool.allowCoreThreadTimeOut(true);
  }

  public Socket connect(String address, int port) {
    try {
      var connection = new Socket(address, port);
      syncConnectionState(true);
      return connection;
    } catch (Exception e) {
      syncConnectionState(false);
      return null;
    }
  }

  public static MailClient getInstance() {
    return instance;
  }

  public <T extends ServerResponse> void sendCommand(
          Command cmd, ResponseFunction responseFunc, Class<T> responseClass) {

    // Add additional data to command
    var user = DataModel.getInstance().getCurrentUser();
    if (user != null) {
      cmd.setUserEmail(user.getEmail());
    }

    // Execute command on thread
    // Each command open a connection and close it after the response is received
    threadPool.execute(() -> {

      var connection = connect("localhost", 8085);
      if (connection == null) {
        responseFunc.run(new ServerResponse(ServerResponseStatuses.ERROR, "Unknown error during connection to server"));
        return;
      }

      String errorMessages = "";
      try (
              BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
              PrintWriter writer = new PrintWriter(connection.getOutputStream(), true)
      ) {

        // Send command
        writer.println(JsonHelper.toJson(cmd));

        /*
        * Wait for response
        */

        // Set a timeout for 5seconds
        // FIXME: Non funziona, il thread non viene chiuso, va in errore il pianeta e mi sminchia tutto in debug
        // connection.setSoTimeout(5000);

        // Read response
        String response = reader.readLine();
        var resp = JsonHelper.fromJson(response, responseClass);
        responseFunc.run(resp);
      } catch (JsonProcessingException e) {
        errorMessages = "Error while parsing response";
      } catch (IOException e) {
        errorMessages = "Error while reading response";
      } catch (Exception e) {
        errorMessages = "Unknown error during connection to server";
      } finally {
        if (!errorMessages.isEmpty()) {
          syncConnectionState(false);
          responseFunc.run(new ServerResponse(ServerResponseStatuses.ERROR, errorMessages));

          // Close connection
          if (connection != null) {
             try {
              connection.close();
            } catch (IOException e) {
              e.printStackTrace();
            }
          }
        }
      }
    });
  }

  private void syncConnectionState(boolean connected) {
    var data = DataModel.getInstance();
    data.setServerStatusConnected(connected);
  }

  @FunctionalInterface
  public interface ResponseFunction {
    void run(ServerResponse response);
  }

  public void close() {
  }
}
