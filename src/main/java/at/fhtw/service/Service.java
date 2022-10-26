package at.fhtw.service;

import at.fhtw.httpserver.request.Request;
import at.fhtw.httpserver.response.Response;

public interface Service {
    Response handelRequest(Request request);
}
