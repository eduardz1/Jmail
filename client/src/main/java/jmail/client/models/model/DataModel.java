package jmail.client.models.model;

import java.util.List;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
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
  private final ObjectProperty<Email> currentEmail;

  // TODO: Preferiti, bozze, etichette
  private final SimpleBooleanProperty serverStatusConnected;

  private DataModel() {
    currentUser = new SimpleObjectProperty<>();
    currentFolder = new SimpleStringProperty();

    inbox = FXCollections.emptyObservableList();
    sent = FXCollections.emptyObservableList();
    trash = FXCollections.emptyObservableList();

    currentEmail = new SimpleObjectProperty<>();
    serverStatusConnected = new SimpleBooleanProperty();

    // FIXME: remove
    currentEmail.set(new Email(java.util.UUID.randomUUID().toString(), "Oggetto prova", 
    "Buongiorno,\nAvrebbe due minuti per parlare del Nostro signore?", "mario@yahoo.it", List.of("emmedeveloper@gmail.com"), java.util.Calendar.getInstance().getTime(), false));
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

  public ObservableObjectValue<Email> getCurrentEmailProperty() {
    return currentEmail;
  }

  public Email getCurrentEmail() {
    return currentEmail.get();
  }

  public void setCurrentEmail(Email email) {
    currentEmail.set(email);
  }

  public boolean isServerStatusConnected() {
    return serverStatusConnected.get();
  }

  public void setServerStatusConnected(boolean serverStatusConnected) {
    this.serverStatusConnected.set(serverStatusConnected);
  }

  public void removeCurrentEmail() {
    switch (currentFolder.get()) {
      case "inbox":
        inbox.remove(currentEmail.get());
        break;
      case "sent":
        sent.remove(currentEmail.get());
        break;
      case "trash":
        trash.remove(currentEmail.get());
        break;
    }
    currentEmail.set(null);
  }
}
