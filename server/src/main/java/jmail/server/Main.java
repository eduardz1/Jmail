package jmail.server;

import java.io.IOException;
import java.util.*;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
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
    launch(args);
  }

  @Override
  public void start(Stage primaryStage) throws Exception {

    FXMLLoader loader = new FXMLLoader(getClass().getResource("server.fxml"));
    Parent root = loader.load();

    FXMLMainController mainController = loader.getController();

    ObservableStreamAppender.getObservable()
        .addListener((observable, oldValue, newValue) -> mainController.setTopText(newValue));
    Scene newScene = new Scene(root);
    Stage newStage = new Stage();
    newStage.setScene(newScene);
    newStage.setOnShown(
        e -> {
          LOGGER.info("debug: ciaooooooo");
          LOGGER.info("lurido");
        });
    newStage.show();
    mainController.setTopText("Trace: dioooooo nmaletto");
  }

  @Override
  public void stop() {}
}
