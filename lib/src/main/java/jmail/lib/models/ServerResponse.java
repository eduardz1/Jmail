package jmail.lib.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import jmail.lib.constants.ServerResponseStatuses;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ServerResponse {
  private String status;
  private String errorMessage;
  private String responseMessage;
  private ServerResponseBody body;

  public ServerResponse() {}

  public ServerResponse(ServerResponseBody body) {
    this.status = ServerResponseStatuses.OK;
    this.body = body;
  }

  public ServerResponse(String status, String errorMessage) {
    this.status = status;
    this.errorMessage = errorMessage;
  }

  public ServerResponse(String status, String responseMessage, String errorMessage) {
    this.status = status;
    this.errorMessage = errorMessage;
    this.responseMessage = responseMessage;
  }

  public static ServerResponse createOkResponse(String message) {
    return new ServerResponse(ServerResponseStatuses.OK, message, "");
  }

  public static ServerResponse createErrorResponse(String message) {
    return new ServerResponse(ServerResponseStatuses.ERROR, message);
  }
}
