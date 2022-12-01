package jmail.client;

import io.github.mimoguz.custom_window.DwmAttribute;
import io.github.mimoguz.custom_window.StageOps;
import java.io.IOException;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import jmail.client.controllers.FXMLController;
import jmail.lib.models.commands.CommandDeleteEmail;

public class Main extends Application {

  public String getGreeting() {
    return "Hello World!";
  }

  private static MailClient client;

  public static void main(String[] args) throws IOException {

    client = new MailClient("127.0.0.1", 32666);
    launch(args);
  }

  @Override
  public void start(Stage primaryStage) throws Exception {
    // Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("client.fxml")));

    FXMLLoader loader = new FXMLLoader(getClass().getResource("client.fxml"));
    Parent root = loader.load();
    Scene scene = new Scene(root);
    primaryStage.setScene(scene);
    FXMLController mainController = loader.getController();
    //mainController.setTopText("Ciao");
    primaryStage.setMinWidth(780);
    primaryStage.setMinHeight(400);
    primaryStage.setTitle("JMAIL");
    primaryStage.getIcons().add(new Image("logo.png"));

    // Forces Dark Mode on Windows11 windows and enables mica effect on transaprent surfaces
    Platform.runLater(
        () -> {
          final var handle = StageOps.findWindowHandle(primaryStage);
          StageOps.dwmSetBooleanValue(handle, DwmAttribute.DWMWA_USE_IMMERSIVE_DARK_MODE, true);
          StageOps.dwmSetIntValue(
              handle, DwmAttribute.DWMWA_SYSTEMBACKDROP_TYPE, DwmAttribute.DWMSBT_MAINWINDOW.value);

//          var cmdPar = new CommandSendEmail.CommandSendEmailParameter();
//          var cmd = new CommandListEmail();
//          cmd.setUserEmail("eduard.occhipinti@edu.unito.it");

//            Calendar today = Calendar.getInstance();
//            today.set(Calendar.HOUR_OF_DAY, 0);

            //          var email =
          //              new Email(
          //                  "",
          //                  "Bella raga",
          //                  "Bella raga sono un cazzo di gesù cristo ariano",
          //                  "emmedeveloper@gmail.com",
          //                  List.of("eduard.occhipinti@edu.unito.it",
          // "marcofrattarola@gmail.com"),
          //                  today.getTime());
          //          cmdPar.setEmail(email);
          //
          //          var cmd = new CommandSendEmail(cmdPar);
          //          cmd.setUserEmail("emmedeveloper@gmail.com");

                    var cmdPar = new CommandDeleteEmail.CommandDeleteEmailParameter();
                    cmdPar.setEmailID("1");
                    var cmd = new CommandDeleteEmail(cmdPar);
                    cmd.setUserEmail("emmedeveloper@gmail.com");
          setTimeout(() -> client.sendCommand(cmd), 1000);
        });
    primaryStage.show();
  }

  public static void setTimeout(Runnable runnable, int delay) {
    new Thread(
            () -> {
              try {
                Thread.sleep(delay);
                runnable.run();
              } catch (Exception e) {
                System.err.println(e);
              }
            })
        .start();
  }
}
