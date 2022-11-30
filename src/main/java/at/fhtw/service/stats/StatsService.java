package at.fhtw.service.stats;

import at.fhtw.httpserver.http.ContentType;
import at.fhtw.httpserver.http.HttpStatus;
import at.fhtw.httpserver.server.Request;
import at.fhtw.httpserver.server.Response;
import at.fhtw.service.Service;

public class StatsService implements Service {
    @Override
    public Response handleRequest(Request request) {
        StatsController statsController = new StatsController();
        Response response;
        switch (request.getMethod()){
            case GET:
                if(request.getServiceRoute().equals("/scoreboard"))
                    response = statsController.handleGetScoreboard(request);
                else if(request.getServiceRoute().equals("/stats"))
                    response = statsController.handleGetStats(request);
                else
                    response = new Response(HttpStatus.BAD_REQUEST,
                            ContentType.PLAIN_TEXT,
                            "");
                break;
            default:
                response = new Response(HttpStatus.BAD_REQUEST,
                        ContentType.PLAIN_TEXT,
                        "");
        }
        return response;
    }
}
