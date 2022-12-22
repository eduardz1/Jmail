module jmail.lib {
  requires com.fasterxml.jackson.annotation;
  requires com.fasterxml.jackson.databind;
  requires ch.qos.logback.classic;
  requires ch.qos.logback.core;
  requires javafx.base;
  requires javafx.graphics;
  requires javafx.controls;
  requires lombok;

  exports jmail.lib.autocompletion.skin;
    exports jmail.lib.autocompletion;
    exports jmail.lib.autocompletion.textfield;

    exports jmail.lib.constants;

    exports jmail.lib.deserializers;

    exports jmail.lib.exceptions;

    exports jmail.lib.helpers;

    exports jmail.lib.layouts;

    exports jmail.lib.logger;

  exports jmail.lib.models.commands;
  exports jmail.lib.models;
}