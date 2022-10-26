package at.fhtw.httpserver;

import at.fhtw.service.Service;

import java.util.HashMap;
import java.util.Map;

public class Router {

    private Map<String, Service> serviceRegistry = new HashMap<>();

    public Service resolve(String route) {
        return this.serviceRegistry.get(route);
    }

    public void addService(String route, Service service) {
        this.serviceRegistry.put(route, service);
    }

    public void removeService(String route) {
        this.serviceRegistry.remove(route);
    }
}
