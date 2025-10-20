import enums.HttpResponseStatus;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

/**
 * Implementation for each route
 */
public class RequestHandler {
    private final static String FILE_DIR = System.getProperty("directory", "");

    public static String home(HttpRequest request) {
        return HttpResponseStatus.OK.getResponse();
    }

    public static String echo(HttpRequest request) {
        String pathVar = getPathVar(request.getPath());

        return HttpResponseStatus.OK_TEXT.getResponse().formatted(pathVar.length(), pathVar);
    }

    public static String userAgent(HttpRequest request) {
        if (request.getHeaders().containsKey("User-Agent")) {
            String headerValue = request.getHeaders().get("User-Agent");
            return HttpResponseStatus.OK_TEXT.getResponse().formatted(headerValue.length(), headerValue);
        }

        return HttpResponseStatus.NOT_FOUND.getResponse();
    }

    public static String getFile(HttpRequest request) {
        String pathVar = getPathVar(request.getPath());
        Path path = Path.of(FILE_DIR +pathVar);
        System.out.println("Checking file at path: " + path);

        try {
            if (Files.exists(path) && Files.isRegularFile(path))
                return HttpResponseStatus.OK_OCTET.getResponse()
                        .formatted(Files.size(path), Files.readString(path));
        } catch (IOException e) {
            System.out.println(e.getMessage());
            return HttpResponseStatus.ERROR.getResponse();
        }

        return HttpResponseStatus.NOT_FOUND.getResponse();
    }

    public static String postFile(HttpRequest request) {
        String pathVar = getPathVar(request.getPath());
        Path path = Path.of(FILE_DIR +pathVar);

        try (OutputStream out = Files.newOutputStream(path, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)) {
            out.write(request.getBody());
            out.flush();
        } catch (IOException iox) {
            System.out.println(iox.getMessage());
            return HttpResponseStatus.ERROR.getResponse();
        }

        return HttpResponseStatus.CREATED.getResponse();
    }

    public static String notFound(HttpRequest request) {
        return HttpResponseStatus.NOT_FOUND.getResponse();
    }

    public static String methodNotAllowed(HttpRequest request) {
        return HttpResponseStatus.METHOD_NOT_ALLOWED.getResponse();
    }

    private static String getPathVar(String path) {
        var pathVar = "";
        int endPointIndex = path.indexOf("/", 1);
        if (endPointIndex > -1)
            pathVar = path.substring(endPointIndex + 1);

        return pathVar;
    }
}
