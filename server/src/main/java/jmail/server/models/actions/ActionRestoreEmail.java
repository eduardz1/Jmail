package jmail.server.models.actions;

import jmail.lib.models.commands.CommandRestoreEmail;
import jmail.server.exceptions.ActionExecutionException;
import jmail.server.handlers.LockHandler;
import jmail.server.helpers.SystemIOHelper;

import java.io.IOException;

public class ActionRestoreEmail extends ActionCommand {

  public ActionRestoreEmail(CommandRestoreEmail cmd) {
    super(cmd);
  }

  @Override
  public void execute() throws ActionExecutionException {

    var cmd = (CommandRestoreEmail) this.command;
    var params = cmd.getParameter();
    var userEmail = cmd.getUserEmail();
    var emailID = params.getEmailID();

    if (userEmail == null || userEmail.isEmpty()) {
      throw new ActionExecutionException("Cannot restore mail: user invalid");
    }

    var handler = LockHandler.getInstance();
    var lock = handler.getWriteLock(userEmail);
    try {
      lock.lock();
      SystemIOHelper.moveFile(
              SystemIOHelper.getDeletedEmailPath(userEmail, emailID),
              SystemIOHelper.getInboxEmailPath(userEmail, emailID)
      );
    } catch (IOException e) {
      throw new ActionExecutionException(e, "Cannot restore email: internal error");
    } finally {
      lock.unlock();
      handler.removeLock(userEmail);
    }


  }
}
