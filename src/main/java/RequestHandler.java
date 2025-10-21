import dto.HttpRequest;
import dto.HttpResponse;
import enums.HttpStatus;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * Implementation for each route
 */
public class RequestHandler {
    private final static String FILE_DIR = System.getProperty("directory", "");

    public static HttpResponse home(HttpRequest request) {
        return HttpResponse.builder(HttpStatus.OK).build();
    }

    public static HttpResponse echo(HttpRequest request) {
        String pathVar = getPathVar(request.getPath());
        String encoding = request.getHeaders().get("Accept-Encoding");

        HttpResponse.Builder response = HttpResponse.builder(HttpStatus.OK)
                .header("Content-Length", String.valueOf(pathVar.length()))
                .body(pathVar.getBytes());

        //HTTP Compression: support only gzip for now
        if (encoding != null && encoding.contains("gzip")) {
            try (ByteArrayOutputStream outStream = new ByteArrayOutputStream();
                 GZIPOutputStream gzipOutStream = new GZIPOutputStream(outStream)) {

                gzipOutStream.write(pathVar.getBytes(StandardCharsets.UTF_8));
                gzipOutStream.finish();
                byte[] out = outStream.toByteArray();

                response.header("Content-Encoding", "gzip")
                        .header("Content-Length", String.valueOf(out.length))
                        .body(out);
            } catch (IOException ex) {
                System.out.println(ex.getMessage());
                return new HttpResponse.Builder(HttpStatus.ERROR).build();
            }
        }

        return response.build();
    }

    public static HttpResponse userAgent(HttpRequest request) {
        if (request.getHeaders().containsKey("User-Agent")) {
            String headerValue = request.getHeaders().get("User-Agent");

            return HttpResponse.builder(HttpStatus.OK)
                    .header("Content-Length", String.valueOf(headerValue.length()))
                    .body(headerValue.getBytes())
                    .build();
        }
        return HttpResponse.builder(HttpStatus.NOT_FOUND).build();
    }

    public static HttpResponse getFile(HttpRequest request) {
        String pathVar = getPathVar(request.getPath());
        Path path = Path.of(FILE_DIR +pathVar);
        System.out.println("Checking file at path: " + path);

        try {
            if (Files.exists(path) && Files.isRegularFile(path)) {
                return HttpResponse.builder(HttpStatus.OK)
                        .header("Content-Type", "application/octet-stream")
                        .header("Content-Length", String.valueOf(Files.size(path)))
                        .body(Files.readAllBytes(path))
                        .build();
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
            return new HttpResponse.Builder(HttpStatus.ERROR).build();
        }

        return HttpResponse.builder(HttpStatus.NOT_FOUND).build();
    }

    public static HttpResponse postFile(HttpRequest request) {
        String pathVar = getPathVar(request.getPath());
        Path path = Path.of(FILE_DIR +pathVar);

        try (OutputStream out = Files.newOutputStream(path, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)) {
            out.write(request.getBody());
            out.flush();
        } catch (IOException iox) {
            System.out.println(iox.getMessage());
            return HttpResponse.builder(HttpStatus.ERROR).build();
        }

        return HttpResponse.builder(HttpStatus.CREATED).build();
    }

    public static HttpResponse notFound(HttpRequest request) {
        return HttpResponse.builder(HttpStatus.NOT_FOUND).build();
    }

    public static HttpResponse methodNotAllowed(HttpRequest request) {
        return HttpResponse.builder(HttpStatus.METHOD_NOT_ALLOWED).build();
    }

    private static String getPathVar(String path) {
        var pathVar = "";
        int endPointIndex = path.indexOf("/", 1);
        if (endPointIndex > -1)
            pathVar = path.substring(endPointIndex + 1);

        return pathVar;
    }
}
