import enums.HttpResponseStatus;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Implementation for each route
 */
public class RequestHandler {
    private final static String FILE_DIR = System.getProperty("directory", "");

    public static String home(HttpRequest httpRequest) {
        return HttpResponseStatus.OK.getResponse();
    }

    public static String echo(HttpRequest httpRequest) {
        String pathVar = getPathVar(httpRequest.getPath());

        return HttpResponseStatus.OK_TEXT.getResponse().formatted(pathVar.length(), pathVar);
    }

    public static String userAgent(HttpRequest httpRequest) {
        if (httpRequest.getHeaders().containsKey("User-Agent")) {
            String headerValue = httpRequest.getHeaders().get("User-Agent");
            return HttpResponseStatus.OK_TEXT.getResponse().formatted(headerValue.length(), headerValue);
        }

        return HttpResponseStatus.NOT_FOUND.getResponse();
    }

    public static String notFound(HttpRequest request) {
        return HttpResponseStatus.NOT_FOUND.getResponse();
    }

    public static String files(HttpRequest httpRequest) {
        String pathVar = getPathVar(httpRequest.getPath());
        Path path = Path.of(FILE_DIR +pathVar);
        System.out.println("Checking file at path: " + path);

        try {
            if (!Files.exists(path) && !Files.isRegularFile(path))
                return HttpResponseStatus.NOT_FOUND.getResponse();

            return HttpResponseStatus.OK_OCTET.getResponse()
                    .formatted(Files.size(path), Files.readString(path));
        } catch (IOException e) {
            System.out.println(e.getMessage());
            return HttpResponseStatus.ERROR.getResponse();
        }
    }

    private static String getPathVar(String path) {
        var pathVar = "";
        int endPointIndex = path.indexOf("/", 1);
        if (endPointIndex > -1)
            pathVar = path.substring(endPointIndex + 1);

        return pathVar;
    }
}
