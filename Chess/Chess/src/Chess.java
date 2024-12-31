import Windows.MainWindow;




public class Chess {
    private static final String DEFAULT_SERVER_ADDRESS = "127.0.0.1";
    private static final int DEFAULT_PORT = 12000;

    public static void main(String[] args) {
        String serverAddress = DEFAULT_SERVER_ADDRESS;
        int port = DEFAULT_PORT;

        // Pokud jsou zadány argumenty, použij je
        if (args.length > 0) {
            String[] input = args[0].split("/");
            if (input.length == 2) {
                serverAddress = input[0];
                try {
                    port = Integer.parseInt(input[1]);
                } catch (NumberFormatException e) {
                    System.out.println("Invalid port number. Using default port: " + DEFAULT_PORT);
                }
            } else {
                System.out.println("Invalid format. Use <IP>/<PORT>. Using default values.");
            }
        }

        MainWindow mainWindow = new MainWindow(serverAddress,port);
        mainWindow.createMainWindow();


    }








}
