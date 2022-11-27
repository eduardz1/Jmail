package jmail.server.helpers;

import java.io.*;
import java.nio.file.*;

public class SystemIOHelper {

    // TODO : Path si legge da configurazione
    private static final String basepath = "C:\\Unito\\Terzo anno\\server";
    private static final String emailpath = basepath + "\\email";
    private static final String userpath = basepath + "\\users";

    public static void createBaseFoldersIfNotExists() throws IOException {
        Path base = Paths.get(basepath);
        Files.createDirectories(base);
        Path user = Paths.get(userpath);
        Files.createDirectories(user);
        Path email = Paths.get(emailpath);
        Files.createDirectories(email);
    }

    public static void createUserFolderIfNotExists(String userEmail) throws IOException {
        Path user = getUserDirectoryPath(userEmail);
        Files.createDirectories(user);

        Path sent = Paths.get(String.format("%s\\%s", user, "sent"));
        Path inbox = Paths.get(String.format("%s\\%s", user, "inbox"));
        Path deleted = Paths.get(String.format("%s\\%s", user, "deleted"));
        Files.createDirectories(sent);
        Files.createDirectories(inbox);
        Files.createDirectories(deleted);
    }

    public static Path getUserDirectoryPath(String userEmail) {
        return Paths.get(userpath + "\\" + userEmail);
    }

    public static Path getUserDeletedDirectoryPath(String userEmail) {
        return getUserSpecificPath(userEmail, "deleted");
    }

    public static Path getUserSentDirectoryPath(String userEmail) {
        return getUserSpecificPath(userEmail, "sent");
    }

    public static Path getUserInboxDirectoryPath(String userEmail) {
        return getUserSpecificPath(userEmail, "inbox");
    }

    public static void writeJSONFile(Path path, String name, String jsonObject) throws IOException {
        var writer = new FileWriter(path + "\\" + name);
        writer.write(jsonObject);
        writer.close();
    }

    public static String readJSONFile(Path path, String name) throws IOException {
        return Files.readString(Path.of(path + "\\" + name));
    }

    private static Path getUserSpecificPath(String userEmail, String folder) {
        return Paths.get(String.format("%s\\%s", getUserDirectoryPath(userEmail), folder));
    }

    public static void moveFile(Path from, Path to) throws IOException {
        Files.move(from, to, StandardCopyOption.ATOMIC_MOVE);
    }

    public static Boolean userExists(String userEmail) {
        Path user = Paths.get(userpath);
        File f = new File(Paths.get(String.format("%s\\%s.dat", user, userEmail)).toUri());
        return f.exists() && !f.isDirectory();
    }

}
