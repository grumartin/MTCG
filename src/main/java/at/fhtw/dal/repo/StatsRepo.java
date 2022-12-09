package at.fhtw.dal.repo;

import at.fhtw.dal.UnitOfWork;
import at.fhtw.models.Pckg;
import at.fhtw.models.User;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class StatsRepo {
    public ResultSet getStats(User user, UnitOfWork unitOfWork) throws SQLException {
        PreparedStatement preparedStatement = unitOfWork
                .getPreparedStatement("SELECT elo, wins, losses FROM user_stats WHERE user_id = ?", false);
        preparedStatement.setInt(1, user.getUid());

        return preparedStatement.executeQuery();
    }

    public ResultSet getAllStats(UnitOfWork unitOfWork) throws SQLException {
        PreparedStatement preparedStatement = unitOfWork
                .getPreparedStatement("SELECT elo, wins, losses, username " +
                                "FROM user_stats " +
                                "INNER JOIN users ON user_id = u_id " +
                                "ORDER BY elo DESC", false);

        return preparedStatement.executeQuery();
    }

    public void updateStats(int player, boolean won, UnitOfWork unitOfWork) throws SQLException {
        PreparedStatement preparedStatement = unitOfWork
                .getPreparedStatement("UPDATE user_stats " +
                        "SET elo = elo + ?, wins = wins + ?, losses = losses + ?, total = total + 1 " +
                        "WHERE user_id = ?", false);
        preparedStatement.setInt(1, won ? 3 : -5);
        preparedStatement.setInt(2, won ? 1 : 0);
        preparedStatement.setInt(3, won ? 0 : 1);
        preparedStatement.setInt(4, player);

        int affectedRows = preparedStatement.executeUpdate();
        if(affectedRows == 0){
            throw new SQLException("Stats Update failed");
        }
    }

    public void updateTotal(int player, UnitOfWork unitOfWork) throws SQLException {
        PreparedStatement preparedStatement = unitOfWork
                .getPreparedStatement("UPDATE user_stats " +
                        "total = total + 1 " +
                        "WHERE user_id = ?", false);
        preparedStatement.setInt(1, player);

        int affectedRows = preparedStatement.executeUpdate();
        if(affectedRows == 0){
            throw new SQLException("Stats Update failed");
        }
    }
}
