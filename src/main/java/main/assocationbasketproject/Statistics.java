package main.assocationbasketproject;

import db.ClassCategory;
import db.ClassPlayer;
import db.ClassStatistique;
import db.ClassTeam;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.Tooltip;
import manager.ClassManager;

import java.net.URL;
import java.util.ArrayList;
import java.util.Objects;
import java.util.ResourceBundle;

public class Statistics implements Initializable {
    public Label lMatch;
    public Label lPoints;
    public ProgressIndicator progressMatch;
    public ProgressIndicator progressPoints;
    public ComboBox<ClassCategory> cbCategory;
    @FXML
    private ComboBox<?> cbPeriod;
    @FXML
    private ComboBox<String> cbStats;
    @FXML
    private ComboBox<ClassTeam> cbTeam;
    @FXML
    private PieChart chartMatch;
    @FXML
    private PieChart chartPoints;
    @FXML
    private LineChart<String,Number> chartScale;
    @FXML
    private BarChart<String, Number> chartTeam;
    private ArrayList<ClassStatistique> teamsStats;
    private ArrayList<ClassStatistique> categoryStats;
    private ClassStatistique statistique;
    private ClassCategory currentCategory;
    public Statistics() throws Exception {}
    public  void fillPage() throws Exception {

        ObservableList<String> listStats =  FXCollections.observableArrayList("Fields Goals", "Points","Steals","Assists", "Blocks","Time game","Rebounds","Accuracy From Downtown");
        cbStats.setItems(listStats);
        cbStats.getSelectionModel().selectFirst();

        ObservableList<ClassTeam> listTeam =  FXCollections.observableArrayList();
        for(ClassTeam team: currentCategory.getTeams()){
            team.initialiseTeam();
            listTeam.add(team);
        }
        cbTeam.setItems(listTeam);
        cbTeam.getSelectionModel().selectFirst();

        fillCategoryStats();
        fillChartTeam();
    }
    @FXML
    void changeCategory() throws Exception {
        currentCategory =  cbCategory.getSelectionModel().getSelectedItem();
        fillPage();
    }
    @FXML
    void fillChartTeam() throws Exception {
        if (!cbTeam.getItems().isEmpty()){
            teamsStats =  statistique.loadTeamStats(cbTeam.getSelectionModel().getSelectedItem().getId());
            //lineChart horizontal (Performance  par joueurs d'une equipe sur une periode et selon un critère)
            XYChart.Series<String ,Number> series = new XYChart.Series<>();
            Tooltip tooltip = new Tooltip();
            for (int i = 0; i<teamsStats.size();i++){
                ClassStatistique playerStat = teamsStats.get(i);
                XYChart.Data<String,Number> data =  new XYChart.Data<>();
                chartTeam.setTitle( cbStats.getSelectionModel().getSelectedItem()+ " en fonction des joueurs");
                switch (cbStats.getSelectionModel().getSelectedItem()){
                    case "Fields Goals" :
                        data.setXValue(String.valueOf(i));
                        data.setYValue(Double.parseDouble(playerStat.getFGPerMatch()));
                        break;
                    case "Points" :
                        data.setXValue(String.valueOf(i));
                        data.setYValue(playerStat.getAveragePoints());
                        break;
                    case "Assists" :
                        data.setXValue(String.valueOf(i));
                        data.setYValue(playerStat.getAverageAssists());
                        break;
                    case "Blocks" :
                        data.setXValue(String.valueOf(i));
                        data.setYValue(playerStat.getAverageBlocks());
                        break;
                    case "Rebounds" :
                        data.setXValue(String.valueOf(i));
                        data.setYValue(playerStat.getAverageRebounds());
                        break;
                    case "Accuracy From Downtown" :
                        data.setXValue(String.valueOf(i));
                        data.setYValue(playerStat.getAccuraryFromDowntown());
                        break;
                    case "Steals" :
                        data.setXValue(String.valueOf(i));
                        data.setYValue(playerStat.getAverageSteals());
                        break;
                    case "Time game" :
                        data.setXValue(String.valueOf(i));
                        data.setYValue(playerStat.getAverageTimeGame());
                        break;
                }
                series.getData().add(data);
            }
            chartTeam.getData().clear();
            chartTeam.getData().add(series);
            chartTeam.getData().forEach( bindSeries -> {
                for (int i = 0; i<bindSeries.getData().size(); i++){
                    ClassStatistique stat = teamsStats.get(i);
                    ClassPlayer player = new ClassPlayer(stat.getIdPlayer());
                    try {
                        player.initialise();
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                    XYChart.Data<String, Number> data =  bindSeries.getData().get(i);
                    Tooltip.install(data.getNode(),tooltip);
                    data.getNode().setOnMouseEntered( event -> {
                        String info = player.getName() +"\n"+ player.getAge()+" ans\n"+player.getHeight()+" cm"+"\n"+"Performance sur "+stat.getGameCounts()+ " matchs";
                        Label label =  new Label();
                        label.setText(info);
                        label.setAlignment(Pos.CENTER_LEFT);
                        tooltip.setGraphic(label);
                        tooltip.show(data.getNode(), event.getScreenX(), event.getScreenY()+10);
                    });
                    data.getNode().setOnMouseExited( event -> tooltip.hide());
                }
            });
        }
    }
    @FXML
    void fillCategoryStats() throws Exception {
        categoryStats =  statistique.loadCategoryStats(currentCategory.getId());
        double succes  = 0;
        double accuracy =0;
        for (ClassStatistique teamStat :categoryStats){
            if (Objects.equals(teamStat.getResult(), "true")){
                succes += ((double)1/categoryStats.size());
            }
        }
        progressMatch.setProgress(succes);
        lMatch.setText(String.valueOf(categoryStats.size()));
        for(ClassStatistique playerStat : categoryStats){
            accuracy += Double.parseDouble(playerStat.getFGPerMatch());
        }
        progressPoints.setProgress((accuracy/categoryStats.size()));
        lPoints.setText(String.valueOf(statistique.getTotalPoints()));

        //LineChart (Points en fonction des matchs )
        XYChart.Series<String ,Number> series = new XYChart.Series<>();
        Tooltip tooltip = new Tooltip();
        for (int i = 0; i < categoryStats.size(); i++){
            ClassStatistique stat =  categoryStats.get(i);
            XYChart.Data<String, Number> data =  new XYChart.Data<>(String.valueOf(i+1),stat.getPoints());
            series.getData().add(data);
        }
        chartScale.getData().clear();
        chartScale.getData().add(series);
        chartScale.getData().forEach( bindSeries -> {
            for (int i = 0; i<bindSeries.getData().size(); i++){
                ClassStatistique stat = categoryStats.get(i);
                XYChart.Data<String, Number> data =  bindSeries.getData().get(i);
                Tooltip.install(data.getNode(),tooltip);
                data.getNode().setOnMouseEntered( event -> {
                    String info = stat.getOppenent() +"\n"+ stat.getDate()+"\n"+stat.getScore();
                    Label label =  new Label();
                    label.setText(info);
                    label.setAlignment(Pos.CENTER_LEFT);
                    tooltip.setGraphic(label);
                    tooltip.show(data.getNode(),event.getScreenX(),event.getScreenY()+10);
                });
                data.getNode().setOnMouseExited( event -> tooltip.hide());
            }
        });
    }
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            ClassManager manager =  ClassManager.getUniqueInstance();
            manager.loadCaterogies();
            ObservableList<ClassCategory> listCategory =  FXCollections.observableArrayList();
            for (ClassCategory category:manager.getCategories()){
                category.initialise();
                listCategory.add(category);
            }
            cbCategory.setItems(listCategory);
            currentCategory = manager.getCurrentCategory() != null ? manager.getCurrentCategory() : manager.getCategories().getFirst() ;
            currentCategory.initialise();
            cbCategory.getSelectionModel().select(currentCategory);

            statistique = new ClassStatistique();
            categoryStats =  statistique.loadCategoryStats(currentCategory.getId());
            teamsStats =  currentCategory.getCurrentTeam()!=null ?statistique.loadTeamStats(currentCategory.getCurrentTeam().getId()) : statistique.loadTeamStats(currentCategory.getTeams().getFirst().getId());
            fillPage();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
