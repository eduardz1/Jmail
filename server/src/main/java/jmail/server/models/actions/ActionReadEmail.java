package jmail.server.models.actions;

import jmail.lib.models.commands.CommandReadEmail;
import jmail.server.exceptions.ActionExecutionException;

public class ActionReadEmail extends ActionCommand {

    public ActionReadEmail(CommandReadEmail cmd) {
        super(cmd);
    }

    @Override
    void Execute() throws ActionExecutionException {

    }
}