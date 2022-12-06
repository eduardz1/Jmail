package jmail.server.models.actions;

import jmail.lib.models.ServerResponseBody;
import jmail.server.exceptions.ActionExecutionException;

public interface ActionCommand {
  default void execute() throws ActionExecutionException {
    throw new ActionExecutionException("Method not implemented");
  }

  default ServerResponseBody executeAndGetResult() throws ActionExecutionException {
    throw new ActionExecutionException("Method not implemented");
  }
}
