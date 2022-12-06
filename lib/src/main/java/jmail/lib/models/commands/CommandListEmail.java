package jmail.lib.models.commands;

import jmail.lib.constants.CommandActions;
import lombok.Getter;
import lombok.Setter;

// @Data
public class CommandListEmail extends Command {
  @Getter @Setter private CommandListEmailParameter parameter;

  public CommandListEmail() {
    super(CommandActions.LIST);
  }

  public CommandListEmail(CommandListEmailParameter parameter) {
    super(CommandActions.LIST);
    this.parameter = parameter;
  }

  public record CommandListEmailParameter(Long lastUnixTimeCheck)
      implements CommandParameters {}
}
