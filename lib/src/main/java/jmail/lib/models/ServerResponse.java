package jmail.lib.models;

import lombok.*;

@Setter
@Getter
@AllArgsConstructor
public class ServerResponse {

  public ServerResponse() {}

  private String status;
  private String errorMessage;
}
