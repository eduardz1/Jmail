package jmail.server.handlers;

import java.util.Map;
import java.util.function.Supplier;
import jmail.lib.enums.Commands;

public class CommandHandler {

  private final Map<Commands, Supplier<Void>> commandMap =
      Map.of(
          Commands.SEND, () -> sendEmail(),
          Commands.LIST, () -> listEmails(),
          Commands.READ, () -> markEmailAsRead(),
          Commands.DELETE, () -> deleteEmail());

  public CommandHandler(int readInt) {
    commandMap.get(Commands.values()[readInt]).get();
  }

  private Void sendEmail() {
    return null;
  }

  private Void listEmails() {
    return null;
  }

  private Void markEmailAsRead() {
    return null;
  }

  private Void deleteEmail() {
    return null;
  }
}
