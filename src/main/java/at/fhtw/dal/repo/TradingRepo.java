package at.fhtw.dal.repo;

import at.fhtw.dal.UnitOfWork;
import at.fhtw.models.TradingDeal;
import at.fhtw.models.User;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class TradingRepo {
    public ResultSet getDealWithId(String id, UnitOfWork unitOfWork) throws SQLException {
        PreparedStatement preparedStatement = unitOfWork
                .getPreparedStatement("SELECT * FROM trading_deal WHERE t_id = ?", false);
        preparedStatement.setString(1, id);
        return preparedStatement.executeQuery();
    }

    public ResultSet getDeals(UnitOfWork unitOfWork) throws SQLException {
        PreparedStatement preparedStatement = unitOfWork
                .getPreparedStatement("SELECT * FROM trading_deal", false);
        return preparedStatement.executeQuery();
    }

    public void createDeal(TradingDeal tradingDeal, User user, UnitOfWork unitOfWork) throws SQLException {
        PreparedStatement preparedStatement = unitOfWork
                .getPreparedStatement("INSERT INTO trading_deal VALUES(?, ?, ?, CAST(? AS TradingType), ?)", false);
        preparedStatement.setString(1, tradingDeal.getId());
        preparedStatement.setString(2, tradingDeal.getCardToTrade());
        preparedStatement.setInt(3, user.getUid());
        preparedStatement.setString(4, tradingDeal.getTypeString());
        preparedStatement.setInt(5, tradingDeal.getMinimumDamage());

        int affectedRows = preparedStatement.executeUpdate();
        if(affectedRows == 0){
            throw new SQLException("trading deal creation failed");
        }
    }

    public void deleteDeal(String tradingDealId, UnitOfWork unitOfWork) throws SQLException {
        PreparedStatement preparedStatement = unitOfWork
                .getPreparedStatement("DELETE FROM trading_deal WHERE t_id = ?", false);
        preparedStatement.setString(1, tradingDealId);

        int affectedRows = preparedStatement.executeUpdate();
        if(affectedRows == 0){
            throw new SQLException("trading deal delete failed");
        }
    }

    public void executeTrade(TradingDeal tradingDeal, User user, String cardId, UnitOfWork unitOfWork) throws SQLException {
        PreparedStatement preparedStatement = unitOfWork
                .getPreparedStatement("UPDATE cards SET user_id = ? WHERE c_id = ?", false);
        preparedStatement.setInt(1, user.getUid());
        preparedStatement.setString(2, tradingDeal.getCardToTrade());

        int affectedRows = preparedStatement.executeUpdate();
        if(affectedRows == 0){
            throw new SQLException("trading deal failed");
        }

        preparedStatement.setInt(1, tradingDeal.getSeller());
        preparedStatement.setString(2, cardId);

        affectedRows = preparedStatement.executeUpdate();
        if(affectedRows == 0){
            throw new SQLException("trading deal failed");
        }
    }
}
