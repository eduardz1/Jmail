package jmail.server.models.actions;

import jmail.lib.models.commands.CommandListEmail;
import jmail.server.exceptions.ActionExecutionException;

public class ActionListEmail extends ActionCommand {

    public ActionListEmail(CommandListEmail cmd) {
        super(cmd);
    }

    @Override
    void Execute() throws ActionExecutionException {

    }
}