package at.fhtw.httpserver.enums;

public enum HttpStatus {
    OK(200, "OK"),
    FORBIDDEN(403, "Forbidden"),
    BAD_REQUEST(400, "Bad Request"),
    NOT_FOUND(404, "Not Found");

    public final int statusCode;
    public final String statusMessage;

    HttpStatus(int statusCode, String statusMessage) {
        this.statusCode = statusCode;
        this.statusMessage = statusMessage;
    }
}
