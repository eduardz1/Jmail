package jmail.lib.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum ServerResponseStatuses {
    OK,
    ERROR,
    NOT_AUTHORIZED;

    @JsonValue
    public int toValue() {
        return ordinal();
    }
}
