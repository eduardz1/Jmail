package jmail.client.models.client;

import jmail.lib.models.ServerResponse;
import jmail.lib.models.commands.Command;
import jmail.server.models.actions.ActionCommand.Response;

public interface IMailClient {

  void sendCommand(Command command, ResponseFunction responseFunc);

  void close();

  @FunctionalInterface
  interface ResponseFunction {
    void run(ServerResponse<Response> response);
  }
}
