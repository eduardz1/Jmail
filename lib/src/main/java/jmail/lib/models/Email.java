package jmail.lib.models;

import java.util.Date;
import java.util.List;

import javafx.util.Pair;
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
        String id,
        String subject,
        String body,
        @NonNull String sender,
        @NonNull List<String> recipients,
        @NonNull Date date
) {

    public Pair<Boolean, String> isValid() {
        if (sender == null || sender.isEmpty())
            return new Pair<>(false, "Email sender null or empty");

        if (
                recipients == null ||
                recipients.isEmpty() ||
                recipients.stream().anyMatch(rec -> rec == null || rec.isEmpty())
        )
            return new Pair<>(false, "Email recipients null, empty o some invalids");

        if (date == null)
            return new Pair<>(false, "Email creationdate null");

        return new Pair<>(true, "");
    }
}