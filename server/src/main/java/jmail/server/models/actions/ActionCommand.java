package jmail.server.models.actions;

import jmail.lib.models.commands.Command;
import jmail.server.exceptions.ActionExecutionException;

public abstract class ActionCommand {
    final Command command;

    public ActionCommand(Command command) {
        this.command = command;
    }

    abstract void Execute() throws ActionExecutionException;
}
