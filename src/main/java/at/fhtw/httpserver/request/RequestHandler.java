package at.fhtw.httpserver.request;

import at.fhtw.httpserver.Router;
import at.fhtw.httpserver.enums.ContentType;
import at.fhtw.httpserver.enums.HttpStatus;
import at.fhtw.httpserver.response.Response;
import at.fhtw.service.Service;

import java.io.*;
import java.net.Socket;

public class RequestHandler implements  Runnable{

    private Socket clientConnection;
    private Router router;
    public RequestHandler(Socket clientConnection, Router router) {
        this.clientConnection = clientConnection;
        this.router = router;
    }

    @Override
    public void run() {
        PrintWriter printWriter = null;
        BufferedReader bufferedReader = null;

        try {
            InputStream inputStream = clientConnection.getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            bufferedReader = new BufferedReader(inputStreamReader);
            OutputStream outputStream = clientConnection.getOutputStream();
            printWriter = new PrintWriter(outputStream);

            Request clientRequest = new RequestBuilder().buildRequest(bufferedReader);
            Response response;
            if(clientRequest.getPathname() == null || clientRequest.getPathname().length() == 1){
                response = new Response(HttpStatus.BAD_REQUEST,
                                        ContentType.PLAIN_TEXT,
                                        "");
            }else{
                Service service = this.router.resolve(clientRequest.getServiceRoute());

                if(service == null){
                    response = new Response(HttpStatus.NOT_FOUND,
                            ContentType.PLAIN_TEXT,
                            "");
                }else {
                    response = service.handelRequest(clientRequest);
                }
            }
            printWriter.write(response.get());
        } catch (IOException exception) {
            exception.printStackTrace();
        }finally{
            try{
                if(printWriter != null){
                    printWriter.close();
                }
                if(bufferedReader != null){
                    bufferedReader.close();
                    clientConnection.close();
                }
            }catch (IOException exception){
                exception.printStackTrace();
            }

        }
    }
}
