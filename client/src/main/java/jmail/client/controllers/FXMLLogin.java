package jmail.client.controllers;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import jmail.client.models.client.MailClient;

public class FXMLLogin implements Initializable {

  @FXML private TextField UsernameField;
  @FXML private PasswordField PasswordField;
  @FXML private Button LoginButton;

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    // TODO:
  }

  @FXML
  public void buttonLogin(javafx.event.ActionEvent e) {
    MailClient.getInstance().login(UsernameField.getText(), PasswordField.getText());
  }
}
