import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.http.HttpResponse;
import java.util.Map;
import java.util.function.BiFunction;

public class Main {
    private static final Map<String, BiFunction<BufferedReader, String,  String>> ROUTES = Map.of(
            "/", RequestHandler::home,
            "/echo", RequestHandler::echo,
            "/user-agent", RequestHandler::userAgent);

    public static void main(String[] args) {
        try {
            ServerSocket serverSocket = new ServerSocket(4221);
            System.out.println(" \u2705 Accepting connection..");

            // Since the tester restarts your program quite often, setting SO_REUSEADDR
            // ensures that we don't run into 'Address already in use' errors
            serverSocket.setReuseAddress(true);

            while (true) {
                Socket connection = serverSocket.accept();
                new Thread(() -> {
                    try (connection) {
                        String response = processResponse(connection);
                        writeResponse(connection, response);
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }).start();
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

    private static String processResponse(Socket connection) throws IOException {
        String endPoint;
        String pathVar = "";
        String requestLine;
        String requestPath;
        int endPointIndex = -1;

        BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        requestLine = br.readLine();

        if (requestLine == null)
            return HttpResponseStatus.NOT_FOUND.getResponse();

        //Get the endpoint
        requestPath = requestLine.split(" ")[1];
        endPointIndex = requestPath.indexOf("/", 1);
        endPoint = (endPointIndex == -1) ? requestPath : requestPath.substring(0, endPointIndex);

        //Get the path variable
        if (endPointIndex > -1)
            pathVar = requestPath.substring(endPointIndex + 1);

        //Route to the correct request handler based on endPoint
        if (!ROUTES.containsKey(endPoint))
            return HttpResponseStatus.NOT_FOUND.getResponse();

        return ROUTES.get(endPoint).apply(br, pathVar);
    }
}
