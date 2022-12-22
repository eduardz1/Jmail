module jmail.server {
  requires jmail.lib;
  requires org.slf4j;
  requires io.github.mimoguz.custom_window;
  requires javafx.fxml;
  requires com.fasterxml.jackson.core;
  requires org.fxmisc.richtext;
  requires static lombok;
  requires javafx.controls;
  requires javafx.graphics;

  opens jmail.server.controllers to javafx.fxml;
    exports jmail.server;

}