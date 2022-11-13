package jmail.lib.models;

import jmail.lib.enums.ServerResponseStatuses;
import lombok.Data;
import lombok.NonNull;

@Data
public class ServerResponse {

    @NonNull private ServerResponseStatuses status;

    private String errorMessage;

}
