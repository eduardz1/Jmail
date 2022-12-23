package jmail.client.controllers;

import com.google.common.hash.Hashing;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import jmail.client.Main;
import jmail.client.models.client.MailClient;
import jmail.client.models.model.DataModel;
import jmail.client.models.responses.LoginResponse;
import jmail.lib.constants.ServerResponseStatuses;
import jmail.lib.models.commands.CommandLogin;

public class FXMLLogin implements Initializable {

    @FXML private TextField UsernameField;

    @FXML private PasswordField PasswordField;

    @FXML private Button LoginButton;

    @FXML private Label connLbl;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        DataModel.getInstance().isServerStatusConnected().addListener((observable, oldValue, newValue) -> {
            Platform.runLater(() -> {
                connLbl.setText(newValue ? "Connected" : "Disconnected"); // TODO: Should stay in view? Boh
            });
        });
    }

    @FXML public void buttonLogin(javafx.event.ActionEvent e) {
        // FIXME: remove this
        login("emmedeveloper@gmail.com", "emme");
        return;
        //    login(UsernameField.getText(), PasswordField.getText());
    }

    public void login(String username, String password) {
        var hashed =
                Hashing.sha256().hashString(password, StandardCharsets.UTF_8).toString();

        var command = new CommandLogin(new CommandLogin.CommandLoginParameter(username, hashed));
        command.setUserEmail(username);

        MailClient.getInstance()
                .sendCommand(
                        command,
                        response -> {
                            if (response.getStatus().equals(ServerResponseStatuses.OK)) {
                                var resp = (LoginResponse) response;
                                DataModel.getInstance().setCurrentUser(resp.getUser());
                                System.out.println("Login successful"); // TODO: use LOGGER here
                                Main.changeScene("client.fxml");
                            } else {
                                Main.showNotConnectServerErrorDialog();
                            }
                        },
                        LoginResponse.class);
    }
}
