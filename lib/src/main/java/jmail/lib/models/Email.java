package jmail.lib.models;

import java.util.Date;
import java.util.List;
import java.util.UUID;
import lombok.NonNull;

public record Email(
    String id,
    String subject,
    String body,
    @NonNull String sender,
    @NonNull List<String> recipients,
    @NonNull Date date,
    @NonNull Boolean read) {
  public Email {
    if (sender.isEmpty()) {
      throw new IllegalArgumentException("Sender cannot be empty");
    }
    if (recipients.isEmpty() || recipients.stream().anyMatch(String::isEmpty)) {
      throw new IllegalArgumentException("Recipients cannot be empty");
    }
  }

  public String getFileID() {
    return String.format(
        "%s_%s",
        id == null || id.isEmpty() ? UUID.randomUUID() : id, date.toInstant().getEpochSecond());
  }
}
