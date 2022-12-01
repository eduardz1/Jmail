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

  @Getter
  @Setter
  public static class CommandRestoreEmailParameter extends CommandParameters {
    private String emailID = "";
  }
}
