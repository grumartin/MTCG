package at.fhtw.service.echo;

import at.fhtw.httpserver.*;
import at.fhtw.httpserver.enums.ContentType;
import at.fhtw.httpserver.enums.HttpStatus;
import at.fhtw.httpserver.request.Request;
import at.fhtw.httpserver.response.Response;
import at.fhtw.service.Service;

public class EchoService implements Service {
    @Override
    public Response handelRequest(Request request) {
        return new Response(HttpStatus.OK,
                ContentType.PLAIN_TEXT,
                "Echo-" + request.getBody());
    }
}
