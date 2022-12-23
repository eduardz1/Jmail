package jmail.client.controllers;

import java.net.URL;
import java.util.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FXMLController implements Initializable {

    private static final Logger LOGGER = LoggerFactory.getLogger(FXMLController.class.getName());
    private Long lastUnixTimeEmailCheck = 0L;

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

    @FXML private Label currentUserEmail;

    @FXML private ListView listEmails;

    @FXML private ListView listFolder;

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

        currentUserEmail
                .textProperty()
                .bind(DataModel.getInstance().getCurrentUserProperty().map(u -> u == null ? "" : u.getEmail()));

        listFolder.getItems().addAll("Inbox", "Sent", "Trash");

        listFolder.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            LOGGER.info("Selected item: {}", newValue);
            DataModel.getInstance().setCurrentFolder(newValue.toString().toLowerCase());
        });
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

    @FXML public void buttonFwdall(ActionEvent e) {
        // TODO: FwdallButton function
        // FIXME: ma come funziona il forward all?? reply all lo capisco ma forward boh
    }

    @FXML public void buttonTrash(ActionEvent e) {
        var currEmail = DataModel.getInstance().getCurrentEmail();
        if (currEmail != null && currEmail.getRead()) {
            var id = currEmail.getId();
            deleteEmail(id);
        }
    }

    public void listEmails() {
        // TODO: Schedule function each 15s

        Calendar today = Calendar.getInstance();
        today.set(Calendar.HOUR_OF_DAY, 0);

        var params = new CommandListEmail.CommandListEmailParameter(lastUnixTimeEmailCheck);
        var command = new CommandListEmail(params);

        MailClient.getInstance()
                .sendCommand(
                        command,
                        response -> {
                            if (response.getStatus().equals(ServerResponseStatuses.OK)) {
                                var resp = (ListEmailResponse) response;
                                DataModel.getInstance()
                                        .addEmail(
                                                "inbox",
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
                                LOGGER.info("Email sent: {}", response);
                            } else {
                                LOGGER.error("Error sending email: {}", response);
                            }
                        },
                        ServerResponse.class);
    }

    public void deleteEmail(String emailID) {
        // TODO: Implement hard delete, with a confirmation dialog
        var params = new CommandDeleteEmailParameter(emailID);
        var command = new CommandDeleteEmail(params);

        MailClient.getInstance()
                .sendCommand(
                        command,
                        response -> {
                            if (response.getStatus().equals(ServerResponseStatuses.OK)) {
                                DataModel.getInstance().removeCurrentEmail();
                                LOGGER.info("DeleteMailResponse: {}", response);
                            } else {
                                LOGGER.error("Error deleting email: {}", response);
                            }
                        },
                        ServerResponse.class);
    }
}
