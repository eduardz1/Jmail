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
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main extends Application {

    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class.getName());

    public static void main(String... args) throws IOException {

        Properties properties = new Properties();
        properties.load(Objects.requireNonNull(
                Thread.currentThread().getContextClassLoader().getResourceAsStream("server.properties")));

        Server server = new Server(Integer.parseInt(properties.getProperty("port")));
        server.start();
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("server.fxml"));
        Parent root = loader.load();

        Scene newScene = new Scene(root);

        primaryStage.setScene(newScene);

        primaryStage.setTitle("SERVER");
        primaryStage.getIcons().add(new Image("icon.png"));

        Platform.runLater(() -> {
            final var handle = StageOps.findWindowHandle(primaryStage);

            // Forces Dark Mode on Windows11
            StageOps.dwmSetBooleanValue(handle, DwmAttribute.DWMWA_USE_IMMERSIVE_DARK_MODE, true);
        });

        primaryStage.show();
    }

    @Override
    public void stop() {}
}
