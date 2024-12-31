package Windows;

import enums.States;

public class Client {
    private String name;
    private boolean isWhite;
    private boolean ready; // Readiness status of the player
    Game game;

    // Constructor
    public Client(String name, boolean isWhite, boolean ready,Game game) {
        this.name = name;
        this.isWhite = isWhite;
        this.ready = ready;
        this.game = game;

    }



    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Game getGame() {
        return game;
    }
    public void setGame(Game game) {
        this.game = game;
    }

    public boolean isReady() {
        return ready;
    }

    public void setReady(boolean ready) {
        this.ready = ready;
    }

    public boolean toggleReadyStatus() {
        this.ready = !this.ready;
        return this.ready;
    }

    public void setWhite(boolean white) {
        isWhite = white;
    }

    public boolean isWhite() {
        return isWhite;
    }
}
