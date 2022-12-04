package jmail.lib.models.commands;

import jmail.lib.constants.CommandActions;
import lombok.Getter;
import lombok.Setter;

public class CommandRestoreEmail extends Command {
  @Getter @Setter private CommandRestoreEmailParameter parameter;

  public CommandRestoreEmail(CommandRestoreEmailParameter parameter) {
    super(CommandActions.RESTORE);
    this.parameter = parameter;
  }

  // FIXME: emailID settato inizialmente a "", non so se era necessario
  public static record CommandRestoreEmailParameter(String emailID) implements CommandParameters {}
}
