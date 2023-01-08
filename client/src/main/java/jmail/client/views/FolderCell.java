package jmail.client.views;

import java.io.IOException;

import org.kordamp.ikonli.javafx.FontIcon;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import jmail.client.Main;
import jmail.client.models.model.DataModel;
import jmail.lib.constants.Folders;

public class FolderCell extends ListCell<String> {

  // Views
  @FXML private Label iconLabel;
  @FXML private Label folderLabel;
  @FXML private Label counterLabel;

  private String folderName;

  public FolderCell() {
    try {
      FXMLLoader loader = new FXMLLoader(Main.class.getResource("folder_cell.fxml"));
      loader.setController(this);
      loader.setRoot(this);
      loader.load();

      initListeners();
    } catch (IOException exception) {
      throw new RuntimeException(exception);
    }
  }

  @Override
  protected void updateItem(String item, boolean empty) {
    super.updateItem(item, empty);
    if (empty || item == null) {
      setText(null);
      setContentDisplay(ContentDisplay.TEXT_ONLY);
      return;
    }
    
    folderName = item;
    setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
    
    // Icon
    var fontIcon = new FontIcon(
        switch (item.toLowerCase()) {
          case Folders.INBOX -> "mdi2i-inbox";
          case Folders.SENT -> "mdi2e-email-send";
          case Folders.TRASH -> "mdi2t-trash-can";
          default -> "mdi2a-folder";
        });
    fontIcon.setIconColor(Paint.valueOf("#afb1b3"));
    iconLabel.setGraphic(fontIcon);
    iconLabel.setFont(Font.font(18));
    iconLabel.setPadding(new Insets(2, 0, 0, 0));

    // Folder name
    folderLabel.setText(item);

    // Counter
    counterLabel.setText(DataModel.getInstance().getNewEmailCount() + "");
    counterLabel.setVisible(item.equals(Folders.INBOX));
  }

  private void initListeners() {
    DataModel.getInstance().getNewEmailCountProperty().addListener((observable, oldValue, newValue) -> {
      if (!folderName.equalsIgnoreCase(Folders.INBOX)) 
        return;
        
      counterLabel.setText(newValue + "");
      counterLabel.setVisible(newValue.intValue() > 0);
    });
  }

}
