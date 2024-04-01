package main.assocationbasketproject;

import db.ClassCoach;
import db.ConnexionASdb;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import service.ClassGmail;
import variables.ClassFieldFormat;
import variables.ToastMessage;

import javax.mail.MessagingException;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.NoSuchAlgorithmException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LogonController implements Initializable {
    @FXML
    private AnchorPane anchorPane;
    @FXML
    private Button btnSignUp;
    @FXML
    private Button btnSubmit;
    @FXML
    private TextField fEmail;
    @FXML
    private TextField fLastName;
    @FXML
    private TextField fName;
    @FXML
    private TextField fLogin;
    @FXML
    private PasswordField fPassword;
    @FXML
    private PasswordField fConfirm;
    @FXML
    private RadioButton intAgree;
    @FXML
    private PasswordField intCodeCheck;
    @FXML
    private Label labelTrace;
    @FXML
    private StackPane stackPane;
    @FXML
    private Circle circleProfile;
    private int code;
    private  int attemp;
    private ConnexionASdb connexionASdb;
    private PreparedStatement statement;
    @FXML
    void CheckEmail(){
        if (!(Objects.equals(intCodeCheck.getText(), ""))){
            if (Integer.parseInt(intCodeCheck.getText()) == code){
                intCodeCheck.setStyle("-fx-border-color:green");
                labelTrace.setText("Code correct");
                labelTrace.setTextFill(Color.GREEN);
                btnSignUp.setDisable(false);
            }else if (attemp < 3){
                intCodeCheck.setStyle("-fx-border-color:red");
                labelTrace.setText("Code incorrect");
                attemp++;
            }else{
                labelTrace.setText("Changez votre email et refaites renvoyer un nouveau code");
            }
        }
    }
    @FXML
    void SendEmailCode() throws MessagingException, IOException {
        if (!fEmail.getText().isEmpty()){
            ArrayList<String> recipients =  new ArrayList<>();
            recipients.add(fEmail.getText());
          code =  ClassGmail.sendEmail("check",null,recipients,null,null);
          btnSubmit.setDisable(false);
        }
    }
    @FXML
    void Discard() throws IOException {
        LinkHaveAccount();
    }
    @FXML
    void LinkHaveAccount() throws IOException {
        stackPane.getChildren().removeAll();
        stackPane.getChildren().clear();
        Parent fxml =  FXMLLoader.load(Objects.requireNonNull(getClass().getResource("login.fxml")));
        stackPane.getChildren().add(fxml);
    }
    @FXML
    void uploadProfile() throws IOException {
        JFileChooser fileChooser = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter(
                "JPG & GIF Images", "jpg", "gif","jpeg");
        fileChooser.setFileFilter(filter);
        int returnChoose = fileChooser.showOpenDialog(null);
        if (returnChoose ==  JFileChooser.APPROVE_OPTION){
            Path sourceFile  = Path.of(fileChooser.getSelectedFile().getPath());
            Path targetFile =  Paths.get("src/main/resources/Image/"+sourceFile.getFileName());
            Files.copy(sourceFile,targetFile, StandardCopyOption.REPLACE_EXISTING);
            Image image =  new Image("file:"+ targetFile);
            circleProfile.setFill(new ImagePattern(image));
        }else ToastMessage.show("Info","Le fichier entré n'est pas pris en compte",3);
    }
    @FXML
    void signUp(ActionEvent event) throws Exception {
        if(intAgree.isSelected()){
            if (!checkFields()){
                if (labelTrace.getText().equals("Code correct")){
                    // Insertation dans la table ba_coach
                    try {
                        connexionASdb = new ConnexionASdb();
                        int idCoach =  insertCoach();
                        if (idCoach>0){
                            // Insert dans la table ba_media dans la table d'associaction
                            int idMedia;
                            Paint fill = circleProfile.getFill();
                            Image image =  null;
                            if (fill instanceof ImagePattern) image =  ((ImagePattern) fill).getImage();
                            if (image!=null){
                                String[] fields = new String[]{"description", "typeMime", "dateCreation", "path"};
                                DateTimeFormatter f = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
                                String[] values = new String[]{"profile", "Image", (LocalDateTime.now()).format(f), image.getUrl().split(":")[1]};
                                idMedia  =  connexionASdb.insert("ba_media",fields,values);
                                if (idMedia > 0){
                                    fields =  new String[]{"idMedia","idCoach"};
                                    values = new String[]{String.valueOf(idMedia), String.valueOf(idCoach)};
                                    connexionASdb.insert("ba_middlemediacoach",fields,values);
                                }
                            }
                            //Initialisation de l'unique instance de Coach;
                            ClassCoach.getInstance().setId(idCoach);
                            ((Stage)(((Button) event.getSource()).getScene()).getWindow()).close();

                            Stage stage = new Stage();
                            FXMLLoader fxml =  new FXMLLoader(Objects.requireNonNull(getClass().getResource("demarrage.fxml")));
                            Scene scene  =  new Scene(fxml.load(),1198,740);
                            stage.setScene(scene);
                            stage.setTitle("Association basket App");
                            stage.show();

                        }
                    } catch (SQLException e) {
                        JOptionPane.showConfirmDialog(null,"La connexion au serveur a été interrompue\nRassurez-vous que le serveur est bien lancé demarrer le serveur et reessayez\nDetails : "+e.getMessage(),"Erreur connection : ", JOptionPane.DEFAULT_OPTION);
                    }
               }else labelTrace.setFocusTraversable(true);
           }else ToastMessage.show("Info","Verifier que tous les champs soient remplies ou que les mots de passe sont identiques",4);fName.setFocusTraversable(true);
       }else ToastMessage.show("Info","Veiller accepter les conditions de l'application",2);
    }
    @FXML
    void checkPassword(KeyEvent event) {
        PasswordField current = (PasswordField) event.getSource();
        if (current.getText().length() >= 6 && current.getText().length() <= 8){
            current.setStyle("-fx-border-color:green");
        }else current.setStyle("-fx-border-color:red");
    }
    @FXML
    void confirmPassword(KeyEvent event){
        PasswordField current = (PasswordField) event.getSource();
        if(!Objects.equals(current.getText(), current.getText())){
            current.setStyle("-fx-border-color:red");
        } current.setStyle("-fx-border-color:green");
    }
    @FXML
    void handleText(KeyEvent event){
        Pattern motif =  Pattern.compile("[^A-Za-z\\s]");
        TextField currentField  = (TextField) event.getSource();
        Matcher matcher = motif.matcher(currentField.getText());
        if (matcher.find()){
            currentField.setStyle("-fx-border-color:red");
        }else{
            currentField.setStyle("-fx-border-color:green");
        }
    }

    @FXML
    void checkInputEmail(KeyEvent event) {
        Pattern motif =  Pattern.compile("[A-Za-z0-9\\@\\.+$]");
        TextField currentField  = (TextField) event.getSource();
        Matcher matcher = motif.matcher(currentField.getText());
        if (!matcher.find()){
            System.out.println(matcher);
            currentField.setStyle("-fx-border-color:red");
        }else{
            currentField.setStyle("-fx-border-color:green");
        }
    }
    @FXML
    void handleField(MouseEvent event){
        TextField currentField =  (TextField) event.getSource();
        ClassFieldFormat.formatField(currentField,getMotif(currentField));
    }
    private String getMotif(TextField currentField) {
        String motif = null;
        if(currentField.equals(fName) || currentField.equals(fLastName) || currentField.equals(fLogin)){
            motif =  "label";
        }else if (currentField.equals(fEmail)){
            motif =  "email";
        } else if (currentField.equals(intCodeCheck)){
            motif =  "number";
        }else if (currentField.equals(fPassword) || currentField.equals(fConfirm) ){
            motif =  "text";
        }
        return motif;
    }
    private  int insertCoach() throws SQLException, NoSuchAlgorithmException {
        int idReturn = 0;
        String sql = "INSERT INTO ba_coach (lastName,firstName,email,login,password) VALUES (?,?,?,?,?);";
        statement =  connexionASdb.getConnection().prepareStatement(sql);
        statement.setString(1,fName.getText());
        statement.setString(2,fLastName.getText());
        statement.setString(3,fEmail.getText());
        statement.setString(4,fLogin.getText());
        statement.setString(5,ConnexionASdb.hash(fConfirm.getText()));
        int affectRows =  statement.executeUpdate();
        if (affectRows > 0){
            String sqlReq = "SELECT id FROM ba_coach WHERE id = LAST_INSERT_ID()";
            ResultSet resultSet =  statement.executeQuery(sqlReq);
            if(resultSet.next()){ idReturn =  resultSet.getInt("id"); }
        }
        return  idReturn;
    }
    private  boolean checkFields(){
        return (fName.getText().isEmpty() &&
                fLastName.getText().isEmpty() &&
                fEmail.getText().isEmpty() &&
                fLogin.getText().isEmpty() &&
                Objects.equals(fConfirm.getText(), fPassword.getText()) &&
                fConfirm.getText().isEmpty()
        );
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {}
}