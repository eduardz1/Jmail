package jmail.client.controllers;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
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
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.SVGPath;
import jmail.client.Main;
import jmail.client.models.model.DataModel;
import jmail.lib.constants.Folders;
import jmail.lib.models.Email;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FXMLEmailController extends AnchorPane {
  private static final Logger LOGGER = LoggerFactory.getLogger(FXMLController.class.getName());

  @FXML public AnchorPane root;

  private FXMLController mainController;

  /*
   * Email views
   */

  // Buttons
  @FXML private Button replyButton;
  @FXML private Button replyAllButton;
  @FXML private Button forwardButton;
  @FXML private Button sendButton;
  // @FXML private Label currentMailField;

  @FXML private AnchorPane emailPane;
  @FXML private VBox topPane;
  @FXML private ScrollPane bottomPane;
  @FXML private AnchorPane scrollContentPane;

  
  
  
  // Edit layout
  @FXML private TextField subjectField;
  @FXML private TextField recipientsField;
  @FXML private TextArea bodyField;
  @FXML private VBox editPane;

  // View layout
  @FXML private VBox viewPane;
  @FXML private Label fromLabel;
  @FXML private Label toLabel;
  @FXML private Label dateLabel;
  @FXML private Label subjectLabel;
  @FXML private Label bodyLabel;

  // Logo
  @FXML private BorderPane logoPane;

  
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

    DataModel.getInstance().getCurrentEmailProperty()
        .addListener((observable, oldValue, newValue) -> Platform.runLater(() -> {
          if (newValue == null) {
            subjectField.setText("");
            recipientsField.setText("");
            bodyField.setText("");
            emailPane.setVisible(false);
            logoPane.setVisible(true);
          } else {

            var recsText = switch (newValue.getRecipients().size()) {
              case 0 -> "";
              default -> String.join(";", newValue.getRecipients());
            };

            // Edit mode
            subjectField.setText(newValue.getSubject());
            recipientsField.setText(recsText);
            bodyField.setText(newValue.getBody());

            // View mode
            fromLabel.setText(newValue.getSender());
            subjectLabel.setText(newValue.getSubject());
            bodyLabel.setText(newValue.getBody());
            toLabel.setText(recsText);

            // Check if date is today and set the date format accordingly
            Calendar today = Calendar.getInstance();
            
            Calendar date = Calendar.getInstance();
            date.setTime(newValue.getDate());
            DateFormat df;
            if (date.get(Calendar.YEAR) == today.get(Calendar.YEAR)
                && date.get(Calendar.DAY_OF_YEAR) == today.get(Calendar.DAY_OF_YEAR)) {
              df = new SimpleDateFormat("HH:mm");
            } else {
              df = new SimpleDateFormat("dd MMM yy, HH:mm");
            }
            dateLabel.setText(df.format(newValue.getDate()));

            emailPane.setVisible(true);
            logoPane.setVisible(false);
          }
        }));

    DataModel.getInstance().isEditingModeProperty().addListener((observable, oldValue, isEditing) -> {
      updateLayout(isEditing);
    });
  }

  private void initView() {
    // Need to set the width of the labels to the width of the viewPane to make the
    // text wrap
    bodyLabel.prefWidthProperty().bind(viewPane.widthProperty());
    topPane.prefWidthProperty().bind(root.widthProperty());
    bottomPane.prefWidthProperty().bind(root.widthProperty().subtract(16));
    bottomPane.prefHeightProperty().bind(root.heightProperty().subtract(104));
    scrollContentPane.minHeightProperty().bind(bottomPane.heightProperty().subtract(4));

    // Set the initial layout
    updateLayout(DataModel.getInstance().isEditingMode());

    // Set the logo
    Platform.runLater(() -> {
      Image image = new Image(getClass().getResourceAsStream("/logo-transaprent.png"));
      var logo = new ImageView(image);
      logo.setImage(image);
      logo.fitHeightProperty().bind(logoPane.heightProperty().divide(2.5));
      logo.fitWidthProperty().bind(logoPane.widthProperty().divide(2.5));
      logo.setPreserveRatio(true);
      logoPane.setCenter(logo);
    });
    
    emailPane.setVisible(false);
    logoPane.setVisible(true);
  }

  private void updateLayout(boolean isEditing) {
    editPane.setVisible(isEditing);
    viewPane.setVisible(!isEditing);
    replyButton.setDisable(isEditing);
    forwardButton.setDisable(isEditing);
    replyAllButton.setDisable(isEditing);
    sendButton.setDisable(!isEditing);
  }

  @FXML
  public void buttonReply(ActionEvent e) {
    DataModel.getInstance().getCurrentEmail().ifPresentOrElse(email -> {
      DataModel.getInstance().setEditingMode(true);
      Calendar today = Calendar.getInstance();
      

      var newEmail = new Email(UUID.randomUUID().toString(), email.getSubject(), email.getBody(),
          DataModel.getInstance().getCurrentUser().getEmail(), List.of(email.getSender()), today.getTime(), false);

      DataModel.getInstance().setCurrentEmail(newEmail);
      LOGGER.info("ReplyButton: {}", newEmail);
    }, () -> LOGGER.info("ReplyButton: No email selected"));
  }

  @FXML
  public void buttonFwd(ActionEvent e) {
    DataModel.getInstance().getCurrentEmail().ifPresentOrElse(email -> {
      var newRecipients = email.getRecipients();
      Calendar today = Calendar.getInstance();
      

      var newEmail = new Email(UUID.randomUUID().toString(), email.getSubject(), email.getBody(),
          DataModel.getInstance().getCurrentUser().getEmail(), newRecipients, today.getTime(), false);

      DataModel.getInstance().setCurrentEmail(newEmail);
      LOGGER.info("FwdButton: {}", newEmail);
    }, () -> LOGGER.info("No email selected"));
  }

  @FXML
  public void buttonTrash(ActionEvent e) {
    DataModel.getInstance().getCurrentEmail().ifPresentOrElse(email -> {
      var currFolder = DataModel.getInstance().getCurrentFolder();

      if (DataModel.getInstance().isEditingMode()) { // clean draft
        DataModel.getInstance().setCurrentEmail(null);
        DataModel.getInstance().setEditingMode(false);
      } else {
        boolean hardDelete = !currFolder.equals(Folders.INBOX);
        // TODO: ask user to confirmation
        mainController.deleteEmail(email.fileID(), currFolder, hardDelete);
      }
    }, () -> LOGGER.info("TrashButton: no email selected"));
  }

  @FXML
  public void buttonSend(ActionEvent e) {
    DataModel.getInstance().getCurrentEmail().ifPresentOrElse(email -> {
      if (!DataModel.getInstance().isEditingMode()) {
        LOGGER.error("Email not editable");
        return;
      }

      Calendar today = Calendar.getInstance();
      

      // Create a new email in order to add a new Object without reference to list
      var newEmail = new Email(UUID.randomUUID().toString(), subjectField.getText(), bodyField.getText(),
          email.getSender(), new ArrayList<>(Arrays.asList(recipientsField.getText().split(";"))), today.getTime(),
          false);

      mainController.sendEmail(newEmail);
      DataModel.getInstance().setCurrentEmail(null);
      DataModel.getInstance().setEditingMode(false);
    }, () -> LOGGER.error("No email to send"));
  }

  @FXML
  public void buttonReplyAll(ActionEvent actionEvent) {
  }

  @FXML
  public void buttonBack(ActionEvent actionEvent) {
    DataModel.getInstance().setEditingMode(false);
    DataModel.getInstance().setCurrentEmail(null);
  }

}
