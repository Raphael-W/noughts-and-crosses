package uk.ac.soton.comp1206;

public class GameBoard {
    private int size;
    private Character[][] grid;
    private Character currentPlayer = 'X';

    // VARIABLES
    private int round = 0;
    private int xWins = 0;
    private int oWins = 0;
    private Character winner;
    private boolean gameOver = false;

    public GameBoard(int size) {
        this.size = size;
        this.grid = new Character[size][size];
    }

    public Character getState(int x, int y) {
        return grid[x][y];
    }

    public Character getPlayer() {
        return currentPlayer;
    }

    public Character getWinner() {
        return winner;
    }

    public int getRound() {
        return round;
    }

    public boolean isGameRunning() {
        return !gameOver;
    }

    public void gameOver() {
        gameOver = true;
    }

    public int getXWins() {
        return xWins;
    }

    public int getOWins() {
        return oWins;
    }

    public void setWinner(Character winner) {
        this.winner = winner;
        if (winner.equals('X')) {
            xWins++;
        }
        else if (winner.equals('O')) {
            oWins++;
        }
    }

    public void resetBoard() {
        grid = new Character[size][size];
        currentPlayer = 'X';
        round = 0;
        winner = null;
        gameOver = false;
    }

    public void switchPlayer() {
        currentPlayer = currentPlayer.equals('X') ? 'O' : 'X';
        round++;
    }

    public void makeMove(int x, int y) {
        if (grid[x][y] == null) {
            grid[x][y] = currentPlayer;
        }
    }

    private int[] compareCells(int x1, int y1, int x2, int y2, int x3, int y3) {
        Character a = grid[x1][y1];
        Character b = grid[x2][y2];
        Character c = grid[x3][y3];

        if (a != null && b != null && c != null) {
            if (a.equals(b) && b.equals(c)) {
                return new int[]{x1,y1, x2,y2, x3,y3};
            }
        }
        return null;
    }

    public int[] checkWinner() {
        int[] comparison;
        // Check rows
        for (int x = 0; x < size; x++) {
            comparison = compareCells(x,0, x,1, x,2);
            if (comparison != null) {
                setWinner(getState(comparison[0], comparison[1]));
                return comparison;
            }
        }

        // Check columns
        for (int y = 0; y < size; y++) {
            comparison = compareCells(0,y, 1,y, 2,y);
            if (comparison != null) {
                setWinner(getState(comparison[0], comparison[1]));
                return comparison;
            }
        }

        // Check top to bottom diagonal
        comparison = compareCells(0,0, 1,1, 2,2);
        if (comparison != null) {
            setWinner(getState(comparison[0], comparison[1]));
            return comparison;
        }

        // Check bottom to top diagonal
        comparison = compareCells(0,2, 1,1, 2,0);
        if (comparison != null) {
            setWinner(getState(comparison[0], comparison[1]));
            return comparison;
        }

        return null;
    }

}
