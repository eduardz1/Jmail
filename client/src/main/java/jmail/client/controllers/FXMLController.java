package jmail.client.controllers;

import java.net.URL;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.UUID;

import org.controlsfx.control.textfield.AutoCompletionBinding;
import org.controlsfx.control.textfield.TextFields;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.PieChart.Data;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import jmail.client.models.client.MailClient;
import jmail.client.models.model.DataModel;
import jmail.client.models.responses.DeleteMailResponse;
import jmail.lib.constants.ServerResponseStatuses;
import jmail.lib.models.Email;
import jmail.lib.models.commands.CommandDeleteEmail;
import jmail.lib.models.commands.CommandDeleteEmail.CommandDeleteEmailParameter;
import jmail.lib.models.commands.CommandSendEmail;
import jmail.lib.models.commands.CommandSendEmail.CommandSendEmailParameter;

public class FXMLController implements Initializable {

  private static final Logger LOGGER = LoggerFactory.getLogger(FXMLController.class.getName());

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

  private Set<String> suggestions = new HashSet<String>();
  private AutoCompletionBinding<String> autoCompletionBinding;

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    autoCompletionBinding = TextFields.bindAutoCompletion(searchField, suggestions);
    
    searchField.setOnKeyPressed(
        e -> {
          if (e.getCode() == javafx.scene.input.KeyCode.ENTER) {
            LOGGER.info("SearchField: {}", searchField.getText().trim());
            // buttonSearch(e); TODO: implement
            learnWord(searchField.getText().trim());
          }
        });

    currentMailField.textProperty().bind(DataModel.getInstance().getCurrentEmailProperty().map(e -> e == null ? "" : e.getSubject()));
    currentUserEmail.textProperty().bind(DataModel.getInstance().getCurrentUserProperty().map(u -> u == null ? "" : u.getEmail()));
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

  @FXML
  public void buttonSearch(ActionEvent e) {
    // TODO SearchButton function
  }

  @FXML
  public void buttonReply(ActionEvent e) {
    var email = DataModel.getInstance().getCurrentEmail();
    Calendar today = Calendar.getInstance();
    today.set(Calendar.HOUR_OF_DAY, 0);

    var newEmail =
        new Email(
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

    var newEmail =
        new Email(
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

  @FXML
  public void buttonFwdall(ActionEvent e) {
    // TODO: FwdallButton function
    // FIXME: ma come funziona il forward all?? reply all lo capisco ma forward boh
  }

  @FXML
  public void buttonTrash(ActionEvent e) {
    var params = new CommandDeleteEmailParameter(DataModel.getInstance().getCurrentEmail().getId());
    var command = new CommandDeleteEmail(params);

    MailClient.getInstance()
        .sendCommand(command, response -> {
          if (response.getStatus().equals(ServerResponseStatuses.OK))  {
            DataModel.getInstance().removeCurrentEmail();
          }
          LOGGER.info("DeleteMailResponse: {}", response);
        }, DeleteMailResponse.class);
  }

}
