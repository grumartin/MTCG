package at.fhtw.service.card;

import at.fhtw.httpserver.http.ContentType;
import at.fhtw.httpserver.http.HttpStatus;
import at.fhtw.httpserver.http.Method;
import at.fhtw.httpserver.server.Request;
import at.fhtw.httpserver.server.Response;
import at.fhtw.models.Card;
import at.fhtw.service.Service;

public class CardService implements Service {
    @Override
    public Response handleRequest(Request request) {
        Response response;
        if(request.getMethod().equals(Method.GET)){
            response = new CardController().handleGet(request);
        }else{
            response = new Response(HttpStatus.BAD_REQUEST,
                    ContentType.PLAIN_TEXT,
                    "");
        }
        return response;
    }
}
