package uk.ac.soton.comp1206;

import static uk.ac.soton.comp1206.Helpers.*;

public class Bot {
    private GameBoard board;
    private char player;

    public Bot(GameBoard board, char player) {
        this.board = board;
        this.player = player;
    }

    public Coord getNextMove() {
        Coord bestMove = new Coord(-1, -1);
        int bestScore = Integer.MIN_VALUE;

        Coord[] availableSquares = board.getAvailableSquares();
        int maxDepth = calculateOptimalDepth(availableSquares.length);

        for (Coord pos : availableSquares) {
            GameBoard sandBoard = new GameBoard(board);
            sandBoard.makeMove(pos);
            int moveScore = minimax(sandBoard, maxDepth);
            if (moveScore > bestScore) {
                bestScore = moveScore;
                bestMove = pos;
            }
        }
        return bestMove;
    }

    private int calculateOptimalDepth(int availableSquares) {
        return Math.max(5, (int) ((7 * Math.log(16)) / Math.log(availableSquares)));
    }

    private int minimax(GameBoard sandboard, int depth) {
        return minimax(sandboard, false, Integer.MIN_VALUE, Integer.MAX_VALUE, depth);
    }

    private int minimax(GameBoard sandBoard, boolean isMaximising, int alpha, int beta, int depth) {
        State boardState = sandBoard.checkWinner();
        if (boardState.winner() != EMPTY) {
            return boardState.winner() == (player) ? 1000 + depth : -(1000 + depth);
        }

        if (sandBoard.getAvailableSquares().length == 0) return 0;
        if (depth == 0) return (boardState.oNears() - boardState.xNears());

        if (isMaximising) {
            int best = Integer.MIN_VALUE;
            for (Coord pos : sandBoard.getAvailableSquares()) {
                sandBoard.makeMove(pos);
                best = Math.max(best, minimax(sandBoard, false, alpha, beta, depth - 1));
                sandBoard.undoMove(pos);
                alpha = Math.max(alpha, best);
                if (beta <= alpha) break; // prune
            }
            return best;
        } else {
            int best = Integer.MAX_VALUE;
            for (Coord pos : sandBoard.getAvailableSquares()) {
                sandBoard.makeMove(pos);
                best = Math.min(best, minimax(sandBoard, true, alpha, beta, depth - 1));
                sandBoard.undoMove(pos);
                beta = Math.min(beta, best);
                if (beta <= alpha) break; // prune
            }
            return best;
        }
    }
}
