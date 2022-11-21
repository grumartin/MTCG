package at.fhtw.service.pckg;

import at.fhtw.httpserver.http.ContentType;
import at.fhtw.httpserver.http.HttpStatus;
import at.fhtw.httpserver.server.Request;
import at.fhtw.httpserver.server.Response;
import at.fhtw.service.Service;

public class PackageService implements Service {
    @Override
    public Response handleRequest(Request request) {
        PackageController packageController = new PackageController();
        TransactionController transactionController = new TransactionController();
        Response response;
        switch (request.getMethod()){
            case POST:
                if(request.getServiceRoute().equals("/packages"))
                    response = packageController.handlePost(request);
                else if(request.getServiceRoute().equals("/transactions"))
                    response = transactionController.handlePost(request);
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
