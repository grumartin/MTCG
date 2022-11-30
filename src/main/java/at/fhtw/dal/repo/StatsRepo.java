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
}
