package jmail.lib.models.commands;


import jmail.lib.constants.CommandActions;
import lombok.Getter;
import lombok.Setter;

public class CommandDeleteEmail extends Command {
    @Getter @Setter private CommandDeleteEmailParameter parameter;

    public CommandDeleteEmail(CommandDeleteEmailParameter parameter) {
        super(CommandActions.DELETE);
        this.parameter = parameter;
    }

    public static class CommandDeleteEmailParameter extends CommandParameters {
        @Getter @Setter private String id;
    }
}