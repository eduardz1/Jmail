package jmail.client;


import jmail.lib.helpers.JsonHelper;
import jmail.lib.models.ServerResponse;
import jmail.lib.models.commands.Command;

import java.io.*;
import java.net.Socket;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.fasterxml.jackson.core.JsonProcessingException;


public class MailClient {

    private final Socket internalServerSocket;
    private final ThreadPoolExecutor threadPool;

    /**
     * Creates a new server instance.
     *
     * @param address The address to listen on.
     * @param port    The port to listen on.
     * @throws IOException If an I/O error occurs when opening the socket.
     */
    public MailClient(String address, int port) throws IOException {
        threadPool = new ThreadPoolExecutor(10, 10, 60L, TimeUnit.SECONDS, new LinkedBlockingQueue<>());
        threadPool.allowCoreThreadTimeOut(true);
        internalServerSocket = new Socket(address, port);
    }

    public void sendCommand(Command cmd) {
        threadPool.execute(() -> {
            try (
                    BufferedReader reader = new BufferedReader(new InputStreamReader(internalServerSocket.getInputStream()));
                    PrintWriter writer = new PrintWriter(internalServerSocket.getOutputStream(), true)
            ) {
                writer.println(JsonHelper.toJson(cmd));

                // Await for response
                String response = reader.readLine();
                try {
                    ServerResponse resp = JsonHelper.fromJson(response, ServerResponse.class);
                    System.out.println(JsonHelper.toJson(resp));
                } catch (JsonProcessingException ex) {
                    System.out.println("Error: [Response:'" + response + "', Error: '" + ex.getLocalizedMessage() + "']");
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }
}