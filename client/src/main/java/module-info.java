module jmail.client {
  requires io.github.mimoguz.custom_window;
  requires javafx.fxml;
  requires javafx.graphics;
  requires jmail.lib;
  requires com.fasterxml.jackson.core;
  requires org.slf4j;
  requires javafx.controls;
  requires com.google.common;
  requires static lombok;

  opens jmail.client.controllers to javafx.fxml;
  exports jmail.client;
}