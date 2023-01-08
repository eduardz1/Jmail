package jmail.client.views;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import jmail.client.Main;
import jmail.lib.models.Email;

public class EmailCell extends ListCell<Email> {

  // Views
  @FXML private Label fromLabel;
  @FXML private Label subjectLabel;
  @FXML private Label dateLabel;
  @FXML private Label bodyLabel;
  @FXML private Region readMarker;

  public EmailCell() {
    try {
      FXMLLoader loader = new FXMLLoader(Main.class.getResource("email_cell.fxml"));
      loader.setController(this);
      loader.setRoot(this);
      loader.load();
    } catch (IOException exception) {
      throw new RuntimeException(exception);
    }
  }

  @Override
  protected void updateItem(Email email, boolean empty) {
    super.updateItem(email, empty);

    if (empty || email == null) {
      setText(null);
      setContentDisplay(ContentDisplay.TEXT_ONLY);
    } else {
      fromLabel.setText(email.getSender());
      subjectLabel.setText(email.getSubject());
      
      Calendar today = Calendar.getInstance();
      
      Calendar date = Calendar.getInstance();
      date.setTime(email.getDate());
      DateFormat df;
      if (date.get(Calendar.YEAR) == today.get(Calendar.YEAR)
          && date.get(Calendar.DAY_OF_YEAR) == today.get(Calendar.DAY_OF_YEAR)) {
        df = new SimpleDateFormat("HH:mm");
      } else {
        df = new SimpleDateFormat("dd MMM yy, HH:mm");
      }
      dateLabel.setText(df.format(email.getDate()));

      bodyLabel.setText(email.getBody());
      readMarker.setStyle("-fx-background-color:" + (email.getRead() ? "#00000000;" : "#009688FF;"));
      setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
    }
  }
  
}
