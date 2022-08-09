package dev.makos.handlers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import dev.makos.entity.Device;
import dev.makos.entity.Port;
import dev.makos.error.ErrorMessage;
import dev.makos.repository.Repository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.List;

public class DevicesHandler implements HttpHandler {

    private static final Logger log = LogManager.getLogger(DevicesHandler.class);

    private ObjectMapper mapper = new ObjectMapper();

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        switch (exchange.getRequestMethod()) {
            case "GET":
                processGet(exchange);
                break;
            case "POST":
                processPost(exchange);
                break;
            case "DELETE":
                processDelete(exchange);
                break;
//            case "PUT":
//                processPut(exchange);
//                break;
        }
    }

    private void processDelete(HttpExchange exchange) throws IOException {
        String url = exchange.getRequestURI().toString();
        log.trace("DELETE url = {}", url);

        String deviceIdValue = url.substring(url.lastIndexOf('/') + 1);
        log.trace("deviceIdValue: {}", deviceIdValue);
        if (deviceIdValue.isEmpty()) {
            nok(exchange, mapper.writeValueAsString(new ErrorMessage("URL doesn't contain deviceId. URL: " + url)));
        }

        try {
            long deviceId = Long.parseLong(deviceIdValue);
            Device device = Repository.removeDevice(deviceId);

            String resultJson = mapper.writeValueAsString(device);
            ok(exchange, resultJson);
        } catch (Exception ex) {
            nok(exchange, mapper.writeValueAsString(new ErrorMessage(ex.getMessage())));
        }
    }

    private void processPost(HttpExchange exchange) throws IOException {
        String url = exchange.getRequestURI().toString();
        log.trace("POST url = {}", url);

        if (url.endsWith("/ports")) {
            String deviceIdValue = url.replaceAll("\\D+", "");
            log.trace("deviceIdValue: {}", deviceIdValue);
            if (deviceIdValue.isEmpty()) {
                nok(exchange, mapper.writeValueAsString(new ErrorMessage("URL doesn't contain deviceID. URL: " + url)));
                return;
            }

            try {
                long deviceId = Long.parseLong(deviceIdValue);

                String portJson = new String(exchange.getRequestBody().readAllBytes());
                Port port = mapper.readValue(portJson, Port.class);

                Repository.addDevicePort(deviceId, port);

                String resultJson = mapper.writeValueAsString(port);
                ok(exchange, resultJson);
            } catch (Exception ex) {
                nok(exchange, mapper.writeValueAsString(new ErrorMessage(ex.getMessage())));
            }
        } else if (url.equals("/devices")) {
            log.trace("try add new device");
            try {
                String deviceJson = new String(exchange.getRequestBody().readAllBytes());
                log.trace("Device data: {}", deviceJson);
                Device device = mapper.readValue(deviceJson, Device.class);

                Repository.addDevice(device);

                log.trace("Device added: {}", device);
                String resultJson = mapper.writeValueAsString(device);
                ok(exchange, resultJson);
            } catch (Exception ex) {
                log.error(ex);
                nok(exchange, mapper.writeValueAsString(new ErrorMessage(ex.getMessage())));
            }
        } else {
            nok(exchange, mapper.writeValueAsString(new ErrorMessage("Can't map URl")));
        }
    }

    private void processGet(HttpExchange exchange) throws IOException {
        String url = exchange.getRequestURI().toString();
        log.trace("GET url = {}", url);

        if ("/devices".equals(url)) {
            try {
                List<Device> devices = Repository.fetchAllDevices();
                String resultJson = mapper.writeValueAsString(devices);

                ok(exchange, resultJson);
            } catch (Exception ex) {
                log.error("Exception during getting all devices", ex);

                nok(exchange, mapper.writeValueAsString(new ErrorMessage(ex.getMessage())));
            }
        } else if (url.endsWith("/ports")) {
            String deviceIdValue = url.replaceAll("\\D+", "");
            log.trace("deviceIdValue: {}", deviceIdValue);
            if (deviceIdValue.isEmpty()) {
                nok(exchange, mapper.writeValueAsString(new ErrorMessage("URL doesn't contain deviceId. URL: " + url)));
                return;
            }
            try {
                long deviceId = Long.parseLong(deviceIdValue);

                List<Port> ports = Repository.fetchAllPortsByDevice(deviceId);
                String resultJson = mapper.writeValueAsString(ports);
                ok(exchange, resultJson);
            } catch (Exception ex) {
                nok(exchange, mapper.writeValueAsString(new ErrorMessage(ex.getMessage())));
            }
        } else {
            String deviceIdValue = url.substring(url.lastIndexOf('/') + 1);
            log.trace("deviceIdValue: {}", deviceIdValue);
            if (deviceIdValue.isEmpty()) {
                nok(exchange, mapper.writeValueAsString(new ErrorMessage("URL doesn't contain deviceId. URL: " + url)));
                return;
            }
            try {
                long deviceId = Long.parseLong(deviceIdValue);
                Device device = Repository.fetchDevice(deviceId);
                String resultJson = mapper.writeValueAsString(device);
                ok(exchange, resultJson);
            } catch (Exception ex) {
                nok(exchange, mapper.writeValueAsString(new ErrorMessage(ex.getMessage())));
            }
        }
    }

    private void ok(HttpExchange exchange, String resultJson) throws IOException {
        exchange.sendResponseHeaders(200, resultJson.getBytes().length);
        exchange.getResponseBody().write(resultJson.getBytes());
        exchange.getResponseBody().flush();
    }

    private void nok(HttpExchange exchange, String resultJson) throws IOException {
        exchange.sendResponseHeaders(503, resultJson.getBytes().length);
        exchange.getResponseBody().write(resultJson.getBytes());
        exchange.getResponseBody().flush();
    }
}


/*
 * GET /devices - get all device from DB
 * GET /devices/{id} - get device by id
 * GET /devices/{id}/ports - get ports of device
 *
 * POST /devices - create new device
 * POST /devices/{id}/ports - create new port for device
 *
 * PUT /devices/{id} - update device by id
 *
 * DELETE /devices/{id} - delete device by id
 *
 * */
