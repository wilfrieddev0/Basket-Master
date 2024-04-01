package main.assocationbasketproject;

import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DateCell;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.util.Callback;

import java.time.LocalDate;

public class Setting {

    @FXML
    private ChoiceBox<?> cbMonths;

    @FXML
    private GridPane gridPane;

    void  initialise(){
        gridPane.setAlignment(Pos.CENTER);
        gridPane.setHgap(10);
        gridPane.setVgap(10);

        DatePicker datePicker = new DatePicker();
        datePicker.show();
            // Personnaliser les cellules du DatePicker
        datePicker.setDayCellFactory(new Callback<DatePicker, DateCell>() {
            @Override
            public DateCell call(DatePicker param) {
                return new DateCell() {
                    @Override
                    public void updateItem(LocalDate date, boolean empty) {
                        super.updateItem(date, empty);

                        if (!empty) {
                            // Créer une carte personnalisée dans chaque cellule
                            VBox card = new VBox();
                            card.setAlignment(Pos.CENTER);
                            card.getStyleClass().add("date-cell");

                            // Créer un AnchorPane en haut avec la date
                            AnchorPane topAnchor = new AnchorPane();
                            Label dateLabel = new Label(String.valueOf(date.getDayOfMonth()));
                            topAnchor.getChildren().add(dateLabel);
                            AnchorPane.setTopAnchor(dateLabel, 10.0);
                            AnchorPane.setLeftAnchor(dateLabel, 10.0);

                            // Créer un AnchorPane en bas avec un label quelconque
                            AnchorPane bottomAnchor = new AnchorPane();
                            Label customLabel = new Label("Label personnalisé");
                            bottomAnchor.getChildren().add(customLabel);
                            AnchorPane.setBottomAnchor(customLabel, 10.0);
                            AnchorPane.setLeftAnchor(customLabel, 10.0);

                            // Ajouter les AnchorPane à la carte
                            card.getChildren().addAll(topAnchor, bottomAnchor);

                            // Afficher la carte personnalisée dans la cellule
                            setGraphic(card);
                        }
                    }
                };
            }
        });

        cbMonths.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                datePicker.setValue(null);
                datePicker.setPromptText((String) newValue);
            }
        });
        gridPane.add(datePicker, 1, 0);
    }

}
