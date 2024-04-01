package db;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;

public class ClassEvent {
    private int mId;
    public int getIdTeam() { return mIdTeam; }
    public void setIdTeam(int mIdCategory) { this.mIdTeam = mIdCategory;}
    private int mIdTeam;
    public String getLocation() {
        return mLocation;
    }
    public void setLocation(String mLocation) {
        this.mLocation = mLocation;
    }
    private String mLocation;
    private  String mName;

    public Date getCurrentDate() {return currentDate;}

    private Date currentDate;
    public void setSubject(String mSubject) { this.mSubject = mSubject;}
    public void setDatePlanned(java.sql.Date datePlanned) { this.mDatePlanned = datePlanned;}
    private String mSubject;
    public java.sql.Date getDatePlanned() { return mDatePlanned; }
    private java.sql.Date mDatePlanned;
    public Time getTime() {  return mTime; }
    public void setTime(Time time) { this.mTime = time; }
    private Time mTime;
    private  TypeEvent mType;
    private ImportanceEvent mImportance;
    private String mDetails;
    public Boolean getClose() {return mClose;}
    public void setClose(Boolean close) { this.mClose = close; }
    public  ClassEvent getEvent(int id) throws Exception {
        ClassEvent temp  = new ClassEvent();
        String sqlReq = "SELECT * FROM ba_event WHERE id=?";
        PreparedStatement statement =  connexionASdb.getConnection().prepareStatement(sqlReq);
        statement.setInt(1,id);
        ResultSet resultSet = statement.executeQuery();
        if(resultSet.next()){
            temp.setId(resultSet.getInt("id"));
            temp.setIdTeam(resultSet.getInt("idTeam"));
            temp.setImportance(ImportanceEvent.valueOf(resultSet.getString("importance")));
            temp.setType(TypeEvent.valueOf(resultSet.getString("type")));
            temp.setSubject(resultSet.getString("subject"));
            temp.setLocation(resultSet.getString("location"));
            temp.setDatePlanned(resultSet.getDate("datePlanned"));
            temp.setTime(resultSet.getTime("time"));
            temp.setCurrentDate(resultSet.getDate("currentDate"));
            temp.setDetails(resultSet.getString("description"));
            temp.setClose(resultSet.getBoolean("close"));
        }
        return temp;
    }
    private  Boolean mClose;
    public ConnexionASdb getConnexionASdb() {return connexionASdb;}
    private ConnexionASdb connexionASdb;
    public ArrayList<ClassEvent> getEvents() { return events; }
    private ArrayList<ClassEvent> events;
    public int getId() {return mId;}
    public String getName() {return mName;}
    public TypeEvent getType() { return mType;}
    public ImportanceEvent getImportance() {return mImportance;}
    public String getDetails() {return mDetails;}
    public String getSubject() {return mSubject;}
    public  ClassEvent loadEvent(int id) throws Exception {
        ResultSet resultSet =  connexionASdb.getStatement().executeQuery("SELECT * FROM ba_event WHERE id="+id);
        fillEvents(resultSet);
        return events.getFirst();
    }
    public ClassEvent() throws Exception {
        events =  new ArrayList<>();
        connexionASdb = new ConnexionASdb();}
    public  void loadEvents(int idCoach,LocalDate date,Boolean close) throws Exception {
        PreparedStatement statement;
        String sqlReq;
        if (date == null){
            sqlReq = "SELECT * FROM ba_event WHERE idCoach="+idCoach+" and close=? LIMIT 30 ";
            statement =  connexionASdb.getConnection().prepareStatement(sqlReq);
            statement.setBoolean(1,close);
        }else{
            sqlReq = "SELECT * FROM ba_event WHERE idCoach="+idCoach+" AND datePlanned=? AND close=? LIMIT 30 ";
            statement =  connexionASdb.getConnection().prepareStatement(sqlReq);
            statement.setDate(1, java.sql.Date.valueOf(date));
            statement.setBoolean(2,close);
        }
            ResultSet resultSet = statement.executeQuery();
            fillEvents(resultSet);
    }
    private void addEvent(ClassEvent event){events.add(event);}
    public void setCurrentDate(Date currentDate) {this.currentDate = currentDate;}
    public void setType(TypeEvent mType) {this.mType = mType;}
    public void setDetails(String mDetails) {this.mDetails = mDetails;}
    public void setId(int mId) {this.mId = mId;}
    public int countEvent(int id,LocalDate date) throws SQLException {
        int nb = 0;
        String sqlReq = "SELECT COUNT(*) AS nbreEvents FROM ba_event WHERE idCoach=? AND datePlanned=? and close='0'";
        PreparedStatement statement = connexionASdb.getConnection().prepareStatement(sqlReq);
        statement.setInt(1,id);
        statement.setDate(2, java.sql.Date.valueOf(date));
        ResultSet resultSet =  statement.executeQuery();
        if (resultSet.next()){
            nb = resultSet.getInt("nbreEvents");
        }
        return  nb;
    }
    public void search(LocalDate begin,LocalDate end,Boolean close,String keyWords) throws Exception {
        String sqlReq =  "SELECT * FROM ba_event WHERE datePlanned BETWEEN ? AND ? AND close=? " +
                "AND MATCH(ba_event.type, ba_event.location, ba_event.subject,ba_event.description,ba_event.importance) AGAINST(?) LIMIT 30";
        PreparedStatement statement = connexionASdb.getConnection().prepareStatement(sqlReq);
        statement.setDate(1, java.sql.Date.valueOf(begin));
        statement.setDate(2, java.sql.Date.valueOf(end));
        statement.setBoolean(3,close);
        statement.setString(4,keyWords);
        ResultSet resultSet = statement.executeQuery();
        fillEvents(resultSet);
    }
    public void setImportance(ImportanceEvent mImportance) {this.mImportance = mImportance;}
    private  void fillEvents(ResultSet resultSet) throws Exception {
        events.clear();
        while(resultSet.next()){
            ClassEvent temp  = new ClassEvent();
            temp.setId(resultSet.getInt("id"));
            temp.setIdTeam(resultSet.getInt("idTeam"));
            temp.setImportance(ImportanceEvent.valueOf(resultSet.getString("importance")));
            temp.setType(TypeEvent.valueOf(resultSet.getString("type")));
            temp.setSubject(resultSet.getString("subject"));
            temp.setLocation(resultSet.getString("location"));
            temp.setDatePlanned(resultSet.getDate("datePlanned"));
            temp.setCurrentDate(resultSet.getDate("currentDate"));
            temp.setTime(resultSet.getTime("time"));
            temp.setDetails(resultSet.getString("description"));
            temp.setClose(resultSet.getBoolean("close"));
            events.add(temp);
        }
    }
}
