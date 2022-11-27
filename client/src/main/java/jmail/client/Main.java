package jmail.client;

import io.github.mimoguz.custom_window.DwmAttribute;
import io.github.mimoguz.custom_window.StageOps;
import java.io.IOException;
import java.sql.Date;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import jmail.lib.models.Email;
import jmail.lib.models.commands.CommandDeleteEmail;
import jmail.lib.models.commands.CommandSendEmail;

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

           var cmdPar = new CommandSendEmail.CommandSendEmailParameter();

            Calendar today = Calendar.getInstance();
            today.set(Calendar.HOUR_OF_DAY, 0);

          var email = new Email("",
                   "Bella raga",
                   "Bella raga sono un cazzo di gesù cristo ariano",
                   "emmedeveloper@gmail.com",
                   List.of("eduocchi@gmail.com", "marcofratta@gmail.com"),
                   today.getTime()
                   );
          cmdPar.setEmail(email);

          var cmd = new CommandSendEmail(cmdPar);
          cmd.setUserEmail("emmedeveloper@gmail.com");


//          var cmdPar = new CommandDeleteEmail.CommandDeleteEmailParameter();
//          cmdPar.setEmailID("1");
//          var cmd = new CommandDeleteEmail(cmdPar);
//          cmd.setUserEmail("emmedeveloper@gmail.com");
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
