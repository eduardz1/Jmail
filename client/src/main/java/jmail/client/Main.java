package jmail.client;

import io.github.mimoguz.custom_window.DwmAttribute;
import io.github.mimoguz.custom_window.StageOps;
import java.util.Objects;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class Main extends Application {

  public String getGreeting() {
    return "Hello World!";
  }

  public static void main(String[] args) {
    launch(args);
  }

  @Override
  public void start(Stage primaryStage) throws Exception {
    Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("client.fxml")));
    Scene scene = new Scene(root);
    primaryStage.setScene(scene);

    primaryStage.setTitle("JMAIL");
    primaryStage.getIcons().add(new Image("logo.png"));

    Platform.runLater(
        () -> {
          final var handle = StageOps.findWindowHandle(primaryStage);
          // Optionally enable the dark mode:
          StageOps.dwmSetBooleanValue(handle, DwmAttribute.DWMWA_USE_IMMERSIVE_DARK_MODE, true);
          // Enable the mica material
          // DWMWA_SYSTEMBACKDROP_TYPE method is the newer way:
          if (!StageOps.dwmSetIntValue(
              handle,
              DwmAttribute.DWMWA_SYSTEMBACKDROP_TYPE,
              // There is also DWMSBT_TABBEDWINDOW option, which gives a more translucent look.
              DwmAttribute.DWMSBT_MAINWINDOW.value)) {
            // This is the "old" way:
            StageOps.dwmSetBooleanValue(handle, DwmAttribute.DWMWA_MICA_EFFECT, true);
          }
        });
    primaryStage.show();
  }
}
