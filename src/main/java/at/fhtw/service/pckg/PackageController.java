package at.fhtw.service.pckg;

import at.fhtw.httpserver.http.ContentType;
import at.fhtw.httpserver.http.HttpStatus;
import at.fhtw.httpserver.server.Request;
import at.fhtw.httpserver.server.Response;

public class PackageController {
    public Response handlePost(Request request){
        if(request.getAuthorizedClient() == null || !request.getAuthorizedClient().equals("admin")){
            return new Response(HttpStatus.UNAUTHORIZED,
                    ContentType.PLAIN_TEXT,
                    "");
        }

    }
}
