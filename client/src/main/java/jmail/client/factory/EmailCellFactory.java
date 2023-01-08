package jmail.client.factory;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.Border;
import javafx.util.Callback;
import jmail.client.views.EmailCell;
import jmail.lib.constants.ColorPalette;
import jmail.lib.models.Email;

public class EmailCellFactory implements Callback<ListView<Email>, ListCell<Email>> {

  private ListView<Email> parent;

  public EmailCellFactory(ListView<Email> listEmails) {
    parent = listEmails;
  }

  @Override
  public ListCell<Email> call(ListView<Email> param) {
    var cell = new EmailCell();  
    cell.prefWidthProperty().bind(parent.widthProperty().subtract(30));
    cell.maxWidthProperty().bind(parent.widthProperty().subtract(30));
    // cell.setBorder(new Border(new BorderStroke(ColorPalette.TEXT.getColor(), BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));
    return cell;
  }
}