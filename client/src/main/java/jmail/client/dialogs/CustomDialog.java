package jmail.client.dialogs;

import java.io.IOException;
import java.util.Objects;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.stage.Modality;
import javafx.stage.Window;

public class CustomDialog extends Dialog<String> {

    @FXML private ButtonType connectButtonType;

    public CustomDialog(Window owner) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/dialog.fxml"));
            var path = getClass().getResource("/dialog.fxml");
            loader.setLocation(path);
            loader.setController(this);

            DialogPane dialogPane = loader.load();
            // dialogPane.lookupButton(connectButtonType).addEventFilter(ActionEvent.ANY, this::onConnect);

            initOwner(owner);
            initModality(Modality.APPLICATION_MODAL);

            setResizable(true);
            setTitle("Свързване с база данни");
            setDialogPane(dialogPane);
            setResultConverter(buttonType -> {
                if (!Objects.equals(ButtonBar.ButtonData.OK_DONE, buttonType.getButtonData())) {
                    return null;
                }

                return "";
            });

            setOnShowing(dialogEvent -> Platform.runLater(() -> {
                // usernameField.requestFocus();
            }));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML private void initialize() {}
}
