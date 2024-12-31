package utils;

import utils.Chessboard;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.File;
import java.io.IOException;

public abstract class Piece {
    protected int col;  // Column position (logical)
    protected int row;  // Row position (logical)
    protected int xPos; // X position in pixels (actual position)
    protected int yPos; // Y position in pixels (actual position)
    public boolean isWhite;  // Boolean to check if the piece is white
    protected Image image;  // Image of the piece
    Chessboard chessboard;
    public boolean isfirstmoved = true;
    // Constructor to initialize piece with column, row, color, and image path
    public Piece(int col, int row, boolean isWhite, String imagePath, Chessboard chessboard) {
        this.col = col;
        this.row = row;
        this.isWhite = isWhite;
        this.chessboard = chessboard;



        // Initialize pixel positions based on logical position
        updatePixelPosition();  // Use this method instead of direct assignment
        loadImage(imagePath);  // Load the piece's image
    }

    // Method to load image from the file system
    private void loadImage(String imagePath) {
        try {
            this.image = ImageIO.read(new File(imagePath));  // Load image from file
        } catch (IOException e) {
            System.err.println("Error loading image: " + imagePath);
            e.printStackTrace();
        }
    }

    public boolean isWhite() {
        return isWhite;
    }

    // Method to update the pixel positions of the piece
    public void updatePixelPosition() {
        this.xPos = col * chessboard.BOXSIZE;
        this.yPos = row * chessboard.BOXSIZE;
    }

    // Getters and Setters for column and row (logical position)
    public int getCol() {
        return col;
    }

    public int getRow() {
        return row;
    }

    // Getters and Setters for xPos and yPos (pixel position)
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

    // Get the image of the piece
    public Image getImage() {
        return image;
    }
    public boolean isValidMovement(int col, int row) {return true;}
    public boolean moveCollidesWithPiece(int col, int row) {return false;}
}
