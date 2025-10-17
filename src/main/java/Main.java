import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Set;


public class Main {
    private static final Set<String> validEndpoints = Set.of("echo");

    public static void main(String[] args) {
        try {
            ServerSocket serverSocket = new ServerSocket(4221);
            System.out.println(" \u2705 Accepting connection..");

            // Since the tester restarts your program quite often, setting SO_REUSEADDR
            // ensures that we don't run into 'Address already in use' errors
            serverSocket.setReuseAddress(true);

            while (true) {
               try (Socket connection = serverSocket.accept()) {
                   String response = processResponse(connection);
                   writeResponse(connection, response);
               }
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

    //Todo: Need refactoring
    private static String processResponse(Socket connection) throws IOException {
          var path = "";
          var response = HttpResponseStatus.NOT_FOUND.getResponse();
          BufferedReader br = new BufferedReader(
                  new InputStreamReader(connection.getInputStream())
          );
          String firstLine = br.readLine();
          System.out.println("Printing first line: " +firstLine);

          if(firstLine != null) {
              path = firstLine.split(" ")[1];
          } else {
              return response;
          }

          //Path should have value like /echo/abc
          //Split to get the endpoint and the path variable
          String[] pathContent = path.split("/");

          //If the path is empty, return 200
          if (pathContent.length == 0) {
              return response = HttpResponseStatus.OK.getResponse();
          }

          //Check the endpoint is allowed and we have path variable
          if (pathContent.length >= 3  && validEndpoints.contains(pathContent[1])) {
              String pathVar = pathContent[2];
              String baseResponse = "HTTP/1.1 200 OK\r\nContent-Type: text/plain\r\nContent-Length: %s\r\n\r\n%s";
              response = baseResponse.formatted(pathVar.length(), pathVar);
          }

          return response;
    }
}
