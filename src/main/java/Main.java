import enums.HttpResponseStatus;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
                        String response = Router.route(httpRequest);
                        writeResponse(connection, response);
                    } catch (IOException ioex) {
                        ioex.printStackTrace();
                    }
                });
            }
        } catch (IOException e) {
            System.out.println("IOException: " + e.getMessage());
        }
    }

    private static void writeResponse(Socket connection, String response) throws IOException {
        OutputStream out = connection.getOutputStream();
        System.out.println(" \u2705 Accepted new connection..");

        System.out.println("Writing HTTP response to client..");
        out.write(response.getBytes());
        out.flush();
    }

    private static HttpRequest getHttpRequest(Socket connection) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        String requestPath = br.readLine().split(" ")[1];

        Map<String, String> headers = br.lines()
                .takeWhile(l -> !l.isEmpty())
                .map(l ->  l.split(":", 2))
                .collect(Collectors.toMap(h -> h[0].trim(), h -> h[1].trim()));

        return new HttpRequest(requestPath, headers);
    }

}
