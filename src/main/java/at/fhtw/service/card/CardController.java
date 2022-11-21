package at.fhtw.service.card;

import at.fhtw.dal.repo.CardRepo;
import at.fhtw.httpserver.http.HttpStatus;
import at.fhtw.httpserver.server.Request;
import at.fhtw.models.Card;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
}
