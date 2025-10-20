import dto.HttpRequest;
import dto.HttpResponse;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class Main {

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(4221);
             ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {

            System.out.println(" \u2705 Accepting connection..");

            if (args.length >= 2 && args[0].equalsIgnoreCase("--directory")) {
                if (args[1] != null)
                    System.setProperty("directory", args[1]);
            }

            // Since the tester restarts your program quite often, setting SO_REUSEADDR
            // ensures that we don't run into 'Address already in use' errors
            serverSocket.setReuseAddress(true);

            while (true) {
                Socket connection = serverSocket.accept();
                executor.submit(() -> {
                    try (connection) {
                        HttpRequest httpRequest = getHttpRequest(connection);
                        HttpResponse response = Router.route(httpRequest);
                        writeResponse(connection, response);
                    } catch (IOException ioex) {
                        ioex.getMessage();
                    }
                });
            }
        } catch (IOException e) {
            System.out.println("IOException: " + e.getMessage());
        }
    }

    private static void writeResponse(Socket connection, HttpResponse response) throws IOException {
        OutputStream out = connection.getOutputStream();
        System.out.println(" \u2705 Accepted new connection..");

        System.out.println("Writing HTTP response to client..");
        out.write(response.toString().getBytes());
        out.flush();
    }

    /**
     * Build the HttpRequest object from the incoming HTTP request.
     * Parse the text to get requestLine, header and body.
     */
    private static HttpRequest getHttpRequest(Socket connection) throws IOException {
        InputStream in = connection.getInputStream();
        BufferedReader br = new BufferedReader(new InputStreamReader(in));
        String requestLine = br.readLine();

        System.out.println("Printing request line: " +requestLine);

        Map<String, String> headers = br.lines()
                .takeWhile(l -> !l.isEmpty())
                .map(l ->  l.split(":", 2))
                .collect(Collectors.toMap(h -> h[0].trim(), h -> h[1].trim()));

        int contentLength = Integer.parseInt(headers.getOrDefault("Content-Length", "0"));
        //Expecting only text for now
        char[] body = new char[contentLength];

        if (contentLength > 0) {
            br.read(body, 0, contentLength);
        }
        return new HttpRequest(requestLine, headers, new String(body).getBytes());
    }

}
