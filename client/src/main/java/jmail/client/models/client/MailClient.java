package jmail.client.models.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.concurrent.*;
import jmail.client.models.model.DataModel;
import jmail.lib.constants.ServerResponseStatuses;
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

    public void connect(String address, int port) throws IOException {
        try {
            internalServerSocket = new Socket(address, port);
        } catch (Exception e) {
            syncConnectionState(false);
            throw e;
        }
    }

    public static MailClient getInstance() {
        return instance;
    }

    public boolean isConnected() {
        return internalServerSocket != null && internalServerSocket.isConnected() && !internalServerSocket.isClosed();
    }

    public <T extends ServerResponse> void sendCommand(
            Command cmd, ResponseFunction responseFunc, Class<T> responseClass) {
        threadPool.execute(() -> {
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
                            "Error parsing response: " + ex.getLocalizedMessage()); // TODO: Show alert to user
                    responseFunc.run(new ServerResponse(ServerResponseStatuses.ERROR, "Error parsing response"));
                }
            } catch (Exception e) {
                responseFunc.run(new ServerResponse(ServerResponseStatuses.ERROR, "Error connecting to server"));
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

    public void close() {}
}
