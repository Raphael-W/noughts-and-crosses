package uk.ac.soton.comp1206;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
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
    public int turn = 0;

    public double calculateButtonSize(double width, double height, int padding) {
        return (double) (Math.min(width, height) - (2 * padding)) / 3;
    }

    public String calculateBorderStyle(int x, int y) {
        String top  = y > 0 ? "border-color" : "transparent";
        String right = x < 2 ? "border-color" : "transparent";
        String bottom = y < 2 ? "border-color" : "transparent";
        String left = x > 0 ? "border-color" : "transparent";

        return "-fx-border-color: " + top + " " + right + " " + bottom + " " + left + ";";
    }

    public void initGridPane(GridPane gridPane, double size) {
        gridPane.setPadding(new Insets(gridPadding));
        gridPane.setPrefSize(size, size);
    }

    public void setState(Button button, String state) {
        int x = GridPane.getColumnIndex(button);
        int y = GridPane.getRowIndex(button);

        button.getStyleClass().removeAll("state-x", "state-o");
        if (state.equals("x")) {
            button.setText(cross);
            button.getStyleClass().add("state-x");
            grid[x][y] = state;

        } else if (state.equals("o")) {
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

    private String compareCells(int x1, int y1, int x2, int y2, int x3, int y3) {
        String a = grid[x1][y1];
        String b = grid[x2][y2];
        String c = grid[x3][y3];

        if (a != null && b != null && c != null) {
            if (a.equals(b) && b.equals(c)) {
                return a;
            }
        }
        return null;
    }

    public String getWinner() {
        // Check rows
        for (int x = 0; x < 3; x++) {
            if (compareCells(x,0, x,1, x,2) != null) {
                return grid[x][0];
            }
        }

        // Check columns
        for (int y = 0; y < 3; y++) {
            if (compareCells(0,y, 1,y, 2,y) != null) {
                return grid[0][y];
            }
        }

        // Check top to bottom diagonal
        if (compareCells(0,0, 1,1, 2,2) != null) {
            return grid[0][0];
        }

        // Check bottom to top diagonal
        if (compareCells(0,2, 1,1, 2,0) != null) {
            return grid[0][2];
        }

        return null;
    }

    public void setTurn(int turn) {
        this.turn = turn;
    }

    public void initNoughtsAndCrossesGrid(GridPane gridPane, double buttonSize) {
        for (int y = 0; y < 3; y++) {
            for (int x = 0; x < 3; x++) {
                var button = new Button();
                button.addEventHandler(ActionEvent.ACTION, new ButtonClickHandler());
                button.setPrefSize(buttonSize, buttonSize);
                button.setStyle(calculateBorderStyle(x, y));
                gridPane.add(button, x, y);
            }
        }
    }

    @Override
    public void start(Stage stage) {
        GridPane gridPane = new GridPane();
        GridPane bottomLabel = new GridPane();

        var root = new StackPane(gridPane, bottomLabel);
        var scene = new Scene(root, 640, 480);

        StackPane.setAlignment(gridPane, Pos.CENTER);

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

            if (getState(button) == null) {
                setState(button, (turn == 0 ? "x" : "o"));
                turn = (turn + 1) % 2;

                String winner = getWinner();
                if (winner != null) {
                    System.out.println(winner + " won!");
                }
            }
        }
    }

    public static void main(String[] args) {
        launch();
    }

}