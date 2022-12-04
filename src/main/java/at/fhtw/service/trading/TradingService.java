package at.fhtw.service.trading;

import at.fhtw.httpserver.http.ContentType;
import at.fhtw.httpserver.http.HttpStatus;
import at.fhtw.httpserver.server.Request;
import at.fhtw.httpserver.server.Response;
import at.fhtw.service.Service;

public class TradingService implements Service {
    @Override
    public Response handleRequest(Request request) {
        if(request.getAuthorizedClient() == null)
            return new Response(HttpStatus.UNAUTHORIZED,
                    ContentType.PLAIN_TEXT,
                    "Authentication information is missing or invalid");

        TradingController tradingController = new TradingController();
        Response response;
        switch (request.getMethod()){
            case POST:
                if(request.getPathParts().size() > 1)
                    response = tradingController.handlePostCarryOut(request);
                else
                    response = tradingController.handlePostCreate(request);
                break;
            case GET:
                response = tradingController.handleGet(request);
                break;
            case DELETE:
                response = tradingController.handleDelete(request);
                break;
            default:
                response = new Response(HttpStatus.BAD_REQUEST,
                        ContentType.PLAIN_TEXT,
                        "");
        }
        return response;
    }
}
