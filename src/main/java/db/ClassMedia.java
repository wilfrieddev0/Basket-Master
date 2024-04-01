package db;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Objects;

public class ClassMedia {
    public int getId() {
        return mId;
    }
    private  int mId;
    private  String mName;
    private  String mDescritption;
    private java.sql.Date mDateCreation;
    private int mSize;
    private String mTypeMime;
    private  String mPath;
    private static  ConnexionASdb connexionASdb;
    private  ClassMedia(){}
    public ClassMedia(int id,String mPath){
        this.mId = id;
        this.mDateCreation = java.sql.Date.valueOf(LocalDate.now());
        this.mPath =  mPath;
        this.mTypeMime =  "Image/img";
    }
    public static ArrayList<ClassMedia> loadMedia(int id,String assosTable) throws Exception {
        ConnexionASdb connexionASdb = new ConnexionASdb();
        ArrayList<ClassMedia> tempMedias =  new ArrayList<>();
        String sqlReq = "SELECT * FROM ba_media JOIN ba_middlemedia"+assosTable+" ON (ba_media.id=ba_middlemedia"+assosTable+".idmedia) WHERE ba_middlemedia"+assosTable+".id"+assosTable +"="+id;
        Connection connection = connexionASdb.getConnection();
        PreparedStatement statement = connection.prepareStatement(sqlReq);
        ResultSet resultSet =  statement.executeQuery();
        if (resultSet != null){
            while (resultSet.next()){
                ClassMedia temp = new ClassMedia();
                temp.setId(resultSet.getInt("id"));
                temp.setDescription(resultSet.getString("description"));
                temp.setDateCreation(resultSet.getDate("dateCreation").toLocalDate());
                temp.setTypeMime(resultSet.getString("typeMime"));
                temp.setName(resultSet.getString("description"));
                temp.setSize(resultSet.getInt("size"));
                temp.setPath(resultSet.getString("path"));
                tempMedias.add(temp);
            }
        }
        return  tempMedias;
    }
    public void create(String[] fields, String[] values) throws SQLException {
        StringBuilder setValues  = new StringBuilder();
        StringBuilder setFiels = new StringBuilder();
        for (int i=1;i<values.length;i++){
            setFiels.append(fields[i]).append(",");
            setValues.append(values[i]).append(",");
        }
        String sqlReq = "INSERT INTO ba_media (?) VALUES (?)" ;
        Connection connection = connexionASdb.getConnection();
        PreparedStatement statement = connection.prepareStatement(sqlReq);
        statement.setString(1,setFiels.toString());
        statement.setString(2,setValues.toString());
        statement.executeQuery();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ClassMedia that)) return false;
        return mId == that.mId && mSize == that.mSize && Objects.equals(mPath, that.mPath);
    }
    @Override
    public int hashCode() {
        return Objects.hash(mId, mSize, mPath);
    }

    public void setId(int mId){this.mId = mId;}
    public void setName(String mName) {this.mName = mName;}
    public void setDateCreation(LocalDate mdateCreation){this.mDateCreation = Date.valueOf(mdateCreation);}
    public void setDescription(String mDescription){this.mDescritption = mDescription;}
    public void setTypeMime(String mTypeMime){this.mTypeMime = mTypeMime;}
    public void setSize(int mSize){this.mSize = mSize;}
    public void setPath(String mPath) {this.mPath = mPath;}
    public String getDescription() {return mDescritption;}
    public String getPath() { return mPath; }
}
