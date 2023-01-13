package jmail.client.controllers;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import javafx.application.Platform;
import javafx.collections.ListChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import jmail.client.Main;
import jmail.client.factory.EmailCellFactory;
import jmail.client.models.model.DataModel;
import jmail.lib.autocompletion.textfield.AutoCompletionBinding;
import jmail.lib.autocompletion.textfield.TextFields;
import jmail.lib.constants.Folders;
import jmail.lib.models.Email;
import me.xdrop.fuzzywuzzy.FuzzySearch;
import me.xdrop.fuzzywuzzy.model.BoundExtractedResult;

public class FXMLListEmailController extends AnchorPane {

  @FXML public AnchorPane root;

  private FXMLController mainController;

  /*
   * Email views
   */
  @FXML private ListView<Email> listEmails;

  @FXML private Button searchButton;

  @FXML private Label currentFolder;

  @FXML private TextField searchField;

  /*
   * Search suggestions
   */
  private final Set<String> suggestions = new HashSet<>();
  private transient AutoCompletionBinding<String> autoCompletionBinding;

  public FXMLListEmailController() {
    // Load
    FXMLLoader loader = new FXMLLoader(Main.class.getResource("list_email.fxml"));
    loader.setController(this);
    try {
      loader.load();
      initViews();
      initListeners();
    } catch (IOException exception) {
      throw new RuntimeException(exception);
    }
  }

  public void setMainController(FXMLController mainController) {
    this.mainController = mainController;
  }

  public void initListeners() {

    autoCompletionBinding = TextFields.bindAutoCompletion(searchField, suggestions);

    searchField.setOnKeyPressed(event -> {
      if (event.getCode() == javafx.scene.input.KeyCode.ENTER) {
        // LOGGER.info("SearchField: {}", searchField.getText().trim());
        buttonSearch(null);
        learnWord(searchField.getText().trim());
      }
    });

    searchField.textProperty().addListener((observable, oldValue, newValue) -> {
      if (newValue.isEmpty()) { // FIXME: non funziona ma basta cambiare folder per ripristinare la lista
        mainController.listEmails(DataModel.getInstance().getCurrentFolder());
      }
    });

    DataModel.getInstance().getCurrentFilteredEmails()
        .addListener((ListChangeListener<Email>) c -> Platform.runLater(() -> {
          listEmails.getItems().clear();
          var list = c.getList();
          listEmails.getItems().addAll(list);

          list.stream().map(Email::getSubject).forEach(this::learnWord);
          DataModel.getInstance().getCurrentEmail().ifPresent(email -> listEmails.getSelectionModel().select(email));
        }));

    listEmails.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
      if (newValue == null || newValue.equals(oldValue)) {
        return;
      }
      DataModel.getInstance().setCurrentEmail(newValue);
      DataModel.getInstance().setEditingMode(false);

      if (!newValue.getRead()) {
        mainController.readEmail(newValue);
      }
    });

    DataModel.getInstance().getCurrentEmailProperty().addListener((observable, oldValue, newValue) -> {
      if (newValue == null || listEmails.getSelectionModel().getSelectedItem() == null
          || !listEmails.getSelectionModel().getSelectedItem().equals(newValue)) {
        listEmails.getSelectionModel().clearSelection();
      }
    });

    DataModel.getInstance().getCurrentFolderProperty().addListener((observable, oldValue, newValue) -> {
      currentFolder.textProperty().set(newValue.toUpperCase());
    });
  }

  public void initViews() {

    // Set listEmails view graphic
    listEmails.setCellFactory(new EmailCellFactory(listEmails));
    listEmails.setStyle("-fx-background-insets: 1 ;");

    // listEmails.set
  }

  @FXML
  public void buttonSearch(ActionEvent e) {
    var text = searchField.textProperty().getValueSafe();
    var currentFolder = DataModel.getInstance().getCurrentFolder();
    var emails = switch (currentFolder) {
    case Folders.INBOX -> DataModel.getInstance().getInbox();
    case Folders.SENT -> DataModel.getInstance().getSent();
    case Folders.TRASH -> DataModel.getInstance().getTrash();
    default -> throw new IllegalStateException("Unexpected value: " + currentFolder);
    };
    if (text.isBlank()) {
      return;
    }

    var filtered = FuzzySearch.extractTop(text, emails,
        email -> email.getSubject() + " " + email.getSender() + " " + email.getBody(), 5);
    DataModel.getInstance()
        .setFilteredEmails(filtered.stream().map(BoundExtractedResult::getReferent).collect(Collectors.toList()));
  }

  private void learnWord(String trim) {
    suggestions.add(trim);
    if (autoCompletionBinding != null) {
      autoCompletionBinding.dispose();
    }

    autoCompletionBinding = TextFields.bindAutoCompletion(searchField, suggestions);

    // Set autocompletation popup width
    autoCompletionBinding.getAutoCompletionPopup().prefWidthProperty().bind(searchField.widthProperty());
  }
}
