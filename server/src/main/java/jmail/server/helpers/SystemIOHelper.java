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

    public static void createUserFolderIfNotExists(String userID) throws IOException {
        Path user = getUserDirectoryPath(userID);
        Files.createDirectories(user);

        Path sent = Paths.get(String.format("%s\\%s", user, "sent"));
        Path inbox = Paths.get(String.format("%s\\%s", user, "inbox"));
        Path deleted = Paths.get(String.format("%s\\%s", user, "deleted"));
        Files.createDirectories(sent);
        Files.createDirectories(inbox);
        Files.createDirectories(deleted);
    }

    public static Path getUserDirectoryPath(String userID) {
        return Paths.get(userpath + "\\" + userID);
    }

    public static Path getUserDeletedDirectoryPath(String userID) {
        return getUserSpecificPath(userID, "deleted");
    }

    public static Path getUserSentDirectoryPath(String userID) {
        return getUserSpecificPath(userID, "sent");
    }

    public static Path getUserInboxDirectoryPath(String userID) {
        return getUserSpecificPath(userID, "inbox");
    }

    public static void writeJSONFile(Path path, String name, String jsonObject) throws IOException {
        var writer = new FileWriter(path + "\\" + name);
        writer.write(jsonObject);
        writer.close();
    }

    public static String readJSONFile(Path path, String name) throws IOException {
        return Files.readString(Path.of(path + "\\" + name));
    }

    private static Path getUserSpecificPath(String userdID, String folder) {
        return Paths.get(String.format("%s\\%s", getUserDeletedDirectoryPath(userdID), folder));
    }

    public static void moveFile(Path from, Path to) throws IOException {
        Files.move(from, to, StandardCopyOption.ATOMIC_MOVE);
    }

}
