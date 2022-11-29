package jmail.server.models.actions;

import jmail.lib.models.commands.Command;
import jmail.server.exceptions.ActionExecutionException;

public abstract class ActionCommand {
  final Command command;

  public ActionCommand(Command command) {
    this.command = command;
  }

  public abstract void execute() throws ActionExecutionException;

  public <T> T executeAndGetResult() throws ActionExecutionException {
    throw new ActionExecutionException("Method not implemented");
  }
}
