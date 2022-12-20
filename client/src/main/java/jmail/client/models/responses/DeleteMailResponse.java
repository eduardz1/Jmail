package jmail.client.models.responses;

import jmail.lib.models.ServerResponse;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DeleteMailResponse extends ServerResponse {

  public DeleteMailResponse() {
    super();
  }

  public DeleteMailResponse(String status, String message) {
    super(status, message);
  }
}
