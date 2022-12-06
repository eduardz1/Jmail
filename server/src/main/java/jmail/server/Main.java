package jmail.server;

import io.github.mimoguz.custom_window.DwmAttribute;
import io.github.mimoguz.custom_window.StageOps;
import java.io.IOException;
import java.util.Objects;
import java.util.Properties;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import jmail.lib.logger.ObservableStreamAppender;
import jmail.server.controllers.FXMLMainController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main extends Application {

  private static final Logger LOGGER = LoggerFactory.getLogger(Main.class.getName());

  public static void main(String... args) throws IOException {

    Properties properties = new Properties();
    properties.load(
        Objects.requireNonNull(
            Main.class.getClassLoader().getResourceAsStream("server.properties")));

    Server server = new Server(Integer.parseInt(properties.getProperty("port")));
    server.start();
    launch(args);
  }

  @Override
  public void start(Stage primaryStage) throws Exception {

    FXMLLoader loader = new FXMLLoader(getClass().getResource("server.fxml"));
    Parent root = loader.load();
    root.setStyle("-fx-background-color: transparent");

    FXMLMainController mainController = loader.getController();

    ObservableStreamAppender.getObservable()
        .addListener((observable, oldValue, newValue) -> mainController.setTopText(newValue));
    Scene newScene = new Scene(root);
    newScene.setFill(Color.TRANSPARENT);

    primaryStage.setScene(newScene);

    primaryStage.setTitle("SERVER");
    primaryStage.getIcons().add(new Image("logo.png"));
    primaryStage.initStyle(StageStyle.UNIFIED);

    // Forces Dark Mode on Windows11 windows and enables mica effect on transaprent surfaces
    Platform.runLater(
        () -> {
          final var handle = StageOps.findWindowHandle(primaryStage);
          StageOps.dwmSetBooleanValue(handle, DwmAttribute.DWMWA_USE_IMMERSIVE_DARK_MODE, true);
          StageOps.dwmSetIntValue(
              handle, DwmAttribute.DWMWA_SYSTEMBACKDROP_TYPE, DwmAttribute.DWMSBT_MAINWINDOW.value);
        });

    primaryStage.show();
  }

  @Override
  public void stop() {}
}
