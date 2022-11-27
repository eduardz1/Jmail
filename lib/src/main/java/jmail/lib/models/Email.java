package jmail.lib.models;

import java.io.Serializable;
import java.sql.Date;
import java.util.List;

import lombok.NonNull;


// TODO: Far leggere a edu
// Non implementeremo serializable perchè useremo json per farlo
// https://stackoverflow.com/questions/11102645/java-serialization-vs-json-vs-xml

//public record Email(
//    String subject,
//    String body,
//    @NonNull String sender,
//    @NonNull List<String> recipients,
//    @NonNull Date date)
//    implements Serializable {}

public record Email(
        String subject,
        String body,
        @NonNull String sender,
        @NonNull List<String> recipients,
        @NonNull Date date
) {
}