package jmail.server.helpers;

import java.io.*;
import java.nio.file.*;

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

  public static String readJSONFile(Path path, String name) throws IOException {
    return Files.readString(Path.of(path + "/" + name));
  }

  // FIXME: non penso che dovremmo tenere due funzioni che come unica cosa
  // mascherano la option utilizzata che tra l'altro può avere solo due valori

  // public static void moveFile(Path from, Path to) throws IOException {
  //   Files.move(from, to, StandardCopyOption.ATOMIC_MOVE);
  // }

  // public static void copyFile(Path from, Path to) throws IOException {
  //   Files.copy(from, to, StandardCopyOption.REPLACE_EXISTING);
  // }

  // TODO: take a look at this method later
  public static Boolean userExists(String userEmail) {
    Path user = Paths.get(emailpath);
    File f = new File(Paths.get(String.format("%s\\%s.dat", user, userEmail)).toUri());
    return f.exists() && !f.isDirectory();
  }
}
