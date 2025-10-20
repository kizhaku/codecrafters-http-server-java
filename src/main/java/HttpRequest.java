import java.util.Map;

public class HttpRequest {
    private final String requestLine;
    private final Map<String, String> headers;
    private final byte[] body;
    private final String endPoint;
    private final String path;
    private final String httpMethod;

    public HttpRequest(String requestLine, Map<String, String> headers, byte[] body) {
        this.requestLine = requestLine;
        this.headers = headers;
        this.body = body;
        this.path = computePath();
        this.endPoint = computeEndpoint();
        this.httpMethod = computeHttpMethod();
    }

    private String computeEndpoint() {
        int endPointIndex = path.indexOf("/", 1);
        return (endPointIndex == -1) ? path : path.substring(0, endPointIndex);
    }

    private String computePath() {
        if (this.requestLine != null)
            return this.requestLine.split(" ")[1].trim();

        return "";
    }

    private String computeHttpMethod() {
        if (this.requestLine != null)
            return this.requestLine.split(" ")[0].trim();

        return "";
    }

    //Getters
    public String getPath() { return path; }
    public Map<String, String> getHeaders() { return headers; }
    public byte[] getBody() { return body; }
    public String getEndPoint() { return endPoint; }
    public String getRequestLine() { return requestLine; }
    public String getHttpMethod() { return httpMethod; }
}
