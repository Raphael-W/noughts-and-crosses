package uk.ac.soton.comp1206;

import java.util.ArrayList;
import java.util.Arrays;
import static uk.ac.soton.comp1206.Helpers.*;


public class GameBoard {
    private int size;
    private char[][] grid;
    private char currentPlayer = 'X';

    // VARIABLES
    private int round = 0;
    private int xWins = 0;
    private int oWins = 0;
    private boolean gameOver = false;

    public GameBoard(int size) {
        this.size = size;
        this.grid = new char[size][size];
    }

    public GameBoard(GameBoard existingBoard) {
        this.size = existingBoard.size;
        this.currentPlayer = existingBoard.currentPlayer == 'X' ? 'X' : 'O';

        this.grid = new char[existingBoard.size][existingBoard.size];
        for (int i = 0; i < existingBoard.size; i++) {
            this.grid[i] = Arrays.copyOf(existingBoard.grid[i], existingBoard.size);
        }
    }

    public char getState(int x, int y) {
        return grid[x][y];
    }

    public char getPlayer() {
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

    public void setWinner(char winner) {
        if (winner == 'X') {
            xWins++;
        }
        else if (winner == 'O') {
            oWins++;
        }
    }

    public void resetBoard() {
        grid = new char[size][size];
        currentPlayer = 'X';
        round = 0;
        gameOver = false;
    }

    public void switchPlayer() {
        currentPlayer = currentPlayer == 'X' ? 'O' : 'X';
        round++;
    }

    public void makeMove(Coord pos) {
        if (grid[pos.x][pos.y] == EMPTY) {
            grid[pos.x][pos.y] = currentPlayer;
            switchPlayer();
        }
    }

    public void undoMove(Coord pos) {
        grid[pos.x][pos.y] = EMPTY;
        switchPlayer();
    }

    private int same(char[] window) {
        int x = 0, o = 0;
        for (char c : window) {
            if (c == 'X') x++;
            else if (c == 'O') o++;
        }
        if (x > 0 && o > 0) return 0; // mixed, no near win
        return x > 0 ? x : -o; // positive = X count, negative = O count
    }

    public State checkWinner(boolean debug) {
        int winLength = size > 4 ? size - 2 : size;
        int windowCount = (size - winLength) + 1;

        int[] winningMove = null;
        int xNearWins = 0;
        int oNearWins = 0;

        int count, absCount;
        char player;
        char[] window;

        // Check rows
        for (int startY = 0; startY < size; startY++) {
            for (int startX = 0; startX < windowCount; startX++) {
                window = new char[winLength];
                for (int offset = 0; offset < winLength; offset++) {
                    window[offset] = getState(startX + offset, startY);
                }

                count = same(window);
                absCount = Math.abs(count);
                player = count > 0 ? 'X' : 'O';

                if (absCount == winLength - 1) {
                    if (player == 'X') xNearWins++;
                    else oNearWins++;
                }
                else if (absCount == winLength) {
                    setWinner(player);
                    winningMove = new int[]{startX, startY, startX + winLength - 1, startY};
                }
            }
        }

        // Check cols
        for (int startX = 0; startX < size; startX++) {
            for (int startY = 0; startY < windowCount; startY++) {
                window = new char[winLength];
                for (int offset = 0; offset < winLength; offset++) {
                    window[offset] = getState(startX, offset + startY);
                }

                count = same(window);
                absCount = Math.abs(count);
                player = count > 0 ? 'X' : 'O';

                if (absCount == winLength - 1) {
                    if (player == 'X') xNearWins++;
                    else oNearWins++;
                }
                else if (absCount == winLength) {
                    setWinner(player);
                    winningMove = new int[]{startX, startY, startX, startY + winLength - 1};
                }
            }
        }

        // Check top to bottom diagonal
        for (int startY = 0; startY < ((size - winLength) + 1); startY++) {
            for (int startX = 0; startX < windowCount; startX++) {
                window = new char[winLength];
                for (int xy = 0; xy < winLength; xy++) {
                    window[xy] = getState(startX + xy, startY + xy);
                }

                count = same(window);
                absCount = Math.abs(count);
                player = count > 0 ? 'X' : 'O';

                if (absCount == winLength - 1) {
                    if (player == 'X') xNearWins++;
                    else oNearWins++;
                }
                else if (absCount == winLength) {
                    setWinner(player);
                    winningMove = new int[]{startX,startY, startX + winLength - 1,startY + winLength - 1};
                }
            }
        }

        // Check bottom to left diagonal
        for (int startY = size - 1; startY >= winLength - 1; startY--) {
            for (int startX = 0; startX < windowCount; startX++) {
                window = new char[winLength];
                for (int xy = 0; xy < winLength; xy++) {
                    window[xy] = getState(startX + xy, startY - xy);
                }

                count = same(window);
                absCount = Math.abs(count);
                player = count > 0 ? 'X' : 'O';

                if (absCount == winLength - 1) {
                    if (player == 'X') xNearWins++;
                    else oNearWins++;
                }
                else if (absCount == winLength) {
                    setWinner(player);
                    winningMove = new int[]{startX,startY, startX + winLength - 1,startY - winLength + 1};
                }
            }
        }

        return createState(winningMove, xNearWins, oNearWins);
    }

    private State createState(int[] moves, int xNears, int oNears) {
        if (moves == null) {
            return new State(EMPTY, null, null, xNears, oNears);
        }
        char winner = getState(moves[0], moves[1]);
        return new State(winner, new Coord(moves[0], moves[1]), new Coord(moves[2], moves[3]), xNears, oNears);
    }

    public Coord[] getAvailableSquares() {
        ArrayList<Coord> freeMoves = new ArrayList<>();
        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
                if (getState(x, y) == EMPTY) {
                    freeMoves.add(new Coord(x, y));
                }
            }
        }

        return freeMoves.toArray(new Coord[0]);
    }

}
