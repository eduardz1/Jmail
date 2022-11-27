package jmail.server.models.actions;

import jmail.lib.models.commands.CommandDeleteEmail;
import jmail.server.exceptions.ActionExecutionException;
import jmail.server.handlers.LockHandler;
import jmail.server.helpers.SystemIOHelper;

import java.io.IOException;
import java.nio.file.Path;

public class ActionDeleteMail extends ActionCommand {

  private static final String EMAIL_EXTENSION  = ".dat";
  public ActionDeleteMail(CommandDeleteEmail cmd) {
    super(cmd);
  }

  @Override
  public void execute() throws ActionExecutionException {

    var cmd = (CommandDeleteEmail)this.command;
    var params = cmd.getParameter();
    var userEmail = cmd.getUserEmail();
    var emailID = params.getEmailID();

    if (userEmail == null || userEmail.isEmpty()) {
      throw new ActionExecutionException("Cannot delete mail: user invalid");
    }

    // TODO: Far vedere a edu
    // Maybe non la migliore idea usare i lock in questo modo? Alternativie
    // https://www.baeldung.com/java-lock-files
    // https://blog.adamgamboa.dev/lock-mechanism-using-files-on-java/

    // TODO: Si può ottimizzare per non passare dalla crazione del lock usando createLock, ma crearlo
    // quando si usa il get write/read
    var handler = LockHandler.getInstance();
    handler.createLock(userEmail);
    handler.getWriteLock(userEmail);

    var inbox = SystemIOHelper.getUserInboxDirectoryPath(userEmail);
    var deleted = SystemIOHelper.getUserDeletedDirectoryPath(userEmail);

    var from = Path.of(inbox + "\\" + emailID + EMAIL_EXTENSION);
    var to = Path.of(deleted + "\\" + emailID + EMAIL_EXTENSION);

    try {
      SystemIOHelper.moveFile(from, to);
    } catch (IOException e) {
      throw new ActionExecutionException(e, "Cannot delete email: internal error");
    }

    handler.removeLock(userEmail);
  }
}
