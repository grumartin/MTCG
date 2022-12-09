package at.fhtw.service;

import at.fhtw.dal.UnitOfWork;
import at.fhtw.httpserver.server.Request;
import at.fhtw.httpserver.server.Response;

public interface Service {
    Response handleRequest(Request request);
}
