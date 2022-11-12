package jmail.server.handlers;

import java.util.Map;
import java.util.function.Supplier;
import jmail.lib.enums.Commands;

public class CommandHandler {
  private static final Commands[] COMMANDS = Commands.values(); // Cache the values() method

  /* For reference, in case we need to change signature of the methods:
   *
   *   in\out:     0            1
   *   0     |  Runnable     Consumer
   *   1     |  Supplier     Function
   */
  private final Map<Commands, Supplier<Void>> commandMap =
      Map.of(
          Commands.SEND, this::sendEmail,
          Commands.LIST, this::listEmails,
          Commands.READ, this::markEmailAsRead,
          Commands.DELETE, this::deleteEmail);

  public CommandHandler(int readInt) {
    commandMap.get(COMMANDS[readInt]).get();
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
