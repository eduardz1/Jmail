package jmail.client.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import javafx.application.Platform;
import javafx.collections.ListChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import jmail.client.models.client.MailClient;
import jmail.client.models.model.DataModel;
import jmail.client.models.responses.ListEmailResponse;
import jmail.lib.autocompletion.textfield.AutoCompletionBinding;
import jmail.lib.autocompletion.textfield.TextFields;
import jmail.lib.constants.ServerResponseStatuses;
import jmail.lib.handlers.LockHandler;
import jmail.lib.helpers.JsonHelper;
import jmail.lib.helpers.SystemIOHelper;
import jmail.lib.helpers.ValidatorHelper;
import jmail.lib.models.Email;
import jmail.lib.models.ServerResponse;
import jmail.lib.models.commands.CommandDeleteEmail;
import jmail.lib.models.commands.CommandDeleteEmail.CommandDeleteEmailParameter;
import jmail.lib.models.commands.CommandListEmail;
import jmail.lib.models.commands.CommandReadEmail;
import jmail.lib.models.commands.CommandSendEmail;
import org.kordamp.ikonli.javafx.FontIcon;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class FXMLController {

  private static final Logger LOGGER = LoggerFactory.getLogger(FXMLController.class.getName());
  private static final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
  private Long lastUnixTimeEmailCheck = 0L;
  private boolean editMode = false;

  @FXML
  private Button newMailButton;

  @FXML
  private Label connLbl;

  @FXML
  private Button searchButton;

  @FXML
  private Button replyButton;

  @FXML
  private Button forwardButton;

  @FXML
  private Button forwardAllButton;

  @FXML
  private Button trashButton;

  @FXML
  private TextField searchField;

  @FXML
  private Label currentMailField;

  @FXML
  private Label currentUserName;

  @FXML
  private Label currentUserEmail;

  @FXML
  private ListView<Email> listEmails;

  @FXML
  private ListView<String> listFolder;

  public TextField subjectField;
  public TextField recipientsField;
  public TextArea bodyField;

    private final Set<String> suggestions = new HashSet<>();
    private transient AutoCompletionBinding<String> autoCompletionBinding;

    Map<String, Path> paths = new HashMap<>();

  // TODO: Qualcosa viene aggiornata e causa un eccezione sul thread UI, ma non ho capito cosa, fixare

  public void initialize() {
    autoCompletionBinding = TextFields.bindAutoCompletion(searchField, suggestions);

    searchField.setOnKeyPressed(event -> {
      if (event.getCode() == javafx.scene.input.KeyCode.ENTER) {
        LOGGER.info("SearchField: {}", searchField.getText().trim());
        // buttonSearch(e); TODO: implement
        learnWord(searchField.getText().trim());
      }
    });

    currentMailField
            .textProperty()
            .bind(DataModel.getInstance().getCurrentEmailProperty().map(e -> e == null ? "" : e.getSubject()));

    currentUserName
            .textProperty()
            .bind(DataModel.getInstance().getCurrentUserProperty().map(u -> u == null ? "" : u.getName()));

    var avatar = DataModel.getInstance().getCurrentUserProperty().getValue().getAvatar();
    Node graphic;
    if (avatar == null) {
      var fontIcon = new FontIcon("mdi2a-account");
      fontIcon.setIconColor(Paint.valueOf("#afb1b3"));
      graphic = fontIcon;
    } else {
      graphic = new ImageView(avatar);
    }
    currentUserName.setGraphic(graphic);

    // Should all done in subcontroller o non so dove
    listFolder.getItems().addAll("Inbox", "Sent", "Trash");
    listFolder.setCellFactory(param -> new ListCell<>() {
      @Override
      protected void updateItem(String item, boolean empty) {
        super.updateItem(item, empty);
        if (empty || item == null) {
          setText(null);
          return;
        }

        var fontIcon = new FontIcon(
                switch (item.toLowerCase()) {
                  case "inbox" -> "mdi2i-inbox";
                  case "sent" -> "mdi2e-email-send";
                  case "trash" -> "mdi2t-trash-can";
                  default -> "mdi2a-folder";
                });
        fontIcon.setIconColor(Paint.valueOf("#afb1b3"));
        setGraphic(fontIcon);
        setText(item);
        setFont(Font.font("System", 16));
      }
    });

    var label = DataModel.getInstance().getCurrentUser().getEmail();
    currentUserEmail.textProperty().set(label);

    listFolder.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
      DataModel.getInstance().setCurrentFolder(newValue.toLowerCase());
      currentUserEmail.textProperty().set(newValue + " - " + label); // FIXME does not seem to work
    });

    listEmails.setCellFactory(lv -> new ListCell<>() {
      @Override
      protected void updateItem(Email item, boolean empty) {
        super.updateItem(item, empty);

        // FIXME: Fare grafica decente
        if (empty || item == null) {
          setText(null);
          return;
        }
        setText((item.getRead() ? "read" : "unread") + " - " + item.getSubject());
      }
    });

    DataModel.getInstance().getCurrentFilteredEmails().addListener((ListChangeListener<Email>)
            c -> {
              Platform.runLater(() -> {
                listEmails.getItems().clear();
                var list = c.getList();
                listEmails.getItems().addAll(list);

                if (!list.isEmpty()) {
                  DataModel.getInstance().getCurrentEmail().ifPresentOrElse((email) -> {
                    for (int i = 0; i < list.size(); i++) {
                      if (list.get(i).getId().equals(email.getId())) {
                        listEmails.getSelectionModel().select(i);
                        break;
                      }
                    }
                  }, () -> list.get(0).getId());
                }
              });
            });

    listEmails.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
      DataModel.getInstance().setCurrentEmail(newValue);
      readEmail(newValue);
      this.editMode = false;
    });

    // FIXME: Non dovrebbero andare dentro i field ma label, sistemare
    DataModel.getInstance()
            .getCurrentEmailProperty()
            .addListener((observable, oldValue, newValue) -> Platform.runLater(() -> {
              if (newValue == null) {
                subjectField.setText("");
                recipientsField.setText("");
                bodyField.setText("");
              } else {
                subjectField.setText(newValue.getSubject());
                recipientsField.setText(newValue.getRecipients().stream()
                        .reduce("", (a, b) -> a + ";" + b)); // FIXME: non popola correttamente
                bodyField.setText(newValue.getBody());
              }
            }));

    DataModel.getInstance()
            .isServerStatusConnected()
            .addListener((observable, oldValue, newValue) -> Platform.runLater(() -> {
              connLbl.setText(newValue ? "Connected" : "Disconnected"); // TODO: Bel pallino verde rosso
            }));

    DataModel.getInstance().getCurrentUserProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null) {
                throw new IllegalStateException("Current user cannot be null");
            }
            paths.put("inbox", SystemIOHelper.getUserInbox(newValue.getEmail()));
            paths.put("sent", SystemIOHelper.getUserSent(newValue.getEmail()));
            paths.put("trash", SystemIOHelper.getUserDeleted(newValue.getEmail()));
        });

        synchronizeEmails();
  }

  private void readEmail(Email email) {
    if (email == null) return;
    if (email.getRead()) return;
    if (!DataModel.getInstance().getCurrentFolder().equals("inbox")) return;

    var params = new CommandReadEmail.CommandReadEmailParameter(email.fileID(), true);
    var cmd = new CommandReadEmail(params);

    MailClient.getInstance()
            .sendCommand(
                    cmd,
                    response -> {
                      if (response.getStatus().equals(ServerResponseStatuses.OK)) {
                        LOGGER.info("Email {} marked as read", email.fileID());
                        email.setRead(true);
                        DataModel.getInstance().syncFilteredEmails();

                        // Update localCache
                        var path = SystemIOHelper.getUserInbox(DataModel.getInstance().getCurrentUser().getEmail());
                        try {
                          SystemIOHelper.writeJSONFile(path, email.fileID(), JsonHelper.toJson(email));
                        } catch (IOException e) {
                          e.printStackTrace();
                        }

                      } else {
                        LOGGER.error("Error marking email {} as read", email.fileID());
                      }
                    },
                    ServerResponse.class);
  }

  private void learnWord(String trim) {
    suggestions.add(trim);
    if (autoCompletionBinding != null) {
      autoCompletionBinding.dispose();
    }
    autoCompletionBinding = TextFields.bindAutoCompletion(searchField, suggestions);

    autoCompletionBinding.getAutoCompletionPopup().prefWidthProperty().bind(searchField.widthProperty());
  }

  @FXML
  public void buttonNewMail(ActionEvent e) {
    this.editMode = true;

    var newEmail = new Email(
            UUID.randomUUID().toString(),
            "1", // FIXME: remove
            "bla bla bla", // FIXME: remove
            DataModel.getInstance().getCurrentUser().getEmail(),
            List.of("emmedeveloper@gmail.com"), // FIXME: remove
            Calendar.getInstance().getTime(),
            false);

    DataModel.getInstance().setCurrentEmail(newEmail);
    LOGGER.info("NewMailButton: {}", newEmail);
  }

  @FXML
  public void buttonSearch(ActionEvent e) {
    // TODO SearchButton function
  }

  @FXML
  public void buttonReply(ActionEvent e) {
    DataModel.getInstance()
            .getCurrentEmail()
            .ifPresentOrElse(
                    email -> {
                      this.editMode = true;
                      Calendar today = Calendar.getInstance();
                      today.set(Calendar.HOUR_OF_DAY, 0);

                      var newEmail = new Email(
                              UUID.randomUUID().toString(),
                              email.getSubject(),
                              email.getBody(),
                              DataModel.getInstance().getCurrentUser().getEmail(),
                              List.of(email.getSender()),
                              today.getTime(),
                              false);

                      DataModel.getInstance().setCurrentEmail(newEmail);
                      LOGGER.info("ReplyButton: {}", newEmail);
                    },
                    () -> LOGGER.info("ReplyButton: No email selected"));
  }

  @FXML // FIXME: da capire come passare un Email object
  public void buttonFwd(ActionEvent e) {
    DataModel.getInstance()
            .getCurrentEmail()
            .ifPresentOrElse(
                    email -> {
                      var newRecipients = email.getRecipients();
                      Calendar today = Calendar.getInstance();
                      today.set(Calendar.HOUR_OF_DAY, 0);

                      var newEmail = new Email(
                              UUID.randomUUID().toString(),
                              email.getSubject(),
                              email.getBody(),
                              DataModel.getInstance().getCurrentUser().getEmail(),
                              newRecipients,
                              today.getTime(),
                              false);

                      DataModel.getInstance().setCurrentEmail(newEmail);
                      LOGGER.info("FwdButton: {}", newEmail);
                    },
                    () -> LOGGER.info("No email selected"));
  }

  @FXML
  public void buttonTrash(ActionEvent e) {
    DataModel.getInstance()
            .getCurrentEmail()
            .ifPresentOrElse(
                    email -> {
                      var currFolder = DataModel.getInstance().getCurrentFolder();

                      if (this.editMode) { // clean draft
                        DataModel.getInstance().setCurrentEmail(null);
                        this.editMode = false;
                      } else {
                        boolean hardDelete = !currFolder.equals("inbox");
                        // TODO: ask user to confirmation
                        deleteEmail(email.fileID(), currFolder, hardDelete);
                      }
                    },
                    () -> LOGGER.info("TrashButton: no email selected"));
  }

  public void listEmails(String folder) {

    // Prevents concurrent write of new email to the list
    var lock = LockHandler.getInstance().getWriteLock(folder);
    lock.lock();

    var params = new CommandListEmail.CommandListEmailParameter(lastUnixTimeEmailCheck, folder);
    var command = new CommandListEmail(params);

    MailClient.getInstance()
            .sendCommand(
                    command,
                    response -> {
                      if (response.getStatus().equals(ServerResponseStatuses.OK)) {
                        var resp = (ListEmailResponse) response;

                        if (resp.getEmails().isEmpty()) return;

                        // Already sorted by date in the server
                        var emails = resp.getEmails().toArray(Email[]::new);
                        System.out.println("sono arrivate email");
                        addEmail(folder, emails);

                        // Aggiorniamo lastUnixTimeEmailCheck con la data più recente tra le email ricevute
                        // Non utilizziamo il tempo corrente per evitare problemi di sincronizzazione:
                        // se viene inviata una email tra il momento in cui viene richiesta la lista di email
                        // e il momento in cui viene ricevuta la risposta, questa email non verrà mai ricevuta
                        // in quanto avrà la data nel momento in cui viene spedita
                        lastUnixTimeEmailCheck = Arrays.stream(emails)
                                .findFirst()
                                .map(Email::getDate)
                                .map(Date::toInstant)
                                .map(Instant::getEpochSecond)
                                .orElse(lastUnixTimeEmailCheck);

                        System.out.println(lastUnixTimeEmailCheck);
                        LOGGER.info("Email list: {}", response);
                      } else {
                        LOGGER.error("Error getting email: {}", response);
                      }
                    },
                    ListEmailResponse.class);

    lock.unlock();
    LockHandler.getInstance().getReadLock(folder);
  }

  public void sendEmail(Email email) {

    var valid = ValidatorHelper.isEmailValid(email);
    if (!valid.getKey()) {
      LOGGER.error("Email not valid: {}", valid.getValue());
      return;
    }

    var params = new CommandSendEmail.CommandSendEmailParameter(email);
    var command = new CommandSendEmail(params);

    MailClient.getInstance()
            .sendCommand(
                    command,
                    response -> {
                      if (response.getStatus().equals(ServerResponseStatuses.OK)) {
                        LOGGER.info("Email sent: {}", response);
                        addEmail("sent", email);
                        // When send an email, not add to inbox, but update it
                        listEmails("inbox");
                      } else {
                        LOGGER.error("Error sending email: {}", response);
                      }
                    },
                    ServerResponse.class);
  }

  public void deleteEmail(String emailID, String folder, Boolean hardDelete) {
    var params = new CommandDeleteEmailParameter(emailID, folder, hardDelete);
    var command = new CommandDeleteEmail(params);

    MailClient.getInstance()
            .sendCommand(
                    command,
                    response -> {
                      if (response.getStatus().equals(ServerResponseStatuses.OK)) {
                        if (!hardDelete) {
                          addEmail(
                                  "trash",
                                  DataModel.getInstance()
                                          .getCurrentEmail()
                                          .orElseThrow());
                        }
                        DataModel.getInstance().removeCurrentEmail();
                        try {
                          SystemIOHelper.deleteFile(SystemIOHelper.getInboxEmailPath(command.getUserEmail(), params.emailID()));
                        } catch (IOException e) {
                          e.printStackTrace();
                        }
                        LOGGER.info("DeleteMailResponse: {}", response);
                      } else {
                        LOGGER.error("Error deleting email: {}", response);
                      }
                    },
                    ServerResponse.class);
  }

  public void buttonSend() {
    DataModel.getInstance()
            .getCurrentEmail()
            .ifPresentOrElse(
                    email -> {
                      if (!this.editMode) {
                        LOGGER.error("Email not editable");
                        return;
                      }

                      Calendar today = Calendar.getInstance();
                      today.set(Calendar.HOUR_OF_DAY, 0);

                      // Create a new email in order to add a new Object without reference to list
                      var newEmail = new Email(
                              UUID.randomUUID().toString(),
                              subjectField.getText(),
                              bodyField.getText(),
                              email.getSender(),
                              new ArrayList<>(Arrays.asList(
                                      recipientsField.getText().split(";"))),
                              today.getTime(),
                              false);

                      sendEmail(newEmail);
                      DataModel.getInstance().setCurrentEmail(null);
                      this.editMode = false;
                    },
                    () -> LOGGER.error("No email to send"));
  }

  public void buttonReplyAll(ActionEvent actionEvent) {
  }

    public void synchronizeEmails() {
        // TODO: ? (già fatto no?) Sincronizzazione email con cache locale al login

        for (String folder : paths.keySet()) {
            var mails = new ArrayList<Email>();
            var files = (new File(paths.get(folder).toUri())).listFiles();
            if (files == null) {
                continue;
            }

            for (File file : files) {
                try {
                    var json = SystemIOHelper.readJSONFile(Path.of(file.getPath()));
                    var mail = JsonHelper.fromJson(json, Email.class);
                    lastUnixTimeEmailCheck =
                            Math.max(mail.getDate().toInstant().getEpochSecond(), lastUnixTimeEmailCheck);
                    mails.add(mail);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            mails.sort(Comparator.comparing(Email::getDate).reversed());
            DataModel.getInstance().addEmail(folder, mails.toArray(Email[]::new));
        }

        listEmails("inbox");
        listEmails("sent");
        listEmails("trash");
        // Make sure that the lastUnixTimeEmailCheck is updated, so scheduler start after first check
//    scheduler.scheduleAtFixedRate(() -> listEmails("inbox"), 20, 20, TimeUnit.SECONDS);
    }

    public void addEmail(String folder, Email... emails) {

    for (Email email : emails) {
      try {
        SystemIOHelper.writeJSONFile(paths.get(folder), email.fileID(), JsonHelper.toJson(email));
      } catch (JsonProcessingException e) {
        throw new RuntimeException(e);
      } catch (IOException e) {
        e.printStackTrace();
      }
    }

    DataModel.getInstance().addEmail(folder, emails);
  }
}
