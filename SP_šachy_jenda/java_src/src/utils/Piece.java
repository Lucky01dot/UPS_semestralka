package utils;

import utils.Chessboard;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.File;
import java.io.IOException;

public abstract class Piece {
    protected int col;  // Sloupec na šachovnici (logická pozice)
    protected int row;  // Řádek na šachovnici (logická pozice)
    protected int xPos; // X pozice v pixelech (skutečná pozice)
    protected int yPos; // Y pozice v pixelech (skutečná pozice)
    public boolean isWhite;  // Boolean pro určení, zda je figura bílá
    protected Image image;  // Obrazek reprezentující figuru
    Chessboard chessboard;
    public boolean isfirstmoved = true;  // Určuje, zda byla figura už jednou přesunuta (např. pro rošádu)

    // Konstruktor pro inicializaci figury s pozicí sloupce, řádku, barvy a cesty k obrázku
    public Piece(int col, int row, boolean isWhite, String imagePath, Chessboard chessboard) {
        this.col = col;  // Nastavení sloupce
        this.row = row;  // Nastavení řádku
        this.isWhite = isWhite;  // Nastavení barvy figury
        this.chessboard = chessboard;  // Nastavení odkazu na šachovnici


        // Inicializace pixelových pozic na základě logických pozic
        updatePixelPosition();  // Zavolání metody pro aktualizaci pixelové pozice
        loadImage(imagePath);  // Načtení obrázku pro tuto figuru
    }

    // Metoda pro načtení obrázku z uložené cesty
    private void loadImage(String imagePath) {
        try {
            this.image = ImageIO.read(new File(imagePath));  // Načtení obrázku ze souboru
        } catch (IOException e) {
            System.err.println("Error loading image: " + imagePath);  // Chybová hláška při problému s načítáním
            e.printStackTrace();
        }
    }

    // Metoda pro získání informace, zda je figura bílá
    public boolean isWhite() {
        return isWhite;
    }

    // Metoda pro aktualizaci pixelových pozic figury na základě její logické pozice
    public void updatePixelPosition() {
        this.xPos = col * chessboard.BOXSIZE;  // Výpočet X pozice podle sloupce
        this.yPos = row * chessboard.BOXSIZE;  // Výpočet Y pozice podle řádku
    }

    // Gettery a settery pro logické pozice (sloupec a řádek)
    public int getCol() {
        return col;
    }

    public int getRow() {
        return row;
    }

    // Gettery a settery pro pixelové pozice (x a y)
    public int getXPos() {
        return xPos;
    }

    public void setXPos(int xPos) {
        this.xPos = xPos;
    }

    public int getYPos() {
        return yPos;
    }

    public void setYPos(int yPos) {
        this.yPos = yPos;
    }

    // Metoda pro získání obrázku figury
    public Image getImage() {
        return image;
    }

    // Abstraktní metody, které budou implementovány v konkrétních typech figur (např. Král, Dáma)
    public boolean isValidMovement(int col, int row) { return true; }
    public boolean moveCollidesWithPiece(int col, int row) { return false; }
}
