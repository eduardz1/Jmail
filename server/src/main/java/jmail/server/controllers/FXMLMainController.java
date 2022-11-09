package jmail.server.controllers;

import java.net.URL;
import java.time.Duration;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextArea;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.LineNumberFactory;
import org.fxmisc.richtext.model.StyleSpans;
import org.fxmisc.richtext.model.StyleSpansBuilder;
import org.reactfx.Subscription;

public class FXMLMainController implements Initializable {
  private static final String sampleCode =
          "Trace: 2019-01-01T00:00:00.0000000+00:00 [1] Microsoft.AspNetCore.Hosting.Internal.WebHost: Information: Request starting HTTP/1.1 GET http://localhost:5000/ \n" +
                  "27.01.2018 19:38:28,982 [8988] [verbose] [Application]: Hello World! \n" +
          "2018-01-27T10:38:24.593Z - error: It Crashed!";

  private static final String TRACE_PATTERN = "\\b(Trace)\\b:";
  private static final String DEBUG_PATTERN = "\\b(DEBUG|Debug)\\b|(?i)\\b(debug):";
  private static final String INFO_PATTERN =
          "\\b(HINT|INFO|INFORMATION|Info|NOTICE|II)\\b|(?i)\\b(info|information):";
  private static final String WARN_PATTERN = "\\b(WARNING|WARN|Warn|WW)\\b|(?i)\\b(warning):";
  private static final String ERROR_PATTERN =
          "\\b(ALERT|CRITICAL|EMERGENCY|ERROR|FAILURE|FAIL|Fatal|FATAL|Error|EE)\\b|(?i)\\b(error):";
  private static final String ISODATES_PATTERN = "\\b\\d{4}-\\d{2}-\\d{2}(T|\\b)";
  private static final String TIME_PATTERN =
          "\\d{1,2}:\\d{2}(:\\d{2}([.,]\\d+)?)?(Z| ?[+-]\\d{1,2}:\\d{2})?\\b";
  private static final String GUID_PATTERN =
          "\\b[0-9a-fA-F]{8}-?([0-9a-fA-F]{4}-?){3}[0-9a-fA-F]{12}\\b";
  private static final String CONSTANTS_PATTERN = "\\b([0-9]+|true|false|null)\\b";
  private static final String HEXCONSTANTS_PATTERN = "\\b(0x[a-fA-F0-9]+)\\b";
  private static final String STRINGCONSTANTS_PATTERN = "\"[^\"]*\"" + "(?<!\\w)'[^']*'";
  private static final String EXCEPTIONS_PATTERN = "\\b([a-zA-Z.]*Exception)\\b";
  private static final String URL_PATTERN = "\\b[a-z]+://\\S+\\b/?";
  private static final String NAMESPACES_PATTERN =
          "(?<![\\w/\\\\])([\\w-]+\\.)+([\\w-])+(?![\\w/\\\\])";

  private static final String OTHER_PATTERN = "\\S+";
  private static final Pattern PATTERN =
          Pattern.compile(
                  "(?<TRACE>"
                          + TRACE_PATTERN
                          + ")"
                          + "|(?<DEBUG>"
                          + DEBUG_PATTERN
                          + ")"
                          + "|(?<INFO>"
                          + INFO_PATTERN
                          + ")"
                          + "|(?<WARN>"
                          + WARN_PATTERN
                          + ")"
                          + "|(?<ERROR>"
                          + ERROR_PATTERN
                          + ")"
                          + "|(?<ISODATES>"
                          + ISODATES_PATTERN
                          + ")"
                          + "|(?<TIME>"
                          + TIME_PATTERN
                          + ")"
                          + "|(?<GUID>"
                          + GUID_PATTERN
                          + ")"
                          + "|(?<CONSTANTS>"
                          + CONSTANTS_PATTERN
                          + ")"
                          + "|(?<HEXCONSTANTS>"
                          + HEXCONSTANTS_PATTERN
                          + ")"
                          + "|(?<STRINGCONSTANTS>"
                          + STRINGCONSTANTS_PATTERN
                          + ")"
                          + "|(?<EXCEPTIONS>"
                          + EXCEPTIONS_PATTERN
                          + ")"
                          + "|(?<URL>"
                          + URL_PATTERN
                          + ")"
                          + "|(?<NAMESPACES>"
                          + NAMESPACES_PATTERN
                          + ")"
                          + "|(?<OTHER>"
                            + OTHER_PATTERN
                            + ")");
  @FXML private CodeArea codeArea;
  @FXML private TextArea delay;

  public void setTopText(String text) {
    // set text from another class
    delay.appendText("\n");
    delay.appendText(text);
  }

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    codeArea.richChanges()
            .filter(ch -> !ch.getInserted().equals(ch.getRemoved()))
            .subscribe(change -> codeArea.setStyleSpans(0, computeHighlighting(codeArea.getText())));

    codeArea.replaceText(0, 0, sampleCode);
  }

  private StyleSpans<Collection<String>> computeHighlighting(String text) {
    System.out.println("computing highlighting on " + text);
    Matcher matcher = PATTERN.matcher(text);
    int lastKwEnd = 0;
    StyleSpansBuilder<Collection<String>> spansBuilder = new StyleSpansBuilder<>();
    while (matcher.find()) {
      String styleClass =
              matcher.group("TRACE") != null
                      ? "trace"
                      : matcher.group("DEBUG") != null
                      ? "debug"
                      : matcher.group("INFO") != null
                      ? "info"
                      : matcher.group("WARN") != null
                      ? "warn"
                      : matcher.group("ERROR") != null
                      ? "error"
                      : matcher.group("ISODATES") != null
                      ? "isodates"
                      : matcher.group("TIME") != null
                      ? "time"
                      : matcher.group("GUID") != null
                      ? "guid"
                      : matcher.group("CONSTANTS") != null
                      ? "constants"
                      : matcher.group("HEXCONSTANTS") != null
                      ? "hexconstants"
                      : matcher.group("STRINGCONSTANTS") != null
                      ? "stringconstants"
                      : matcher.group("EXCEPTIONS") != null
                      ? "exceptions"
                      : matcher.group("URL") != null
                      ? "url"
                      : matcher.group("NAMESPACES") != null
                      ? "namespaces"
                        : matcher.group("OTHER") != null
                        ? "other"
                      : null; /* never happens */
      assert styleClass != null;
      spansBuilder.add(Collections.emptyList(), matcher.start() - lastKwEnd);
      spansBuilder.add(Collections.singleton(styleClass), matcher.end() - matcher.start());
      lastKwEnd = matcher.end();
    }
    spansBuilder.add(Collections.emptyList(), text.length() - lastKwEnd);
    return spansBuilder.create();
  }
}
