package Constants;

// Třída Constants slouží jako úložiště pro statické konstantní hodnoty aplikace.
// Tyto hodnoty zahrnují rozměry oken, texty tlačítek a další řetězce, které se používají napříč aplikací.
public class Constants {

    // --- Rozměry hlavního okna ---
    final public static int MainW_WIDTH = 800;  // Šířka hlavního okna
    final public static int MainW_HEIGHT = 800; // Výška hlavního okna

    // --- Rozměry tlačítek ---
    final public static int ReadyB_WIDTH = 200;  // Šířka tlačítka "Ready"
    final public static int ReadyB_HEIGHT = 80;  // Výška tlačítka "Ready"

    // --- Rozměry oken pro různé herní režimy ---
    final public static int SingW_WIDTH = 1200;  // Šířka okna pro Singleplayer
    final public static int SingW_HEIGHT = 1000; // Výška okna pro Singleplayer

    final public static int MultW_WIDTH = 1200;  // Šířka okna pro Multiplayer
    final public static int MultW_HEIGHT = 1000; // Výška okna pro Multiplayer

    // --- Tituly oken ---
    final public static String SingG_title = "Chess - Singleplayer";  // Titulek okna pro Singleplayer
    final public static String MultG_title = "Chess - Multiplayer";  // Titulek okna pro Multiplayer
    final public static String Lobby_title = "Lobby";                // Titulek okna lobby
    final public static String BackButton_title = "← Back to Menu";  // Text tlačítka pro návrat do menu

    // --- Názvy tlačítek v hlavním menu ---
    final public static String Singleplayer_button_name = "Singleplayer"; // Text tlačítka pro režim Singleplayer
    final public static String Multiplayer_button_name = "Multiplayer";  // Text tlačítka pro režim Multiplayer
    final public static String Exit_button_name = "Exit";                // Text tlačítka pro ukončení aplikace

    // --- Texty pro přihlašování ---
    final public static String loginName = "Enter your name: ";  // Popisek pro zadání jména při přihlášení
    public static final String buttonLogin = "Login";            // Text tlačítka pro přihlášení
    final public static String Login_title = "Login";            // Titulek přihlašovacího okna

    // --- Další texty ---
    final public static String Leave_title = "Leave game";  // Text tlačítka pro opuštění hry

    // --- Ostatní ---
    public static final String valueSeparator = ";";  // Oddělovač hodnot, který se používá například v komunikaci s klientem nebo serverem
}
