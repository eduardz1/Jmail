package jmail.server.models.actions;

import com.fasterxml.jackson.core.JsonProcessingException;
import jmail.lib.helpers.JsonHelper;
import jmail.lib.models.Email;
import jmail.lib.models.commands.CommandReadEmail;
import jmail.lib.models.commands.CommandSendEmail;
import jmail.server.exceptions.ActionExecutionException;
import jmail.server.handlers.LockHandler;
import jmail.server.helpers.SystemIOHelper;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Date;
import java.util.UUID;

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

        StringBuilder errorMessage = new StringBuilder();
        errorMessage.append("Cannot send email: \n");
        var email = params.getEmail();

        var areAllRecipientsValid = email
                .recipients()
                .stream()
                .allMatch(user -> {
                    if (!SystemIOHelper.userExists(user)) {
                        errorMessage.append("User not found with email: " + user + "\n");
                        return false;
                    }
                    return true;
                });

        if (!areAllRecipientsValid) {
            throw new ActionExecutionException(errorMessage.toString());
        }

        // Lock user folder that send email
        var handler = LockHandler.getInstance();
        handler.createLock(userEmail);
        handler.getWriteLock(userEmail);

        var sent = SystemIOHelper.getUserSentDirectoryPath(userEmail);
        Path sentPath;
        String fileName;

        try {
            var jsonEmail = JsonHelper.toJson(email);
            var time = getEmailUnixTime(email);
            fileName = String.format("%s_%s.dat", UUID.randomUUID(), time);

            SystemIOHelper.writeJSONFile(sent, fileName, jsonEmail );
            sentPath = Path.of(sent + "\\" + fileName);
        } catch (IOException e) {
            handler.removeLock(userEmail);
            throw new ActionExecutionException(e, "Cannot send email: internal error");
        }

        // All recs valid, send email
        for (String user : email.recipients()) {
            handler.createLock(user);
            handler.getWriteLock(user);

            var inbox = SystemIOHelper.getUserInboxDirectoryPath(user);
            try {
                SystemIOHelper.copyFile(sentPath, Path.of(inbox + "\\" + fileName));
            } catch (IOException e) {
                throw new ActionExecutionException(e, "Cannot send email: internal error");
            }
            finally {
                handler.removeLock(user);
            }
        }

        handler.removeLock(userEmail);
    }

    private long getEmailUnixTime(Email email) {
        return email.date().toInstant().getEpochSecond();
    }
}


/**
 * var cmd = (CommandDeleteEmail)this.command;
 * var params = cmd.getParameter();
 * var userEmail = cmd.getUserEmail();
 * var emailID = params.getEmailID();
 * <p>
 * if (userEmail == null || userEmail.isEmpty()) {
 * throw new ActionExecutionException("Cannot delete mail: user invalid");
 * }
 * <p>
 * // TODO: Far vedere a edu
 * // Maybe non la migliore idea usare i lock in questo modo? Alternativie
 * // https://www.baeldung.com/java-lock-files
 * // https://blog.adamgamboa.dev/lock-mechanism-using-files-on-java/
 * <p>
 * // TODO: Si può ottimizzare per non passare dalla crazione del lock usando createLock, ma crearlo
 * // quando si usa il get write/read
 * var handler = LockHandler.getInstance();
 * handler.createLock(userEmail);
 * handler.getWriteLock(userEmail);
 * <p>
 * var inbox = SystemIOHelper.getUserInboxDirectoryPath(userEmail);
 * var deleted = SystemIOHelper.getUserDeletedDirectoryPath(userEmail);
 * <p>
 * var from = Path.of(inbox + "\\" + emailID + EMAIL_EXTENSION);
 * var to = Path.of(deleted + "\\" + emailID + EMAIL_EXTENSION);
 * <p>
 * try {
 * SystemIOHelper.moveFile(from, to);
 * } catch (IOException e) {
 * throw new ActionExecutionException(e, "Cannot delete email: internal error");
 * }
 * <p>
 * handler.removeLock(userEmail);
 */
