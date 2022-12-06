package jmail.server;

import com.google.common.hash.Hashing;
import java.nio.charset.StandardCharsets;
import jmail.lib.models.User;
import org.junit.jupiter.api.Test;

class ServerTest {

  // test creation of new users
  @Test
  void testCreateNewUser() {
    var edu1 =
        User.builder()
            .email("occhipinti.eduard@gmail.com")
            .name("Eduard")
            .surname("Occhipinti")
            .passwordSHA256(Hashing.sha256().hashString("edu1", StandardCharsets.UTF_8).toString())
            .build();
    var edu2 =
        User.builder()
            .email("eduard.occhipinti@edu.unito.it")
            .name("Eduard")
            .surname("Occhipinti")
            .passwordSHA256(Hashing.sha256().hashString("edu2", StandardCharsets.UTF_8).toString())
            .build();
    var fratta =
        User.builder()
            .email("marcofrattarola@gmail.com")
            .name("Marco")
            .surname("Frattarola")
            .passwordSHA256(
                Hashing.sha256().hashString("fratta", StandardCharsets.UTF_8).toString())
            .build();
    var emme =
        User.builder()
            .email("emmedeveloper@gmail.com")
            .name("Marco")
            .surname("Molica")
            .passwordSHA256(Hashing.sha256().hashString("emme", StandardCharsets.UTF_8).toString())
            .build();

    edu1.save();
    edu2.save();
    fratta.save();
    emme.save();
  }
}
