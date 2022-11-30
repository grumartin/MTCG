package at.fhtw.service.card;

import at.fhtw.dal.repo.CardRepo;
import at.fhtw.dal.repo.PackageRepo;
import at.fhtw.httpserver.http.ContentType;
import at.fhtw.httpserver.http.HttpStatus;
import at.fhtw.httpserver.server.Request;
import at.fhtw.httpserver.server.Response;
import at.fhtw.models.Card;
import at.fhtw.models.Pckg;
import at.fhtw.models.User;
import at.fhtw.service.user.UserController;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static at.fhtw.service.Service.unitOfWork;

public class CardController {
    private CardRepo cardRepo;

    public CardController() {
        this.cardRepo = new CardRepo();
    }

    public HttpStatus createCards(Request request, int pckg_id) {
        Card[] cards = new Gson().fromJson(request.getBody(), Card[].class);
        List<Card> cardsList = Arrays.asList(cards);

        for (Card card : cardsList) {
            card.setPckg_id(pckg_id);
            HttpStatus cardStatus = this.cardRepo.addCard(card, unitOfWork);
            if(cardStatus != HttpStatus.CREATED)
                return cardStatus;
        }

        return HttpStatus.CREATED;
    }

    public Response handleGet(Request request) {
        if(request.getAuthorizedClient() == null)
            return new Response(HttpStatus.UNAUTHORIZED,
                    ContentType.PLAIN_TEXT,
                    "");

        User user = new UserController().getUserWithUserName(request.getAuthorizedClient().getUsername());

        try {
            ResultSet resultSetCards = new CardRepo().getCardsFromUser(user, unitOfWork);

            List<Map<String, String>> cards = new ArrayList<Map<String, String>>();
            while(resultSetCards.next()){
                cards.add(new Card(resultSetCards.getString(1),
                        resultSetCards.getString(2),
                        resultSetCards.getFloat(3)).getCardProperties());
            }

            if(cards.isEmpty()) {     //User has no cards
                return new Response(HttpStatus.NO_CONTENT,
                        ContentType.PLAIN_TEXT,
                        "");
            }else{
                return new Response(HttpStatus.OK,
                        ContentType.JSON,
                        new ObjectMapper().writeValueAsString(cards));
            }

        }catch(Exception e){
            e.printStackTrace();
            return new Response(HttpStatus.INTERNAL_SERVER_ERROR,
                    ContentType.PLAIN_TEXT,
                    "");
        }
    }
}
