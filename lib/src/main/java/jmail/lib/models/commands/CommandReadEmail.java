package jmail.lib.models.commands;

import jmail.lib.constants.CommandActions;
import lombok.Getter;
import lombok.Setter;

public class CommandReadEmail extends Command {

  @Getter @Setter private CommandReadEmailParameter parameter;

  public CommandReadEmail(CommandReadEmailParameter parameter) {
    super(CommandActions.READ);
    this.parameter = parameter;
  }

  public static record CommandReadEmailParameter(String emailID, Boolean setAsRead)
      implements CommandParameters {}
}
