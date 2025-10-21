import dto.HttpRequest;
import dto.HttpResponse;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class HttpServer {
    private static final int SERVER_PORT = 4221;

    public static void start() {
        try (ServerSocket serverSocket = new ServerSocket(SERVER_PORT)) {
            serverSocket.setReuseAddress(true);
            acceptConnections(serverSocket);
        } catch (IOException ex) {
            System.out.println("IOException: " + ex.getMessage());
        }
    }

    private static void acceptConnections(ServerSocket serverSocket) {
        System.out.println(" \u2705 Accepting connection..");

        try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
            while (true) {
                Socket connection = serverSocket.accept();
                executor.submit(() -> {
                    try (connection) {
                        HttpRequest httpRequest = getRequest(connection);
                        HttpResponse response = Router.route(httpRequest);
                        writeResponse(connection, response);
                    } catch (IOException ex) {
                        System.out.println(ex.getMessage());
                    }
                });
            }
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }

    private static void writeResponse(Socket connection, HttpResponse response) throws IOException {
        OutputStream out = connection.getOutputStream();
        System.out.println(" \u2705 Accepted new connection..");

        System.out.println("Writing HTTP response to client..");
        out.write(response.toStringHeaders().getBytes());
        out.write(response.getBody());
        out.flush();
    }

    /**
     * Build the HttpRequest object from the incoming HTTP request.
     * Parse the text to get requestLine, header and body.
     */
    private static HttpRequest getRequest(Socket connection) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        String requestLine = getRequestLine(br);
        System.out.println("Printing request line: " +requestLine);

        Map<String, String> headers = getRequestHeaders(br);
        int contentLength = Integer.parseInt(headers.getOrDefault("Content-Length", "0"));
        byte[] body = getRequestBody(br, contentLength);

        return new HttpRequest(requestLine, headers, body);
    }

    private static String getRequestLine(BufferedReader br) throws IOException {
        return br.readLine();
    }

    private static Map<String, String> getRequestHeaders(BufferedReader br) {
        return br.lines()
                .takeWhile(l -> !l.isEmpty())
                .map(l ->  l.split(":", 2))
                .collect(Collectors.toMap(h -> h[0].trim(), h -> h[1].trim()));
    }

    private static byte[] getRequestBody(BufferedReader br, int contentLength) throws IOException {
        char[] body = new char[contentLength]; //Expecting only text for now
        if (contentLength > 0) {
            br.read(body, 0, contentLength);
        }

        return new String(body).getBytes();
    }
}
