package jmail.lib.models;

import java.util.Date;
import java.util.List;
import lombok.NonNull;

// Non implementeremo serializable perchè useremo json per farlo
// https://stackoverflow.com/questions/11102645/java-serialization-vs-json-vs-xml
public record Email(
    String id,
    String subject,
    String body,
    @NonNull String sender,
    @NonNull List<String> recipients,
    @NonNull Date date) {

  public Email {
    if (sender.isEmpty()) {
      throw new IllegalArgumentException("Sender cannot be empty");
    }
    if (recipients.isEmpty() || recipients.stream().anyMatch(String::isEmpty)) {
      throw new IllegalArgumentException("Recipients cannot be empty");
    }
  }
}
