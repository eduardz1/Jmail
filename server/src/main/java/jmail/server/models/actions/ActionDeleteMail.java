package jmail.server.models.actions;

import jmail.lib.models.commands.CommandDeleteEmail;
import jmail.server.exceptions.ActionExecutionException;

public class ActionDeleteMail extends ActionCommand {

    public ActionDeleteMail(CommandDeleteEmail cmd) {
        super(cmd);
    }

    @Override
    void Execute() throws ActionExecutionException {

    }
}