package jmail.client.models.model;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableBooleanValue;
import javafx.beans.value.ObservableObjectValue;
import javafx.beans.value.ObservableStringValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import jmail.lib.models.Email;
import jmail.lib.models.User;

public class DataModel {

    private static final DataModel instance = new DataModel();
    private final ObjectProperty<User> currentUser;
    private final SimpleStringProperty currentFolder; // TODO: Enum: inbox, sent, trash
    private final ObservableList<Email> inbox;
    private final ObservableList<Email> sent;
    private final ObservableList<Email> trash;

    private final ObservableList<Email> currentFilteredEmails; // Used for filtering emails to show bby search
    private final ObjectProperty<Email> currentEmail;

    // TODO: Preferiti, bozze, etichette
    private final SimpleBooleanProperty serverStatusConnected;

    private DataModel() {
        currentUser = new SimpleObjectProperty<>();
        currentFolder = new SimpleStringProperty();

        inbox = FXCollections.observableArrayList();
        sent = FXCollections.observableArrayList();
        trash = FXCollections.observableArrayList();
        currentFilteredEmails = FXCollections.observableArrayList();

        currentEmail = new SimpleObjectProperty<>();
        serverStatusConnected = new SimpleBooleanProperty();

        // FIXME: remove
        currentEmail.set(new Email(
                java.util.UUID.randomUUID().toString(),
                "Oggetto prova",
                "Buongiorno,\nAvrebbe due minuti per parlare del Nostro signore?",
                "mario@yahoo.it",
                List.of("emmedeveloper@gmail.com"),
                java.util.Calendar.getInstance().getTime(),
                false));
    }

    public static DataModel getInstance() {
        return instance;
    }

    public ObservableObjectValue<User> getCurrentUserProperty() {
        return currentUser;
    }

    public User getCurrentUser() {
        return currentUser.get();
    }

    public void setCurrentUser(User user) {
        currentUser.set(user);
    }

    public ObservableStringValue getCurrentFolderProperty() {
        return currentFolder;
    }

    public String getCurrentFolder() {
        return currentFolder.get();
    }

    public void setCurrentFolder(String folder) {
        currentFolder.set(folder);
        syncFilteredEmails();
    }

    public ObservableList<Email> getInbox() {
        return inbox;
    }

    public void setInbox(List<Email> emails) {
        inbox.setAll(emails);
    }

    public ObservableList<Email> getSent() {
        return sent;
    }

    public void setSent(List<Email> emails) {
        sent.setAll(emails);
    }

    public ObservableList<Email> getTrash() {
        return trash;
    }

    public void setTrash(List<Email> emails) {
        trash.setAll(emails);
    }

    public ObservableList<Email> getCurrentFilteredEmails() {
        return currentFilteredEmails;
    }

    public ObservableObjectValue<Email> getCurrentEmailProperty() {
        return currentEmail;
    }

    public Optional<Email> getCurrentEmail() {
        return Optional.ofNullable(currentEmail.get());
    }

    public void setCurrentEmail(Email email) {
        currentEmail.set(email);
    }

    public ObservableBooleanValue isServerStatusConnected() {
        return serverStatusConnected;
    }

    public void setServerStatusConnected(boolean serverStatusConnected) {
        this.serverStatusConnected.set(serverStatusConnected);
    }

    public void removeCurrentEmail() {
        switch (currentFolder.get()) {
            case "inbox" -> inbox.remove(currentEmail.get());
            case "sent" -> sent.remove(currentEmail.get());
            case "trash" -> trash.remove(currentEmail.get());
        }
        syncFilteredEmails();
        currentEmail.set(null);
    }

    public void addEmail(String folder, Email... emails) {
        // isEmpty
        if (Arrays.stream(emails).findAny().isEmpty()) {
            return;
        }

        // append array to start of list
        switch (folder) {
            case "inbox" -> inbox.addAll(0, Arrays.asList(emails));
            case "sent" -> sent.addAll(0, Arrays.asList(emails));
            case "trash" -> trash.addAll(0, Arrays.asList(emails));
        }

        if (folder.equalsIgnoreCase(getCurrentFolder())) {
            syncFilteredEmails();
        }
    }

    private void syncFilteredEmails() {
        // TODO: implement search filter logic
        switch (currentFolder.get()) {
            case "inbox" -> currentFilteredEmails.setAll(inbox);
            case "sent" -> currentFilteredEmails.setAll(sent);
            case "trash" -> currentFilteredEmails.setAll(trash);
        }
    }
}
