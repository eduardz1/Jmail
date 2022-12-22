package jmail.client.controllers;

import com.google.common.hash.Hashing;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import jmail.client.Main;
import jmail.client.models.client.MailClient;
import jmail.client.models.model.DataModel;
import jmail.client.models.responses.LoginResponse;
import jmail.lib.constants.ServerResponseStatuses;
import jmail.lib.models.commands.CommandLogin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FXMLLogin implements Initializable {

    private static final Logger LOGGER = LoggerFactory.getLogger(FXMLLogin.class.getName());

    @FXML private TextField UsernameField;

    @FXML private PasswordField PasswordField;

    @FXML private Button LoginButton;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // TODO:
    }

    @FXML public void buttonLogin(javafx.event.ActionEvent e) {

        var username = UsernameField.getText();
        var hashed = Hashing.sha256()
                .hashString(PasswordField.getText(), StandardCharsets.UTF_8)
                .toString();
        var params = new CommandLogin.CommandLoginParameter(username, hashed);
        var command = new CommandLogin(params);
        command.setUserEmail(username);

        MailClient.getInstance()
                .sendCommand(
                        command,
                        response -> {
                            if (response.getStatus().equals(ServerResponseStatuses.OK)) {
                                var resp = (LoginResponse) response;
                                DataModel.getInstance().setCurrentUser(resp.getUser());
                                Main.changeScene("client.fxml");
                            }

                            LOGGER.info(response.getMessage());
                        },
                        LoginResponse.class);
    }
}
