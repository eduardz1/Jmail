package jmail.server.models.actions;

import jmail.lib.models.commands.CommandSendEmail;
import jmail.server.exceptions.ActionExecutionException;

public class ActionSendEmail extends ActionCommand {
    public ActionSendEmail(CommandSendEmail cmd) {
        super(cmd);
    }

    @Override
    void Execute() throws ActionExecutionException {

    }
}