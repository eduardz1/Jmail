package jmail.client;

import io.github.mimoguz.custom_window.DwmAttribute;
import io.github.mimoguz.custom_window.StageOps;
import java.io.IOException;
import java.util.Objects;
import javafx.application.Application;
import javafx.application.ConditionalFeature;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import jmail.client.models.client.MailClient;

public class Main extends Application {

  private static Stage primaryStage;
  private static Boolean enableConditionalJavaFXFeatures;

  public static void main(String[] args) throws IOException {
    MailClient.getInstance().connect("localhost", 32666); // FIXME: hardcoded
    launch(args);
  }

  public static void changeScene(String fxml) {
    Parent pane = null;
    try {
      pane = FXMLLoader.load(Objects.requireNonNull(Main.class.getResource(fxml)));
    } catch (IOException e) {
      throw new RuntimeException(e); // TODO: handle exception
    }
    pane.setStyle("-fx-background-color: transparent");
    primaryStage.getScene().setRoot(pane);
    primaryStage.sizeToScene();
  }

  public String getGreeting() {
    return "Hello World!";
  }

  @Override
  public void start(Stage primaryStage) throws Exception {
    // this check needs to happen in the event thread
    enableConditionalJavaFXFeatures = Platform.isSupported(ConditionalFeature.UNIFIED_WINDOW);

    Main.primaryStage = primaryStage;

    // opens the login window
    FXMLLoader loader = new FXMLLoader(getClass().getResource("login.fxml"));
    Parent root = loader.load();

    Scene scene = new Scene(root);

    primaryStage.setTitle("JMAIL");
    primaryStage.getIcons().add(new Image("logo.png"));
    primaryStage.setScene(scene);

    if (enableConditionalJavaFXFeatures) {
      root.setStyle("-fx-background-color: transparent");
      scene.setFill(Color.TRANSPARENT);
      primaryStage.initStyle(StageStyle.UNIFIED);
    }

    Platform.runLater(
        () -> {
          // Forces Dark Mode on Windows11 windows and enables mica effect on transparent surfaces
          final var handle = StageOps.findWindowHandle(primaryStage);
          StageOps.dwmSetBooleanValue(handle, DwmAttribute.DWMWA_USE_IMMERSIVE_DARK_MODE, true);
          StageOps.dwmSetIntValue(
              handle, DwmAttribute.DWMWA_SYSTEMBACKDROP_TYPE, DwmAttribute.DWMSBT_MAINWINDOW.value);
        });

    primaryStage.show();
  }
}
