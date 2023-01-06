package jmail.client.controllers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.UUID;

import org.kordamp.ikonli.javafx.FontIcon;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Paint;
import jmail.client.Main;
import jmail.client.models.model.DataModel;
import jmail.lib.constants.ColorPalette;
import jmail.lib.models.Email;
import javafx.scene.text.Font;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FXMLFolderController extends AnchorPane {

  private static final Logger LOGGER = LoggerFactory.getLogger(FXMLController.class.getName());

  @FXML
  public AnchorPane root;
  
  /*
   * Folder views
   */
  @FXML private Button newMailButton;
  @FXML private ListView<String> listFolder;
  @FXML private Label currentUserName;
  @FXML private Label connectionLabel;

  public FXMLFolderController() {
    // Load
    FXMLLoader loader = new FXMLLoader(Main.class.getResource("folder.fxml"));
    
    loader.setController(this);
    try {
      loader.load();

      initView();
      initListeners();

    } catch (IOException exception) {
      throw new RuntimeException(exception);
    }
  }

  private void initView() {
    
    // Set avatar icon
    var avatar = DataModel.getInstance().getCurrentUserProperty().getValue().getAvatar();
    Node graphic;
    if (avatar == null) {
        var fontIcon = new FontIcon("mdi2a-account");
        fontIcon.setIconColor(Paint.valueOf(ColorPalette.TEXT.getHexValue()));
        graphic = fontIcon;
    } else {
        graphic = new ImageView(avatar);
    }
    currentUserName.setGraphic(graphic);

    // Init folder list
    listFolder.getItems().addAll("Inbox", "Sent", "Trash");

    // Set graphic for each item
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
  }

  private void initListeners() {
    
    // Update username
    currentUserName
    .textProperty()
    .bind(DataModel
          .getInstance()
          .getCurrentUserProperty()
          .map(u -> u == null ? "" : u.getName()));
          
    // Update currentFolder model based on selected item
    listFolder.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
      DataModel.getInstance().setCurrentFolder(newValue.toLowerCase());
    });

    // Update connection status
    var fontIcon = new FontIcon("mdi2w-web-box");
    fontIcon.setIconColor(Paint.valueOf(ColorPalette.GREEN.getHexValue()));
    connectionLabel.setGraphic(fontIcon);
    DataModel.getInstance()
        .isServerStatusConnected()
        .addListener((observable, oldValue, newValue) -> Platform.runLater(() -> {
          var newFontIcon = new FontIcon("mdi2w-web-box");
          newFontIcon.setIconColor(
              newValue
                  ? Paint.valueOf(ColorPalette.GREEN.getHexValue())
                  : Paint.valueOf(ColorPalette.RED.getHexValue()));
          connectionLabel.setGraphic(newFontIcon);
        }));

  }

  @FXML
  public void buttonNewMail(ActionEvent e) {
    DataModel.getInstance().setEditingMode(true);

    var newEmail = new Email(
        UUID.randomUUID().toString(),
        "",
        "",
        DataModel.getInstance().getCurrentUser().getEmail(),
        new ArrayList<>(),
        Calendar.getInstance().getTime(),
        false);

    DataModel.getInstance().setCurrentEmail(newEmail);
    LOGGER.info("NewMailButton: {}", newEmail);
  }

}