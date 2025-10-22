import dto.HttpRequest;
import dto.HttpResponse;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.*;
import java.util.stream.Collectors;

public class HttpServer {
    private static final int SERVER_PORT = 4221;
    private static final int SOCKET_TIMEOUT = 10000;

    public static void start() {
        try {
            ServerSocket serverSocket = new ServerSocket(SERVER_PORT);
            serverSocket.setReuseAddress(true);
            acceptConnections(serverSocket);
        } catch (IOException ex) {
            System.out.println("IOException: " + ex.getMessage());
        }
    }

    private static void acceptConnections(ServerSocket serverSocket) {
        System.out.println(" \u2705 Accepting connection..");
        ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();
        while (true) {
            try {
                Socket socket = serverSocket.accept();
                socket.setSoTimeout(SOCKET_TIMEOUT);
                System.out.println(" \u2705 Accepted new connection..");
                processConnection(socket, executor);
            }
            catch (Exception ex) {
                System.out.println("Exception in acceptConnections: " +ex.getMessage());
            }
        }
    }

    private static void processConnection(Socket socket, ExecutorService executor) {
        CompletableFuture.runAsync(() -> {
            try {
                HttpRequest httpRequest = getRequest(socket);
                HttpResponse response = Router.route(httpRequest);
                writeResponse(socket, httpRequest, response);
            } catch (SocketTimeoutException sox) {
                try {
                    socket.close();
                } catch (Exception ex) {
                    System.out.println("Error closing connection: " +ex.getMessage());
                }
                System.out.println("Socket connection has timed out: " +sox.getMessage());
            } catch (IOException ex) {
                System.out.println("Exception in processConnection: " +ex.getMessage());
            }
        }, executor)
        .thenRun(() -> {
            //Keeping connection alive till timeout for persistent connections.
            if (!socket.isClosed() && socket.isConnected()) {
                processConnection(socket, executor);
            }
        });
    }

    private static void writeResponse(Socket connection, HttpRequest httpRequest, HttpResponse response) throws IOException {
        OutputStream out = connection.getOutputStream();
        StringBuilder headers = response.getHeadersStringBuilder();
        boolean closeConnection = false;

        //todo: Refactor. Header shouldn't be modified here
        if (httpRequest.getHeaders().containsKey("Connection")
                && httpRequest.getHeaders().get("Connection").equalsIgnoreCase("close")) {
            headers.append("Connection: close").append("\r\n\r\n");
            closeConnection = true;
        } else {
            headers.append("Connection: keep-alive").append("\r\n\r\n");
        }

        System.out.println("Writing HTTP response to client..");
        out.write(headers.toString().getBytes());
        out.write(response.getBody());
        out.flush();

        if (closeConnection) connection.close();
    }

    /**
     * Build the HttpRequest object from the incoming HTTP request.
     * Parse the text to get requestLine, header and body.
     */
    private static HttpRequest getRequest(Socket connection) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8));
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
