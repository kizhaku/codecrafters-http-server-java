package enums;

/*
    No longer using this. Replaced with HttpStatus
 */
public enum HttpResponseStatus {
    OK("HTTP/1.1 200 OK\r\n\r\n"),
    OK_TEXT("HTTP/1.1 200 OK\r\nContent-Type: text/plain\r\nContent-Length: %s\r\n\r\n%s"),
    OK_OCTET("HTTP/1.1 200 OK\r\nContent-Type: application/octet-stream\r\nContent-Length: %s\r\n\r\n%s"),
    CREATED("HTTP/1.1 201 Created\r\n\r\n"),
    NOT_FOUND("HTTP/1.1 404 Not Found\r\n\r\n"),
    METHOD_NOT_ALLOWED("HTTP/1.1 405 Method Not Allowed\r\n\r\n"),
    ERROR("HTTP/1.1 500 Server Error\r\n\r\n");

    private final String response;

    HttpResponseStatus(String response) {
        this.response = response;
    }

    public String getResponse() {
        return response;
    }
}
