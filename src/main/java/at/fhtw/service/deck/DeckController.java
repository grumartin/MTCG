package at.fhtw.service.deck;

import at.fhtw.dal.UnitOfWork;
import at.fhtw.dal.repo.CardRepo;
import at.fhtw.dal.repo.DeckRepo;
import at.fhtw.httpserver.http.ContentType;
import at.fhtw.httpserver.http.HttpStatus;
import at.fhtw.httpserver.server.Request;
import at.fhtw.httpserver.server.Response;
import at.fhtw.models.Card;
import at.fhtw.models.User;
import at.fhtw.service.user.UserController;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class DeckController {
    public Response handleGet(Request request) {
        if(request.getAuthorizedClient() == null)
            return new Response(HttpStatus.UNAUTHORIZED,
                    ContentType.PLAIN_TEXT,
                    "Authentication information is missing or invalid");

        UnitOfWork unitOfWork = new UnitOfWork();
        User user = new UserController().getUserWithUserName(request.getAuthorizedClient().getUsername(), unitOfWork);

        try {
            int deckId = new DeckRepo().getDeckIdFromUser(user, unitOfWork);
            if(deckId == -1) {     //Deck has no cards
                return new Response(HttpStatus.NO_CONTENT,
                        ContentType.PLAIN_TEXT,
                        "");
            }
            ResultSet resultSetCards = new DeckRepo().getCardsfromDeck(deckId, unitOfWork);
            unitOfWork.close();

            List<Map<String, String>> cards = new ArrayList<Map<String, String>>();
            while(resultSetCards.next()){
                cards.add(new Card(resultSetCards.getString(1),
                        resultSetCards.getString(2),
                        resultSetCards.getFloat(3)).getCardProperties());
            }

            if(request.getParams() != null){
                return new Response(HttpStatus.OK,
                        ContentType.PLAIN_TEXT,
                        new ObjectMapper().writeValueAsString(cards));
            }
            return new Response(HttpStatus.OK,
                    ContentType.JSON,
                    new ObjectMapper().writeValueAsString(cards));
        }catch(Exception e){
            e.printStackTrace();
            unitOfWork.close();
            return new Response(HttpStatus.INTERNAL_SERVER_ERROR,
                    ContentType.PLAIN_TEXT,
                    "");
        }

    }

    public Response handlePut(Request request) {
        if(request.getAuthorizedClient() == null)
            return new Response(HttpStatus.UNAUTHORIZED,
                    ContentType.PLAIN_TEXT,
                    "Authentication information is missing or invalid");

        UnitOfWork unitOfWork = new UnitOfWork();
        User user = new UserController().getUserWithUserName(request.getAuthorizedClient().getUsername(), unitOfWork);

        try {
            int deckId = new DeckRepo().createDeck(user, unitOfWork);

            List<String> cardIds = getIdsfromPayload(request.getBody());
            if(cardIds.size() != 4){
                unitOfWork.rollback();
                return new Response(HttpStatus.BAD_REQUEST,
                        ContentType.PLAIN_TEXT,
                        "The provided deck did not include the required amount of cards");
            }

            if(new DeckRepo().configureDeck(user, cardIds, deckId, unitOfWork) == HttpStatus.OK){
                unitOfWork.commit();
                unitOfWork.close();
                return new Response(HttpStatus.OK,
                        ContentType.PLAIN_TEXT,
                        "The deck has been successfully configured");
            }else{
                unitOfWork.rollback();
                return new Response(HttpStatus.FORBIDDEN,
                        ContentType.PLAIN_TEXT,
                        "At least one of the provided cards does not belong to the user or is not available.");
            }
        } catch (Exception e) {
            unitOfWork.rollback();
            e.printStackTrace();
        }
        unitOfWork.close();
        return new Response(HttpStatus.INTERNAL_SERVER_ERROR,
                ContentType.PLAIN_TEXT,
                "");
    }

    public List<String> getIdsfromPayload(String body){
        String newBody = body.replace("[", "")
                .replace("]", "")
                .replaceAll("\"", "")
                .trim();

        String[] ids = newBody.split(",");
        List<String> idsClean = new ArrayList<>();
        for(String id : ids){
            idsClean.add(id.trim());
        }
        return idsClean;
    }

    public ResultSet getDeckFromPlayer(int id, UnitOfWork unitOfWork) throws SQLException {
        User user = new User();
        user.setUid(id);
        int deckId = new DeckRepo().getDeckIdFromUser(user, unitOfWork);
        if(deckId == -1)
            return null;
        return new DeckRepo().getCardsfromDeck(deckId, unitOfWork);
    }
}
