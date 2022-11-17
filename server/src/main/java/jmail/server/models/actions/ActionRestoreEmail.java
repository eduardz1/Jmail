package jmail.server.models.actions;

import jmail.lib.models.commands.CommandRestoreEmail;
import jmail.server.exceptions.ActionExecutionException;


public class ActionRestoreEmail extends ActionCommand {

    public ActionRestoreEmail(CommandRestoreEmail cmd) {
        super(cmd);
    }

    @Override
    void Execute() throws ActionExecutionException {

    }
}