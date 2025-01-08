import Windows.MainWindow;

public class Chess {
    // Výchozí adresa serveru a port
    private static final String DEFAULT_SERVER_ADDRESS = "127.0.0.1";
    private static final int DEFAULT_PORT = 12000;

    public static void main(String[] args) {
        // Nastavení výchozích hodnot pro adresu serveru a port
        String serverAddress = DEFAULT_SERVER_ADDRESS;
        int port = DEFAULT_PORT;

        // Zpracování argumentů příkazového řádku (pokud jsou zadány)
        if (args.length > 0) {
            String[] input = args[0].split("/");  // Předpokládá formát <IP>/<PORT>
            if (input.length == 2) {  // Pokud je argument ve správném formátu
                serverAddress = input[0];  // Nastaví IP adresu serveru
                try {
                    port = Integer.parseInt(input[1]);  // Parsuje port
                } catch (NumberFormatException e) {
                    // Pokud je číslo portu neplatné, použije se výchozí port
                    System.out.println("Invalid port number. Using default port: " + DEFAULT_PORT);
                }
            } else {
                // Pokud argument není ve správném formátu, vypíše se varování
                System.out.println("Invalid format. Use <IP>/<PORT>. Using default values.");
            }
        }

        // Vytvoření hlavního okna aplikace s předanými parametry (IP a port)
        MainWindow mainWindow = new MainWindow(serverAddress, port);
        mainWindow.createMainWindow();  // Vytvoří a zobrazí hlavní okno hry
    }
}
