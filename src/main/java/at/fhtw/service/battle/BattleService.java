package at.fhtw.service.battle;

import at.fhtw.httpserver.http.ContentType;
import at.fhtw.httpserver.http.HttpStatus;
import at.fhtw.httpserver.http.Method;
import at.fhtw.httpserver.server.Request;
import at.fhtw.httpserver.server.Response;
import at.fhtw.service.Service;
import at.fhtw.service.card.CardController;

public class BattleService implements Service {
    @Override
    public Response handleRequest(Request request) {
        Response response;
        if(request.getMethod().equals(Method.POST)){
            response = new BattleController().handlePost(request);
        }else{
            response = new Response(HttpStatus.BAD_REQUEST,
                    ContentType.PLAIN_TEXT,
                    "");
        }
        return response;
    }
}
