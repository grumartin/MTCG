package at.fhtw.service.trading;

import at.fhtw.dal.repo.CardRepo;
import at.fhtw.dal.repo.TradingRepo;
import at.fhtw.httpserver.http.ContentType;
import at.fhtw.httpserver.http.HttpStatus;
import at.fhtw.httpserver.server.Request;
import at.fhtw.httpserver.server.Response;
import at.fhtw.models.Card;
import at.fhtw.models.TradingDeal;
import at.fhtw.models.User;
import at.fhtw.service.card.CardController;
import at.fhtw.service.user.UserController;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static at.fhtw.service.Service.unitOfWork;

public class TradingController {
    public Response handlePostCreate(Request request) {
        User user = new UserController().getUserWithUserName(request.getAuthorizedClient().getUsername());

        TradingDeal tradingDeal =  new Gson().fromJson(request.getBody(), TradingDeal.class);

        if(tradingDeal == null || tradingDeal.getCardToTrade() == null || tradingDeal.getId() == null){
            return new Response(HttpStatus.BAD_REQUEST,
                    ContentType.PLAIN_TEXT,
                    "");
        }

        if(!(new CardController().checkIfUserOwnsCard(user, tradingDeal.getCardToTrade())))     //The deal contains a card that is not owned by the user or locked in the deck
            return new Response(HttpStatus.FORBIDDEN,
                    ContentType.PLAIN_TEXT,
                    "The deal contains a card that is not owned by the user or locked in the deck.");

        TradingRepo tradingRepo = new TradingRepo();
        try {
            ResultSet resultSet = tradingRepo.getDealWithId(tradingDeal.getId(), unitOfWork);       //check if deal with this Id exists
            if(resultSet.next())
                return new Response(HttpStatus.CONFLICT,
                        ContentType.PLAIN_TEXT,
                        "A deal with this deal ID already exists.");

            tradingRepo.createDeal(tradingDeal, user, unitOfWork);      //create trading deal
            unitOfWork.commit();
            return new Response(HttpStatus.CREATED,
                    ContentType.PLAIN_TEXT,
                    "Trading deal successfully created");
        } catch (SQLException e) {
            e.printStackTrace();
            unitOfWork.rollback();
        }
        return new Response(HttpStatus.INTERNAL_SERVER_ERROR,
                ContentType.PLAIN_TEXT,
                "");
    }

    public Response handlePostCarryOut(Request request) {
        User user = new UserController().getUserWithUserName(request.getAuthorizedClient().getUsername());
        String tradingDealId = request.getPathParts().get(1);
        TradingRepo tradingRepo = new TradingRepo();
        String cardId = request.getBody().trim();

        try {
            ResultSet resultSet = tradingRepo.getDealWithId(tradingDealId, unitOfWork);     //check if deal with this Id exists
            if(!resultSet.next())
                return new Response(HttpStatus.NOT_FOUND,
                        ContentType.PLAIN_TEXT,
                        "The provided deal ID was not found.");

            TradingDeal tradingDeal = new TradingDeal(resultSet.getString(1),
                    resultSet.getString(2),
                    resultSet.getInt(3),
                    resultSet.getString(4),
                    resultSet.getInt(5));

            if(!(new CardController().checkIfUserOwnsCard(user, cardId)) || !(new CardController().checkRequirements(cardId, tradingDeal)))     //check if card is owned by user and not locked in deck
                return new Response(HttpStatus.FORBIDDEN,                                                                       //and meets the requirements
                        ContentType.PLAIN_TEXT,
                        "The offered card is not owned by the user, or the requirements are not met (Type, MinimumDamage), or the offered card is locked in the deck.");

            tradingRepo.executeTrade(tradingDeal, user, cardId, unitOfWork);        //execute deal
            tradingRepo.deleteDeal(tradingDealId, unitOfWork);                      //delete deal
            unitOfWork.commit();
            return new Response(HttpStatus.OK,
                    ContentType.PLAIN_TEXT,
                    "Trading deal successfully executed.");
        } catch (SQLException e) {
            e.printStackTrace();
            unitOfWork.rollback();
        }
        return new Response(HttpStatus.INTERNAL_SERVER_ERROR,
                ContentType.PLAIN_TEXT,
                "");

    }

    public Response handleGet(Request request) {
        try {
            ResultSet resultSetDeals = new TradingRepo().getDeals(unitOfWork);

            List<Map<String, String>> deals = new ArrayList<Map<String, String>>();
            while(resultSetDeals.next()){
                deals.add(new TradingDeal(resultSetDeals.getString(1),
                        resultSetDeals.getString(2),
                        resultSetDeals.getString(4),
                        resultSetDeals.getInt(5)).getDealProperties());
            }

            if(deals.isEmpty()) {     //No available deals
                return new Response(HttpStatus.NO_CONTENT,
                        ContentType.PLAIN_TEXT,
                        "The request was fine, but there are no trading deals available");
            }else{
                return new Response(HttpStatus.OK,
                        ContentType.JSON,
                        new ObjectMapper().writeValueAsString(deals));
            }

        }catch(Exception e){
            e.printStackTrace();
            return new Response(HttpStatus.INTERNAL_SERVER_ERROR,
                    ContentType.PLAIN_TEXT,
                    "");
        }
    }

    public Response handleDelete(Request request) {
        User user = new UserController().getUserWithUserName(request.getAuthorizedClient().getUsername());
        String tradingDealId = request.getPathParts().get(1);
        TradingRepo tradingRepo = new TradingRepo();


        try {
            ResultSet resultSet = tradingRepo.getDealWithId(tradingDealId, unitOfWork);     //check if deal with this Id exists
            if(!resultSet.next())
                return new Response(HttpStatus.NOT_FOUND,
                        ContentType.PLAIN_TEXT,
                        "The provided deal ID was not found.");


            if(!(new CardController().checkIfUserOwnsCard(user, resultSet.getString(2))))     //check if deal contains card from user
                return new Response(HttpStatus.FORBIDDEN,
                        ContentType.PLAIN_TEXT,
                        "The deal contains a card that is not owned by the user.");

            tradingRepo.deleteDeal(tradingDealId, unitOfWork);
            unitOfWork.commit();
            return new Response(HttpStatus.OK,
                    ContentType.PLAIN_TEXT,
                    "Trading deal successfully deleted");
        } catch (SQLException e) {
            e.printStackTrace();
            unitOfWork.rollback();
        }
        return new Response(HttpStatus.INTERNAL_SERVER_ERROR,
                ContentType.PLAIN_TEXT,
                "");
    }
}
