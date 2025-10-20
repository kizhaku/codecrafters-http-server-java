package enums;

public enum HttpResponseStatus {
    OK("HTTP/1.1 200 OK\r\n\r\n"),
    OK_TEXT("HTTP/1.1 200 OK\r\nContent-Type: text/plain\r\nContent-Length: %s\r\n\r\n%s"),
    OK_OCTET("HTTP/1.1 200 OK\r\nContent-Type: application/octet-stream\r\nContent-Length: %s\r\n\r\n%s"),
    NOT_FOUND("HTTP/1.1 404 Not Found\r\n\r\n"),
    ERROR("HTTP/1.1 500 Server Error\r\n\r\n");

    private final String response;

    HttpResponseStatus(String response) {
        this.response = response;
    }

    public String getResponse() {
        return response;
    }
}
