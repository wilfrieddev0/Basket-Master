package main.assocationbasketproject.dialog;

import db.ClassCategory;
import db.ClassCoach;
import db.ClassTeam;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.stage.Stage;
import manager.ClassManager;
import variables.ClassFieldFormat;
import variables.ToastMessage;

import javax.swing.*;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class FillCategory implements Initializable {
    @FXML
    private Button btnDeleteTeam;
    @FXML
    private Button btnFillTeam;
    private Boolean isUpdate;
    @FXML
    private ComboBox<String> cbGender;
    @FXML
    private Button btnSave;
    @FXML
    private TextField fMaxAge;
    @FXML
    private TextField fMinAge;
    @FXML
    private TextField fName;
    @FXML
    private TextArea fStory;
    @FXML
    private TableView<ClassTeam> teamTable;
    @FXML
    private TableColumn<ClassTeam, String> cName;
    @FXML
    private TableColumn<ClassTeam, String> cGamePriority         ;
    @FXML
    private TableColumn<ClassTeam, String> cGamePlan;
    @FXML
    private TableColumn<ClassTeam, Integer> cId;
    @FXML
    private TableColumn<ClassTeam,Integer>cAgeRange;
    private ClassManager manager;
    private ClassCategory currentCategory;
    private final ObservableList<ClassTeam> listTeam = FXCollections.observableArrayList();
    @FXML
    void cancel(ActionEvent event) {
        Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
        stage.close();
    }
    @FXML
    void addTeam(ActionEvent event) throws Exception {
        if (btnSave.isDisable()){
            if (!(fName.getText().isEmpty() && fMinAge.getText().isEmpty() && fMaxAge.getText().isEmpty())){
                if(JOptionPane.showConfirmDialog(null,"Attention! Après confirmation vous ne pourrez plus moditfier les informations de la categorie","Confirm", JOptionPane.YES_NO_OPTION)==0){
                    if(currentCategory == null){
                        currentCategory = getClassCategory();
                        manager.getCategories().add(currentCategory);
                        manager.setCurrentCategory(currentCategory);
                    }
                    // Création de la première équipe (par défaut)
                    ClassTeam fistTeam =  new ClassTeam(manager.getCurrentCategory().getId(),"Team A","Main","Jeu Interieur/Exterieur");
                    listTeam.add(fistTeam);
                    teamTable.setItems(listTeam);
                    disableFields();
                    btnSave.setDisable(false);
                }
            }else ToastMessage.show("","Entrer les informations de la catégorie ",3);
        }else {
            ClassTeam fistTeam =  new ClassTeam(manager.getCurrentCategory().getId(),"Team A","Main","Jeu Interieur/Exterieur");
            listTeam.add(fistTeam);
            teamTable.setItems(listTeam);
        }
    }
    private ClassCategory getClassCategory() throws Exception {
        // Enregistrement de la categorie
        int rangeAge = (Integer.parseInt(fMaxAge.getText())+Integer.parseInt(fMinAge.getText()))/2;
        String[] fields =  {"idCoach","name","gender","story","averageAge"};
        String[] values =  {String.valueOf(ClassCoach.getInstance().getId()),fName.getText(),cbGender.getSelectionModel().getSelectedItem(),fStory.getText(), String.valueOf(rangeAge)};
        return new ClassCategory(fields,values);
    }
    @FXML
    void deleteTeam() throws SQLException {
        ObservableList<ClassTeam> currentList  = teamTable.getItems();
        ClassTeam tempTeam =  teamTable.getSelectionModel().getSelectedItem();
        if (tempTeam != null){
            if (tempTeam.getId() > 0 ) currentCategory.deleteTeam();
            currentList.removeIf(item -> item.equals(tempTeam));
            teamTable.setItems(currentList);
        }
    }
    @FXML
    void deleteAll(ActionEvent event){
        if (JOptionPane.showConfirmDialog(null,"Etes-vous sûre de vouloir vider le tableau des equipes??")==1){
            teamTable.getItems().removeAll();
        }
    }
    @FXML
    void fillTeam() throws IOException, SQLException, NoSuchAlgorithmException {
        ClassTeam team  =  teamTable.getSelectionModel().getSelectedItem();
        if (team != null){
            if (team.getId() == 0 ){
                String[] fields =  {"idCategory","name","gamePriority","gamePlan"};
                String[] values =  {team.toString(true)};
                int idTeam =  manager.getConnexionASdb().insert("ba_team",fields,values);
                if (idTeam>0) team.setId(idTeam); else ToastMessage.show("Error","L'insertion a échoué!Consulter les logs",3);
            }
            // C'est avec les propriétés current*** que les states sont partagés dans l'application
            currentCategory.setCurrentTeam(team);
            manager.setCurrentCategory(currentCategory);
            //
            Path path = Path.of("fillTeam.fxml");
            FXMLLoader fxml = new FXMLLoader(getClass().getResource(path.toString()));
            DialogPane dialogPane  =  fxml.load();
            Stage stage =  new Stage();
            stage.setScene(new Scene(dialogPane));
            stage.setTitle("Fill Team");
            stage.showAndWait();
        }

    }
    @FXML
    void save(ActionEvent event) throws SQLException, NoSuchAlgorithmException {
        String[] fields =  {"idCategory","name","gamePriority","gamePlan"};
        String[] values = teamTable.getItems().stream().map(ClassTeam::toString).toArray(String[]::new);
        if (isUpdate){
            int rowAffected =  manager.getConnexionASdb().update(currentCategory.getId(),"ba_team",fields,values);
            if (rowAffected > 0){ ToastMessage.show("","Opération terminée",3);}
        }
        ((Stage) ((Button) event.getSource()).getScene().getWindow()).close();
    }
    @FXML
    void formatField(MouseEvent event){
        TextField current  = (TextField) event.getSource();
        ClassFieldFormat.formatField(current,getFieldType(current));
    }
    @FXML
    private  String getFieldType(TextField textField){
        String motif = null;
        if(textField.equals(fName)){
            motif =  "label";
        } else if (textField.equals(fMaxAge) || textField.equals(fMinAge) ) {
            motif =  "number";
        }
        return motif;
    }
    private  void bindTabtoClass(){
        cId.setCellValueFactory(new PropertyValueFactory<>("idCategory"));
        cName.setCellValueFactory(new PropertyValueFactory<>("Name"));
        cGamePriority.setCellValueFactory(new PropertyValueFactory<>("GamePriority"));
        cGamePlan.setCellValueFactory(new PropertyValueFactory<>("GamePlan"));

        cName.setCellFactory(TextFieldTableCell.forTableColumn());
        cGamePriority.setCellFactory(TextFieldTableCell.forTableColumn());
        cGamePlan.setCellFactory(TextFieldTableCell.forTableColumn());

        cName.setOnEditCommit(event -> {
            isUpdate = true;
            ClassTeam team = event.getRowValue();
            team.setName(event.getNewValue());
        });
        cGamePriority.setOnEditCommit(event -> {
            isUpdate = true;
            ClassTeam team = event.getRowValue();
            team.setGamePriority(event.getNewValue());
        });
        cGamePlan.setOnEditCommit(event -> {
            isUpdate = true;
            ClassTeam team = event.getRowValue();
            team.setGamePlan(event.getNewValue());
        });
    }
    private void disableFields(){
        fName.setDisable(true);
        fStory.setDisable(true);
        cbGender.setDisable(true);
        fMaxAge.setDisable(true);
        fMinAge.setDisable(true);

        btnFillTeam.setDisable(false);
        btnDeleteTeam.setDisable(false);
    }
    private void fillPage(){
        ClassCategory current =  manager.getCurrentCategory();
        fName.setText(current.getName());
        fStory.setText(current.getStory());
        cbGender.getSelectionModel().select(current.getGender());

        listTeam.addAll(current.getTeams());
        if (listTeam.isEmpty()){
            btnDeleteTeam.setDisable(true);
            btnFillTeam.setDisable(true);
        }else teamTable.getItems().removeAll(); teamTable.setItems(listTeam);
    }
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            bindTabtoClass();
            manager =  ClassManager.getUniqueInstance();
            if (manager.getCurrentCategory() == null){
                ObservableList<String> gender = FXCollections.observableArrayList(new String[]{"Man", "Woman"});
                cbGender.setItems(gender);
                cbGender.getSelectionModel().selectFirst();
            }else {
                isUpdate =  true;
                currentCategory =  manager.getCurrentCategory();
                fillPage();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }
}
