package variables;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class CardDay {
    @FXML
    private VBox cardDay;
    @FXML
    private Label labelCardDay;
    @FXML
    private Label labelCardEvent;

    public VBox getCardDay() {
        return cardDay;
    }
    public void  initialise(int day,int events){
        labelCardDay.setText(String.valueOf(day));
        labelCardEvent.setText("("+events+") Event(s)");
    }
    public void setTextDay(String text) {
        labelCardDay.setText(text);
    }

    public void setLabelEvent(String text) {
        labelCardEvent.setText(text);
    }
}
