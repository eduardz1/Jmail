package jmail.lib.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import jmail.lib.constants.ServerResponseStatuses;
import lombok.*;

@Setter
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ServerResponse<T> {
  private String status;
  private String errorMessage;
  private String responseMessage;
  private T body;

  public ServerResponse() {}

  public ServerResponse(T body) {
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

  public static ServerResponse CreateOkResponse(String message) {
    return new ServerResponse(ServerResponseStatuses.OK, message, "");
  }

  public static ServerResponse CreateErrorResponse(String message) {
    return new ServerResponse(ServerResponseStatuses.ERROR, message);
  }
}
