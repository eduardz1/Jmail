package jmail.lib.models.commands;

import jmail.lib.constants.CommandActions;
import jmail.lib.models.Email;
import lombok.Getter;
import lombok.Setter;

public class CommandSendEmail extends Command {
  @Getter @Setter private CommandSendEmailParameter parameter;

  public CommandSendEmail(CommandSendEmailParameter parameter) {
    super(CommandActions.SEND);
    this.parameter = parameter;
  }

  public static record CommandSendEmailParameter(Email email) implements CommandParameters {}
}
