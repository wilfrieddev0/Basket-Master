package db;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Objects;

public class ClassTeam {
    private int mId;
    private int idCategory;
    private  String mName;
    private  String  mGamePriority;
    private int mTotalPlayer;
    private String mGamePlane;
    public ArrayList<ClassPlayer> mPlayers =  new ArrayList<>();
    private  ClassPlayer mCurrentPlayer;
    private  ConnexionASdb connexionASdb;
    public ClassTeam(int id) throws Exception {
        mId = id;
        connexionASdb = new ConnexionASdb();
    }
    public ClassTeam(int idCategory,String mName, String gamePriority,String gamePlan){
        this.mName  = mName;
        this.idCategory =  idCategory;
        this.mGamePriority =  gamePriority;
        this.mGamePlane = gamePlan;
    }
    public void initialiseTeam() throws SQLException {
        String sqlReqCoach = "SELECT * FROM ba_Team WHERE id="+mId;
        Statement statementCoach= connexionASdb.getStatement();
        ResultSet res =  statementCoach.executeQuery(sqlReqCoach);
        if(res != null && res.next()){
        //
            this.mId =  res.getInt("id");
            this.idCategory = res.getInt("idCategory");
            this.mName = res.getString("name");
            this.mGamePlane = res.getString("gamePlan");
            this.mGamePriority = res.getString("gamePriority");
        // Filling of  mPlayers...
            String sqlReqPlayer="SELECT * FROM ba_player WHERE idTeam="+mId;
            Statement statementPlayer = connexionASdb.getStatement();
            ResultSet resultSet = statementPlayer.executeQuery(sqlReqPlayer);
            mPlayers =  new ArrayList<>();
            while(resultSet.next()){
                this.mPlayers.add(new ClassPlayer(resultSet.getInt("id")));
            }
        }
    }
    public void add(String []fields,String[] values) throws Exception {
        int id = connexionASdb.insert("ba_player",fields,values);
        mPlayers.add(new ClassPlayer(id));
    }
    public void update(String[] fields,String[] values) throws SQLException {
        connexionASdb.update(mCurrentPlayer.getId(),"ba_player",fields,values);
    }
    public void delete(int id) throws SQLException {
        mPlayers.remove(mCurrentPlayer);
        connexionASdb.delete("ba_player",id);
    }
    public void setId(int mId) { this.mId = mId;}
    public void setGamePlan(String mGamePlane) { this.mGamePlane = mGamePlane;}
    public void setName(String mName) {this.mName = mName;}
    public void setCurrentPlayer(ClassPlayer mCurrentPlayer) {this.mCurrentPlayer = mCurrentPlayer; }
    public void setGamePriority(String mGamePriority) {this.mGamePriority = mGamePriority;}
    public void setTotalPlayer(int mTotalPlayer) {this.mTotalPlayer = mTotalPlayer;}
    public int getId() { return mId;}
    public String getName() { return mName; }
    public String getGamePriority() { return mGamePriority;}
    public int getTotalPlayer() {return mTotalPlayer;}
    public String getGamePlan() { return mGamePlane;}
    public ArrayList<ClassPlayer> getPlayers() {return mPlayers;}
    public String toString(boolean insert){
        return "(" +
                " " + idCategory +
                ",'" + mName + '\'' +
                ",'" + mGamePriority + '\'' +
                ",'" + mGamePlane + '\'' +
                ')';
    }
    @Override
    public String toString() {
        return mName;
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ClassTeam team)) return false;
        return mId == team.mId && mTotalPlayer == team.mTotalPlayer && Objects.equals(mName, team.mName);
    }
    @Override
    public int hashCode() {
        return Objects.hash(mId, mName, mTotalPlayer);
    }
}
