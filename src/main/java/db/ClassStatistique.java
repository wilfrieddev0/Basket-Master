package db;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Locale;

public class ClassStatistique {
    private  int mId;
    private int mIdPlayer;
    public int getIdTeam() {
        return mIdTeam;
    }
    public void setIdTeam(int mIdTeam) {
        this.mIdTeam = mIdTeam;
    }
    private int mIdTeam;
    private Date mDate;
    private  String mOppenent;
    private String mScore;
    private int mTimeGame;
    private int mPoints;
    private int mRebounds;
    private int mAssists;
    private int mSteals;
    private int mBlocks;
    private int mAttempts;
    private int m3PointsAttempts;
    private int m3PointsPlay;
    private String mVictory;
    public int getGameCounts() {
        return gameCounts;
    }
    public void setGameCounts(int gameCounts,boolean add) {
        if (!add) this.gameCounts = gameCounts; else this.gameCounts += gameCounts;
    }
    private int gameCounts;
    private  ConnexionASdb connexionASdb;

    public ArrayList<ClassStatistique> getStatistiques() {
        return statistiques;
    }

    private ArrayList<ClassStatistique> statistiques = new ArrayList<>();
    public ClassStatistique(){}
    public void search(String mWords, Date mFrom, Date  mTo) throws Exception {
        connexionASdb =  new ConnexionASdb();
        String sql  =  "SELECT * " +
                "FROM ba_statistique " +
                "JOIN ba_player ON (idPlayer=ba_player.id) " +
                "WHERE ba_statistique.date BETWEEN ? " +
                "AND ? " +
                "OR MATCH(ba_player.firstName,ba_player.lastName,ba_player.email,ba_player.position) AGAINST (?) OR MATCH(ba_statistique.oppenent) AGAINST (?);";
        PreparedStatement statement =  connexionASdb.getConnection().prepareStatement(sql);
        statement.setDate(1,mFrom);
        statement.setDate(2,mTo);
        statement.setString(3,mWords);
        statement.setString(4,mWords);
        ResultSet resultSet = statement.executeQuery();
        fillStatistiques(resultSet,false);
        connexionASdb.getConnection().close();
    }
    public  void loadStatistiques(int id) throws Exception {
        connexionASdb =  new ConnexionASdb();
        String sqlReq =  "SELECT * FROM ba_statistique WHERE idPlayer="+id;
        ResultSet resultSet =  connexionASdb.getStatement().executeQuery(sqlReq);
        fillStatistiques(resultSet,false);
        connexionASdb.getConnection().close();
    }
    public ArrayList<ClassStatistique> loadCategoryStats(int idCategory) throws Exception {
        connexionASdb = new ConnexionASdb();
        String sqlReq= "SELECT ba_statistique.id,ba_statistique.idTeam,ba_statistique.idPlayer,ba_statistique.date,ba_statistique.oppenent,ba_statistique.score,ba_statistique.timeGame,ba_statistique.points,ba_statistique.rebounds,ba_statistique.assists,ba_statistique.steals,ba_statistique.blocks,ba_statistique.attempts,ba_statistique.3pointsShotsAttempts,ba_statistique.3pointsPlay,ba_statistique.Victory" +
                " FROM ba_statistique" +
                " JOIN ba_team ON ba_statistique.idTeam=ba_team.id " +
                " JOIN ba_category ON ba_team.idCategory=ba_category.id" +
                " WHERE ba_statistique.idPlayer IS NULL AND  ba_category.id ="+idCategory;
        ResultSet resultSet =  connexionASdb.getStatement().executeQuery(sqlReq);
        fillStatistiques(resultSet,false);
        connexionASdb.getConnection().close();
        return  statistiques;
    }
    public ArrayList<ClassStatistique> loadTeamStats(int idTeam) throws Exception {
        connexionASdb = new ConnexionASdb();
        String sqlReq= "SELECT * FROM ba_statistique " +
                "WHERE ba_statistique.idPlayer IS NOT NULL AND ba_statistique.idTeam ="+idTeam;
        ResultSet resultSet =  connexionASdb.getStatement().executeQuery(sqlReq);
        fillStatistiques(resultSet,true);
        connexionASdb.getConnection().close();
        return  statistiques;
    }
    private  void fillStatistiques(ResultSet resultSet,boolean dist) throws SQLException {
        statistiques =  new ArrayList<>();
        while(resultSet.next()){
            ClassStatistique temp = null;
            boolean add = false;
            if (dist) for (ClassStatistique playerStat : statistiques){
                if (playerStat.getIdPlayer()==resultSet.getInt("idPlayer")){
                    add = true;
                    temp =  playerStat;
                }
            }
            if (temp==null){
                temp =  new ClassStatistique();
                temp.setId(resultSet.getInt("id"));
                temp.setIdPlayer(resultSet.getInt("idPlayer"));
                temp.setIdTeam(resultSet.getInt("idTeam"));
                temp.setDate(resultSet.getDate("date"));
            }
            temp.setOppenent(resultSet.getString("oppenent"),dist);
            temp.setScore(resultSet.getString("score"),dist);
            temp.setTimeGame(resultSet.getInt("timeGame"), dist);
            temp.setPoints(resultSet.getInt("points"),dist);
            temp.setRebounds(resultSet.getInt("rebounds"),dist);
            temp.setAssists(resultSet.getInt("assists"),dist);
            temp.setSteals(resultSet.getInt("steals"),dist);
            temp.setBlocks(resultSet.getInt("blocks"),dist);
            temp.setAttempts(resultSet.getInt("attempts"),dist);
            temp.set3PointsAttempts(resultSet.getInt("3pointsShotsAttempts"),dist);
            temp.set3PointsPlay(resultSet.getInt("3pointsPlay"),dist);
            temp.setVictory(resultSet.getString("victory"));
            if (!add) {
                temp.setGameCounts(1,false);
                statistiques.add(temp);
            } else temp.setGameCounts(1,add);
        }
    }
    public int getId() {
        return mId;
    }
    public void setId(int mId) {
        this.mId = mId;
    }
    public int getIdPlayer() {
        return mIdPlayer;
    }
    public void setIdPlayer(int mIdPlayer) {
        this.mIdPlayer = mIdPlayer;
    }
    public Date getDate() {
        return mDate;
    }
    public void setDate(Date mDate) {
        this.mDate = mDate;
    }
    public String getOppenent() {
        return mOppenent;
    }
    public void setOppenent(String mOppenent,boolean add) {
        if (!add) this.mOppenent = mOppenent; else this.mOppenent += " - "+ mOppenent;
    }
    public String getScore() {return mScore;}
    public void setScore(String mScore,boolean add) {
        if (!add) this.mScore = mScore; else this.mScore += " - "+ mScore;
    }
    public int getTimeGame() {  return mTimeGame;}
    public double getAverageTimeGame() {
        if (statistiques.isEmpty()) {
            return ((double) mTimeGame / gameCounts);
        }return statistiques.stream().mapToInt(ClassStatistique::getTimeGame).average().getAsDouble();
    }
    public void setTimeGame(int mTimeGame,boolean add) {
        if (!add) this.mTimeGame = mTimeGame; else this.mTimeGame += mTimeGame;
    }
    public int getPoints() {
        return mPoints;
    }
    public double getAveragePoints() {
        if (!statistiques.isEmpty()){
            return statistiques.stream().mapToInt(ClassStatistique::getPoints).average().getAsDouble();
        }else{
            return ((double) mPoints/gameCounts);
        }
    }
    public int getTotalAttempts() {
        return statistiques.stream().mapToInt(ClassStatistique::getAttempts).sum();
    }
    public void setPoints(int mPoints,boolean add) {
        if (!add) this.mPoints = mPoints; else this.mPoints += mPoints;
    }
    public int getRebounds() {
        return mRebounds;
    }
    public double getAverageRebounds() {
        if (!statistiques.isEmpty()){
            return statistiques.stream().mapToInt(ClassStatistique::getRebounds).average().getAsDouble();
        }else {
            return  ((double) mRebounds/gameCounts);
        }
    }
    public void setRebounds(int mRebounds,boolean add) {
        if (!add) this.mRebounds = mRebounds; else this.mRebounds += mRebounds;
    }
    public int getAssists() {
        return mAssists;
    }
    public double getAverageAssists() {
        if(!statistiques.isEmpty()){
            return statistiques.stream().mapToInt(ClassStatistique::getAssists).average().getAsDouble();
        }else {
            return ((double)mAssists/gameCounts);
        }
    }
    public void setAssists(int mAssists,boolean add) {
        if (!add) this.mAssists = mAssists; else this.mAssists += mAssists;
    }
    public int getSteals() {
        return mSteals;
    }
    public double getAverageSteals() {
        if (!statistiques.isEmpty()){
            return statistiques.stream().mapToInt(ClassStatistique::getSteals).average().getAsDouble();
        }else {
            return ((double)mSteals/gameCounts);
        }
    }
    public void setSteals(int mSteals,boolean add) {
        if (!add)this.mSteals = mSteals; else this.mSteals+= mSteals;
    }
    public int getBlocks() {
        return mBlocks;
    }
    public double getAverageBlocks() {
        if (!statistiques.isEmpty()){
            return statistiques.stream().mapToInt(ClassStatistique::getBlocks).average().getAsDouble();
        }else {
            return ((double)mSteals/gameCounts);
        }
    }
    public void setBlocks(int mBlocks,boolean add) {
        if (!add) this.mBlocks = mBlocks; else this.mBlocks += mBlocks;
    }
    public int getAttempts() {
        return mAttempts;
    }
    public double getAverageAttempts() {
        if (!statistiques.isEmpty()){
            return statistiques.stream().mapToInt(ClassStatistique::getAttempts).average().getAsDouble();
        }else {
            return ((double)mAttempts/gameCounts);
        }
    }
    public double getAccuraryFromDowntown(){
        if(statistiques.isEmpty()){
            return ((double) m3PointsAttempts/m3PointsPlay);
        }else {
            int attempts = statistiques.stream().mapToInt(ClassStatistique::get3PointsAttempts).sum();
            int counts = statistiques.stream().mapToInt(ClassStatistique::get3PointsPlay).sum();
            return ((double)counts /attempts)*100;
        }
    }
    public String getAccuraryPerMatch(){
        return String.format(Locale.US,"%.2f",((double)get3PointsPlay() /get3PointsAttempts())*100);
    }
    public Double getFieldsGoals(){
        int attempts = statistiques.stream().mapToInt(ClassStatistique::getAttempts).sum();
        int counts = statistiques.stream().mapToInt(ClassStatistique::getPoints).sum();
        return ((double)counts/attempts)*100;
    }
    public String getFGPerMatch(){
        return String.format(Locale.US,"%.2f",((double)getPoints()/getAttempts()*100));
    }
    public void setAttempts(int mAttempts,boolean add) {
        if (!add) this.mAttempts = mAttempts; else this.mAttempts += mAttempts;}
    public int get3PointsAttempts() {
        return m3PointsAttempts;
    }
    public double getAverage3pointsAttempts() {
        if (!statistiques.isEmpty()){
            return statistiques.stream().mapToInt(ClassStatistique::get3PointsAttempts).average().getAsDouble();
        }else {
            return ((double)m3PointsAttempts/gameCounts);
        }
    }
    public void set3PointsAttempts(int m3PointsAttempts,boolean add) {
        if (!add) this.m3PointsAttempts = m3PointsAttempts; else this.m3PointsAttempts += m3PointsAttempts;
    }
    public int get3PointsPlay() {
        return m3PointsPlay;
    }
    public double getAverage3pointsPlay() {
        if (!statistiques.isEmpty()){
            return statistiques.stream().mapToInt(ClassStatistique::get3PointsPlay).average().getAsDouble();
        }else {
            return ((double)m3PointsPlay/gameCounts);
        }
    }
    public void set3PointsPlay(int m3PointsPlay,boolean add) {
        if(!add) this.m3PointsPlay = m3PointsPlay; else this.m3PointsPlay+= m3PointsPlay;
    }
    public String getResult() {
        return mVictory;
    }
    public int getTotalVictories() {return statistiques.stream().filter(stat -> stat.getResult().equals("true")).toArray().length;}
    public int getTotalPoints(){ return  statistiques.stream().mapToInt(ClassStatistique::getPoints).sum();}
    public int getTotalEgalities() {
        return statistiques.stream().filter(stat -> stat.getResult().equals("null")).toArray().length;
    }
    public int getTotalLosses() {
        return statistiques.stream().filter(stat -> stat.getResult().equals("false")).toArray().length;
    }
    public  int getTotalMatch(){
        return statistiques.size();
    }
    public void setVictory(String mVictory) {
        this.mVictory = mVictory;
    }
    public static void main(String[] args) throws Exception {
        ClassStatistique statistique =  new ClassStatistique();
        statistique.loadCategoryStats(2);
    }
}
