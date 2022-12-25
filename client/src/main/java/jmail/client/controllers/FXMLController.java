package jmail.client.controllers;

import javafx.collections.ListChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
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
import jmail.lib.helpers.ValidatorHelper;
import jmail.lib.models.Email;
import jmail.lib.models.ServerResponse;
import jmail.lib.models.commands.CommandDeleteEmail;
import jmail.lib.models.commands.CommandDeleteEmail.CommandDeleteEmailParameter;
import jmail.lib.models.commands.CommandListEmail;
import jmail.lib.models.commands.CommandSendEmail;
import org.kordamp.ikonli.javafx.FontIcon;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class FXMLController implements Initializable {

    private static final Logger LOGGER = LoggerFactory.getLogger(FXMLController.class.getName());
    private static final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    private Long lastUnixTimeEmailCheck = 0L;
    private boolean editMode = false;

    @FXML private Button newMailButton;

    //  public void initializeData(MailClient client) {
    //    this.client = client;
    //  }
    @FXML private Button searchButton;

    @FXML private Button replyButton;

    @FXML private Button forwardButton;

    @FXML private Button forwardAllButton;

    @FXML private Button trashButton;

    @FXML private TextField searchField;

    @FXML private Label currentMailField;

    @FXML private Label currentUserName;
    @FXML private Label currentUserEmail;

    @FXML private ListView<Email> listEmails;

    @FXML private ListView<String> listFolder;

    public TextField subjectField;
    public TextField recipientsField;
    public TextArea bodyField;


    private final Set<String> suggestions = new HashSet<>();
    private AutoCompletionBinding<String> autoCompletionBinding;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
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
        if(avatar == null) {
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

                switch(item.toLowerCase()) {
                    case "inbox":
                        var fontIcon1 = new FontIcon("mdi2i-inbox");
                        fontIcon1.setIconColor(Paint.valueOf("#afb1b3"));
                        setGraphic(fontIcon1);
                        break;
                    case "sent":
                        var fontIcon2 = new FontIcon("mdi2e-email-send");
                        fontIcon2.setIconColor(Paint.valueOf("#afb1b3"));
                         setGraphic(fontIcon2);
                         break;
                    case "trash":
                            var fontIcon3 = new FontIcon("mdi2t-trash-can");
                            fontIcon3.setIconColor(Paint.valueOf("#afb1b3"));
                         setGraphic(fontIcon3);
                         break;
                }
                setText(item);
                setFont(Font.font("System", 16));
            }
        });

        var label = new String(DataModel.getInstance().getCurrentUser().getEmail());
        currentUserEmail.textProperty().set(label);

        listFolder.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            DataModel.getInstance().setCurrentFolder(newValue.toString().toLowerCase());
            currentUserEmail.textProperty().set(newValue + " - " + label); // FIXME does not seem to work
        });

        listEmails.setCellFactory(lv -> new ListCell<Email>() {
            @Override
            protected void updateItem(Email item, boolean empty) {
                super.updateItem(item, empty);
                setText(item == null ? null : item.getSubject() );
            }
        });

        DataModel.getInstance().getCurrentFilteredEmails().addListener((ListChangeListener<Email>) c -> {
            var emails = c.getList();
            listEmails.getItems().clear();
            listEmails.getItems().addAll(emails);
        });

        listEmails.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            DataModel.getInstance().setCurrentEmail(newValue);
            this.editMode = false;
        });


        // FIXME: Non dovrebbero andare dentro i field ma label, sistemare
        DataModel.getInstance().getCurrentEmailProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null) {
                subjectField.setText("");
                recipientsField.setText("");
                bodyField.setText("");
                return;
            }
            subjectField.setText(newValue.getSubject());
            recipientsField.setText(newValue.getRecipients().stream().reduce("", (a, b) -> a + ";" + b));
            bodyField.setText(newValue.getBody());
        });

        scheduler.scheduleAtFixedRate(() -> listEmails("inbox"), 20, 20, TimeUnit.SECONDS);

        syncronizeEmails();
    }

    private void learnWord(String trim) {
        suggestions.add(trim);
        if (autoCompletionBinding != null) {
            autoCompletionBinding.dispose();
        }
        autoCompletionBinding = TextFields.bindAutoCompletion(searchField, suggestions);

        autoCompletionBinding.getAutoCompletionPopup().prefWidthProperty().bind(searchField.widthProperty());
    }

    @FXML public void buttonNewMail(ActionEvent e) {
        this.editMode = true;
        var newEmail = new Email(
                UUID.randomUUID().toString(),
                null,
                null,
                DataModel.getInstance().getCurrentUser().getEmail(),
                List.of(),
                Calendar.getInstance().getTime(),
                false);

        DataModel.getInstance().setCurrentEmail(newEmail);
        LOGGER.info("NewMailButton: {}", newEmail);
    }

    @FXML public void buttonSearch(ActionEvent e) {
        // TODO SearchButton function
    }

    @FXML public void buttonReply(ActionEvent e) {
        this.editMode = true;

        var email = DataModel.getInstance().getCurrentEmail();
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
    }

    @FXML // FIXME: da capire come passare un Email object
    public void buttonFwd(ActionEvent e) {
        var email = DataModel.getInstance().getCurrentEmail();
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
    }

    @FXML public void buttonTrash(ActionEvent e) {
        var currEmail = DataModel.getInstance().getCurrentEmail();
        var currFolder = DataModel.getInstance().getCurrentFolder();
        Boolean hardDelete = true;
        if (currEmail != null) {

            if (this.editMode) {
                // clean draft
                DataModel.getInstance().setCurrentEmail(null);
                this.editMode = false;
            } else {
                if (currFolder.equals("inbox")) {
                    // TODO: ask user to confirm
                    hardDelete = false;
                };
                var id = currEmail.fileID();
                deleteEmail(id,currFolder,hardDelete);
            }
        }
    }

    public void listEmails(String folder) {
        Calendar today = Calendar.getInstance();
        today.set(Calendar.HOUR_OF_DAY, 0);

        var params = new CommandListEmail.CommandListEmailParameter(lastUnixTimeEmailCheck, folder);
        var command = new CommandListEmail(params);

        MailClient.getInstance()
                .sendCommand(
                        command,
                        response -> {
                            if (response.getStatus().equals(ServerResponseStatuses.OK)) {
                                var resp = (ListEmailResponse) response;
                                DataModel.getInstance()
                                        .addEmail(
                                                folder,
                                                resp.getEmails().stream().toArray(Email[]::new));
                                lastUnixTimeEmailCheck = today.getTimeInMillis();
                                LOGGER.info("Email list: {}", response);
                            } else {
                                LOGGER.error("Error getting email: {}", response);
                            }
                        },
                        ListEmailResponse.class);
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
                                DataModel.getInstance().addEmail("sent", email);
                                // When send an email, not add to inbox, but update it
                                listEmails("inbox");
                                LOGGER.info("Email sent: {}", response);
                            } else {
                                LOGGER.error("Error sending email: {}", response);
                            }
                        },
                        ServerResponse.class);
    }

    public void deleteEmail(String emailID,  String folder, Boolean hardDelete) {
        var params = new CommandDeleteEmailParameter(emailID, folder, hardDelete);
        var command = new CommandDeleteEmail(params);

        MailClient.getInstance()
                .sendCommand(
                        command,
                        response -> {
                            if (response.getStatus().equals(ServerResponseStatuses.OK)) {
                                DataModel.getInstance().addEmail("trash", DataModel.getInstance().getCurrentEmail());
                                DataModel.getInstance().removeCurrentEmail();
                                LOGGER.info("DeleteMailResponse: {}", response);
                            } else {
                                LOGGER.error("Error deleting email: {}", response);
                            }
                        },
                        ServerResponse.class);
    }

    public void buttonSend(ActionEvent actionEvent) {

        var currEmail = DataModel.getInstance().getCurrentEmail();

        if (currEmail == null) {
            LOGGER.error("No email selected");
            return;
        }

        if (!this.editMode) {
            LOGGER.error("Email not editable");
            return;
        }

        var recipients = recipientsField.getText().split(";");
        var subject = subjectField.getText();
        var body = bodyField.getText();

        currEmail.setRecipients(new ArrayList<>(Arrays.asList(recipients)));
        currEmail.setSubject(subject);
        currEmail.setBody(body);

        sendEmail(currEmail);
    }

    public void buttonReplyAll(ActionEvent actionEvent) {
    }

    public void syncronizeEmails() {
        // TODO: Gestione della sincronizzazione con la cache
        listEmails("inbox");
        listEmails("sent");
        listEmails("trash");
    }
}
