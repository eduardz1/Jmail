package jmail.client.models.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.common.hash.Hashing;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import jmail.client.Main;
import jmail.lib.helpers.JsonHelper;
import jmail.lib.models.ServerResponse;
import jmail.lib.models.commands.Command;
import jmail.lib.models.commands.CommandLogin;

public class MailClient {
  private static final MailClient instance = new MailClient();
  private final ThreadPoolExecutor threadPool;
  private Socket internalServerSocket;

  //  /** FIXME: thinking about making it static and calling a "connect" method
  //   * Creates a new server instance.
  //   *
  //   * @param address The address to listen on.
  //   * @param port The port to listen on.
  //   * @throws IOException If an I/O error occurs when opening the socket.
  //   */
  //  public MailClient(String address, int port) throws IOException {
  //    threadPool = new ThreadPoolExecutor(10, 10, 60L, TimeUnit.SECONDS, new
  // LinkedBlockingQueue<>());
  //    threadPool.allowCoreThreadTimeOut(true);
  //    internalServerSocket = new Socket(address, port);
  //  }

  private MailClient() {
    threadPool = new ThreadPoolExecutor(10, 10, 60L, TimeUnit.SECONDS, new LinkedBlockingQueue<>());
    threadPool.allowCoreThreadTimeOut(true);
  }

  public static MailClient getInstance() {
    return instance;
  }

  public void connect(String address, int port) throws IOException {
    internalServerSocket = new Socket(address, port);
  }

  public void disconnect() throws IOException {
    internalServerSocket.close();
  }

  public boolean isConnected() {
    return internalServerSocket != null && internalServerSocket.isConnected();
  }

  public void sendCommand(Command cmd, ResponseFunction responseFunc) {
    threadPool.execute(
        () -> {
          try (BufferedReader reader =
                  new BufferedReader(new InputStreamReader(internalServerSocket.getInputStream()));
              PrintWriter writer = new PrintWriter(internalServerSocket.getOutputStream(), true)) {
            writer.println(JsonHelper.toJson(cmd));

            // Await for response
            String response = reader.readLine();
            try {
              var resp = JsonHelper.fromJson(response, ServerResponse.class);
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

  public void close() {}

  public void login(String username, String password) {
    var hashed = Hashing.sha256().hashString(password, StandardCharsets.UTF_8).toString();
    var command = new CommandLogin(new CommandLogin.CommandLoginParameter(username, hashed));
    command.setUserEmail(username);
    Main.changeScene("client.fxml");
    // sendCommand(
    //     command,
    //     response -> {
    //       if (response
    //           .getStatus()
    //           .equals(
    //               ServerResponseStatuses
    //                   .OK)) { // TODO: does the response belong in the ServerResponse.body? Is it
    //         // translated to the other fields?
    //         System.out.println("Login successful"); // TODO: use LOGGER here
    //         Main.changeScene("client.fxml");
    //       } else {
    //         System.out.println("Login failed");
    //       }
    //     });
  }
}
