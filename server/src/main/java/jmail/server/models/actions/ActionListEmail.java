package jmail.server.models.actions;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;
import jmail.lib.helpers.JsonHelper;
import jmail.lib.models.Email;
import jmail.lib.models.commands.CommandListEmail;
import jmail.server.exceptions.ActionExecutionException;
import jmail.server.handlers.LockHandler;
import jmail.server.helpers.SystemIOHelper;
import lombok.NonNull;

public class ActionListEmail extends ActionCommand {

  public ActionListEmail(CommandListEmail cmd) {
    super(cmd);
  }

  @Override
  public void execute() throws ActionExecutionException {}

  @Override
  public ActionListEmailResponse executeAndGetResult() throws ActionExecutionException {
    var cmd = (CommandListEmail) this.command;
    var params = cmd.getParameter();
    var userEmail = cmd.getUserEmail();

    if (userEmail == null || userEmail.isEmpty()) {
      throw new ActionExecutionException("Cannot send mail: user invalid");
    }

    var handler = LockHandler.getInstance();
    handler.createLock(userEmail); // FIXME:ok ma ci serve davvero un lock per una read?
    handler.getReadLock(userEmail);

    var inbox = SystemIOHelper.getUserInbox(userEmail);

    var files = (new File(inbox.toUri())).listFiles();
    if (files == null) {
      handler.removeLock(userEmail);
      return new ActionListEmailResponse(new ArrayList<Email>());
    }

    var mails =
        Arrays.stream(files)
            .parallel()
            .filter(
                f ->
                    params == null
                        || params.getLastUnixTimeCheck() == null
                        || getUnixTimeFromFilename(f) > params.getLastUnixTimeCheck())
            .map(this::getEmailFromFile)
            .sorted(Comparator.comparing(Email::date))
            .collect(Collectors.toList());

    handler.removeLock(userEmail);
    return new ActionListEmailResponse(mails);
  }

  private Email getEmailFromFile(File f) {
    try {
      return JsonHelper.fromJson(SystemIOHelper.readJSONFile(Path.of(f.getPath())), Email.class);
    } catch (IOException e) {
      throw new RuntimeException(
          "Cannot get emails: internal error"); // FIXME: logic behind excpetions here
    }
  }

  private Long getUnixTimeFromFilename(File file) {
    var name = file.getName();
    var last_ = name.lastIndexOf("_");
    return Long.getLong(name.substring(last_ + 1));
  }

  public record ActionListEmailResponse(@NonNull List<Email> emails) implements Response {}
}
