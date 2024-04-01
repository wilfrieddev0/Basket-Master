package main.assocationbasketproject.dialog;

import db.ClassCategory;
import db.ClassPlayer;
import db.ClassTeam;
import db.ConnexionASdb;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.ImagePattern;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import javafx.util.converter.IntegerStringConverter;
import javafx.util.converter.LocalDateStringConverter;
import manager.ClassManager;
import variables.ClassFieldFormat;
import variables.ToastMessage;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.NoSuchAlgorithmException;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;

public class FillTeam implements Initializable {
    @FXML
    public TextField fId;
    @FXML
    public TextField fIdMedia;
    @FXML
    private Button btnClear;
    @FXML
    private Button btnDelete;
    @FXML
    private Button btnFill;
    @FXML
    private Button btnUndo;
    @FXML
    private Label Gender;
    @FXML
    private Circle circleProfile;
    @FXML
    private ComboBox<String> cbGender;
    @FXML
    private ComboBox<String> cbPosition;
    @FXML
    private DatePicker dBirth;
    @FXML
    private TextArea fDesc;
    @FXML
    private TextField fEPhone;
    @FXML
    private TextField fWeight;
    @FXML
    private TextField fHeight;
    @FXML
    private TextField fEmail;
    @FXML
    private TextField fFName;
    @FXML
    private TextField fLName;
    @FXML
    private TextField fPhone;
    @FXML
    private TextField fPostal;
    @FXML
    private Label lDate;
    @FXML
    private Label lName;
    @FXML
    private Label lGamePlan;
    @FXML
    private Label lGamePriority;
    @FXML
    private Label lGender;
    @FXML
    private Label lTeam;
    @FXML
    private TableView<ClassPlayer> tabPlayer;
    @FXML
    public TableColumn<ClassPlayer, Integer> cId;
    @FXML
    private TableColumn<ClassPlayer,Integer> cIdTeam;
    @FXML
    private TableColumn<ClassPlayer,Integer> cIdMedia;
    @FXML
    private TableColumn<ClassPlayer,String> cFirstName;
    @FXML
    private TableColumn<ClassPlayer,String> cLastName;
    @FXML
    private TableColumn<ClassPlayer,String> cEmail;
    @FXML
    private TableColumn<ClassPlayer, LocalDate> cBirthDay;
    @FXML
    private TableColumn<ClassPlayer,Integer> cPhone;
    @FXML
    private TableColumn<ClassPlayer,Integer> cPhoneEmergency;
    @FXML
    private TableColumn<ClassPlayer,Integer> cHeight;
    @FXML
    private TableColumn<ClassPlayer,Integer> cWeight;
    @FXML
    private TableColumn<ClassPlayer,String> cPosition;
    @FXML
    private TableColumn<ClassPlayer,String> cDescription;
    @FXML
    public TableColumn<ClassPlayer,String> cPathProfile;
    private ObservableList<ClassPlayer> listPlayer =  FXCollections.observableArrayList();
    private final ArrayList<ClassPlayer> cloneInitialPlayer =  new ArrayList<>();// Stocke le state des joueurs deja existant dans la base
    private  ConnexionASdb connexionASdb;
    private ClassTeam currentTeam;
    private ClassCategory currentCategory;
    private ClassPlayer currentPlayer;
    private final Queue<ClassPlayer> playerQueue =  new ArrayDeque<>();
    @FXML
    void editPlayer() throws URISyntaxException {
        currentPlayer =  tabPlayer.getSelectionModel().getSelectedItem();
        formatInFields();
    }
    @FXML
    void add() throws MalformedURLException {
        // Insertaion dans la table ba_coach et suppression des postes en trop
        if (!(fFName.getText().isEmpty() && fLName.getText().isEmpty() && fEmail.getText().isEmpty() && dBirth.getValue()==null && fEPhone.getText().isEmpty() && fPhone.getText().isEmpty() && fHeight.getText().isEmpty() && fWeight.getText().isEmpty() && fDesc.getText().isEmpty())){
            Paint fill = circleProfile.getFill();
            Image image =  null;
            if (fill instanceof ImagePattern) image =  ((ImagePattern) fill).getImage();
            if (image != null){
                ClassPlayer temp  =  new ClassPlayer(
                        currentTeam.getId(),
                        cbGender.getSelectionModel().getSelectedItem(),
                        fFName.getText(),
                        fLName.getText(),
                        fEmail.getText(),
                        Date.valueOf(dBirth.getValue()),
                        Integer.parseInt(fEPhone.getText()),
                        Integer.parseInt(fPhone.getText()),
                        Integer.parseInt(fHeight.getText()),
                        Integer.parseInt(fWeight.getText()),
                        cbPosition.getSelectionModel().getSelectedItem(),
                        fDesc.getText(),
                        //Les informations du l'image de profile
                        fIdMedia.getText().isEmpty() ?0:Integer.parseInt(fIdMedia.getText()),
                        image.getUrl().split(":")[1]
                );
                //Je recupere l'id du joueur si present
                if(Integer.parseInt(fId.getText().isEmpty()? "0":fId.getText())>0) temp.setId(Integer.parseInt(fId.getText()));
                tabPlayer.getItems().add(temp);
                if (tabPlayer.getItems().stream().filter(player -> player.getPosition().equals(temp.getPosition())).toArray().length==2){
                    cbPosition.getItems().removeIf(position -> position.equals(temp.getPosition()));
                }
                emptyFields();
            }else ToastMessage.show("Info","Veillez selectionner une photo de profile",3);
        }else ToastMessage.show("Info","Veillez remplir tous les champs avant de valider!", 3);
    }
    private void emptyFields(){
        fId.setText("");
        fFName.setText("");
        fLName.setText("");
        fEmail.setText("");
        dBirth.setValue(LocalDate.now());
        fEPhone.setText("");
        fPhone.setText("");
        fHeight.setText("");
        fWeight.setText("");
        cbPosition.getSelectionModel().selectFirst();
        fDesc.setText("");
        circleProfile.setFill(null);

        btnFill.setDisable(false);
        btnClear.setDisable(false);
        btnDelete.setDisable(false);
        btnUndo.setDisable(false);
    }
    @FXML
    void save(ActionEvent event) throws SQLException, NoSuchAlgorithmException, IOException, URISyntaxException {
        if(!tabPlayer.getItems().isEmpty()){
            String[] fields = {"idTeam","gender","lastName","firstName","email","birthday","description","phone","phoneEmergency","height","weight","position","hurt","available"};
            //On récupére les nouveaux joueurs qui seront inserés
            String[] values = new String[]{};
            List<ClassPlayer> listInserts = tabPlayer.getItems().stream().filter(player -> player.getId() == 0).toList();
            //Insertions des joueurs
            if (!listInserts.isEmpty()){
                String sql="INSERT INTO ba_player (idTeam,gender,lastName,firstName,email,birthday,description,phone,phoneEmergency,height,weight,position) " +
                           "VALUES (?,?,?,?,?,?,?,?,?,?,?,?);";
                PreparedStatement statement =  this.connexionASdb.getConnection().prepareStatement(sql);
                listInserts.forEach(player-> {
                    try {
                        statement.setInt(1,player.getIdTeam());
                        statement.setString(2, player.getGender());
                        statement.setString(3, player.getLastName());
                        statement.setString(4, player.getFirstName());
                        statement.setString(5, player.getEmail());
                        statement.setDate(6, Date.valueOf(player.getBirthDay()));
                        statement.setString(7, player.getDescription());
                        statement.setInt(8, player.getPhone());
                        statement.setInt(9, player.getPhoneEmergency());
                        statement.setInt(10, player.getHeight());
                        statement.setInt(11, player.getWeight());
                        statement.setString(12, player.getPosition());
                        statement.addBatch();
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }

                });
                int[] result = statement.executeBatch();
                if (result.length> 0){
                    sql = "SELECT id FROM ba_player WHERE id=LAST_INSERT_ID()";
                    ResultSet resultSet = statement.executeQuery(sql);
                    if (resultSet.next()){
                        int idLastPlayerInsered =  resultSet.getInt("id");
                        if (idLastPlayerInsered > 0){
                            for (ClassPlayer player : listInserts){
                                player.setId(idLastPlayerInsered);
                                fields = new String[]{"description", "typeMime", "dateCreation", "path"};
                                values  = new String[]{"Image de profile","Image",String.valueOf(dBirth.getValue()),player.getPathProfile()};
                                int idLastMedia = connexionASdb.insert("ba_media",fields,values);
                                // Remplissage de la table d'association
                                fields =  new String[]{"idMedia","idPlayer"};
                                values = new String[]{String.valueOf(idLastMedia), String.valueOf(idLastPlayerInsered)};
                                connexionASdb.insert("ba_middlemediaplayer",fields,values);
                                //On décremente sur l'id du dernier enregistrement  inséré dans la table ba_player pour avoir l'id des autres joueurs...
                                idLastPlayerInsered-=1;
                            }
                        }
                    }
                }
            }
            // Mise à jour des joueurs si changement
            //On récupére les joueurs qui seront mis à jour
            List<ClassPlayer> listUpdates =  tabPlayer.getItems().stream().filter(player -> player.getId() > 0).toList();
            //On boucle sur la liste initiale et celle obtenue apres mis à jour pour verifier elles sont differentes...
            int nbMaJ =  0;
            for(ClassPlayer player: listUpdates){
                for (ClassPlayer initialPlayer : cloneInitialPlayer){
                    if(initialPlayer.getId()==player.getId()){
                        if (!initialPlayer.equals(player)){
                            nbMaJ+=1;
                            //Mis à jour du profile dans la table ba_player
                            fields = new String[]{"lastName", "firstName", "email", "birthday", "description","phone", "phoneEmergency", "height", "weight", "position"};
                            String sValues =  player.getString(true);
                            values = sValues.split(",");
                            connexionASdb.update(player.getId(),"ba_player",fields,values);
                            //Mise à jour de la photo de profile dans la table ba_media
                            fields = new String[]{"description", "typeMime", "path"};
                            values  = new String[]{"Image de profile","Image",player.getPathProfile()};
                            if(!initialPlayer.getMedias().isEmpty() && player.getMedias().getFirst().getId()!=0){
                                connexionASdb.update(player.getMedias().getFirst().getId(),"ba_media",fields,values);
                            }else {
                                int idMedia =connexionASdb.insert("ba_media",fields,values);
                                fields = new String[]{"idPlayer", "idMedia"};
                                values = new String[]{String.valueOf(player.getId()), String.valueOf(idMedia)};
                                connexionASdb.insert("ba_middlemediaplayer",fields,values);
                            }
                        }
                    }
                }
            }
            //On met a jour la collection initiale
            cloneInitialPlayer.clear();
            cloneInitialPlayer.addAll(listInserts);
            cloneInitialPlayer.addAll(listUpdates);
            if(JOptionPane.showConfirmDialog(null,"Operation terminée.\n "+listInserts.size()+" Insertion(s) et "+nbMaJ+" mise(s) à jour\n Voulez-vous fermer la fenêtre?","Confirm",JOptionPane.YES_NO_OPTION)==0){
                Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
                stage.close();
            }
        }
    }
    @FXML
    void cancel(ActionEvent event){
        Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
        stage.close();
    }
    @FXML
    void delete(){
        tabPlayer.getItems().forEach(player -> {
            try {
                if (player.equals(tabPlayer.getSelectionModel().getSelectedItem())){
                    if (player.getId()>0){
                        if (JOptionPane.showConfirmDialog(null,"Il s'agit d'un joueur deja persister dans votre base.Est vous sûr de vouloir le supprimmer definitivemenT??","Confirmer l'action",JOptionPane.YES_NO_OPTION)==0){
                            connexionASdb.delete("ba_player",player.getId());
                            playerQueue.offer(player);
                            tabPlayer.getItems().remove(player);
                        }
                    }else{
                        playerQueue.offer(player);
                        tabPlayer.getItems().remove(player);
                    }
                }
            }catch (SQLException e){
                e.printStackTrace();
            }
        });
    }
    @FXML
    void deleteAll(){
        playerQueue.addAll(tabPlayer.getItems());
        tabPlayer.getItems().removeAll();
    }
    @FXML
    void undo(){
        if (!playerQueue.isEmpty()){
            while(!playerQueue.isEmpty()){
                tabPlayer.getItems().add(playerQueue.poll());
            }
        }
    }
    @FXML
    private void formatInFields() throws URISyntaxException {
        currentPlayer =  tabPlayer.getSelectionModel().getSelectedItem();
        if (currentPlayer != null){
            fId.setText(String.valueOf(currentPlayer.getId()));
            fFName.setText(currentPlayer.getFirstName());
            fLName.setText(currentPlayer.getLastName());
            fEmail.setText(currentPlayer.getEmail());
            dBirth.setValue(LocalDate.parse(currentPlayer.getBirthDay().toString()));
            fEPhone.setText(String.valueOf(currentPlayer.getPhoneEmergency()));
            fPhone.setText(String.valueOf(currentPlayer.getPhone()));
            fHeight.setText(String.valueOf(currentPlayer.getHeight()));
            fWeight.setText(String.valueOf(currentPlayer.getWeight()));
            cbPosition.getSelectionModel().select(currentPlayer.getPosition());
            fDesc.setText(currentPlayer.getDescription());
            if (!currentPlayer.getPathProfile().isEmpty()){
                fIdMedia.setText(String.valueOf(currentPlayer.getMedias().getFirst().getId()));
                Image image  =  new Image("file:"+currentPlayer.getPathProfile());
                circleProfile.setFill(new ImagePattern(image));
            }
            tabPlayer.getItems().remove(currentPlayer);
        }
    }
    @FXML
    private void addProfile() throws IOException {
        JFileChooser fileChooser = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter("JPG & GIF Images", "jpg", "gif","jpeg");
        fileChooser.setFileFilter(filter);
        int returnChoose = fileChooser.showOpenDialog(null);
        if (returnChoose ==  JFileChooser.APPROVE_OPTION){
//            Je charge le fichier dans le rep dédié du projet ...
            Path sourceFile  = Path.of(fileChooser.getSelectedFile().getPath());
            Path targetFile =  Paths.get("src/main/resources/Image/"+sourceFile.getFileName());
            Files.copy(sourceFile,targetFile, StandardCopyOption.REPLACE_EXISTING);
            Image image =  new Image("file:"+ targetFile);
            circleProfile.setFill(new ImagePattern(image));

        }else ToastMessage.show("Info","Le fichier entré n'est pas pris en compte",3);
    }
    @FXML
    void formatField(MouseEvent event) {
        TextField current  = (TextField) event.getSource();
        ClassFieldFormat.formatField(current,getFieldType(current));
    }
    private  String getFieldType(TextField textField){
        String motif = null;
        if(textField.equals(fFName) || textField.equals(fLName)){
            motif =  "label";
        }else if (textField.equals(fEmail)){
            motif =  "email";
        } else if (textField.equals(fPhone) || textField.equals(fPostal) || textField.equals(fEPhone) || textField.equals(fHeight) || textField.equals(fWeight)) {
            motif = "number";
        }
        return motif;
    }
    private void BindTabClass(){
        cId.setCellValueFactory(new PropertyValueFactory<>("Id"));
        cIdTeam.setCellValueFactory(new PropertyValueFactory<>("IdTeam"));
        cFirstName.setCellValueFactory(new PropertyValueFactory<>("FirstName"));
        cLastName.setCellValueFactory(new PropertyValueFactory<>("LastName"));
        cEmail.setCellValueFactory(new PropertyValueFactory<>("Email"));
        cBirthDay.setCellValueFactory(new PropertyValueFactory<>("BirthDay"));
        cPhoneEmergency.setCellValueFactory(new PropertyValueFactory<>("PhoneEmergency"));
        cHeight.setCellValueFactory(new PropertyValueFactory<>("Height"));
        cWeight.setCellValueFactory(new PropertyValueFactory<>("Weight"));
        cPosition.setCellValueFactory(new PropertyValueFactory<>("Position"));
        cDescription.setCellValueFactory(new PropertyValueFactory<>("Description"));
        // Le chemin du fichier uploader lier
        cPathProfile.setCellValueFactory(new PropertyValueFactory<>("PathProfile"));
        cIdMedia.setCellValueFactory(new PropertyValueFactory<>("IdMedia"));
        //On rend editable toutes les colonnes du tableau TabPlayers
        cIdTeam.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        cFirstName.setCellFactory(TextFieldTableCell.forTableColumn());
        cLastName.setCellFactory(TextFieldTableCell.forTableColumn());
        cEmail.setCellFactory(TextFieldTableCell.forTableColumn());
        cBirthDay.setCellFactory(TextFieldTableCell.forTableColumn(new LocalDateStringConverter()));
        cPhone.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter() ));
        cPhoneEmergency.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        cHeight.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        cWeight.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        cPosition.setCellFactory(TextFieldTableCell.forTableColumn());
        cDescription.setCellFactory(TextFieldTableCell.forTableColumn());
        //On recupere les valeurs après edition ...
        cFirstName.setOnEditCommit( item -> {
            ClassPlayer player = item.getRowValue();
            player.setFirstName(item.getNewValue());
        });
        cLastName.setOnEditCommit( item -> {
            ClassPlayer player = item.getRowValue();
            player.setLastName(item.getNewValue());
        });
        cEmail.setOnEditCommit( item -> {
            ClassPlayer player = item.getRowValue();
            player.setEmail(item.getNewValue());
        });

    }
    public void fillPage() throws Exception {
        lName.setText(currentCategory.getName());
        lDate.setText(currentCategory.getDateCreation().toString());
        lGender.setText(currentCategory.getGender());
        lTeam.setText(currentTeam.getName());
        lGamePriority.setText(currentTeam.getGamePriority());
        lGamePlan.setText(currentTeam.getGamePlan());

        ObservableList<String> listPosition = FXCollections.observableArrayList(new String[]{"PG - Point Guard", "SG - Shooting Guard"," SF - Small Forward", "PF - Power Forward", "C - Center"});
        cbPosition.setItems(listPosition);
        cbPosition.getSelectionModel().selectFirst();

        ObservableList<String> gender = FXCollections.observableArrayList(new String[]{"Man","Woman"});
        cbGender.setItems(gender);
        cbGender.getSelectionModel().select(currentCategory.getGender());
// J'ajoute les joueurs et supprime les positions déjà prises
        if (!currentTeam.getPlayers().isEmpty()) {
            for (ClassPlayer player : currentTeam.getPlayers()) {
                player.initialise();
                cloneInitialPlayer.add(player.clone());
                listPlayer.add(player);
                if (cloneInitialPlayer.stream().filter(onePlayer -> onePlayer.getPosition().equals(player.getPosition())).toArray().length==2){
                    cbPosition.getItems().removeIf(position -> position.equals(player.getPosition()));
                }
            }

        }else{
            btnFill.setDisable(true);
            btnClear.setDisable(true);
            btnDelete.setDisable(true);
            btnUndo.setDisable(true);
        }
        tabPlayer.setItems(listPlayer);
    }
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            listPlayer =  FXCollections.observableArrayList();
            currentCategory =  ClassManager.getUniqueInstance().getCurrentCategory();
            currentTeam = currentCategory.getCurrentTeam();
            connexionASdb =  new ConnexionASdb();
            BindTabClass();
            fillPage();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

