package db;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;

public class ClassCategory {
    private  int mId;
    private  int mIdCoach;
    private String mName;
    private int mMaxAge;
    private int mMinAge;
    private String mGender;
    private Date mDateCreation;
    private String mStory;
    private int mRangeAge;
    public ArrayList<ClassTeam> getTeams() { return mTeams; }
    private ArrayList<ClassTeam> mTeams;
    private ClassTeam mCurrentTeam;
    private ClassStatistique mStatistique;
    private ConnexionASdb connexionASdb;
    public ClassCategory(int id) throws Exception { mId = id;connexionASdb = new ConnexionASdb();}
    public void initialise() throws Exception {
        String sqlReq =  "SELECT * FROM ba_category WHERE id="+mId;
        Statement statement = connexionASdb.getStatement();
        ResultSet resultSet = statement.executeQuery(sqlReq);
        if (resultSet.next()){
                mId=resultSet.getInt("id");
                mIdCoach=resultSet.getInt("idCoach");
                mName=resultSet.getString("name");
                mGender=resultSet.getString("gender");
                mRangeAge=resultSet.getInt("averageAge");
                mDateCreation=resultSet.getDate("dateCreation");
                mStory=resultSet.getString("story");
                // On ne charge par entierement l'equipe ici pour optimiser l'app.
                String sqlReqTeam = "SELECT id FROM ba_team WHERE idCategory="+mId;
                ResultSet resTeam =  connexionASdb.getStatement().executeQuery(sqlReqTeam);
                ArrayList<ClassTeam> teams =  new ArrayList<ClassTeam>();
                while (resTeam.next()){
                    teams.add(new ClassTeam(resTeam.getInt("id")));
                }
                mTeams = teams;
        }
    }
    public void add(String []fields,String[] values) throws Exception {
        int id = connexionASdb.insert("ba_team",fields,values);
        mTeams.add(new ClassTeam(id));
    }
    public ClassCategory(String []fields,String[] values) throws Exception {
        connexionASdb = new ConnexionASdb();
        if (connexionASdb.insert("ba_category",fields,values)> 0){
            String sqlReq  =  "SELECT * FROM ba_category WHERE name=?";
            PreparedStatement statement =  connexionASdb.getConnection().prepareStatement(sqlReq);
            statement.setString(1,values[1]);
            ResultSet resultSet = statement.executeQuery();
            if( resultSet.next()){
                mId=resultSet.getInt("id");
                mIdCoach=resultSet.getInt("idCoach");
                mName=resultSet.getString("name");
                mGender=resultSet.getString("gender");
                mRangeAge=resultSet.getInt("averageAge");
                mDateCreation=resultSet.getDate("dateCreation");
                mStory=resultSet.getString("story");
            }
        };
    }
    public void update(String[] fields,String[] values) throws SQLException {
        connexionASdb.update(mCurrentTeam.getId(),"ba_team",fields,values);
    }
    public void deleteTeam() throws SQLException {
        connexionASdb.delete("ba_team",mCurrentTeam.getId());
        mTeams.remove(mCurrentTeam);
    }
    public void setId(int mId) { this.mId = mId;}
    public void setName(String mName) {this.mName = mName;}
    public void setCurrentTeam(ClassTeam mCurrentTeam) { this.mCurrentTeam = mCurrentTeam; }
    public ClassTeam getCurrentTeam() { return mCurrentTeam; }
    public void setMaxAge(int mAge) {  this.mMaxAge = mAge; }
    public void setMinAge(int mAge) {  this.mMinAge= mAge; }
    public void setGender(String mGender) {  this.mGender = mGender;  }
    public void setStatistique(ClassStatistique mStatistique) { this.mStatistique = mStatistique; }
    public void setDateCreation(Date mDateCreation) {  this.mDateCreation = mDateCreation; }
    public void setStory(String mStory) { this.mStory = mStory; }
    public void setIdCoach(int mIdCoach) {this.mIdCoach = mIdCoach; }
    public int getId() { return mId; }
    public String getName() { return mName; }
    public int getAge() { return mRangeAge; }
    public String getGender() { return mGender; }
    public Date getDateCreation() { return mDateCreation; }
    public String getStory() {return mStory; }
    public ClassStatistique getStatistique() { return mStatistique; }
    public int getRangeAge(){
        return (mMaxAge + mMinAge)/2;
    }
    @Override
    public String toString(){
        return this.mName;
    }
}