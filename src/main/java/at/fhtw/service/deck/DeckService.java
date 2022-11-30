package at.fhtw.service.deck;

import at.fhtw.httpserver.http.ContentType;
import at.fhtw.httpserver.http.HttpStatus;
import at.fhtw.httpserver.server.Request;
import at.fhtw.httpserver.server.Response;
import at.fhtw.service.Service;

public class DeckService implements Service {
    @Override
    public Response handleRequest(Request request) {
        DeckController deckController = new DeckController();
        Response response;
        switch (request.getMethod()){
            case GET:
                response = deckController.handleGet(request);
                break;
            case PUT:
                response = deckController.handlePut(request);
                break;
            default:
                response = new Response(HttpStatus.BAD_REQUEST,
                        ContentType.PLAIN_TEXT,
                        "");
        }
        return response;
    }
}
