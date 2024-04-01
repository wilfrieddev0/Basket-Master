package manager;

import db.ClassCategory;
import db.ClassPlayer;
import db.ConnexionASdb;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
public class ClassManager {
    private static volatile ClassManager uniqueInstance;
    private int idCoach;
    public ArrayList<ClassCategory> getCategories() {return categories;}
    private ArrayList<ClassCategory> categories;
    public ConnexionASdb getConnexionASdb() { return connexionASdb;}
    private ConnexionASdb connexionASdb;
    public ClassCategory getCurrentCategory() { return currentCategorie;}
    private ClassCategory currentCategorie;
    private ClassManager(){}
    public void setId(int id){idCoach=id;}
    public void loadCaterogies() throws Exception {
        categories =  new ArrayList<>();
        String sqlReq= "SELECT id FROM ba_category WHERE idCoach="+idCoach;
        ResultSet resultSet = connexionASdb.getStatement().executeQuery(sqlReq);
        if(resultSet!= null){
            while (resultSet.next()){
                categories.add(new ClassCategory(resultSet.getInt("id")));
            }
        }
    }
    public void add(String []fields,String[] values) throws Exception {
        int id = connexionASdb.insert("ba_category",fields,values);
        categories.add(new ClassCategory(id));
    }
    public void update(String[] fields,String[] values) throws SQLException {
        connexionASdb.update(currentCategorie.getId(),"ba_category",fields,values);
    }
    public void delete() throws SQLException {
        connexionASdb.delete("ba_category", currentCategorie.getId());
        categories.remove(currentCategorie);
    }
    public void setCurrentCategory(ClassCategory current){
            currentCategorie =  current;
    }
    public static ClassManager getUniqueInstance() throws Exception {
        if (uniqueInstance == null){
            synchronized(ClassManager.class){
                if (uniqueInstance == null){
                    uniqueInstance =  new ClassManager();
                    uniqueInstance.setConnexionASdb(new ConnexionASdb());
                }
            }
        }
        return uniqueInstance;
    }
    public void setConnexionASdb(ConnexionASdb connexionASdb) { this.connexionASdb = connexionASdb;}
    public ObservableList<ClassPlayer> allPlayers() throws SQLException {
        ObservableList<ClassPlayer> listPLayers = FXCollections.observableArrayList();
        String sqlReq = "SELECT ba_player.id, ba_player.firstName, birthday, ba_player.lastName FROM ba_player";
        Statement statement = connexionASdb.getStatement();
        ResultSet resultSet =  statement.executeQuery(sqlReq);
        while (resultSet.next()){
            ClassPlayer temp  =  new ClassPlayer();
            temp.setId(resultSet.getInt("id"));
            temp.setLastName(resultSet.getString("lastName"));
            temp.setFirstName(resultSet.getString("firstName"));
            temp.setBirthDay(resultSet.getDate("birthday"));
            listPLayers.add(temp);
        }
        return listPLayers;
    }
}
