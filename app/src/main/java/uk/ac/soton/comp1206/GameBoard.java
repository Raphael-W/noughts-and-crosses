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

    private boolean all(Character[] window) {
        if (window.length == 0) return true;
        if (window[0] == null) return false;

        for (int i = 1; i < window.length; i++) {
            if (window[i] == null || !window[0].equals(window[i])) return false;
        }
        return true;
    }

    public int[] checkWinner() {
        int winLength = size > 4 ? size - 2 : size;
        int windowCount = (size - winLength) + 1;

        // Check rows
        for (int y = 0; y < size; y++) {
            for (int startX = 0; startX < windowCount; startX++) {
                Character[] window = new Character[winLength];
                for (int x = 0; x < winLength; x++) {
                    window[x] = getState(x + startX, y);
                }
                if (all(window)) {
                    setWinner(window[0]);
                    return new int[]{startX,y, startX + winLength - 1,y};
                }
            }
        }

        // Check cols
        for (int x = 0; x < size; x++) {
            for (int startY = 0; startY < windowCount; startY++) {
                Character[] window = new Character[winLength];
                for (int y = 0; y < winLength; y++) {
                    window[y] = getState(x, y + startY);
                }
                if (all(window)) {
                    setWinner(window[0]);
                    return new int[]{x, startY, x, startY + winLength - 1};
                }
            }
        }

        // Check top to bottom diagonal
        for (int startY = 0; startY < ((size - winLength) + 1); startY++) {
            for (int startX = 0; startX < windowCount; startX++) {
                Character[] window = new Character[winLength];
                for (int xy = 0; xy < winLength; xy++) {
                    window[xy] = getState(startX + xy, startY + xy);
                }
                if (all(window)) {
                    setWinner(window[0]);
                    return new int[]{startX,startY, startX + winLength - 1,startY + winLength - 1};
                }
            }
        }

        // Check bottom to left diagonal
        for (int startY = size - 1; startY >= ((size - winLength) + 1); startY--) {
            for (int startX = 0; startX < windowCount; startX++) {
                Character[] window = new Character[winLength];
                for (int xy = 0; xy < winLength; xy++) {
                    window[xy] = getState(startX + xy, startY - xy);
                }
                if (all(window)) {
                    setWinner(window[0]);
                    return new int[]{startX,startY, startX + winLength - 1,startY - winLength + 1};
                }
            }
        }

        return null;
    }

}
