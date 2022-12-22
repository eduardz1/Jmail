package jmail.lib.helpers;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import jmail.lib.models.User;

public class SystemIOHelper {

  private static final String emailpath = "email";
  private static final String userpath = "users";

  public static void createBaseFoldersIfNotExists() throws IOException {
    Files.createDirectories(Paths.get(userpath));
    Files.createDirectories(Paths.get(emailpath));
  }

  public static void createUserFolderIfNotExists(String userEmail) throws IOException {
    Path user = getUserDirectory(userEmail);
    Files.createDirectories(user);

    Files.createDirectories(Paths.get(user + "/" + "sent"));
    Files.createDirectories(Paths.get(user + "/" + "inbox"));
    Files.createDirectories(Paths.get(user + "/" + "deleted"));
  }

  public static Path getUserDirectory(String userEmail) {
    String[] s = userEmail.split("@");
    return Paths.get(userpath + "/" + s[1] + "/" + s[0]);
  }

  public static Path getUserDeleted(String userEmail) {
    return getUserSpecificPath(userEmail, "deleted");
  }

  public static Path getUserSent(String userEmail) {
    return getUserSpecificPath(userEmail, "sent");
  }

  public static Path getUserInbox(String userEmail) {
    return getUserSpecificPath(userEmail, "inbox");
  }

  private static Path getUserSpecificPath(String userEmail, String folder) {
    return Paths.get(getUserDirectory(userEmail) + "/" + folder);
  }

  public static void writeJSONFile(Path path, String name, String jsonObject) throws IOException {
    var writer = new FileWriter(path + "/" + name);
    writer.write(jsonObject);
    writer.close();
  }

  public static String readJSONFile(Path path) throws IOException {
    return Files.readString(path);
  }

  // FIXME: non penso che dovremmo tenere due funzioni che come unica cosa
  // mascherano la option utilizzata che tra l'altro può avere solo due valori

  public static void moveFile(Path from, Path to) throws IOException {
    Files.move(from, to, StandardCopyOption.ATOMIC_MOVE);
  }

  public static void copyFile(Path from, Path to) throws IOException {
    Files.copy(from, to, StandardCopyOption.REPLACE_EXISTING);
  }

  public static Path getInboxEmailPath(String userEmail, String emailID) {

    return Path.of(getUserInbox(userEmail) + "/" + emailID);
  }

  public static Path getDeletedEmailPath(String userEmail, String emailID) {
    return Path.of(getUserDeleted(userEmail) + "/" + emailID);
  }

  public static Path getSentEmailPath(String userEmail, String emailID) {
    return Path.of(getUserSent(userEmail) + "/" + emailID);
  }

  // TODO: take a look at this method later
  //    public static Boolean userExists(String userEmail) {
  //        Path user = Paths.get(emailpath);
  //        File f = new File(Paths.get(String.format("%s\\%s.dat", user, userEmail)).toUri());
  //        return f.exists() && !f.isDirectory();
  //    }

  public static Boolean userExists(String userEmail) {
    File f = new File(getUserDirectory(userEmail).toUri());
    return f.exists() && f.isDirectory();
  }

  public static User getUser(String email) {
    User user = null;
    try {
      var json = readJSONFile(getUserDirectory(email).resolve("user.json"));
      user = JsonHelper.fromJson(json, User.class);
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    return user;
  }
}
