package jmail.lib.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import jmail.lib.constants.ServerResponseStatuses;
import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;

@Data
@Builder
@Jacksonized
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ServerResponse {
  private String status;
  private String errorMessage;
  private String responseMessage;
  private ServerResponseBody body;

  public static ServerResponse createOkResponse(String message) {
    return new ServerResponseBuilder()
        .status(ServerResponseStatuses.OK)
        .responseMessage(message)
        .build();
  }

  public static ServerResponse createErrorResponse(String message) {
    return new ServerResponseBuilder()
        .status(ServerResponseStatuses.ERROR)
        .errorMessage(message)
        .build();
  }
}
