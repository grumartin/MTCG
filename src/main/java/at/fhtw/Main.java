package at.fhtw;

import at.fhtw.httpserver.server.Server;
import at.fhtw.httpserver.utils.Router;
import at.fhtw.service.card.CardService;
import at.fhtw.service.deck.DeckService;
import at.fhtw.service.echo.EchoService;
import at.fhtw.service.pckg.PackageService;
import at.fhtw.service.session.SessionService;
import at.fhtw.service.stats.StatsService;
import at.fhtw.service.trading.TradingService;
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
        router.addService("/cards", new CardService());
        router.addService("/deck", new DeckService());
        router.addService("/stats", new StatsService());
        router.addService("/scoreboard", new StatsService());
        router.addService("/tradings", new TradingService());
        return router;
    }
}