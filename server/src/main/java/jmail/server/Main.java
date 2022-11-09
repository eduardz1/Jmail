package jmail.server;

import java.io.IOException;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javafx.application.Application;
import javafx.concurrent.Task;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import jmail.lib.logger.ObservableStreamAppender;
import jmail.server.controllers.FXMLMainController;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.LineNumberFactory;
import org.fxmisc.richtext.model.StyleSpans;
import org.fxmisc.richtext.model.StyleSpansBuilder;
import org.reactfx.Subscription;
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
          LOGGER.info("ciaooooooo");
          LOGGER.info("lurido");
        });
    newStage.show();
    mainController.setTopText("dioooooo nmaletto");
  }

  @Override
  public void stop() {
  }


}
