package at.fhtw;

import at.fhtw.httpserver.server.Server;
import at.fhtw.httpserver.utils.Router;
import at.fhtw.service.echo.EchoService;
import at.fhtw.service.pckg.PackageService;
import at.fhtw.service.session.SessionService;
import at.fhtw.service.user.UserService;

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
        router.addService("/users", new UserService());
        router.addService("/sessions", new SessionService());
        router.addService("/packages", new PackageService());
        router.addService("/transactions", new PackageService());
        return router;
    }
}