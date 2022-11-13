package jmail.lib.models.commands;


import jmail.lib.constants.CommandActions;

public class CommandDeleteEmail extends Command {
    private final CommandDeleteEmailParameter parameter;

    public CommandDeleteEmail(CommandDeleteEmailParameter parameter) {
        super(CommandActions.DELETE);
        this.parameter = parameter;
    }

    public static class CommandDeleteEmailParameter extends CommandParameters {
        public String id;
    }
}