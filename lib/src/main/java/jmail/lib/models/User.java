package jmail.lib.models;

import java.io.IOException;
import jmail.lib.helpers.JsonHelper;
import jmail.lib.helpers.SystemIOHelper;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

@Builder
@Data
public class User {
  @NonNull private String email;
  @NonNull private String name;
  @NonNull private String surname;
  @NonNull private String passwordSHA256;
  private String avatar; // PATH

  public void save() { // FIXME: dont know if it's the best way to do it
    try {
      SystemIOHelper.createUserFolderIfNotExists(email);
      SystemIOHelper.writeJSONFile(
          SystemIOHelper.getUserDirectory(email), "user.json", JsonHelper.toJson(this));
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }
}
