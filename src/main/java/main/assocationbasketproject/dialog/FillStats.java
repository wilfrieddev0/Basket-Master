package main.assocationbasketproject.dialog;

import db.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import manager.ClassManager;
import variables.ClassFieldFormat;
import variables.ToastMessage;

import javax.swing.*;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;

public class FillStats{
    @FXML
    private Button btnSave;
    @FXML
    private Button btnFin;
    @FXML
    private DatePicker dDate;
    @FXML
    private TextField f3PAttempts;
    @FXML
    private TextField f3PPlay;
    @FXML
    private TextField fAssists;
    @FXML
    private TextField fAttempts;
    @FXML
    private TextField fBlocks;
    @FXML
    private TextField fOppenent;
    @FXML
    private TextField fPoints;
    @FXML
    private TextField fRebounds;
    @FXML
    private TextField fScore;
    @FXML
    private TextField fScore1;
    @FXML
    private TextField fSteals;
    @FXML
    private TextField fTimeGame;
    @FXML
    private Label lName;
    @FXML
    private Label lGender;
    @FXML
    private Label lCategory;
    @FXML
    private ListView<ClassPlayer>  listView;
    private ClassTeam currentTeam;
    private ClassCategory currentCategory;
    private ConnexionASdb connexionASdb;
    private  ClassEvent event;
    private PreparedStatement statement;
    private final ObservableList<String> listStats =  FXCollections.observableArrayList();
    @FXML
    void cancel(ActionEvent event) {
        Stage stage =  (Stage) ((Scene)((Button) event.getSource()).getScene()).getWindow();
        stage.close();
    }
    @FXML
    void force(){
        if (listStats.size()>=5){
            if (JOptionPane.showConfirmDialog(null,"Cette action est irrevovable et enregistrera les données saisies jusqu'à present","Info",JOptionPane.YES_NO_OPTION)==0){
                listView.getItems().removeAll();
                btnSave.setText( "Finish" );
                btnSave.setFocusTraversable(true);
            }
        }
    }
    @FXML
     void formatField(MouseEvent event){
        ClassFieldFormat.formatField((TextField) event.getSource(),"number");
    }
    @FXML
    void save(ActionEvent event) throws Exception {
        if (!checkFields()){
            ClassPlayer player =  listView.getSelectionModel().getSelectedItem();
            if (player != null){
                if(JOptionPane.showConfirmDialog(null, "Confirmez-vous les stats saisies?? Une fois validées, il est desormais impossible de les mofifier","confirm",JOptionPane.YES_NO_OPTION)==0){
                    // Ajout des données de joueurs dans la requête preparée
                    addBatch(player);
                    listView.getItems().removeIf(classPlayer -> classPlayer.equals(player));
                    if (listView.getItems().isEmpty()){
                        // Ajout des données de l'équipe et persitance
                        addBatch(new ClassPlayer());
                        int[] results  =  statement.executeBatch();
                        if (results.length> 0) {
                            JOptionPane.showConfirmDialog(null, "Opération  terminée.\n La fenêtre de dialog va se fermer", "Success!", JOptionPane.DEFAULT_OPTION);
                            //On close l'evenemement par la même occasion
                            closeEvent();
                            statement.close();
                            //connexionASdb.getConnection().close();
                            Button button = (Button) event.getSource();
                            Stage stage = (Stage) button.getScene().getWindow();
                            stage.close();
                        }else JOptionPane.showConfirmDialog(null,"Un problème a ete rencontré!Consuluter les stacks pour plus details","Erreur",JOptionPane.DEFAULT_OPTION);
                    }else btnSave.setText( "Save next");  listView.getSelectionModel().selectFirst(); emptyFields();
                }
            }
        }else ToastMessage.show("","Tous les champs sont obligatoires",3);
    }
    private void closeEvent() throws SQLException {
        connexionASdb.update(event.getId(),"ba_event",new String[]{"close"},new String[]{"1"});
    }
    private  void addBatch(ClassPlayer player) throws SQLException {
        String result;
        if (Integer.parseInt(fScore.getText()) > Integer.parseInt(fScore1.getText())){
            result = "true";
        }else if (Integer.parseInt(fScore.getText()) < Integer.parseInt(fScore1.getText())){
            result =  "false";
        }else {
            result =  "null";
        }
        statement.setInt(1,currentTeam.getId());
        if (player.getId() == 0) {
            statement.setNull(2, Types.INTEGER);
            statement.setInt(7, Integer.parseInt(fScore.getText()));
        }else {
            statement.setInt(2,player.getId());
            statement.setInt(7, Integer.parseInt(fPoints.getText()));

        }
        statement.setDate(3, Date.valueOf(dDate.getValue()));
        statement.setString(4,fOppenent.getText());
        statement.setString(5,fScore.getText()+":"+fScore1.getText());
        statement.setInt(6, Integer.parseInt(fTimeGame.getText()));
        statement.setInt(8, Integer.parseInt(fRebounds.getText()));
        statement.setInt(9, Integer.parseInt(fAssists.getText()));
        statement.setInt(10, Integer.parseInt(fSteals.getText()));
        statement.setInt(11, Integer.parseInt(fBlocks.getText()));
        statement.setInt(12, Integer.parseInt(fAttempts.getText()));
        statement.setInt(13, Integer.parseInt(f3PAttempts.getText()));
        statement.setInt(14, Integer.parseInt(f3PPlay.getText()));
        statement.setString(15,result);
        statement.addBatch();
    }
    private void fillPage(){
        lName.setText(currentTeam.getName());
        lGender.setText(currentCategory.getGender());
        lCategory.setText(currentCategory.getName());

        ObservableList<ClassPlayer> list = FXCollections.observableArrayList();
        list.setAll(currentTeam.getPlayers());
        listView.setItems(list);
        listView.getSelectionModel().selectFirst();

        fOppenent.setText(event.getSubject());
        dDate.setValue(event.getDatePlanned().toLocalDate());

        btnSave.setText( "Save first");
    }
    private Boolean checkFields(){
       return (
                fScore.getText().isEmpty() &&
                fScore1.getText().isEmpty() &&
                fTimeGame.getText().isEmpty() &&
                fPoints.getText().isEmpty() &&
                fRebounds.getText().isEmpty() &&
                fAssists.getText().isEmpty() &&
                fSteals.getText().isEmpty() &&
                fBlocks.getText().isEmpty() &&
                fAttempts.getText().isEmpty() &&
                f3PAttempts.getText().isEmpty() &&
                f3PPlay.getText().isEmpty());
    }
    private void emptyFields(){
        fBlocks.setText("");
        fPoints.setText("");
        fSteals.setText("");
        fAssists.setText("");
        fRebounds.setText("");
        f3PAttempts.setText("");
        f3PPlay.setText("");
        fTimeGame.setText("");
        fAttempts.setText("");
        if (listStats.size() > 5){
            btnFin.setVisible(true);
        }
    }
    private void customListview(){
        listView.setCellFactory( cell -> new ListCell<>(){
            @Override
            protected void updateItem(ClassPlayer classPlayer, boolean b) {
                super.updateItem(classPlayer, b);
                if (classPlayer!=null){
                    try {
                        classPlayer.initialise();
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                    setText(classPlayer.getName() + " " + classPlayer.getPosition()+" "+"("+ classPlayer.getAge()+" Years old)" );
                    setGraphic(null);
                }
            }
        });
    }
    public void initialize(int id){
        try {
            connexionASdb  = ClassManager.getUniqueInstance().getConnexionASdb();
            currentCategory = ClassManager.getUniqueInstance().getCurrentCategory();
            currentTeam =  ClassManager.getUniqueInstance().getCurrentCategory().getCurrentTeam();
            String sql = "INSERT INTO ba_statistique (idTeam,idPlayer,date,oppenent,score,timeGame,points,rebounds,assists,steals,blocks,attempts,3pointsShotsAttempts,3pointsPlay,victory) VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?);";
            statement = connexionASdb.getConnection().prepareStatement(sql);
            currentTeam.initialiseTeam();
            event =  (new ClassEvent()).loadEvent(id);
            fillPage();
            customListview();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
