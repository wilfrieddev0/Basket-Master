package variables;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;


public class ToastMessage {
    public static void show(String mTitle,String mMessage,int hideAfter) {
        Stage notificationStage = new Stage();
        notificationStage.initStyle(StageStyle.UNDECORATED);
        notificationStage.initModality(Modality.APPLICATION_MODAL);
        notificationStage.setTitle(mTitle);

        Label label = new Label(mMessage);
        StackPane layout = new StackPane();
        layout.getChildren().add(label);
        layout.setAlignment(Pos.CENTER);

        layout.setStyle("-fx-border-style:solid; -fx-border-radius:5px; -fx-border-color: gray");
        StackPane.setMargin(label, new javafx.geometry.Insets(20));

        Scene scene = new Scene(layout, 250, 100);
        notificationStage.setScene(scene);

        // Définir une timeline pour cacher la fenêtre après un délai de 3 secondes (modifiable selon vos besoins)
        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(hideAfter), event -> notificationStage.close()));
        timeline.play();

        notificationStage.showAndWait();
    }
}