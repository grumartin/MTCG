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
                    .getPreparedStatement("INSERT INTO users(username, password, coins, bio, token) VALUES(?,?,?,?,?)", true);
            preparedStatement.setString(1, user.getUsername());
            preparedStatement.setString(2, user.getPassword());
            preparedStatement.setInt(3, user.getCoins());
            preparedStatement.setString(4, user.getBio());
            preparedStatement.setString(5, user.getToken());

            int affectedRows = preparedStatement.executeUpdate();

            if(affectedRows > 0){
                try (ResultSet generatedKey = preparedStatement.getGeneratedKeys()) {
                    if (generatedKey.next()){
                        preparedStatement = unitOfWork.getPreparedStatement("INSERT INTO user_stats(user_id) VALUES(?)", false);
                        preparedStatement.setInt(1, generatedKey.getInt(1));
                        if(preparedStatement.executeUpdate() != 0)
                            return HttpStatus.CREATED;
                        else
                            throw new RuntimeException("user_stats creation failed");
                    }
                }
            }
        }catch(Exception exception){
            //exception.printStackTrace();
            if(exception.getClass().equals(RuntimeException.class))
                return HttpStatus.INTERNAL_SERVER_ERROR;
        }
        return HttpStatus.CONFLICT;
    }

    public ResultSet getUser(String username, UnitOfWork unitOfWork) throws SQLException{
        PreparedStatement preparedStatement = unitOfWork
                    .getPreparedStatement("SELECT u_id, username, password, coins, bio, token FROM users WHERE username = ?", false);

        preparedStatement.setString(1, username);
        return preparedStatement.executeQuery();
    }

    public String getUserById(int id, UnitOfWork unitOfWork) throws SQLException{
        PreparedStatement preparedStatement = unitOfWork
                .getPreparedStatement("SELECT username FROM users WHERE u_id = ?", false);

        preparedStatement.setInt(1, id);
        ResultSet resultSet = preparedStatement.executeQuery();
        if(resultSet.next()){
            return resultSet.getString(1);
        }else{
            throw new SQLException("Get User failed");
        }
    }

    public void updateUser(User newUser, User oldUser, UnitOfWork unitOfWork) throws SQLException{
        PreparedStatement preparedStatement = unitOfWork
                .getPreparedStatement("UPDATE users SET username = ?, bio = ? WHERE u_id = ?", false);
        preparedStatement.setString(1, newUser.getUsername() == null ? oldUser.getUsername() : newUser.getUsername());
        preparedStatement.setString(2, newUser.getBio() == null ? oldUser.getBio() : newUser.getBio());
        preparedStatement.setInt(3, oldUser.getUid());
        preparedStatement.executeUpdate();
        preparedStatement.close();
    }

    public void updateUserCoins(User user, int coinsSpent, UnitOfWork unitOfWork) throws SQLException{
        PreparedStatement preparedStatement = unitOfWork
                .getPreparedStatement("UPDATE users SET coins = coins - ? WHERE u_id = ?", false);
        preparedStatement.setInt(1, coinsSpent);
        preparedStatement.setInt(2, user.getUid());
        preparedStatement.executeUpdate();
        preparedStatement.close();
    }

    public void addDraw(UnitOfWork unitOfWork){
        try{
            PreparedStatement preparedStatement = unitOfWork
                    .getPreparedStatement("SELECT * FROM users WHERE u_id = -1", false);
            ResultSet resultSet = preparedStatement.executeQuery();
            if(!resultSet.next()){
                preparedStatement = unitOfWork.getPreparedStatement("INSERT INTO users VALUES(-1, 'draw', 'draw', 0, '', '')", false);
                preparedStatement.executeUpdate();
            }
        }catch(SQLException e){
            e.printStackTrace();
        }
    }
}
