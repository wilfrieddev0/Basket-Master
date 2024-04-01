package main.assocationbasketproject;

import db.ClassCoach;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import manager.ClassManager;

import javax.swing.*;
import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

public class DemarrageController implements Initializable {
    @FXML
    private StackPane stackPane;
    @FXML
    private Button btnPlayer;
    @FXML
    private Button btnCategories;
    @FXML
    private Button btnLogout;
    @FXML
    private Button btnHome;
    @FXML
    private Button btnStats;
    @FXML
    private Label lName;
    @FXML
    private Circle circleProfile;
    @FXML
    void handlePane(ActionEvent event) throws IOException {
        Button currentBtn =  (Button) event.getSource();
        FXMLLoader fxmlLoader = new FXMLLoader();
        if (currentBtn.equals(btnHome)){
            fxmlLoader.setLocation(getClass().getResource("homePane.fxml"));
        } else if (currentBtn.equals(btnCategories)) {
            fxmlLoader.setLocation(getClass().getResource("category.fxml"));
        } else if (currentBtn.equals(btnPlayer)) {
            fxmlLoader.setLocation(getClass().getResource("player.fxml"));
        } else if (currentBtn.equals(btnStats)) {
            fxmlLoader.setLocation(getClass().getResource("stats.fxml"));
        }
        Parent pane =  fxmlLoader.load();
        stackPane.getChildren().removeAll();
        stackPane.getChildren().clear();
        stackPane.setPrefSize(200,150);
        stackPane.getChildren().setAll(pane);
    }
    @FXML
    void logout(ActionEvent event) throws IOException {
        if(JOptionPane.showConfirmDialog(null,"Etes-vous sûr de vouloir sortir?\n Si oui, vous allez être dirigé vers la fenêtre de connexion","Exit",JOptionPane.YES_NO_OPTION)==0){
            ((Stage)(((Button) event.getSource()).getScene().getWindow())).close();
            Stage stage = new Stage();
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("login.fxml"));
            Scene scene = new Scene(fxmlLoader.load(),830,750);
            stage.setScene(scene);
            stage.setTitle("Association basket App");
            stage.setResizable(false);
            stage.show();
        }
    }
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            ClassCoach coach = ClassCoach.getInstance();
            ClassManager manager = ClassManager.getUniqueInstance();
            manager.setId(coach.getId());
            String path = coach.loadMedia("profile");
            Image image = new Image("file:"+path);
            circleProfile.setFill(new ImagePattern(image));
            coach.initialiseCoatch();
            lName.setText(coach.getName() + " " + coach.getLastName());
            // Definition du plan par defaut
            Parent defaultPane = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("homePane.fxml")));
            stackPane.getChildren().removeAll();
            stackPane.getChildren().setAll(defaultPane);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }
}
