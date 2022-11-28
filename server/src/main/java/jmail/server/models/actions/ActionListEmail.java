package jmail.server.models.actions;

import jmail.lib.helpers.JsonHelper;
import jmail.lib.models.Email;
import jmail.lib.models.commands.CommandListEmail;
import jmail.lib.models.commands.CommandSendEmail;
import jmail.server.exceptions.ActionExecutionException;
import jmail.server.helpers.SystemIOHelper;
import lombok.Data;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.stream.Stream;

public class ActionListEmail extends ActionCommand {

    public ActionListEmail(CommandListEmail cmd) {
        super(cmd);
    }

    @Override
    public void execute() throws ActionExecutionException {

        var cmd = (CommandListEmail) this.command;
        var params = cmd.getParameter();
        var userEmail = cmd.getUserEmail();

        if (userEmail == null || userEmail.isEmpty()) {
            throw new ActionExecutionException("Cannot send mail: user invalid");
        }

        boolean shouldCheckUnixTime = params != null && params.getLastUnixTimeCheck() != null;

        var inbox = SystemIOHelper.getUserInboxDirectoryPath(userEmail);
        // If shouldCheckUnixTime is false means that user needs to load all emails

        var mails = new ArrayList<Email>();
        File[] files = (new File(inbox.toUri())).listFiles();
        for (File file : files) {
            if (!shouldCheckUnixTime || getUnixTimeFromFilename(file) > params.getLastUnixTimeCheck()) {
                try {
                    var json = SystemIOHelper.readJSONFile(Path.of(file.getPath()));
                    var mail = JsonHelper.fromJson(json, Email.class);
                    mails.add(mail);
                } catch (IOException e) {
                    throw new ActionExecutionException(e, "Cannot get emails: internal error");
                }
            }
        }
        Collections.sort(mails, Comparator.comparing(Email::date));




    }

    private Long getUnixTimeFromFilename(File file ) {
        var name = file.getName();
        var last_ = name.lastIndexOf("_");
        return Long.getLong(name.substring(last_ + 1));
    }
@Data
public class ActionListEmailResponse {
    @NonNull
    public ArrayList<Email> Emails;
}
}
