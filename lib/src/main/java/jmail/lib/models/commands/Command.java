package jmail.lib.models.commands;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import jmail.lib.deserializers.CommandDeserializer;
import lombok.Data;
import lombok.NonNull;

@Data
@JsonDeserialize(using = CommandDeserializer.class)
public class Command {
  @NonNull private String action;
  private String userEmail;

  public boolean hasEmail() {
    return userEmail != null && !userEmail.isEmpty();
  }
}
