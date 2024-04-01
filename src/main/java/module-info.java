module com.example.assocationbasketproject {
    requires javafx.controls;
    requires javafx.fxml;
    requires jdk.httpserver;

    requires net.synedra.validatorfx;
    requires java.sql;
    requires com.google.api.client.extensions.java6.auth;
    requires google.api.client;
    requires com.google.api.client;
    requires com.google.api.services.gmail;
    requires com.google.api.client.json.gson;
    requires com.google.api.client.extensions.jetty.auth;
    requires mail;
    requires org.apache.commons.codec;
    requires java.desktop;
    requires controlsfx;
    requires  com.jfoenix;
    requires google.maps.services;

    opens main.assocationbasketproject to javafx.fxml;
    opens main.assocationbasketproject.dialog to javafx.fxml;
    exports main.assocationbasketproject;
    exports main.assocationbasketproject.dialog;
    exports db;
    exports variables;
    opens db to javafx.fxml;
    opens variables to javafx.fxml;

}