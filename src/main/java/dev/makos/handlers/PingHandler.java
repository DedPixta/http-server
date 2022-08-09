package dev.makos.handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

public class PingHandler implements HttpHandler {

    private static final Logger log = LogManager.getLogger(PingHandler.class);

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        log.trace("Ping.request. \n Method: {} \n Headers: {} \n Bode: {}",
                exchange.getRequestBody(), exchange.getRequestHeaders(), new String(exchange.getRequestBody().readAllBytes()));
        log.trace("User data. IP: {}, Protocol: {}, URI: {}",
                exchange.getRemoteAddress(), exchange.getProtocol(), exchange.getRequestURI());

        String pingMessage = "{\"ping\": \"success\"}";

        exchange.sendResponseHeaders(200, pingMessage.getBytes().length);
        exchange.getResponseBody().write(pingMessage.getBytes());
        exchange.getResponseBody().flush();

        log.trace("Ping is finished!");
    }
}
