package enums;

public enum Type {
    PAWN("pawn"),
    KNIGHT("knight"),
    BISHOP("bishop"),
    ROOK("rook"),
    QUEEN("queen"),
    KING("king");

    private final String name;

    Type(String name) {
        this.name = name;
    }

    public String getTypeName() {
        return this.name;
    }

    @Override
    public String toString() {
        return this.name;
    }
}
