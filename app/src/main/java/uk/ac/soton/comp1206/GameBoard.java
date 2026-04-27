package uk.ac.soton.comp1206;

import java.util.Optional;

public class GameBoard {
    private int size;
    private Character[][] grid;
    private Character currentPlayer = 'X';

    // VARIABLES
    private int round = 0;
    private int xWins = 0;
    private int oWins = 0;
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

    private boolean all(Character[] window) {
        if (window.length == 0) return true;
        if (window[0] == null) return false;

        for (int i = 1; i < window.length; i++) {
            if (window[i] == null || !window[0].equals(window[i])) return false;
        }
        return true;
    }

    public Win checkWinner() {
        int winLength = size > 4 ? size - 2 : size;
        int windowCount = (size - winLength) + 1;

        // Check rows
        for (int startY = 0; startY < size; startY++) {
            for (int startX = 0; startX < windowCount; startX++) {
                Character[] window = new Character[winLength];
                for (int offset = 0; offset < winLength; offset++) {
                    window[offset] = getState(startX + offset, startY);
                }
                if (all(window)) {
                    setWinner(window[0]);
                    return createWin(startX, startY, startX + winLength - 1, startY);
                }
            }
        }

        // Check cols
        for (int startX = 0; startX < size; startX++) {
            for (int startY = 0; startY < windowCount; startY++) {
                Character[] window = new Character[winLength];
                for (int offset = 0; offset < winLength; offset++) {
                    window[offset] = getState(startX, offset + startY);
                }
                if (all(window)) {
                    setWinner(window[0]);
                    return createWin(startX, startY, startX, startY + winLength - 1);
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
                    return createWin(startX,startY, startX + winLength - 1,startY + winLength - 1);
                }
            }
        }

        // Check bottom to left diagonal
        for (int startY = size - 1; startY >= winLength - 1; startY--) {
            for (int startX = 0; startX < windowCount; startX++) {
                Character[] window = new Character[winLength];
                for (int xy = 0; xy < winLength; xy++) {
                    window[xy] = getState(startX + xy, startY - xy);
                }
                if (all(window)) {
                    setWinner(window[0]);
                    return createWin(startX,startY, startX + winLength - 1,startY - winLength + 1);
                }
            }
        }

        return null;
    }

    public Win createWin(int fromX, int fromY, int toX, int toY) {
        Character winner = getState(fromX, fromY);
        return new Win(winner, new int[]{fromX, fromY}, new int[]{toX, toY});
    }

}
