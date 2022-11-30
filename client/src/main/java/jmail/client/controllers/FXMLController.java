package jmail.client.controllers;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;

public class FXMLController implements Initializable {

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    // TODO Auto-generated method stub

  }

  @FXML private Button NewMailButton;
  @FXML private Button SearchButton;
  @FXML private Button ReplyButton;
  @FXML private Button FwdButton;
  @FXML private Button FwdallButton;
  @FXML private Button TrashButton;

  @FXML
  public void buttonNewMail(javafx.event.ActionEvent e) {
    // TODO NewMailButton function
  }

  @FXML
  public void buttonSearch(javafx.event.ActionEvent e) {
    // TODO SearchButton function
  }

  @FXML
  public void buttonReply(javafx.event.ActionEvent e) {
    // TODO ReplyButton function
  }

  @FXML
  public void buttonFwd(javafx.event.ActionEvent e) {
    // TODO FwdButton function
  }

  @FXML
  public void buttonFwdall(javafx.event.ActionEvent e) {
    // TODO FwdallButton function
  }

  @FXML
  public void buttonTrash(javafx.event.ActionEvent e) {
    // TODO TrashButton function
  }

  public void setTopText(String text) {
    // set text from another class
    NewMailButton.setText("\n" + text);
  }
}
