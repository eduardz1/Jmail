package jmail.client.controllers;

import com.google.common.hash.Hashing;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.paint.Paint;
import jmail.client.Main;
import jmail.client.models.client.MailClient;
import jmail.client.models.model.DataModel;
import jmail.client.models.responses.LoginResponse;
import jmail.lib.constants.ColorPalette;
import jmail.lib.constants.ServerResponseStatuses;
import jmail.lib.helpers.SystemIOHelper;
import jmail.lib.models.Email;
import jmail.lib.models.commands.CommandLogin;
import org.kordamp.ikonli.javafx.FontIcon;

public class FXMLLogin {

    @FXML private TextField UsernameField;

    @FXML private PasswordField PasswordField;

    @FXML private Button LoginButton;

    @FXML private Label connectionLabel;

    public void initialize() {
        var fontIcon = new FontIcon("mdi2w-web-box");
        fontIcon.setIconColor(Paint.valueOf(ColorPalette.GREEN.getHexValue()));
        connectionLabel.setGraphic(fontIcon);
        connectionLabel.setText("connected");
        DataModel.getInstance()
                .isServerStatusConnected()
                .addListener((observable, oldValue, newValue) -> Platform.runLater(() -> {
                    var newFontIcon = new FontIcon("mdi2w-web-box");
                    newFontIcon.setIconColor(
                            newValue
                                    ? Paint.valueOf(ColorPalette.GREEN.getHexValue())
                                    : Paint.valueOf(ColorPalette.RED.getHexValue()));
                    connectionLabel.setGraphic(newFontIcon); // TODO: Should stay in view? Boh
                    connectionLabel.setText(newValue ? "connected" : "not connected");
                }));
    }

    @FXML public void buttonLogin(javafx.event.ActionEvent e) {
        login(UsernameField.getText(), PasswordField.getText());
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
                                try {
                                    SystemIOHelper.createUserFolderIfNotExists(
                                            resp.getUser().getEmail());
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                Main.changeScene("client.fxml");
                            } else {
                                Main.showNotConnectServerErrorDialog();
                            }
                        },
                        LoginResponse.class);
    }

    private void addEmails() {

        var in = new Email[] {
            mockEmail("inbox"),
            //            mockEmail("inbox"), mockEmail("inbox"), mockEmail("inbox"), mockEmail("inbox"),
            // mockEmail("inbox")
        };
        DataModel.getInstance().addEmail("inbox", in);

        var out = new Email[] {
            mockEmail("out"), mockEmail("out"), mockEmail("out"),
        };
        DataModel.getInstance().addEmail("sent", out);

        var tr = new Email[] {
            mockEmail("tra"), mockEmail("tra"), mockEmail("tra"),
        };
        DataModel.getInstance().addEmail("trash", tr);
    }

    private Email mockEmail(String sub) {
        return new Email(
                java.util.UUID.randomUUID().toString(),
                sub + " Oggetto " + java.util.UUID.randomUUID(),
                "Buongiorno,\nAvrebbe due minuti per parlare del Nostro signore?",
                "mario@yahoo.it",
                List.of("emmedeveloper@gmail.com"),
                java.util.Calendar.getInstance().getTime(),
                false);
    }
}
