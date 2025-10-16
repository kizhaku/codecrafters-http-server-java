import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Main {
  public static void main(String[] args) {
     try {
       ServerSocket serverSocket = new ServerSocket(4221);
       int connectionLife = 2; //Two requests only for this test.

        System.out.println(" \u2705 Accepting connection..");

       // Since the tester restarts your program quite often, setting SO_REUSEADDR
       // ensures that we don't run into 'Address already in use' errors
       serverSocket.setReuseAddress(true);

       while (connectionLife > 0) {
           try (Socket connection = serverSocket.accept()) {
               String response = getResponseFromInput(connection);
               writeResponse(connection, response);
               connectionLife--;
           }
       }
       System.out.println("\uD83D\uDD12 Closing connection..");

     } catch (IOException e) {
       System.out.println("IOException: " + e.getMessage());
     }
  }

  private static String getResponseFromInput(Socket connection) throws IOException {
      String response = "HTTP/1.1 404 Not Found\r\n\r\n";
      String path = "";
      BufferedReader br = new BufferedReader(
              new InputStreamReader(connection.getInputStream())
      );

      String firstLine = br.readLine();

      if (firstLine != null) {
          path = firstLine.split(" ")[1];
      }

      if(path.equalsIgnoreCase("/")) {
          response = "HTTP/1.1 200 OK\r\n\r\n";
      }

      return response;
  }

  private static void writeResponse(Socket connection, String response) throws IOException {
      OutputStream out = connection.getOutputStream();
      System.out.println(" \u2705 Accepted new connection..");

      System.out.println("Writing HTTP response to client..");
      out.write(response.getBytes());
      out.flush();
  }
}
