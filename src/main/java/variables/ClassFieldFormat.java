package variables;

import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;

import java.util.function.UnaryOperator;

public class ClassFieldFormat {
    public static void formatField(TextField textField, String fieldType){
        String finalMotif = getMotif(fieldType);
        UnaryOperator<TextFormatter.Change> filter = change -> {
            if (change.getText().matches(finalMotif)) {
                return null;
            }
            return change;
        };
        TextFormatter<String> textFormatter = new TextFormatter<>(filter);
        textField.setTextFormatter(textFormatter);
    }

    private static String getMotif(String typeField) {
        String motif = null;
        if(typeField.equals("label")){
            motif =  "[^A-Za-z\\s]";
        }else if (typeField.equals("email")){
            motif =  "[^A-Za-z0-9\\@\\.+$]";
        } else if (typeField.equals("number")) {
            motif =  "[^0-9]";
        }else if (typeField.equals("text")){
            motif =  "[^A-Za-z0-9\\s]";
        }
        return motif;
    }
}
