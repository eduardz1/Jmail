package jmail.lib.models.commands;

import jmail.lib.constants.CommandActions;
import jmail.lib.models.Email;
import lombok.Getter;
import lombok.Setter;

public class CommandSendEmail extends Command {
    private final CommandSendEmailParameter parameter;

    public CommandSendEmail(CommandSendEmailParameter parameter) {
        super(CommandActions.RESTORE);
        this.parameter = parameter;
    }

    @Getter @Setter
    public static class CommandSendEmailParameter extends CommandParameters {
        private Email email;
    }
}
