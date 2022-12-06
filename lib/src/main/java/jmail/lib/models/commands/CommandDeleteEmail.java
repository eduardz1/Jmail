package jmail.lib.models.commands;

import jmail.lib.constants.CommandActions;

public class CommandDeleteEmail extends Command {

  public CommandDeleteEmail(CommandDeleteEmailParameter parameter) {
    super(CommandActions.DELETE);
    super.setParameter(parameter);
  }

  @Override
  public CommandDeleteEmailParameter getParameter() {
    return (CommandDeleteEmailParameter) super.getParameter();
  }

  public static record CommandDeleteEmailParameter(String emailID) implements CommandParameters {}
}
