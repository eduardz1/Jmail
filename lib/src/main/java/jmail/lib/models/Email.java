package jmail.lib.models;

import java.util.Date;
import java.util.List;
import java.util.UUID;
import lombok.Data;
import lombok.NonNull;

@Data
public class Email {
  private final String id;
  private String subject;
  private String body;
  private final @NonNull String sender;
  private final @NonNull List<String> recipients;
  private final @NonNull Date date;
  private @NonNull Boolean read;

  public Email(
      String id,
      String subject,
      String body,
      @NonNull String sender,
      @NonNull List<String> recipients,
      @NonNull Date date,
      @NonNull Boolean read) {
    if (sender.isEmpty()) {
      throw new IllegalArgumentException("Sender cannot be empty");
    }
    if (recipients.isEmpty() || recipients.stream().anyMatch(String::isEmpty)) {
      throw new IllegalArgumentException("Recipients cannot be empty");
    }
    this.id = id;
    this.subject = subject;
    this.body = body;
    this.sender = sender;
    this.recipients = recipients;
    this.date = date;
    this.read = read;
  }

  public String getFileID() {
    return String.format(
        "%s_%s",
        id == null || id.isEmpty() ? UUID.randomUUID() : id, date.toInstant().getEpochSecond());
  }
}
