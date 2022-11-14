package at.fhtw.dal.repo;

import at.fhtw.dal.DBSingleton;
import at.fhtw.dal.UnitOfWork;
import at.fhtw.httpserver.http.HttpStatus;
import at.fhtw.models.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserRepo {
    public HttpStatus addUser(User user, UnitOfWork unitOfWork){
        try{
            PreparedStatement preparedStatement = unitOfWork
                    .getPreparedStatement("INSERT INTO users(username, password, coins, bio, token) VALUES(?,?,?,?,?)");
            preparedStatement.setString(1, user.getUsername());
            preparedStatement.setString(2, user.getPassword());
            preparedStatement.setInt(3, user.getCoins());
            preparedStatement.setString(4, user.getBio());
            preparedStatement.setString(5, user.getToken());

            int affectedRows = preparedStatement.executeUpdate();
            preparedStatement.close();

            if(affectedRows > 0)
                return HttpStatus.CREATED;
        }catch(Exception exception){
            if(exception.getClass().equals(RuntimeException.class))
                return HttpStatus.INTERNAL_SERVER_ERROR;
        }
        return HttpStatus.CONFLICT;
    }

    public ResultSet getUser(String username, UnitOfWork unitOfWork) throws SQLException{
        PreparedStatement preparedStatement = unitOfWork
                    .getPreparedStatement("SELECT u_id, username, password, coins, bio, token FROM users WHERE username = ?");

        preparedStatement.setString(1, username);
        return preparedStatement.executeQuery();
    }

    public void updateUser(User newUser, User oldUser, UnitOfWork unitOfWork) throws SQLException{
        PreparedStatement preparedStatement = unitOfWork
                .getPreparedStatement("UPDATE users SET username = ?, bio = ? WHERE u_id = ?");
        preparedStatement.setString(1, newUser.getUsername() == null ? oldUser.getUsername() : newUser.getUsername());
        preparedStatement.setString(2, newUser.getBio() == null ? oldUser.getBio() : newUser.getBio());
        preparedStatement.setInt(3, oldUser.getUid());
        preparedStatement.executeUpdate();
        preparedStatement.close();
    }
}
