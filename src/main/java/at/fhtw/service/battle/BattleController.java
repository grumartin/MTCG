package at.fhtw.service.battle;

import at.fhtw.httpserver.http.ContentType;
import at.fhtw.httpserver.http.HttpStatus;
import at.fhtw.httpserver.server.Request;
import at.fhtw.httpserver.server.Response;

public class BattleController {
    public Response handlePost(Request request) {
        if(request.getAuthorizedClient() == null)
            return new Response(HttpStatus.UNAUTHORIZED,
                    ContentType.PLAIN_TEXT,
                    "");

        return null;
    }
}
