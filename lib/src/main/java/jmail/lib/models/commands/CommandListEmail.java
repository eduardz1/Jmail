package jmail.lib.models.commands;

import jmail.lib.constants.CommandActions;
import lombok.Getter;
import lombok.Setter;

// @Data
public class CommandListEmail extends Command {
  @Getter @Setter private CommandListEmailParameter parameter;

  public CommandListEmail(CommandListEmailParameter parameter) {
    super(CommandActions.LIST);
    this.parameter = parameter;
  }

  public static class CommandListEmailParameter extends CommandParameters {
    @Getter @Setter private Long lastUnixTimeCheck;
  }
}
