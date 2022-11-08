package jmail.server;

import java.io.IOException;
import java.util.Objects;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

  private static final Logger LOGGER = LoggerFactory.getLogger(Main.class.getName());

  public static void main(String... args) throws IOException {
    LOGGER.info("Starting server...");

    Properties properties = new Properties();
    properties.load(
        Objects.requireNonNull(
            Main.class.getClassLoader().getResourceAsStream("server.properties")));

    Server server = new Server(Integer.parseInt(properties.getProperty("port")));
    launch(args);
  }

  @Override
  public void start(Stage primaryStage) throws Exception {
    Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("server.fxml")));
    Scene scene = new Scene(root);
    primaryStage.setScene(scene);
    primaryStage.show();
  }
}
