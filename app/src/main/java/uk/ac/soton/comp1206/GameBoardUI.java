package uk.ac.soton.comp1206;

import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;

import java.util.function.Consumer;

import static uk.ac.soton.comp1206.Helpers.*;

public class GameBoardUI {
    private final GameBoard board;
    private final double symbolSize;

    private final double outerPadding;
    private final double sideLength;
    private final int size;

    // ELEMENTS
    private final Button[][] boardButtons;
    private final GridPane boardPane;
    private final Pane overlayPane;

    // CONSUMERS
    private Consumer<Character> onTurnChange;
    private Consumer<Character> onWin;
    private Runnable onDraw;

    public GameBoardUI(double sideLength, double outerPadding, int size) {
        this.outerPadding = outerPadding;
        this.sideLength = sideLength;
        this.size = size;
        this.symbolSize = calculateButtonSize() * 0.25;

        board = new GameBoard(size);
        boardPane = createBoardGridPane(sideLength, outerPadding);
        overlayPane = createBoardPane(boardPane);
        boardButtons = initNoughtsAndCrossesGrid();
    }

    public Pane getPane() {
        return overlayPane;
    }

    public void makeMove(Button button) {
        int x = GridPane.getColumnIndex(button);
        int y = GridPane.getRowIndex(button);

        board.makeMove(x, y);
        Character player = board.getPlayer();

        if (player.equals('X')) {
            drawX(button);

        } else if (player.equals('O')) {
            drawO(button);
        }
    }

    public Button getButton(int x, int y) {
        return boardButtons[x][y];
    }

    public Button getButton(int[] xy) {
        return boardButtons[xy[0]][xy[1]];
    }

    public void reset() {
        board.resetBoard();
        overlayPane.getChildren().removeIf(node -> node != boardPane);
    }

    public void setOnTurnChange(Consumer<Character> listener) {
        this.onTurnChange = listener;
    }

    public void setOnWin(Consumer<Character> listener) {
        this.onWin = listener;
    }

    public void setOnDraw(Runnable listener) {
        this.onDraw = listener;
    }

    public Character getPlayer() {
        return board.getPlayer();
    }

    public int getXWins() {
        return board.getXWins();
    }

    public int getOWins() {
        return board.getOWins();
    }

    private Button[][] initNoughtsAndCrossesGrid() {
        double buttonSize = calculateButtonSize();
        Button[][] gridButtons = new Button[size][size];

        for (int y = 0; y < size; y++) {
            for (int x = 0; x < size; x++) {
                var button = new Button();
                button.setOnAction(this::handleMove);
                button.setPrefSize(buttonSize, buttonSize);
                button.setStyle(calculateBorderStyle(x, y));

                gridButtons[x][y] = button;
                boardPane.add(button, x, y);
            }
        }
        return gridButtons;
    }

    private void handleMove(ActionEvent e) {
        Button button = (Button) e.getSource();
        if (getState(button) == null && board.isGameRunning()) {
            makeMove(button);

            if (board.getRound() == (size * size) - 1) {
                onDraw.run();
                board.gameOver();
            }

            Win win = board.checkWinner();
            if (win != null) {
                onWin.accept(win.winner());

                Button fromButton = getButton(win.fromPos());
                Button toButton = getButton(win.toPos());
                drawWinLine(fromButton, toButton);
                board.gameOver();
            }

            if (board.isGameRunning()) {
                board.switchPlayer();
                onTurnChange.accept(board.getPlayer());
            }
        }
    }

    private GridPane createBoardGridPane(double sideLength, double gridPadding) {
        GridPane gridPane = new GridPane();
        gridPane.setAlignment(Pos.TOP_CENTER);
        gridPane.setPadding(new Insets(gridPadding, gridPadding, 0, gridPadding));
        gridPane.setPrefSize(sideLength, sideLength);
        return gridPane;
    }

    private Pane createBoardPane(GridPane gridPane) {
        Pane overlayPane = new Pane(gridPane);
        overlayPane.setMouseTransparent(false);
        overlayPane.setMaxWidth(sideLength);

        return overlayPane;
    }

    private void drawX(Button button) {
        double buttonX = getGlobalX(button, overlayPane);
        double buttonY = getGlobalY(button, overlayPane);

        addLine(buttonX - symbolSize, buttonY - symbolSize, buttonX + symbolSize, buttonY + symbolSize);
        addLine(buttonX - symbolSize, buttonY + symbolSize, buttonX + symbolSize, buttonY - symbolSize);
    }

    private void drawO(Button button) {
        double buttonX = getGlobalX(button, overlayPane);
        double buttonY = getGlobalY(button, overlayPane);

        addCircle(buttonX, buttonY, symbolSize);
    }

    private Line addLine(double x1, double y1, double x2, double y2) {
        Line line = new Line(x1, y1, x2, y2);
        line.getStyleClass().add("line");
        overlayPane.getChildren().add(line);
        line.setStyle(line.getStyle() + "-fx-stroke-width: " + calculateStrokeWidth() + ";");

        return line;
    }

    private Line addLine(double[] coords) {
        return addLine(coords[0], coords[1], coords[2], coords[3]);
    }

    private Circle addCircle(double x, double y, double r) {
        Circle circle = new Circle(x, y, r);
        circle.getStyleClass().add("circle");
        overlayPane.getChildren().add(circle);
        circle.setStyle(circle.getStyle() + "-fx-stroke-width: " + calculateStrokeWidth() + ";");
        return circle;
    }

    private void drawWinLine(Button from, Button to) {
        double fromX = getGlobalX(from, overlayPane);
        double fromY = getGlobalY(from, overlayPane);

        double toX = getGlobalX(to, overlayPane);
        double toY = getGlobalY(to, overlayPane);

        double[] extendedLine = extendLine(fromX, fromY, toX, toY, symbolSize * 3);
        Line line = addLine(extendedLine);
        line.getStyleClass().add("red");
    }

    private String calculateBorderStyle(int x, int y) {
        String top  = y > 0 ? "line-color" : "transparent";
        String right = x < (size - 1) ? "line-color" : "transparent";
        String bottom = y < (size - 1) ? "line-color" : "transparent";
        String left = x > 0 ? "line-color" : "transparent";

        double strokeWidth = (calculateStrokeWidth() / 2) + 0.5;
        return "-fx-border-color: " + top + " " + right + " " + bottom + " " + left + "; " +
                "-fx-border-width:" + strokeWidth + " " + strokeWidth + " " + strokeWidth + " " + strokeWidth + ";";
    }

    private double calculateButtonSize() {
        return (sideLength - (2 * outerPadding)) / size;
    }

    private double calculateStrokeWidth() {
        return 8 * Math.sqrt((double) 3 / size);
    }

    private Character getState(Button button) {
        int x = GridPane.getColumnIndex(button);
        int y = GridPane.getRowIndex(button);
        return board.getState(x, y);
    }
}
