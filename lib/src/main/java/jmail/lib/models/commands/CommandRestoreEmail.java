package jmail.lib.models.commands;

import jmail.lib.constants.CommandActions;

public class CommandRestoreEmail extends Command {

  public CommandRestoreEmail(CommandRestoreEmailParameter parameter) {
    super(CommandActions.RESTORE);
    super.setParameter(parameter);
  }

  @Override
  public CommandRestoreEmailParameter getParameter() {
    return (CommandRestoreEmailParameter) super.getParameter();
  }

  // FIXME: emailID settato inizialmente a "", non so se era necessario
  public record CommandRestoreEmailParameter(String emailID) implements CommandParameters {}
}
