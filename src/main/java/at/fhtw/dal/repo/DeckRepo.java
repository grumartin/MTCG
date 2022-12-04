package at.fhtw.dal.repo;

import at.fhtw.dal.UnitOfWork;
import at.fhtw.httpserver.http.HttpStatus;
import at.fhtw.models.Battle;
import at.fhtw.models.User;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class DeckRepo {
    public int createDeck(User user, UnitOfWork unitOfWork) throws Exception {
        PreparedStatement preparedStatement = unitOfWork
                .getPreparedStatement("INSERT INTO deck(user_id) VALUES(?)", true);
        preparedStatement.setInt(1, user.getUid());

        int affectedRows = preparedStatement.executeUpdate();

        if (affectedRows > 0) {
            try (ResultSet generatedKey = preparedStatement.getGeneratedKeys()) {
                if (generatedKey.next())
                    return generatedKey.getInt(1);
            }
        }
        throw new RuntimeException("Package creation failed!");
    }

    public HttpStatus configureDeck(User user, List<String> cardIds, int deckId, UnitOfWork unitOfWork) throws SQLException {
        PreparedStatement preparedStatement = unitOfWork
                .getPreparedStatement("UPDATE cards SET deck_id = ? WHERE c_id IN (?,?,?,?) AND user_id = ?", false);
        preparedStatement.setInt(1, deckId);
        preparedStatement.setString(2, cardIds.get(0));
        preparedStatement.setString(3, cardIds.get(1));
        preparedStatement.setString(4, cardIds.get(2));
        preparedStatement.setString(5, cardIds.get(3));
        preparedStatement.setInt(6, user.getUid());

        int affectedRows = preparedStatement.executeUpdate();

        if(affectedRows < 4){  //At least one of the provided cards does not belong to the user or is not available
            return HttpStatus.FORBIDDEN;
        }else{
            return HttpStatus.OK;
        }
    }

    public ResultSet getCardsfromDeck(int deckId, UnitOfWork unitOfWork) throws SQLException{
        PreparedStatement preparedStatement = unitOfWork
                .getPreparedStatement("SELECT * FROM cards WHERE deck_id = ?", false);
        preparedStatement.setInt(1, deckId);

        return preparedStatement.executeQuery();
    }

    public int getDeckIdFromUser(User user, UnitOfWork unitOfWork) throws SQLException {
        PreparedStatement preparedStatement = unitOfWork
                .getPreparedStatement("SELECT d_id FROM deck WHERE user_id = ?", false);
        preparedStatement.setInt(1, user.getUid());

        ResultSet result = preparedStatement.executeQuery();
        if(result.next()){
            return result.getInt(1);
        }else{
            return -1;
        }
    }

    public void clearDeck(Battle battle, UnitOfWork unitOfWork) throws SQLException {
        PreparedStatement preparedStatement = unitOfWork
                .getPreparedStatement("UPDATE cards SET deck_id = NULL WHERE user_id = ? OR user_id = ?", false);
        preparedStatement.setInt(1, battle.getPlayerA());
        preparedStatement.setInt(2, battle.getPlayerB());

        int affectedRows = preparedStatement.executeUpdate();
        if(affectedRows == 0)
            throw new SQLException("Update failed");
    }
}
