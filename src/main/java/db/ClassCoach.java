package db;

import java.security.NoSuchAlgorithmException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class ClassCoach {
    private int mId;
    private String mName;
    private String mLastName;
    private String mEmail;
    private String mLogin;
    private String mPassword;
    private ArrayList<ClassMedia> mMedias;
    private ConnexionASdb connexionASdb;
    private volatile  static ClassCoach instance;
    private ClassCoach() {}
    public static ClassCoach getInstance() throws Exception {
        if(instance == null){
            synchronized (ClassCoach.class){
                if (instance == null){
                    instance = new ClassCoach();
                    instance.setConnexionASdb(new ConnexionASdb());
                }
            }
        }
        return instance;
    }
    public void initialiseCoatch() throws Exception {
         String sqlReq =  "SELECT * FROM ba_coach WHERE id="+mId;
         PreparedStatement statement =  connexionASdb.getConnection().prepareStatement(sqlReq);
         ResultSet resultSet = statement.executeQuery();
        if (resultSet != null){
            while ( resultSet.next()){
                mId =  resultSet.getInt("id");
                mName = resultSet.getString("firstName");
                mLastName = resultSet.getString("lastName");
                mEmail =  resultSet.getString("email");
            }
        }
        mMedias = ClassMedia.loadMedia(this.mId,"coach");
    }
    public String loadMedia(String name) throws Exception {
        String path = "";
        mMedias =  ClassMedia.loadMedia(mId,"coach");
        for(ClassMedia occ : mMedias){
            if (occ.getDescription().equals(name)){
                path = occ.getPath();
                break;
            }
        }
        return path;
    }
    public void update(String[] fields, String[] values) throws SQLException {
        connexionASdb.update(instance.mId, "ba_coach",fields,values);
    }
    public void delete() throws SQLException {
        connexionASdb.delete("ba_coach", mId);
    }
    public void createCoatch(String[] fields,String[] values) throws SQLException, NoSuchAlgorithmException {
        connexionASdb.insert("ba_coach",fields,values);
    }
    public void setId(int mId) { this.mId = mId; }
    public int getId() {return mId;}
    public void setConnexionASdb(ConnexionASdb connexionASdb) {this.connexionASdb = connexionASdb;}
    public String getName() { return mName;}
    public String getEmail() {return mEmail; }
    public String getLastName(){return  mLastName;}
}
