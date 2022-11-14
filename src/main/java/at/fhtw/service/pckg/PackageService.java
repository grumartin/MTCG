package at.fhtw.service.pckg;

import at.fhtw.httpserver.http.ContentType;
import at.fhtw.httpserver.http.HttpStatus;
import at.fhtw.httpserver.server.Request;
import at.fhtw.httpserver.server.Response;
import at.fhtw.service.Service;
import at.fhtw.service.user.UserController;

public class PackageService implements Service {
    @Override
    public Response handleRequest(Request request) {
        PackageController packageController = new PackageController();
        Response response = null;
        switch (request.getMethod()){
            case POST:
                response = packageController.handlePost(request);
                break;
            default:
                response = new Response(HttpStatus.BAD_REQUEST,
                        ContentType.PLAIN_TEXT,
                        "");
        }
        return response;
    }
}
