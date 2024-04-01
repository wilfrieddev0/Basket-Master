package db;

import java.sql.ResultSet;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.Objects;

public class ClassPlayer implements Cloneable {
    private int mId;
    private  int mIdTeam;
    public String getGender() {
        return mGender;
    }
    public void setGender(String mGender) {
        this.mGender = mGender;
    }
    private String mGender;
    private String mFirstName;
    private String mLastName;
    private java.sql.Date mBirthDay;
    private  String mEmail;
    private String mDescription;
    private String mCategory;
    private  int mPhone;
    private  int mPhoneEmergency;
    private  int mHeight;
    private  int mWeight;
    private String mTeamName;
    public String getTeamName() {
        return mTeamName;
    }
    public String getGategoryName() {
        return mCategoryName;
    }
    private String mCategoryName;
    public  ClassPlayer(){}
    public String getPosition() {return mPosition;}
    private String mPosition;
    private  boolean mHurt;
    private  boolean mAvailable;
    private ConnexionASdb connexionASdb;
    private ArrayList<ClassMedia> mMedias = new ArrayList<>();
    public ClassPlayer(Integer mId){ this.mId =  mId;}
    public ClassPlayer(int mIdTeam,String mGender,String mFirstName, String mLastName, String mEmail,java.sql.Date mBirthDay,int mPhone, int mPhoneEmergency, int mHeight, int mWeight, String mPosition,String mDescription,int idMedia,String pathProfile) {
        this.mIdTeam        = mIdTeam;
        this.mGender        = mGender;
        this.mFirstName     = mFirstName;
        this.mLastName      = mLastName;
        this.mEmail         = mEmail;
        this.mBirthDay      = mBirthDay;
        this.mPhone         = mPhone;
        this.mPhoneEmergency = mPhoneEmergency;
        this.mHeight         = mHeight;
        this.mWeight         = mWeight;
        this.mPosition       = mPosition;
        this.mDescription    =  mDescription;
        this.mMedias.add(new ClassMedia(idMedia,pathProfile));
        this.mAvailable = true;
    }
    public void setId(int mId) { this.mId = mId; }
    public void initialise() throws Exception {
        connexionASdb =  new ConnexionASdb();
        String sqlQuery = "SELECT " +
                "ba_player.id, ba_player.idTeam,  ba_player.gender, ba_player.firstName, "+
                "ba_player.lastName, ba_player.birthday, ba_player.height, ba_player.weight," +
                "ba_player.position, ba_team.name AS teamName, ba_category.name as categoryName, " +
                "ba_player.description, ba_player.phone, ba_player.phoneEmergency, ba_player.email, ba_player.hurt, ba_player.available " +
                "FROM ba_player " +
                "JOIN ba_team ON ba_player.idTeam = ba_team.id " +
                "JOIN ba_category ON ba_category.id  = ba_team.idCategory " +
                "WHERE ba_player.id="+mId+" "+
                "UNION " +
                "SELECT " +
                "id, idTeam, gender, firstName, lastName, birthday, height, weight, position, " +
                "NULL AS teamName, NULL AS categoryName, description, phone, phoneEmergency, email, hurt, available " +
                "FROM ba_player " +
                "WHERE id ="+mId+";"; // Replace ? with your variable
        Statement statementPlayer = connexionASdb.getStatement();
        ResultSet resultSet = statementPlayer.executeQuery(sqlQuery);
        if(resultSet.next()){
            mId=resultSet.getInt("id");
            mIdTeam=resultSet.getInt("idTeam");
            mGender = resultSet.getString("gender");
            mFirstName=resultSet.getString("lastName");
            mLastName=resultSet.getString("firstName");
            mBirthDay=resultSet.getDate("birthday");
            mDescription=resultSet.getString("description");
            mPhone=resultSet.getInt("phone");
            mPhoneEmergency=resultSet.getInt("phoneEmergency");
            mEmail=resultSet.getString("email");
            mHeight=resultSet.getInt("height");
            mWeight=resultSet.getInt("weight");
            mPosition=resultSet.getString("position");
            mHurt=resultSet.getBoolean("hurt");
            mAvailable=resultSet.getBoolean("available");
            mTeamName = resultSet.getString("teamName");
            mCategoryName = resultSet.getString("categoryName");
            //On charge les medias du joueur
            mMedias =  ClassMedia.loadMedia(mId,"player");
        }
    }
    public void setIdTeam(int mIdTeam) {
        this.mIdTeam = mIdTeam;
    }
    public void setAvailable(boolean mAvailable) {
        this.mAvailable = mAvailable;
    }
    public void setBirthDay(java.sql.Date mBirthDay) {
        this.mBirthDay = mBirthDay;
    }
    public void setDescription(String mDescription) {
        this.mDescription = mDescription;
    }
    public void setEmail(String mEmail) {
        this.mEmail = mEmail;
    }
    public void setFirstName(String mFirstName) {
        this.mFirstName = mFirstName;
    }
    public void setHeight(int mHeight) {
        this.mHeight = mHeight;
    }
    public void setHurt(boolean mHurt) {
        this.mHurt = mHurt;
    }
    public void setLastName(String mLastName) {
        this.mLastName = mLastName;
    }
    public void setPhone(int mPhone) {
        this.mPhone = mPhone;
    }
    public void setPhoneEmergency(int mPhoneEmergency) {
        this.mPhoneEmergency = mPhoneEmergency;
    }
    public void setWeight(int mWeight) {
        this.mWeight = mWeight;
    }
    public void setCountry(String mCategory) { this.mCategory = mCategory;}
    public int getId() { return mId;}
    public int getIdTeam() {return mIdTeam; }
    public int getAge(){ return Period.between(mBirthDay.toLocalDate(), LocalDate.now()).getYears(); }
    public String getFirstName(){return mFirstName;}
    public String getLastName(){return mLastName;}
    public String getName() { return mFirstName +" "+ mLastName;}
    public LocalDate getBirthDay() {
        return mBirthDay.toLocalDate();
    }
    public String getEmail() {return mEmail; }
    public String getDescription() {
        return mDescription;
    }
    public int getPhone() {
        return mPhone;
    }
    public int getPhoneEmergency() {
        return mPhoneEmergency;
    }
    public int getHeight() {
        return mHeight;
    }
    public int getWeight() {
        return mWeight;
    }
    public String getCountry() {return mCategory;}
    public Boolean isHurt() { return mHurt; }
    public void isHurt(Boolean b) { mHurt = b; }
    public Boolean isAvailable(){ return mAvailable;}
    public void isAvailable(Boolean b) { mAvailable = b;}
    public String getPathProfile() {return (!mMedias.isEmpty()) ? mMedias.getFirst().getPath() : ""; }
    public int getIdMedia() {return (!mMedias.isEmpty()) ? mMedias.getFirst().getId() : 0; }
    public void setPosition(String mPosition) {
        this.mPosition = mPosition;
    }
    public void setMedias() throws Exception { mMedias =  ClassMedia.loadMedia(this.mId,"Team");}
    public ArrayList<ClassMedia> getMedias() {
        return mMedias;
    }
    @Override
    public String toString() {
        return getName() +" ("+getAge()+"ans)";
   }
    public String getString(boolean toUpdate){
        return mLastName + ',' + mFirstName + ',' +mEmail + ',' +mBirthDay +',' +mDescription + ',' +
                mPhone +"," + mPhoneEmergency +"," + mHeight +"," + mWeight +"," + mPosition;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ClassPlayer player)) return false;
        return mId == player.mId && mIdTeam == player.mIdTeam && mPhone == player.mPhone && mPhoneEmergency == player.mPhoneEmergency && mHeight == player.mHeight && mWeight == player.mWeight && Objects.equals(mGender, player.mGender) && Objects.equals(mFirstName, player.mFirstName) && Objects.equals(mLastName, player.mLastName) && Objects.equals(mBirthDay, player.mBirthDay) && Objects.equals(mEmail, player.mEmail) && Objects.equals(mDescription, player.mDescription) && Objects.equals(mCategory, player.mCategory) && Objects.equals(mTeamName, player.mTeamName) && Objects.equals(mCategoryName, player.mCategoryName) && Objects.equals(mMedias, player.mMedias);
    }

    @Override
    public int hashCode() {
        return Objects.hash(mId, mIdTeam, mGender, mFirstName, mLastName, mBirthDay, mEmail, mDescription, mCategory, mPhone, mPhoneEmergency, mHeight, mWeight, mTeamName, mCategoryName, mMedias);
    }

    @Override
    public ClassPlayer clone() {
        try {
            return  (ClassPlayer) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}
