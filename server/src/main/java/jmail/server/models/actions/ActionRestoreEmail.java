package jmail.server.models.actions;

import java.io.IOException;
import jmail.lib.helpers.SystemIOHelper;
import jmail.lib.models.commands.CommandRestoreEmail;
import jmail.server.exceptions.ActionExecutionException;
import jmail.server.handlers.LockHandler;

public class ActionRestoreEmail implements ActionCommand {
  private final CommandRestoreEmail command;

  public ActionRestoreEmail(CommandRestoreEmail cmd) {
    this.command = cmd;
  }

  @Override
  public void execute() throws ActionExecutionException {

    var cmd = (CommandRestoreEmail) this.command;
    var params = cmd.getParameter();
    var userEmail = cmd.getUserEmail();
    var emailID = params.emailID();

    if (userEmail == null || userEmail.isEmpty()) {
      throw new ActionExecutionException("Cannot restore mail: user invalid");
    }

    var handler = LockHandler.getInstance();
    var lock = handler.getWriteLock(userEmail);
    try {
      lock.lock();
      SystemIOHelper.moveFile(
          SystemIOHelper.getDeletedEmailPath(userEmail, emailID),
          SystemIOHelper.getInboxEmailPath(userEmail, emailID));
    } catch (IOException e) {
      throw new ActionExecutionException(e, "Cannot restore email: internal error");
    } finally {
      lock.unlock();
      handler.removeLock(userEmail);
    }
  }
}
