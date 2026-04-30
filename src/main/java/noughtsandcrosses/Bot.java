package noughtsandcrosses;

import static noughtsandcrosses.Helpers.*;

public class Bot {
    private GameBoard board;

    public Bot(GameBoard board) {
        this.board = board;
    }

    // Calls the minimax algorithm on each of the available squares to evaluate how 'good' each move would be
    public Coord getNextMove() {
        Coord bestMove = new Coord(-1, -1);
        int bestScore = Integer.MIN_VALUE;
        int bestOpponentNears = Integer.MAX_VALUE;

        Coord[] availableSquares = board.getAvailableSquares();
        int maxDepth = calculateOptimalDepth(availableSquares.length);

        for (Coord pos : availableSquares) {
            GameBoard sandBoard = new GameBoard(board);
            sandBoard.makeMove(pos);
            int moveScore = minimax(sandBoard, maxDepth);
            State postMove = sandBoard.checkWinner();
            int opponentNears = board.getPlayer() == 'O' ? postMove.xNears() : postMove.oNears();

            // In a guaranteed loss, it still tries to win, rather than giving up
            if (moveScore > bestScore || (moveScore == bestScore && opponentNears < bestOpponentNears)) {
                bestScore = moveScore;
                bestOpponentNears = opponentNears;
                bestMove = pos;
            }
        }
        return bestMove;
    }

    // Calculates the maximum search depth without being toooo slow
    private int calculateOptimalDepth(int availableSquares) {
        // Uses the idea that at a recursion depth of 7 on a 4x4 grid, it was an acceptable speed
        return Math.max(5, (int) ((7 * Math.log(16)) / Math.log(availableSquares)));
    }

    // Sets default value
    private int minimax(GameBoard sandboard, int depth) {
        return minimax(sandboard, false, Integer.MIN_VALUE, Integer.MAX_VALUE, depth);
    }

    private int minimax(GameBoard sandBoard, boolean isMaximising, int alpha, int beta, int depth) {
        State boardState = sandBoard.checkWinner();
        if (boardState.winner() != EMPTY) {
            // Using the depth as a score encourages faster wins and slower losses
            return boardState.winner() == (board.getPlayer()) ? 1000 + depth : -(1000 + depth);
        }

        if (sandBoard.getAvailableSquares().length == 0) return 0;
        if (depth == 0) return (boardState.oNears() - boardState.xNears()); //oNears - xNears is a heuristic to approximate who's winning

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
