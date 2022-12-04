package at.fhtw.dal.repo;

import at.fhtw.dal.UnitOfWork;
import at.fhtw.models.Battle;
import at.fhtw.models.Card;
import at.fhtw.models.User;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class BattleRepo {
    public ResultSet fetchLobby(UnitOfWork unitOfWork) throws SQLException {
        PreparedStatement preparedStatement = unitOfWork
                .getPreparedStatement("SELECT * FROM battle WHERE playerB IS NULL LIMIT 1", false);

        return preparedStatement.executeQuery();
    }

    public int createLobby(UnitOfWork unitOfWork) throws SQLException {
        PreparedStatement preparedStatement = unitOfWork
                .getPreparedStatement("INSERT INTO battle VALUES(DEFAULT)", true);

        int affectedRows = preparedStatement.executeUpdate();

        if (affectedRows > 0) {
            try (ResultSet generatedKey = preparedStatement.getGeneratedKeys()) {
                if (generatedKey.next())
                    return generatedKey.getInt(1);
            }
        }
        throw new RuntimeException("Lobby creation failed!");
    }

    public void addUser(User user, Battle battle, UnitOfWork unitOfWork) throws SQLException {
        PreparedStatement preparedStatement;
        if(battle.getPlayerA() == 0){
            battle.setPlayerA(user.getUid());
            preparedStatement = unitOfWork
                    .getPreparedStatement("UPDATE battle SET playerA = ? WHERE b_id = ?", false);
        }else{
            battle.setPlayerB(user.getUid());
            preparedStatement = unitOfWork
                    .getPreparedStatement("UPDATE battle SET playerB = ? WHERE b_id = ?", false);
        }
        preparedStatement.setInt(1, user.getUid());
        preparedStatement.setInt(2, battle.getB_id());

        if(preparedStatement.executeUpdate() == 0)
            throw new SQLException("Battle Update failed");
    }

    public boolean checkFinished(Battle battle, UnitOfWork unitOfWork) throws SQLException {
        PreparedStatement preparedStatement = unitOfWork
                .getPreparedStatement("SELECT winner FROM battle WHERE b_id = ?", false);
        preparedStatement.setInt(1, battle.getB_id());

        ResultSet resultSet = preparedStatement.executeQuery();
        if(resultSet.next()){
            if(resultSet.getInt(1) != 0)
                return true;
        }
        return false;
    }

    public void addRound(Battle battle, Card cardA, Card cardB, Card winner, UnitOfWork unitOfWork) throws SQLException {
        PreparedStatement preparedStatement = unitOfWork
                .getPreparedStatement("INSERT INTO battle_round(cardA, cardB, winner, b_id) VALUES(?,?,?,?)", false);
        preparedStatement.setString(1, cardA.getC_id());
        preparedStatement.setString(2, cardB.getC_id());
        preparedStatement.setString(3, winner.getC_id());
        preparedStatement.setInt(4, battle.getB_id());

        int affectedRows = preparedStatement.executeUpdate();

        if (affectedRows == 0) {
            throw new SQLException("Battle round creation failed");
        }
    }

    public ResultSet getAllRounds(int b_id, UnitOfWork unitOfWork) throws SQLException {
        PreparedStatement preparedStatement = unitOfWork
                .getPreparedStatement("SELECT * FROM battle_round WHERE b_id = ?", false);
        preparedStatement.setInt(1, b_id);

        return preparedStatement.executeQuery();
    }

    public void setWinner(Battle battle, UnitOfWork unitOfWork) throws SQLException {
        PreparedStatement preparedStatement = unitOfWork
                .getPreparedStatement("UPDATE battle SET winner = ? WHERE b_id = ?", false);
        preparedStatement.setInt(1, battle.getWinner());
        preparedStatement.setInt(2, battle.getB_id());

        int affectedRows = preparedStatement.executeUpdate();

        if (affectedRows == 0) {
            throw new SQLException("Update failed");
        }
    }

    public ResultSet getBattle(int b_id, UnitOfWork unitOfWork) throws SQLException {
        PreparedStatement preparedStatement = unitOfWork
                .getPreparedStatement("SELECT * FROM battle WHERE b_id = ?", false);
        preparedStatement.setInt(1, b_id);

        return preparedStatement.executeQuery();
    }
}
