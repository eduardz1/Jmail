package jmail.server.models.actions;

import jmail.server.exceptions.ActionExecutionException;

public interface ActionCommand {
  default void execute() throws ActionExecutionException {
    throw new ActionExecutionException("Method not implemented");
  }

  default Response executeAndGetResult() throws ActionExecutionException {
    throw new ActionExecutionException("Method not implemented");
  }

  /** Marker interface */
  public interface Response {}
}
