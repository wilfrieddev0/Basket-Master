package main.assocationbasketproject;
import db.ClassCoach;
import db.ConnexionASdb;
import db.ReturnCheck;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import javax.swing.*;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.Objects;
import java.util.Properties;
import java.util.ResourceBundle;

import static javafx.scene.paint.Color.GREEN;
import static javafx.scene.paint.Color.RED;
public class LoginController implements Initializable {
    @FXML
    private RadioButton IntAgree;
    @FXML
    private RadioButton intAgree;
    @FXML
    private Region LinkHome;
    @FXML
    private TextField inpuLogin;
    @FXML
    private PasswordField inputPassword;
    @FXML
    private Label labelTrace;
    @FXML
    private StackPane stackPane;
    @FXML
    void GitHubConect(ActionEvent event) {}
    @FXML
    void GoogleConnect(ActionEvent event) {}
    @FXML
    void LinkMdpOublié(ActionEvent event) throws IOException {
        stackPane.getChildren().removeAll();
        stackPane.getChildren().clear();
        Parent fxml =  FXMLLoader.load(Objects.requireNonNull(getClass().getResource("passwordForget.fxml")));
        stackPane.getChildren().setAll(fxml);
    }
    @FXML
    void btnConnecter() throws Exception {
       if (!inpuLogin.getText().isEmpty() && !inputPassword.getText().isEmpty()){
           try{
               ConnexionASdb connexionASdb =  new ConnexionASdb();
               ReturnCheck result = connexionASdb.checkCredentials(inpuLogin.getText(),inputPassword.getText());
               if (result.id > 0 ){
                   labelTrace.setText("Correct");
                   labelTrace.setTextFill(GREEN);
                   //Je ferme la fenêtre courante,je set le coach et j'ouvre la fenêtre principale
                   ClassCoach.getInstance().setId(result.id);
                   ((Stage) labelTrace.getScene().getWindow()).close();

                   Stage stage = new Stage();
                   FXMLLoader fxml = new FXMLLoader(Objects.requireNonNull(getClass().getResource("demarrage.fxml")));
                   Scene scene = new Scene(fxml.load(),1198
                           ,740);
                   stage.setTitle("Association basket App");
                   stage.setScene(scene);
                   stage.show();
               }else{
                   labelTrace.setText(result.text);
                   labelTrace.setTextFill(RED);
               }
           }catch (SQLException e){
               JOptionPane.showConfirmDialog(null,"La connexion au serveur a échoué!\nRassurez-vous que le serveur est bien lancé et reessayez\nDetails : "+e.getMessage(),"Erreur connection : ", JOptionPane.DEFAULT_OPTION);
           }
       }else inpuLogin.requestFocus();
    }
    @FXML
    void linkPasCompte(ActionEvent event) throws IOException {
        stackPane.getChildren().removeAll();
        stackPane.getChildren().clear();
        Parent fxml = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("logon.fxml")));
        stackPane.getChildren().setAll(fxml);
    }
    @FXML
    void saveCredentials(ActionEvent event) throws Exception {
        Properties credentiales  =  new Properties();
        String path = "src/main/resources/config/credentials.properties";
        FileInputStream inFile  =  new FileInputStream(path);
        credentiales.load(inFile);

        credentiales.setProperty("login",inpuLogin.getText());
        credentiales.setProperty("password",inputPassword.getText());
        FileOutputStream outFile = new FileOutputStream(path);
        credentiales.store(outFile,null);
    }
    void loadCredential() throws Exception{
        Properties credentials  =  new Properties();
        FileInputStream file  =  new FileInputStream("src/main/resources/config/credentials.properties");
        credentials.load(file);
        inpuLogin.setText(credentials.getProperty("login"));
        inputPassword.setText(credentials.getProperty("password"));
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            loadCredential();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
