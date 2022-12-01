package jmail.server.models.actions;

import java.io.IOException;
import java.nio.file.Path;
import jmail.lib.helpers.JsonHelper;
import jmail.lib.models.commands.CommandSendEmail;
import jmail.server.exceptions.ActionExecutionException;
import jmail.server.handlers.LockHandler;
import jmail.server.helpers.SystemIOHelper;

public class ActionSendEmail extends ActionCommand {
  public ActionSendEmail(CommandSendEmail cmd) {
    super(cmd);
  }

  @Override
  public void execute() throws ActionExecutionException {

    var cmd = (CommandSendEmail) this.command;
    var params = cmd.getParameter();
    var userEmail = cmd.getUserEmail();

    if (userEmail == null || userEmail.isEmpty()) {
      throw new ActionExecutionException("Cannot send mail: user invalid");
    }

    // Check recipients and compose errorMessage, if some error will occure
    StringBuilder errorMessage = new StringBuilder();
    errorMessage.append("Cannot send email: \n");
    var email = params.getEmail();

    var areAllRecipientsValid =
        email.recipients().stream()
            .allMatch(
                user -> {
                  if (!SystemIOHelper.userExists(user)) {
                    errorMessage.append("User not found with email: ").append(user).append("\n");
                    return false;
                  }
                  return true;
                });

    if (!areAllRecipientsValid) {
      throw new ActionExecutionException(errorMessage.toString());
    }

    // Lock user folder that send email
    var handler = LockHandler.getInstance();
    var userLock = handler.getWriteLock(userEmail);

    var sent = SystemIOHelper.getUserSent(userEmail);
    Path sentPath;
    var fileName = email.getFileID();

    try {
      userLock.lock();
      var jsonEmail = JsonHelper.toJson(email);
      SystemIOHelper.writeJSONFile(sent, fileName, jsonEmail);
      sentPath = SystemIOHelper.getSentEmailPath(userEmail, fileName);
    } catch (IOException e) {
      userLock.unlock();
      handler.removeLock(userEmail);
      throw new ActionExecutionException(e, "Cannot send email: internal error");
    }

    // All recs valid, send email
    for (String receiver : email.recipients()) {
      var receiverLock = handler.getWriteLock(receiver);
      try {
        receiverLock.lock();
        SystemIOHelper.copyFile(
                sentPath,
                SystemIOHelper.getInboxEmailPath(receiver, fileName)
        );
      } catch (IOException e) {
        throw new ActionExecutionException(e, "Cannot send email: internal error");
      } finally {
        receiverLock.unlock();
        handler.removeLock(receiver);
      }
    }

    // Release lock if no errors occured
    userLock.unlock();
    handler.removeLock(userEmail);
  }
}
