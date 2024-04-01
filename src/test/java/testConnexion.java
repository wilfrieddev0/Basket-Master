import db.ConnexionASdb;
import org.junit.jupiter.api.Test;

import java.sql.Connection;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class testConnexion {
    @Test
    public void test(){
        try{
            ConnexionASdb connexionASdb= new ConnexionASdb();
            Connection connexion =  connexionASdb.getConnection();
            assertNotNull(connexion,"La connexion à la base de données a echoué...");
        }catch (Exception exception){
            exception.printStackTrace();
        }
    }
}
