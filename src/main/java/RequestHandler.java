import dto.HttpRequest;
import dto.HttpResponse;
import enums.HttpResponseStatus;
import enums.HttpStatus;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

/**
 * Implementation for each route
 */
public class RequestHandler {
    private final static String FILE_DIR = System.getProperty("directory", "");

    public static HttpResponse home(HttpRequest request) {
        return new HttpResponse.Builder(HttpStatus.OK).build();
    }

    public static HttpResponse echo(HttpRequest request) {
        String pathVar = getPathVar(request.getPath());
        String encoding = request.getHeaders().get("Accept-Encoding");

        HttpResponse.Builder httpResponseBuild = new HttpResponse.Builder(HttpStatus.OK)
                .header("Content-Length", String.valueOf(pathVar.length()))
                .body(pathVar);

        //HTTP Compression: support only gzip for now
        if (encoding !=null && encoding.contains("gzip"))
            httpResponseBuild.header("Content-Encoding", encoding);

        return httpResponseBuild.build();
    }

    public static HttpResponse userAgent(HttpRequest request) {
        if (request.getHeaders().containsKey("User-Agent")) {
            String headerValue = request.getHeaders().get("User-Agent");

            return new HttpResponse.Builder(HttpStatus.OK)
                    .header("Content-Length", String.valueOf(headerValue.length()))
                    .body(headerValue)
                    .build();
        }
        return new HttpResponse.Builder(HttpStatus.NOT_FOUND).build();
    }

    public static HttpResponse getFile(HttpRequest request) {
        String pathVar = getPathVar(request.getPath());
        Path path = Path.of(FILE_DIR +pathVar);
        System.out.println("Checking file at path: " + path);

        try {
            if (Files.exists(path) && Files.isRegularFile(path)) {
                return new HttpResponse.Builder(HttpStatus.OK)
                        .header("Content-Type", "application/octet-stream")
                        .header("Content-Length", String.valueOf(Files.size(path)))
                        .body(Files.readString(path))
                        .build();
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
            return new HttpResponse.Builder(HttpStatus.ERROR).build();
        }

        return new HttpResponse.Builder(HttpStatus.NOT_FOUND).build();
    }

    public static HttpResponse postFile(HttpRequest request) {
        String pathVar = getPathVar(request.getPath());
        Path path = Path.of(FILE_DIR +pathVar);

        try (OutputStream out = Files.newOutputStream(path, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)) {
            out.write(request.getBody());
            out.flush();
        } catch (IOException iox) {
            System.out.println(iox.getMessage());
            return new HttpResponse.Builder(HttpStatus.ERROR).build();
        }

        return new HttpResponse.Builder(HttpStatus.CREATED).build();
    }

    public static HttpResponse notFound(HttpRequest request) {
        return new HttpResponse.Builder(HttpStatus.NOT_FOUND).build();
    }

    public static HttpResponse methodNotAllowed(HttpRequest request) {
        return new HttpResponse.Builder(HttpStatus.METHOD_NOT_ALLOWED).build();
    }

    private static String getPathVar(String path) {
        var pathVar = "";
        int endPointIndex = path.indexOf("/", 1);
        if (endPointIndex > -1)
            pathVar = path.substring(endPointIndex + 1);

        return pathVar;
    }
}
