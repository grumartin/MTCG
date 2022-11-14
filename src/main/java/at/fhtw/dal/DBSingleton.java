package at.fhtw.dal;

import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;

public enum DBSingleton {
    INSTANCE;

    public Connection Connect(){
        try(InputStream input = new FileInputStream("C:\\Users\\martin\\Workspaces\\MTCG\\src\\main\\resources\\config.properties")){
            Properties prop = new Properties();

            // load a properties file
            prop.load(input);

            Connection dbConn = DriverManager.getConnection(prop.getProperty("db.url"),
                    prop.getProperty("db.user"),
                    prop.getProperty("db.password"));
            dbConn.setAutoCommit(false);
            return dbConn;
        }catch(Exception exception){
            exception.printStackTrace();
        }
        return null;
    }
}
