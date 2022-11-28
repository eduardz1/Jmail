package jmail.client.controllers;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.scene.control.Button;


import javafx.fxml.FXML;
import javafx.fxml.Initializable;

public class FXMLController implements Initializable {

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    // TODO Auto-generated method stub


  }

  @FXML
  private Button NewMailButton;


  @FXML
  public void buttonOnAction(javafx.event.ActionEvent e) {
    NewMailButton.setText("Ciao");
  }
  public void setTopText(String text) {
    // set text from another class
    NewMailButton.setText("\n" + text);
  }
}


