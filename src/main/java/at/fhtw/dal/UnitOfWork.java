package at.fhtw.dal;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.concurrent.ExecutionException;

public class UnitOfWork {
    private static Connection DBConnection;

    public UnitOfWork() {
        //get connection
        this.DBConnection = DBSingleton.INSTANCE.Connect();
    }

    public void commit(){
        try {
            this.DBConnection.commit();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void rollback(){
        try {
            this.DBConnection.rollback();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public PreparedStatement getPreparedStatement(String sqlStatement) throws RuntimeException{
        try {
            return this.DBConnection.prepareStatement(sqlStatement);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}
