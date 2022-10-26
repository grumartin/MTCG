package at.fhtw.httpserver;

import at.fhtw.httpserver.request.RequestHandler;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {

    private int port;
    private Router router;
    public Server(int port, Router router) {
        this.port = port;
        this.router = router;
    }

    public void start() throws IOException {

        ExecutorService executorService = Executors.newFixedThreadPool(10);

        try (ServerSocket serverSocket = new ServerSocket(this.port)) {
            System.out.println("Server started ...");
            while(true) {
                Socket clientConnection = serverSocket.accept();
                executorService.execute(new RequestHandler(clientConnection, this.router));
            }
        }
    }
}
