

public class Main {
    public static void main(String[] args) {
        if (args.length >= 2 && args[0].equalsIgnoreCase("--directory")) {
            if (args[1] != null)
                System.setProperty("directory", args[1]);
        }

        HttpServer.start();
    }
}
