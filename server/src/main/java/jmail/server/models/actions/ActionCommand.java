package jmail.server.models.actions;

import jmail.lib.models.Response;
import jmail.server.exceptions.ActionExecutionException;

public interface ActionCommand {
  default void execute() throws ActionExecutionException {
    throw new ActionExecutionException("Method not implemented");
  }

  default Response executeAndGetResult() throws ActionExecutionException {
    throw new ActionExecutionException("Method not implemented");
  }

  // TODO: Response was here before but can't be accessed by client if left here, instead I placed
  // it in lib.models
}
