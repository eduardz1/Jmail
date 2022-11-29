package jmail.server.models.actions;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.*;
import jmail.lib.helpers.JsonHelper;
import jmail.lib.models.Email;
import jmail.lib.models.commands.CommandListEmail;
import jmail.server.exceptions.ActionExecutionException;
import jmail.server.handlers.LockHandler;
import jmail.server.helpers.SystemIOHelper;
import lombok.Data;
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

    boolean shouldCheckUnixTime = params != null && params.getLastUnixTimeCheck() != null;
    // If shouldCheckUnixTime is false means that user needs to load all emails

    var handler = LockHandler.getInstance();
    handler.createLock(userEmail);
    handler.getReadLock(userEmail);

    var inbox = SystemIOHelper.getUserInbox(userEmail);

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

    handler.removeLock(userEmail);
    return new ActionListEmailResponse(mails);
  }

  private Long getUnixTimeFromFilename(File file) {
    var name = file.getName();
    var last_ = name.lastIndexOf("_");
    return Long.getLong(name.substring(last_ + 1));
  }

  @Data
  public class ActionListEmailResponse {
    @NonNull public ArrayList<Email> Emails;
  }
}
