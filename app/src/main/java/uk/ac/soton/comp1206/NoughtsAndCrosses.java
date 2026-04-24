package uk.ac.soton.comp1206;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Line;
import javafx.stage.Stage;

import java.util.Objects;


/**
 * JavaFX App
 */
public class NoughtsAndCrosses extends Application {
    private final int gridPadding = 50;

    private final String nought = "⭘";
    private final String cross = "✕";

    public String[][] grid = new String[3][3];
    public Button[][] gridButtons = new Button[3][3];
    public int turn = 0;
    public boolean gameOver = false;

    public Label bottomLabel;
    public Pane overlayPane;

    public double calculateButtonSize(double width, double height, int padding) {
        return (Math.min(width, height - 70) - (2 * padding)) / 3;
    }

    public String calculateBorderStyle(int x, int y) {
        String top  = y > 0 ? "border-color" : "transparent";
        String right = x < 2 ? "border-color" : "transparent";
        String bottom = y < 2 ? "border-color" : "transparent";
        String left = x > 0 ? "border-color" : "transparent";

        return "-fx-border-color: " + top + " " + right + " " + bottom + " " + left + ";";
    }

    public void initGridPane(GridPane gridPane, double size) {
        gridPane.setAlignment(Pos.TOP_CENTER);
        gridPane.setPadding(new Insets(gridPadding, gridPadding, 0, gridPadding));
        gridPane.setPrefSize(size, size);
    }

    public void setState(Button button, String state) {
        int x = GridPane.getColumnIndex(button);
        int y = GridPane.getRowIndex(button);

        button.getStyleClass().removeAll("state-x", "state-o");
        if (state.equals(cross)) {
            button.setText(cross);
            button.getStyleClass().add("state-x");
            grid[x][y] = state;

        } else if (state.equals(nought)) {
            button.setText(nought);
            button.getStyleClass().add("state-o");
            grid[x][y] = state;
        }
    }

    public String getState(Button button) {
        int x = GridPane.getColumnIndex(button);
        int y = GridPane.getRowIndex(button);
        return grid[x][y];
    }

    private int[] compareCells(int x1, int y1, int x2, int y2, int x3, int y3) {
        String a = grid[x1][y1];
        String b = grid[x2][y2];
        String c = grid[x3][y3];

        if (a != null && b != null && c != null) {
            if (a.equals(b) && b.equals(c)) {
                return new int[]{x1,y1, x2,y2, x3,y3};
            }
        }
        return null;
    }

    public int[] getWinner() {
        int[] comparison;
        // Check rows
        for (int x = 0; x < 3; x++) {
            comparison = compareCells(x,0, x,1, x,2);
            if (comparison != null) {
                return comparison;
            }
        }

        // Check columns
        for (int y = 0; y < 3; y++) {
            comparison = compareCells(0,y, 1,y, 2,y);
            if (comparison != null) {
                return comparison;
            }
        }

        // Check top to bottom diagonal
        comparison = compareCells(0,0, 1,1, 2,2);
        if (comparison != null) {
            return comparison;
        }

        // Check bottom to top diagonal
        comparison = compareCells(0,2, 1,1, 2,0);
        if (comparison != null) {
            return comparison;
        }

        return null;
    }

    public String turnToSymbol(int turn) {
        return (turn == 0 ? cross : nought);
    }

    public void setTurn(int turn) {
        this.turn = turn;
        bottomLabel.setText(turnToSymbol(turn) + "'s turn");
    }

    public double[] extendLine(double x1, double y1, double x2, double y2, int extension) {
        double dx = x2 - x1;
        double dy = y2 - y1;
        double length = Math.sqrt(dx * dx + dy * dy);

        dx /= length;
        dy /= length;

        return new double[]{
                x1 - (dx * extension),
                y1 - (dy * extension),
                x2 + (dx * extension),
                y2 + (dy * extension)};
    }

    public void initNoughtsAndCrossesGrid(GridPane gridPane, double buttonSize) {
        for (int y = 0; y < 3; y++) {
            for (int x = 0; x < 3; x++) {
                var button = new Button();
                button.addEventHandler(ActionEvent.ACTION, new ButtonClickHandler());
                button.setPrefSize(buttonSize, buttonSize);
                button.setStyle(calculateBorderStyle(x, y));

                gridButtons[x][y] = button;
                gridPane.add(button, x, y);
            }
        }
    }

    @Override
    public void start(Stage stage) {
        GridPane gridPane = new GridPane();

        bottomLabel = new Label();
        setTurn(turn);

        var root = new VBox(gridPane, bottomLabel);
        overlayPane = new Pane();
        overlayPane.setMouseTransparent(true);
        var stack = new StackPane(root, overlayPane);
        var scene = new Scene(stack, 640, 480);

        VBox.setMargin(bottomLabel, new Insets(40));
        root.setAlignment(Pos.CENTER);
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("styles.css")).toExternalForm());

        double squareSide = Math.min(scene.getWidth(), scene.getHeight());
        double buttonSize = calculateButtonSize(squareSide, squareSide, gridPadding);
        initGridPane(gridPane, squareSide);
        initNoughtsAndCrossesGrid(gridPane, buttonSize);

        stage.setScene(scene);
        stage.setResizable(false);
        stage.setTitle("Naughts and Crosses");
        stage.show();
    }

    public class ButtonClickHandler implements EventHandler<ActionEvent> {
        public ButtonClickHandler () {}

        @Override
        public void handle(ActionEvent actionEvent) {
            Button button = (Button) actionEvent.getSource();

            if (getState(button) == null && !gameOver) {
                setState(button, turnToSymbol(turn));
                setTurn((turn + 1) % 2);

                int[] winner = getWinner();
                if (winner != null) {
                    bottomLabel.setText(grid[winner[0]][winner[1]] + " won!");

                    Button fromButton =  gridButtons[winner[0]][winner[1]];
                    Bounds fromBounds = fromButton.localToScene(fromButton.getBoundsInLocal());
                    double fromX = fromBounds.getCenterX();
                    double fromY = fromBounds.getCenterY();

                    Button toButton =  gridButtons[winner[4]][winner[5]];
                    Bounds toBounds = toButton.localToScene(toButton.getBoundsInLocal());
                    double toX = toBounds.getCenterX();
                    double toY = toBounds.getCenterY();

                    double[] ex = extendLine(fromX, fromY, toX, toY, 50);

                    Line line = new Line(ex[0], ex[1], ex[2], ex[3]);
                    line.getStyleClass().add("line");
                    overlayPane.getChildren().add(line);

                    gameOver = true;
                }
            }
        }
    }

    public static void main(String[] args) {
        launch();
    }

}