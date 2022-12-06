package jmail.client.models.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import jmail.client.models.model.DataModel;
import jmail.lib.helpers.JsonHelper;
import jmail.lib.models.ServerResponse;
import jmail.lib.models.commands.Command;

import javax.xml.crypto.Data;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class MailClient {
    private Socket internalServerSocket;
    private final ThreadPoolExecutor threadPool;

    /**
     * Creates a new server instance.
     *
     * @param address The address to listen on.
     * @param port    The port to listen on.
     * @throws IOException If an I/O error occurs when opening the socket.
     */
    public MailClient(String address, int port) {
        threadPool = new ThreadPoolExecutor(10, 10, 60L, TimeUnit.SECONDS, new LinkedBlockingQueue<>());
        threadPool.allowCoreThreadTimeOut(true);
        try {
            internalServerSocket = new Socket(address, port);
        } catch (Exception e) {
            changeConnectionState(false);
        }
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

    private void changeConnectionState(boolean isConnected) {
        var data = DataModel.getInstance();
        data.setServerStatusConnected(isConnected);
    }

    @FunctionalInterface
    public interface ResponseFunction {
        void run(ServerResponse response);
    }
}
