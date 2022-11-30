package at.fhtw.dal.repo;

import at.fhtw.dal.UnitOfWork;
import at.fhtw.httpserver.http.HttpStatus;
import at.fhtw.models.Card;
import at.fhtw.models.Pckg;
import at.fhtw.models.User;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CardRepo {
    public HttpStatus addCard(Card card, UnitOfWork unitOfWork){
        try{
            PreparedStatement preparedStatement = unitOfWork
                    .getPreparedStatement("INSERT INTO cards(c_id, c_name, c_dmg, pckg_id) VALUES(?,?,?,?)", false);
            preparedStatement.setString(1, card.getC_id());
            preparedStatement.setString(2, card.getC_name());
            preparedStatement.setInt(3, (int)card.getC_dmg());
            preparedStatement.setInt(4, card.getPckg_id());

            int affectedRows = preparedStatement.executeUpdate();

            if(affectedRows > 0)
                return HttpStatus.CREATED;
        }catch(Exception exception){
            exception.printStackTrace();
        }
        return HttpStatus.CONFLICT;
    }

    public ResultSet acquireCards(Pckg pckg, UnitOfWork unitOfWork) throws SQLException {
        PreparedStatement preparedStatement = unitOfWork
                .getPreparedStatement("SELECT c_id, c_name, c_dmg FROM cards WHERE pckg_id = ?", false);
        preparedStatement.setInt(1, pckg.getP_id());

        return preparedStatement.executeQuery();
    }

    public void addCardsToUser(Pckg pckg, User user, UnitOfWork unitOfWork) throws Exception {
        PreparedStatement preparedStatement = unitOfWork
                .getPreparedStatement("UPDATE cards SET user_id = ? WHERE pckg_id = ?", false);
        preparedStatement.setInt(1, user.getUid());
        preparedStatement.setInt(2, pckg.getP_id());

        int affectedRows = preparedStatement.executeUpdate();

        if(affectedRows == 0)
            throw new RuntimeException("Cards could not be assigned to user!");
    }

    public void removeCardsFromPackage(Pckg pckg, UnitOfWork unitOfWork) throws Exception {
        PreparedStatement preparedStatement = unitOfWork
                .getPreparedStatement("UPDATE cards SET pckg_id = NULL WHERE pckg_id = ?", false);
        preparedStatement.setInt(1, pckg.getP_id());

        int affectedRows = preparedStatement.executeUpdate();

        if(affectedRows == 0)
            throw new RuntimeException("Cards couldnt be removed from package");
    }

    public ResultSet getCardsFromUser(User user, UnitOfWork unitOfWork) throws SQLException{
        PreparedStatement preparedStatement = unitOfWork
                .getPreparedStatement("SELECT c_id, c_name, c_dmg FROM cards WHERE user_id = ?", false);
        preparedStatement.setInt(1, user.getUid());

        return preparedStatement.executeQuery();
    }
}
