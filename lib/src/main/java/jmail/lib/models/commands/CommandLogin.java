package jmail.lib.models.commands;

import jmail.lib.constants.CommandActions;
import lombok.Getter;
import lombok.Setter;

public class CommandLogin extends Command {

  @Getter @Setter private CommandLoginParameter parameter;

  public CommandLogin(CommandLoginParameter parameter) {
    super(CommandActions.LOGIN);
    this.parameter = parameter;
  }

  public record CommandLoginParameter(String email, String hashedPassword)
      implements CommandParameters {}
}
