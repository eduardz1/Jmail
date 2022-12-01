package jmail.server.models.actions;

import jmail.lib.helpers.JsonHelper;
import jmail.lib.models.Email;
import jmail.lib.models.commands.CommandReadEmail;
import jmail.server.exceptions.ActionExecutionException;
import jmail.server.handlers.LockHandler;
import jmail.server.helpers.SystemIOHelper;

import java.io.IOException;
import java.nio.file.Files;

public class ActionReadEmail extends ActionCommand {

    public ActionReadEmail(CommandReadEmail cmd) {
        super(cmd);
    }

    @Override
    public void execute() throws ActionExecutionException {

        var cmd = (CommandReadEmail) this.command;
        var params = cmd.getParameter();
        var userEmail = cmd.getUserEmail();
        var emailID = params.getEmailID();

        if (userEmail == null || userEmail.isEmpty()) {
            throw new ActionExecutionException("Cannot read mail: user invalid");
        }

        var handler = LockHandler.getInstance();
        var lock = handler.getWriteLock(userEmail);
        try {
            var path = SystemIOHelper.getInboxEmailPath(userEmail, emailID);
            var json = SystemIOHelper.readJSONFile(path);
            var mail = JsonHelper.fromJson(json, Email.class);

            if (mail.read() != params.getSetAsRead()) {
                var email = new Email(
                        emailID,
                        mail.subject(),
                        mail.body(),
                        mail.sender(),
                        mail.recipients(),
                        mail.date(),
                        params.getSetAsRead()
                );
                Files.write(path, JsonHelper.toJson(email).getBytes());
            }

        } catch (IOException e) {
            throw new ActionExecutionException(e, "Cannot read email: internal error");
        } finally {
            lock.unlock();
            handler.removeLock(userEmail);
        }


    }
}
