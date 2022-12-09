package at.fhtw.dal;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;
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

    public void close()  {
        try {
            this.DBConnection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean isClosed(){
        try {
            return this.DBConnection.isClosed();
        } catch (SQLException e) {
            e.printStackTrace();
            return true;
        }
    }

    public PreparedStatement getPreparedStatement(String sqlStatement, boolean returnGeneratedKey) throws RuntimeException{
        try {
            return this.DBConnection.prepareStatement(sqlStatement, returnGeneratedKey ? Statement.RETURN_GENERATED_KEYS : Statement.NO_GENERATED_KEYS);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}
