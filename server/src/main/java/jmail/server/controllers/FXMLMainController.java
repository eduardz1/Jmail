package jmail.server.controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import org.fxmisc.richtext.CodeArea;

import java.net.URL;
import java.util.ResourceBundle;

public class FXMLMainController implements Initializable {
    private CodeArea codeArea = new CodeArea();
    private static final String[] KEYWORDS = new String[] {};

    @FXML
    private TextArea delay;
    public void setTopText(String text) {
        // set text from another class
        delay.appendText("\n");
        delay.appendText(text);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }
}