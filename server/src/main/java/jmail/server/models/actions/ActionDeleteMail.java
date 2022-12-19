package jmail.server.models.actions;

import java.io.IOException;

import jmail.lib.constants.ServerResponseStatuses;
import jmail.lib.helpers.SystemIOHelper;
import jmail.lib.models.ServerResponse;
import jmail.lib.models.User;
import jmail.lib.models.commands.CommandDeleteEmail;
import jmail.server.exceptions.ActionExecutionException;
import jmail.server.handlers.LockHandler;
import lombok.Getter;
import lombok.Setter;

public class ActionDeleteMail implements ActionCommand {
  private final CommandDeleteEmail command;

  public ActionDeleteMail(CommandDeleteEmail cmd) {
    this.command = cmd;
  }

  @Override
  public void execute() throws ActionExecutionException {
    var userEmail = command.getUserEmail();
    var emailID = command.getParameter().emailID();

    if (userEmail == null || userEmail.isEmpty()) {
      throw new ActionExecutionException("Cannot delete mail: user invalid");
    }
    var handler = LockHandler.getInstance();
    var lock = handler.getWriteLock(userEmail);
    try {
      lock.lock();
      SystemIOHelper.moveFile(
          SystemIOHelper.getInboxEmailPath(userEmail, emailID),
          SystemIOHelper.getDeletedEmailPath(userEmail, emailID));
    } catch (IOException e) {
      throw new ActionExecutionException(e, "Cannot delete email: internal error");
    } finally {
      lock.unlock();
      handler.removeLock(userEmail);
    }
  }

  @Getter
  @Setter
  public class ActionDeleteMailServerResponse extends ServerResponse {

    public ActionDeleteMailServerResponse(User user) {
      super(ServerResponseStatuses.OK, "Mail deleted");
    }
  }
}
