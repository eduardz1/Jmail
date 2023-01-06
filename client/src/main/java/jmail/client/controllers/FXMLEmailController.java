package jmail.client.controllers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import jmail.client.Main;
import jmail.client.models.model.DataModel;
import jmail.lib.models.Email;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FXMLEmailController extends AnchorPane {
  private static final Logger LOGGER = LoggerFactory.getLogger(FXMLController.class.getName());

  @FXML
  public AnchorPane root;

  private FXMLController mainController;

  /*
   * Email views
   */

   @FXML private Button replyButton;
   @FXML private Button forwardButton;
   @FXML private Button forwardAllButton;
   @FXML private Button trashButton;
   @FXML private Label currentMailField;
 
   public TextField subjectField;
   public TextField recipientsField;
   public TextArea bodyField;


   public FXMLEmailController() {
    // Load
    FXMLLoader loader = new FXMLLoader(Main.class.getResource("email.fxml"));
    
    loader.setController(this);
    try {
      loader.load();

      initView();
      initListeners();

    } catch (IOException exception) {
      throw new RuntimeException(exception);
    }
  }

  public void setMainController(FXMLController mainController) {
    this.mainController = mainController;
  }

  private void initListeners() {

    currentMailField
    .textProperty()
    .bind(DataModel.getInstance().getCurrentEmailProperty().map(e -> e == null ? "" : e.getSubject()));

    
    DataModel.getInstance()
        .getCurrentEmailProperty()
        .addListener((observable, oldValue, newValue) -> Platform.runLater(() -> {
          if (newValue == null) {
            subjectField.setPromptText("Subject");
            recipientsField.setPromptText("Recipients");
            bodyField.setPromptText("Body");
          } else {
            subjectField.setText(newValue.getSubject());
            recipientsField.setText(String.join(";", newValue.getRecipients()));
            bodyField.setText(newValue.getBody());
          }
        }));
  }


  private void initView() {

    
  }

  @FXML
  public void buttonReply(ActionEvent e) {
    DataModel.getInstance()
        .getCurrentEmail()
        .ifPresentOrElse(
            email -> {
              DataModel.getInstance().setEditingMode(true);
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

  @FXML
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

              if (DataModel.getInstance().isEditingMode()) { // clean draft
                DataModel.getInstance().setCurrentEmail(null);
                DataModel.getInstance().setEditingMode(false);
              } else {
                boolean hardDelete = !currFolder.equals("inbox");
                // TODO: ask user to confirmation
                mainController.deleteEmail(email.fileID(), currFolder, hardDelete);
              }
            },
            () -> LOGGER.info("TrashButton: no email selected"));
  }

  @FXML
  public void buttonSend(ActionEvent e) {
    DataModel.getInstance()
        .getCurrentEmail()
        .ifPresentOrElse(
            email -> {
              if (!DataModel.getInstance().isEditingMode()) {
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

              mainController.sendEmail(newEmail);
              DataModel.getInstance().setCurrentEmail(null);
              DataModel.getInstance().setEditingMode(false);
            },
            () -> LOGGER.error("No email to send"));
  }
  
}
