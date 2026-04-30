package noughtsandcrosses;

public class Player {
    private String name = "";
    private char symbol;
    private boolean isAI = false;
    private int wins = 0;

    public Player(char symbol) {
        this.symbol = symbol;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setIsAI(boolean isAI) {
        this.isAI = isAI;
    }

    public String getName() {
        return name;
    }

    public String getNameAndSymbol() {
        return name + " (" + symbol + ")";
    }

    public int getWins() {
        return wins;
    }

    public void incWin() {
        wins++;
    }

    public char getSymbol() {
        return symbol;
    }

    public boolean isAI() {
        return isAI;
    }

    @Override
    public String toString() {
        return String.valueOf(symbol);
    }
}
