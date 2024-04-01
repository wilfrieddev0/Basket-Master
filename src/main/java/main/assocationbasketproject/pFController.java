package main.assocationbasketproject;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.Parent;

import java.io.IOException;
import java.util.Objects;

public class pFController {
    @FXML
    private PasswordField inputConfirm;

    @FXML
    private PasswordField inputPassword;

    @FXML
    private Label labelTrace;

    @FXML
    private StackPane stackPane;

    @FXML
    void Home(MouseEvent event) throws IOException {
        stackPane.getChildren().removeAll();
        stackPane.getChildren().clear();
        Parent fxml = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("login.fxml")));
        stackPane.getChildren().setAll(fxml);
    }

    public void Submit(ActionEvent actionEvent) {
    }
}
