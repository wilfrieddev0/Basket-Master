package db;

import java.io.FileInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.util.Objects;
import java.util.Properties;

public class ConnexionASdb {
    private Connection connection = null;
    private Statement statement= null;
    private  PreparedStatement preparedStatement;
    public ConnexionASdb(){
        try{
            Properties properties =  new Properties();
            FileInputStream file = new FileInputStream("src/main/resources/Config/conf.properties");
            properties.load(file);
            Class.forName(properties.getProperty("jdbc.driver.class"));
            String url =  properties.getProperty("jdbc.url");
            String login =  properties.getProperty("jdbc.login");
            String password =  properties.getProperty("jdbc.password");
            this.connection = DriverManager.getConnection(url,login,password);
            this.statement = connection.createStatement();
        }catch (Exception exception){
            this.connection =  null;
            throw new RuntimeException(exception);
        }
    }
    public Connection getConnection(){
        return this.connection;
    }
    public ReturnCheck checkCredentials(String login, String passWord) throws NoSuchAlgorithmException {
        ReturnCheck ReturnCheck =  new ReturnCheck();
        try{
            String reqLogin =  "SELECT id,password FROM ba_coach WHERE login=?";
            preparedStatement = connection.prepareStatement(reqLogin);
            preparedStatement.setString(1,login);
            ResultSet resultSet = preparedStatement.executeQuery();
            if(resultSet.next()){
                String mdp  = hash(passWord);
                if (resultSet.getString("password").equals(mdp)){
                    ReturnCheck.id =   resultSet.getInt("id");
                }else ReturnCheck.text = "Wrong password";
            }else  ReturnCheck.text =  "Don't exist";
        }catch (SQLException ex){
            System.out.println(ex.getMessage());
        }
        return ReturnCheck;
    }
    public int insert(String table,String[] fields, String[] values) throws SQLException, NoSuchAlgorithmException {
        StringBuilder setValues  = new StringBuilder();
        StringBuilder setFiels = new StringBuilder();
        int idReturn  = 0;
        if (fields.length == values.length){
            for (int i=0;i<fields.length; i++){
                // Hachage du mot de passe pour table ba_coach
                if(Objects.equals(table, "ba_coach")){
                    if(fields[i].equals("password")){
                        values[i] =  hash(values[i]);
                    }
                }
                setFiels.append(fields[i]).append(",");
                setValues.append("'").append(values[i]).append("',");
            }
            setFiels.deleteCharAt((setFiels.length()-1));
            setValues.deleteCharAt((setValues.length()-1));
        }else{
            for (String field : fields) {
                setFiels.append(field).append(",");
            }
            for (int i=0;i< values.length;i++) {
                if (i==0) {
                    values[i] = values[i].substring(1);
                    if (values.length==1) {
                        values[i] = values[i].substring(0,(values[i].length()-1));
                        setValues.append(values[i]);
                    }else setValues.append(values[i]).append(",");
                } else if (i== (values.length-1)) {
                    values[i] = values[i].substring(0,(values[i].length()-1));
                    setValues.append(values[i]);
                }else {
                    setValues.append(values[i]).append(",");
                }
            }
            setFiels.deleteCharAt((setFiels.length()-1));
        }
        String sqlReq = "INSERT INTO "+table+" ("+setFiels+") VALUES ("+setValues+");" ;
        PreparedStatement statement = connection.prepareStatement(sqlReq,Statement.RETURN_GENERATED_KEYS);
        int affectRows =  statement.executeUpdate();
        if (affectRows > 0){
            sqlReq = "SELECT id FROM "+table+" WHERE id = LAST_INSERT_ID()";
            ResultSet resultSet =  statement.executeQuery(sqlReq);
            if(resultSet.next()){ idReturn =  resultSet.getInt("id"); }
        }
        return  idReturn;
    }
    public int  delete(String table,int idOcc) throws SQLException {
        String sqlReq = "DELETE FROM "+table+" WHERE id="+idOcc;
        preparedStatement =  connection.prepareStatement(sqlReq);
        return preparedStatement.executeUpdate();
    }
    public static String hash(String word ) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        md.update(word.getBytes());
        byte[] byteData = md.digest();
        StringBuilder hexString = new StringBuilder();
        for (byte byteDatum : byteData) {
            String hex = Integer.toHexString(0xff & byteDatum);
            if (hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }
        return hexString.toString();
    }
    public int update(int idOcc, String table, String[] fields, String[] values) throws SQLException {
        StringBuilder setParameters = new StringBuilder();
        for (int i=0;i<values.length;i++){
            setParameters.append(fields[i]).append("='").append(values[i]).append("',");
        }
        setParameters.deleteCharAt((setParameters.length()-1));
        String sqlReq  =  "UPDATE "+table+" SET "+setParameters+" WHERE id ="+idOcc+";";
        preparedStatement =  connection.prepareStatement(sqlReq);
        return preparedStatement.executeUpdate();
    }
    public Statement getStatement(){return statement;}
}

