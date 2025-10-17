import java.io.BufferedReader;

public class RequestHandler {
    private static final String BASE_RESPONSE = "HTTP/1.1 200 OK\r\nContent-Type: text/plain\r\nContent-Length: %s\r\n\r\n%s";

    public static String home(BufferedReader br, String pathVar) {
        return HttpResponseStatus.OK.getResponse();
    }

    public static String echo(BufferedReader br, String pathVar) {
        return BASE_RESPONSE.formatted(pathVar.length(), pathVar);
    }

    public static String userAgent(BufferedReader br, String pathVar) {
        String line;

        try {
            while ((line = br.readLine()) != null && !line.isEmpty()) {
                int headerEnd = line.indexOf(":");
                String headerKey = line.substring(0, headerEnd);

                if (headerKey.equalsIgnoreCase("User-Agent")) {
                    String headerValue = line.substring(headerEnd + 1).trim();

                    return BASE_RESPONSE.formatted(headerValue.length(), headerValue);
                }
            }
        } catch (Exception ex) {
            throw new RuntimeException();
        }

        return "";
    }
}
