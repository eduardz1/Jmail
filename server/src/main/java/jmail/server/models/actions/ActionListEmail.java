package jmail.server.models.actions;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import jmail.lib.helpers.JsonHelper;
import jmail.lib.models.Email;
import jmail.lib.models.Response;
import jmail.lib.models.commands.CommandListEmail;
import jmail.server.exceptions.ActionExecutionException;
import jmail.server.handlers.LockHandler;
import jmail.server.helpers.SystemIOHelper;
import lombok.NonNull;

public class ActionListEmail implements ActionCommand {
  private final CommandListEmail command;

  public ActionListEmail(CommandListEmail cmd) {
    this.command = cmd;
  }

  @Override
  public ActionListEmailResponse executeAndGetResult() throws ActionExecutionException {
    var cmd = (CommandListEmail) this.command;
    var params = cmd.getParameter();
    var userEmail = cmd.getUserEmail();

    if (userEmail == null || userEmail.isEmpty()) {
      throw new ActionExecutionException("Cannot send mail: user invalid");
    }

    boolean shouldCheckUnixTime = params != null && params.lastUnixTimeCheck() != null;
    // If shouldCheckUnixTime is false means that user needs to load all emails

    var handler = LockHandler.getInstance();
    var userLock = handler.getReadLock(userEmail);

    var inbox = SystemIOHelper.getUserInbox(userEmail);

    var mails = new ArrayList<Email>();
    File[] files = (new File(inbox.toUri())).listFiles();
    files = files == null ? new File[] {} : files;
    for (File file : files) {
      if (!shouldCheckUnixTime || getUnixTimeFromFilename(file) > params.lastUnixTimeCheck()) {
        try {
          var json = SystemIOHelper.readJSONFile(Path.of(file.getPath()));
          var mail = JsonHelper.fromJson(json, Email.class);
          mails.add(mail);
        } catch (IOException e) {
          userLock.unlock();
          handler.removeLock(userEmail); // Release lock before throwing exception
          throw new ActionExecutionException(e, "Cannot get emails: internal error");
        }
      }
    }
    mails.sort(Comparator.comparing(Email::date));

    // FIXME: Non mi piace per niente questo metodo qua
    // è poco leggibile per chi non conosce bene gli stream
    // controllare params e lastunixtime di params ogni volta è meno performante del check di una
    // variabile
    // Usare il map con una lambda in questo caso non mi piace,
    //  la lambda nasconde l'eccezione corretta, e la logica di rimozione del lock
    // Lo trovo poco utile in generale complicarci la vita per usare gli stream, non ci porta
    // vantaggio
    // lascio commentato ma andrà rimosso

    // NB: il lock in lettura ha senso, se tagghi una mail come letta mentre in parallelo
    // viene mandata una richiesta di cancellazione va in conflitto

    //     var mails =
    //        Arrays.stream(files)
    //            .parallel()
    //            .filter(
    //                f ->
    //                    params == null
    //                        || params.getLastUnixTimeCheck() == null
    //                        || getUnixTimeFromFilename(f) > params.getLastUnixTimeCheck())
    //            .map(this::getEmailFromFile)
    //            .sorted(Comparator.comparing(Email::date))
    //            .collect(Collectors.toList());

    userLock.unlock();
    handler.removeLock(userEmail);
    return new ActionListEmailResponse(mails);
  }

  private Long getUnixTimeFromFilename(File file) {
    var name = file.getName();
    var last_ = name.lastIndexOf("_");
    return Long.getLong(name.substring(last_ + 1));
  }

  public record ActionListEmailResponse(@NonNull List<Email> emails) implements Response {}
}
