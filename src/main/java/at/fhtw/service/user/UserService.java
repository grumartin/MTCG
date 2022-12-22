package at.fhtw.service.user;
import at.fhtw.httpserver.http.ContentType;
import at.fhtw.httpserver.http.HttpStatus;
import at.fhtw.httpserver.server.Request;
import at.fhtw.httpserver.server.Response;
import at.fhtw.service.Service;

import java.io.IOException;

public class UserService implements Service {
    @Override
    public Response handleRequest(Request request) {
        UserController userController = new UserController();
        Response response = null;
        switch (request.getMethod()){
            case POST:
                response = userController.handlePost(request);
                break;
            case GET:
                response = userController.handleGet(request);
                break;
            case PUT:
                response = userController.handlePut(request);
                break;
            case DELETE:
                response = userController.handleDelete(request);
                break;
            default:
                response = new Response(HttpStatus.BAD_REQUEST,
                        ContentType.PLAIN_TEXT,
                        "");
        }
        return response;
    }
}
