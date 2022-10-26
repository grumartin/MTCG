package at.fhtw;

import at.fhtw.httpserver.Router;
import at.fhtw.httpserver.Server;
import at.fhtw.service.echo.EchoService;

import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        Server server = new Server(10001, configureRouter());
        try {
            server.start();
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    private static Router configureRouter(){
        Router router = new Router();
        //alle Services einfügen
        router.addService("/echo", new EchoService());
        return router;
    }
}