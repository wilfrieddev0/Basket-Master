package main.assocationbasketproject;

import db.ClassPlayer;
import db.ClassStatistique;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import manager.ClassManager;

import java.net.URL;
import java.sql.Date;
import java.time.LocalDate;
import java.util.ResourceBundle;

public class Player implements Initializable {

    public Label lVictory;
    public DatePicker dFrom;
    public DatePicker dTo;
    @FXML
    private TableColumn<ClassStatistique, Integer> cId;
    @FXML
    private TableColumn<ClassStatistique, Integer> cIdPlayer;
    @FXML
    private TableColumn<ClassStatistique, String> cAccuracy;
    @FXML
    private TableColumn<ClassStatistique, Double> cAssists;
    @FXML
    private TableColumn<ClassStatistique, Double> cBLocks;
    @FXML
    private TableColumn<ClassStatistique, LocalDate> cDate;
    @FXML
    private TableColumn<ClassStatistique, String> cFG;
    @FXML
    private TableColumn<ClassStatistique, String> cOppenent;
    @FXML
    private TableColumn<ClassStatistique, Double> cPoints;
    @FXML
    private TableColumn<ClassStatistique, Double> cRebounds;
    @FXML
    private TableColumn<ClassStatistique,String> cScore;
    @FXML
    private TableColumn<ClassStatistique, String> cResults;
    @FXML
    private TableColumn<ClassStatistique, Double> cSteals;
    @FXML
    private TableColumn<ClassStatistique, Double> cTimeGame;
    @FXML
    private Circle circleProfile;
    @FXML
    private TextField fWords;
    @FXML
    private ComboBox<ClassPlayer> comboSearch;
    @FXML
    private Label lAccuracy;
    @FXML
    private Label lAge;
    @FXML
    private Label lAssits;
    @FXML
    private Label lBlocks;
    @FXML
    private Label lCategory;
    @FXML
    private Label lTeam;
    @FXML
    private Label lFieldsGoals;
    @FXML
    private Label lMatchs;
    @FXML
    private Label lName;
    @FXML
    private Label lPosition;
    @FXML
    private Label lPoints;
    @FXML
    private Label lRebounds;
    @FXML
    private Label lSize;
    @FXML
    private Label lSteals;
    @FXML
    private  TableView<ClassStatistique>  tabStats;
    private ClassPlayer currentPlayer;
    private ObservableList<ClassPlayer> filteredList;
    private  ClassManager manager;
    @FXML
    void searchStats() throws Exception {
        ClassStatistique statistique =  new ClassStatistique();
        String word  =  fWords.getText().isEmpty() ? "" : fWords.getText();
        Date to =  dTo.getValue()==null ? Date.valueOf(LocalDate.now()) : Date.valueOf(dFrom.getValue());
        Date from =  dFrom.getValue()==null ? Date.valueOf("2023-06-27") : Date.valueOf(dTo.getValue());
        statistique.search(word, from,to);
        if (!statistique.getStatistiques().isEmpty()){
            ObservableList<ClassStatistique> listStats = FXCollections.observableArrayList();
            listStats.setAll(statistique.getStatistiques());
            tabStats.setItems(null);
            bindTabletoClass();
            tabStats.setItems(listStats);
        }
    }
    @FXML
    void fillPage() throws Exception {
        if(comboSearch.getValue() !=null && comboSearch.getValue().getId() >0){
            currentPlayer = new ClassPlayer(comboSearch.getValue().getId());
            currentPlayer.initialise();
            comboSearch.getEditor().setText(currentPlayer.getName() + "("+currentPlayer.getAge()+"ans)");
            lName.setText(currentPlayer.getName());
            lAge.setText(currentPlayer.getAge() + " years old");
            lPosition.setText(currentPlayer.getPosition());
            lTeam.setText(currentPlayer.getTeamName());
            lSize.setText(currentPlayer.getHeight() + " / "+ currentPlayer.getWeight());
            lCategory.setText(currentPlayer.getGategoryName());
            if (!currentPlayer.getPathProfile().isEmpty()){
                Image image =  new Image("file:"+currentPlayer.getPathProfile());
                if(!image.isError()) {
                    circleProfile.setFill(new ImagePattern(image));
                }
            }
            //Les stats
            loadStats();
        }
    }
    private void  loadStats() throws Exception {
        ClassStatistique statistique =  new ClassStatistique();
        statistique.loadStatistiques(currentPlayer.getId());
        if (!statistique.getStatistiques().isEmpty()){
            // Remplissage
            lMatchs.setText(String.valueOf(statistique.getTotalMatch()));
            lPoints.setText(String.format("%.2f",statistique.getAveragePoints()));
            lAssits.setText(String.format("%.2f",statistique.getAverageAssists()));
            lRebounds.setText(String.format("%.2f",statistique.getAverageRebounds()));
            lSteals.setText(String.format("%.2f",statistique.getAverageSteals()));
            lBlocks.setText(String.format("%.2f",statistique.getAverageBlocks()));
            lFieldsGoals.setText(String.format("%.2f",statistique.getFieldsGoals()));
            lAccuracy.setText(String.format("%.2f",statistique.getAccuraryFromDowntown()));
            lVictory.setText(String.valueOf(statistique.getTotalVictories()));
        }else{
            lMatchs.setText("0");
            lPoints.setText("0");
            lAssits.setText("0");
            lRebounds.setText("0");
            lSteals.setText("0");
            lBlocks.setText("0");
            lFieldsGoals.setText("0");
            lAccuracy.setText("0");
            lVictory.setText("0");
        }
    }
    private void bindTabletoClass(){
          cId.setCellValueFactory(new PropertyValueFactory<>("Id"));
          cIdPlayer.setCellValueFactory(new PropertyValueFactory<>("IdPlayer"));
          cAssists.setCellValueFactory(new PropertyValueFactory<>("Assists"));
          cDate.setCellValueFactory(new PropertyValueFactory<>("Date"));
          cFG.setCellValueFactory(new PropertyValueFactory<>("FGPerMatch"));
          cOppenent.setCellValueFactory(new PropertyValueFactory<>("Oppenent"));
          cPoints.setCellValueFactory(new PropertyValueFactory<>("Points"));
          cRebounds.setCellValueFactory(new PropertyValueFactory<>("Rebounds"));
          cTimeGame.setCellValueFactory(new PropertyValueFactory<>("TimeGame"));
          cBLocks.setCellValueFactory(new PropertyValueFactory<>("Blocks"));
          cAccuracy.setCellValueFactory(new PropertyValueFactory<>("AccuraryPerMatch"));
          cSteals.setCellValueFactory(new PropertyValueFactory<>("Steals"));
          cScore.setCellValueFactory(new PropertyValueFactory<>("Score"));
          cResults.setCellValueFactory(new PropertyValueFactory<>("Result"));
      }
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            manager = ClassManager.getUniqueInstance();
            filteredList = FXCollections.observableList(manager.allPlayers());
            comboSearch.setItems(filteredList);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
