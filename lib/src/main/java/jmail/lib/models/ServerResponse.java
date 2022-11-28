package jmail.lib.models;

import jmail.lib.constants.ServerResponseStatuses;
import lombok.*;

@Setter
@Getter
public class ServerResponse {
  private String status;
  private String errorMessage;
  private String responseMessage;

  public ServerResponse() {}

  public ServerResponse(String status, String errorMessage) {
    this.status = status;
    this.errorMessage = errorMessage;
  }

  public ServerResponse(String status, String responseMessage, String errorMessage) {
    this.status = status;
    this.errorMessage = errorMessage;
    this.responseMessage = responseMessage;
  }

  public static ServerResponse CreateOkResponse(String message) {
    return new ServerResponse(ServerResponseStatuses.OK, message, "");
  }

  public static ServerResponse CreateErrorResponse(String message) {
    return new ServerResponse(ServerResponseStatuses.ERROR, message);
  }
}
