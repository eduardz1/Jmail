package jmail.client;

import io.github.mimoguz.custom_window.DwmAttribute;
import io.github.mimoguz.custom_window.StageOps;
import java.io.IOException;
import java.util.Objects;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import jmail.client.models.client.MailClient;

public class Main extends Application {

  private static Stage primaryStage;

  public static void main(String[] args) throws IOException {
    MailClient.getInstance().connect("localhost", 8085); // FIXME: hardcoded
    launch(args);
  }

  public static void changeScene(String fxml) {
    Parent pane = null;
    try {
      pane = FXMLLoader.load(Objects.requireNonNull(Main.class.getResource(fxml)));
    } catch (IOException e) {
      throw new RuntimeException(e); // TODO: handle exception
    }
    primaryStage.getScene().setRoot(pane);
    primaryStage.sizeToScene();
  }

  public String getGreeting() {
    return "Hello World!";
  }

  @Override
  public void start(Stage primaryStage) throws Exception {
    Main.primaryStage = primaryStage;

    FXMLLoader loader = new FXMLLoader(getClass().getResource("login.fxml"));
    Parent root = loader.load();

    Scene scene = new Scene(root);

    primaryStage.setTitle("JMAIL");
    primaryStage.getIcons().add(new Image("logo.png"));
    primaryStage.setScene(scene);

    Platform.runLater(
        () -> {
          final var handle = StageOps.findWindowHandle(primaryStage);

          // Forces Dark Mode on Windows11
          StageOps.dwmSetBooleanValue(handle, DwmAttribute.DWMWA_USE_IMMERSIVE_DARK_MODE, true);
        });

    primaryStage.show();
  }
}
