public enum HttpResponseStatus {
    NOT_FOUND("HTTP/1.1 404 Not Found\r\n\r\n"),
    OK("HTTP/1.1 200 OK\r\n\r\n");

    private final String response;

    HttpResponseStatus(String response) {
        this.response = response;
    }

    public String getResponse() {
        return response;
    }
}
