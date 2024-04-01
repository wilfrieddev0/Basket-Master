package main.assocationbasketproject;

import db.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Callback;
import main.assocationbasketproject.dialog.FillNewEvent;
import main.assocationbasketproject.dialog.FillStats;
import manager.ClassManager;
import variables.ToastMessage;

import java.net.URL;
import java.sql.Time;
import java.time.LocalDate;
import java.util.*;

public class HomePane implements Initializable {
    @FXML
    private Button btnUpdate;
    @FXML
    private Button btnUndo;
    @FXML
    private Button btnFillStats;
    @FXML
    private Button btnClose;
    @FXML
    private DatePicker datePicker;
    @FXML
    private TableView<ClassEvent> otherTab;
    @FXML
    private TableView<ClassEvent> tabSearch;
    @FXML
    private TableView<ClassEvent> tabMatch;
    @FXML
    private TableColumn<ClassEvent, Integer> idColumn;
    @FXML
    private TableColumn<ClassEvent, String> commentColumn;
    @FXML
    private TableColumn<ClassEvent, String> opponentColumn;
    @FXML
    private TableColumn<ClassEvent, Date> dateColumn;
    @FXML
    private TableColumn<ClassEvent, Integer> oIdColumn;
    @FXML
    private TableColumn<ClassEvent, String> oCommentColumn;
    @FXML
    private TableColumn<ClassEvent, String> oSubjectColumn;
    @FXML
    private TableColumn<ClassEvent, Date> oDateColumn;
    @FXML
    private TableColumn<ClassEvent, Date> cId;
    @FXML
    private TableColumn<ClassEvent, Date> cDate;
    @FXML
    private TableColumn<ClassEvent, Time> cTime;
    @FXML
    private TableColumn<ClassEvent, Date> cScheduleAt;
    @FXML
    private TableColumn<ClassEvent, Date> cType;
    @FXML
    private TableColumn<ClassEvent, Date> cImportance;
    @FXML
    private TableColumn<ClassEvent, Date> cOppenent;
    @FXML
    private TableColumn<ClassEvent, Date> cDescription;
    @FXML
    private TableColumn<ClassEvent, Date> cClose;
    @FXML
    private  ComboBox<Boolean> cbClose;
    @FXML
    private TextField fKeyWord;
    @FXML
    private DatePicker dateBegin;
    @FXML
    private DatePicker dateEnd;
    private Queue<ClassEvent> queueEvent;
    private ClassEvent oneSchedule;
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        queueEvent = new ArrayDeque<>();
        try {
            oneSchedule = new ClassEvent();
            oneSchedule.loadEvents(ClassCoach.getInstance().getId(),null,false);
            fillTabs(oneSchedule.getEvents(),false);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        bindColumnsTables();
        customDatePicker();
        ObservableList<Boolean> listboolean  = FXCollections.observableArrayList();
        listboolean.add(true);
        listboolean.add(false);
        cbClose.setItems(listboolean);
        cbClose.getSelectionModel().select(1);
    }
    void customDatePicker() {
        // Personnaliser le DatePicker
        datePicker.setDayCellFactory(new Callback<DatePicker, DateCell>() {
            @Override
            public DateCell call(DatePicker param) {
                return new DateCell() {
                    @Override
                    public void updateItem(LocalDate date, boolean empty) {
                        super.updateItem(date, empty);
                        setText(null);
                        if (!empty) {
                            //On empecher la fermeture du calendrier
                            setOnMouseClicked(event -> {
                                handleAction(event);
                                datePicker.setValue(date);
                                datePicker.setPromptText(date.toString());
                                datePicker.show();
                                try {
                                    oneSchedule.loadEvents(ClassCoach.getInstance().getId(),date,false);
                                    fillTabs(oneSchedule.getEvents(),false );
                                } catch (Exception e) {
                                    throw new RuntimeException(e);
                                }
                                if (event.getButton().equals(MouseButton.PRIMARY) && event.getClickCount()==2){
                                    try {
                                        FXMLLoader fxml =  new FXMLLoader(Objects.requireNonNull(getClass().getResource("dialog/dialogEvent.fxml")));
                                        DialogPane dialogPane =  fxml.load();
                                        FillNewEvent dialogEvent =  fxml.getController();
                                        dialogEvent.initialise(date,null);
                                        Stage stage =  new Stage();
                                        stage.setTitle("Fills infos of events");
                                        stage.setScene(new Scene(dialogPane));
                                        stage.showAndWait();
                                        //Refresh les tableaux
                                        oneSchedule.loadEvents(ClassCoach.getInstance().getId(),date,false);
                                        fillTabs(oneSchedule.getEvents(),false);

                                    } catch (Exception e) {
                                        throw new RuntimeException(e);
                                    }
                                }
                            });
                            // Créer une carte personnalisée dans chaque cellule
                            VBox card = new VBox();
                            card.setAlignment(Pos.CENTER);
                            card.getStyleClass().add("date-cell");

                            // Créer un AnchorPane en haut avec la date
                            AnchorPane topAnchor = new AnchorPane();
                            Label dateLabel = new Label(String.valueOf(date.getDayOfMonth()));
                            dateLabel.setAlignment(Pos.CENTER);
                            topAnchor.getChildren().add(dateLabel);
                            AnchorPane.setTopAnchor(dateLabel, 10.0);
                            AnchorPane.setLeftAnchor(dateLabel, 30.0);

                            // Créer un AnchorPane en bas avec un label quelconque
                            AnchorPane bottomAnchor = new AnchorPane();
                            int nb;
                            try {
                                nb  = oneSchedule.countEvent(ClassCoach.getInstance().getId(), date);
                            } catch (Exception e) {
                                throw new RuntimeException(e);
                            }
                            Label customLabel = new Label("("+nb+") Event(s)");
                            customLabel.setAlignment(Pos.CENTER);
                            bottomAnchor.getChildren().add(customLabel);
                            AnchorPane.setBottomAnchor(customLabel, 10.0);
                            AnchorPane.setLeftAnchor(customLabel, 10.0);

                            // Ajouter les AnchorPane à la carte
                            card.getChildren().addAll(topAnchor, bottomAnchor);
                            card.setPrefSize(50,50);
                            // Afficher la carte personnalisée dans la cellule
                            setGraphic(card);
                        }
                    }
                };
            }
        });
    }
    void handleAction(MouseEvent event){}
    void fillTabs(ArrayList<ClassEvent> events,Boolean search){
        ObservableList<ClassEvent> mList =FXCollections.observableArrayList();
        ObservableList<ClassEvent> oList =FXCollections.observableArrayList();
        if (!events.isEmpty()){
            for (ClassEvent event :  events){
                if(!search){
                    if (event.getType().toString().equals("Other")){
                        oList.add(event);
                    }else{
                        mList.add(event);
                    }
                }else {
                    mList.add(event);
                }
            }
            if (!search){
                tabMatch.setItems(mList);
                otherTab.setItems(oList);
            }else tabSearch.setItems(mList);
            disableBtns(false);
        }else disableBtns(true);
    }
    private void disableBtns(boolean b){
        btnUpdate.setDisable(b);
        btnUndo.setDisable(b);
        btnFillStats.setDisable(b);
        btnClose.setDisable(b);
    }
    @FXML
    void addEvent() throws Exception {
        FXMLLoader fxml =  new FXMLLoader(Objects.requireNonNull(getClass().getResource("dialog/dialogEvent.fxml")));
        DialogPane dialogPane =  fxml.load();
        FillNewEvent dialogEvent =  fxml.getController();
        dialogEvent.initialise(datePicker.getValue(),null);
        Stage stage =  new Stage();
        stage.setTitle("Fills infos of events");
        stage.setScene(new Scene(dialogPane));
        stage.showAndWait();

        oneSchedule.loadEvents(ClassCoach.getInstance().getId(),null,false);
        fillTabs(oneSchedule.getEvents(),false);
    }
    @FXML
    void deleteEvent(ActionEvent event) throws Exception {
        //Petite mécanique pour le controlle de la selection d'une ligne des deux tableaux presentés...
        ClassEvent currentEvent = tabMatch.getSelectionModel().getSelectedItem()!=null? tabMatch.getSelectionModel().getSelectedItem() : null;
        if (currentEvent==null) currentEvent =otherTab.getSelectionModel().getSelectedItem()!=null?  otherTab.getSelectionModel().getSelectedItem():null;
        if (currentEvent!=null){
            String[] fields =  {"close"};
            String[] values = {"1"};
            ConnexionASdb connexionASdb = ClassManager.getUniqueInstance().getConnexionASdb();
            queueEvent.offer(oneSchedule.getEvent(currentEvent.getId()));
            connexionASdb.update(currentEvent.getId(),"ba_event",fields,values);
            oneSchedule.loadEvents(ClassCoach.getInstance().getId(),null,false);
            fillTabs(oneSchedule.getEvents(),false);
        }else ToastMessage.show("Info","Veillez selectionner la ligne a closer!",3);
    }
    @FXML
    void updateEvent(ActionEvent event) throws Exception {
        ClassEvent currentEvent = tabMatch.getSelectionModel().getSelectedItem()!=null? tabMatch.getSelectionModel().getSelectedItem() : null;
        if (currentEvent==null) currentEvent =otherTab.getSelectionModel().getSelectedItem()!=null?  otherTab.getSelectionModel().getSelectedItem():null;
        // La mise a jour est faite pour une ligne en fonction de l'id
        if (currentEvent!=null) {
            FXMLLoader fxml =  new FXMLLoader(Objects.requireNonNull(getClass().getResource("dialog/dialogEvent.fxml")));
            DialogPane dialogPane =  fxml.load();
            FillNewEvent dialogEvent =  fxml.getController();
            dialogEvent.initialise(datePicker.getValue(),currentEvent.getId());
            Stage stage =  new Stage();
            stage.setTitle("Fills infos of events");
            stage.setScene(new Scene(dialogPane));
            stage.showAndWait();
            oneSchedule.loadEvents(ClassCoach.getInstance().getId(),null,false);
            fillTabs(oneSchedule.getEvents(),false);
        }else ToastMessage.show("Info","Veillez selectionner la ligne à mettre à jour!",3);
    }
    @FXML
    void undo() throws Exception {
        if (!queueEvent.isEmpty()){
            ClassEvent lastEvent = queueEvent.poll();
            String[] fields =  {"close"};
            String[] values = {"0"};
            ConnexionASdb connexionASdb = ClassManager.getUniqueInstance().getConnexionASdb();
            connexionASdb.update(lastEvent.getId(),"ba_event",fields,values);
            oneSchedule.loadEvents(ClassCoach.getInstance().getId(),null,false);
            fillTabs(oneSchedule.getEvents(),false);
        }
    }
    @FXML
    void search(ActionEvent event) throws Exception {
        if (dateBegin.getValue()==null && dateEnd.getValue()==null){
            dateBegin.setValue(LocalDate.parse("2023-05-01"));
            dateEnd.setValue(LocalDate.now());
        }
        oneSchedule.search(dateBegin.getValue(),dateEnd.getValue(), cbClose.getSelectionModel().getSelectedItem(),fKeyWord.getText());
        fillTabs(oneSchedule.getEvents(),true);
    }
    @FXML
    void fillStats(ActionEvent event) throws Exception {
        ClassEvent currentEvent = tabMatch.getSelectionModel().getSelectedItem()==null?null:tabMatch.getSelectionModel().getSelectedItem();
        if(currentEvent!=null){
            ClassManager manager =  ClassManager.getUniqueInstance();
            manager.loadCaterogies();
            for (ClassCategory category :manager.getCategories()){
                category.initialise();
                for (ClassTeam team : category.getTeams()){
                    if ( currentEvent.getIdTeam() == team.getId()) {
                        manager.setCurrentCategory(category);
                        category.setCurrentTeam(team);
                        FXMLLoader fxml = new FXMLLoader(getClass().getResource("dialog/fillStats.fxml"));
                        DialogPane dialogPane = fxml.load();
                        ((FillStats) fxml.getController()).initialize(currentEvent.getId());
                        Stage stage = new Stage();
                        stage.setScene(new Scene(dialogPane));
                        stage.showAndWait();

                        oneSchedule.loadEvents(ClassCoach.getInstance().getId(), null, false);
                        fillTabs(oneSchedule.getEvents(), false);
                        break;
                    }else ToastMessage.show("Info","Cette evenement n'est pas lié à une equipe",3);
                }
            }
        }else ToastMessage.show("Info","Veillez selectionner la ligne a closer!",3);
    }
    private  void bindColumnsTables(){
        // Je lie les colonnes des tableaux aux propriétes des classes par lesquelles elles seront remplies
        //tables Match and  training
        idColumn.setCellValueFactory(new PropertyValueFactory<>("Id"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("datePlanned"));
        opponentColumn.setCellValueFactory(new PropertyValueFactory<>("Subject"));
        commentColumn.setCellValueFactory(new PropertyValueFactory<>("Details"));
        // Table Other
        oIdColumn.setCellValueFactory(new PropertyValueFactory<>("Id"));
        oDateColumn.setCellValueFactory(new PropertyValueFactory<>("datePlanned"));
        oSubjectColumn.setCellValueFactory(new PropertyValueFactory<>("Subject"));
        oCommentColumn.setCellValueFactory(new PropertyValueFactory<>("Details"));
        //Table Search
        cId.setCellValueFactory(new PropertyValueFactory<>("Id"));
        cDate.setCellValueFactory(new PropertyValueFactory<>("DatePlanned"));
        cTime.setCellValueFactory(new PropertyValueFactory<>("Time"));
        cScheduleAt.setCellValueFactory(new PropertyValueFactory<>("CurrentDate"));
        cType.setCellValueFactory(new PropertyValueFactory<>("Type"));
        cImportance.setCellValueFactory(new PropertyValueFactory<>("Importance"));
        cOppenent.setCellValueFactory(new PropertyValueFactory<>("Subject"));
        cDescription.setCellValueFactory(new PropertyValueFactory<>("Details"));
        cClose.setCellValueFactory(new PropertyValueFactory<>("Close"));
    }
}

