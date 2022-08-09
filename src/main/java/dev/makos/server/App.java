package dev.makos.server;


import com.sun.net.httpserver.HttpServer;
import dev.makos.handlers.DevicesHandler;
import dev.makos.handlers.PingHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.InetSocketAddress;

public class App {

    private static final Logger log = LogManager.getLogger(App.class);

    public static void main(String[] args) {
       log.trace("Server is starting...");

        HttpServer server;
        try {
            server = HttpServer.create();
            server.bind(new InetSocketAddress(8765), 0);
        } catch (IOException ex) {
            log.error("Server can't start! Reason: {}", ex.getMessage(), ex);
            return;
        }

        server.createContext("/", new PingHandler());
        server.createContext("/devices", new DevicesHandler());
//        server.createContext("/cables", new CableHandler());

        server.start();
        log.trace("Server started");
    }

}
