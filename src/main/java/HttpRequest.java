import java.util.Map;

public class HttpRequest {
    private final String path;
    private final Map<String, String> headers;
    private final byte[] body;
    private final String endPoint;

    public HttpRequest(String path, Map<String, String> headers) {
        int endPointIndex = path.indexOf("/", 1);
        String endPoint = (endPointIndex == -1) ? path : path.substring(0, endPointIndex);
        this.path = path;
        this.headers = headers;
        this.body = new byte[0];
        this.endPoint = endPoint;
    }

    public String getPath() { return path; }
    public Map<String, String> getHeaders() { return headers; }
    public byte[] getBody() { return body; }
    public String getEndPoint() { return endPoint; }
}
