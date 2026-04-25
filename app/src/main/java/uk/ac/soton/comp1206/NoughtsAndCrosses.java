package uk.ac.soton.comp1206;

import static uk.ac.soton.comp1206.Helpers.*;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.stage.Stage;

import java.util.Objects;


/**
 * JavaFX App
 */
public class NoughtsAndCrosses extends Application {
    private final int gridPadding = 50;

    private final int symbolSize = 20;

    public Character[][] grid = new Character[3][3];
    public Button[][] gridButtons = new Button[3][3];
    public int round = -1;
    public boolean gameOver = false;

    public Label bottomLabel;
    public Pane overlayPane;

    public void initGridPane(GridPane gridPane, double size) {
        gridPane.setAlignment(Pos.TOP_CENTER);
        gridPane.setPadding(new Insets(gridPadding, gridPadding, 0, gridPadding));
        gridPane.setPrefSize(size, size);
    }

    public void setState(Button button, Character state) {
        int x = GridPane.getColumnIndex(button);
        int y = GridPane.getRowIndex(button);

        double buttonX = getGlobalX(button);
        double buttonY = getGlobalY(button);

        button.getStyleClass().removeAll("state-x", "state-o");
        if (state.equals('x')) {
            // Text set out of technicality, but set to transparent
            button.setText(state.toString());

            addLine(buttonX - symbolSize, buttonY - symbolSize, buttonX + symbolSize, buttonY + symbolSize);
            addLine(buttonX - symbolSize, buttonY + symbolSize, buttonX + symbolSize, buttonY - symbolSize);

            grid[x][y] = state;

        } else if (state.equals('o')) {
            // Text set out of technicality, but set to transparent
            button.setText(state.toString());

            addCircle(buttonX, buttonY, symbolSize);

            grid[x][y] = state;
        }
    }

    public Character getState(Button button) {
        int x = GridPane.getColumnIndex(button);
        int y = GridPane.getRowIndex(button);
        return grid[x][y];
    }

    public int[] getWinner() {
        int[] comparison;
        // Check rows
        for (int x = 0; x < 3; x++) {
            comparison = compareCells(grid, x,0, x,1, x,2);
            if (comparison != null) {
                return comparison;
            }
        }

        // Check columns
        for (int y = 0; y < 3; y++) {
            comparison = compareCells(grid, 0,y, 1,y, 2,y);
            if (comparison != null) {
                return comparison;
            }
        }

        // Check top to bottom diagonal
        comparison = compareCells(grid, 0,0, 1,1, 2,2);
        if (comparison != null) {
            return comparison;
        }

        // Check bottom to top diagonal
        comparison = compareCells(grid, 0,2, 1,1, 2,0);
        if (comparison != null) {
            return comparison;
        }

        return null;
    }

    public void nextRound() {
        round++;
        // TODO: Detect when there is a draw (round == 9)
        // TODO: Add a reset button
        bottomLabel.setText(roundToSymbol(round) + "'s round");
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

    public Line addLine(double x1, double y1, double x2, double y2) {
        Line line = new Line(x1, y1, x2, y2);
        line.getStyleClass().add("line");
        overlayPane.getChildren().add(line);
        return line;
    }

    public Line addLine(double[] coords) {
        return addLine(coords[0], coords[1], coords[2], coords[3]);
    }

    public Circle addCircle(double x, double y, double r) {
        Circle circle = new Circle(x, y, r);
        circle.getStyleClass().add("circle");
        overlayPane.getChildren().add(circle);
        return circle;
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
        nextRound();

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
        stage.setTitle("Noughts and Crosses");
        stage.show();
    }

    public class ButtonClickHandler implements EventHandler<ActionEvent> {
        public ButtonClickHandler () {}

        @Override
        public void handle(ActionEvent actionEvent) {
            Button button = (Button) actionEvent.getSource();

            if (getState(button) == null && !gameOver) {
                setState(button, roundToSymbol(round));

                int[] winner = getWinner();
                if (winner != null) {
                    bottomLabel.setText(grid[winner[0]][winner[1]] + " won!");

                    Button fromButton =  gridButtons[winner[0]][winner[1]];
                    double fromX = getGlobalX(fromButton);
                    double fromY = getGlobalY(fromButton);

                    Button toButton =  gridButtons[winner[4]][winner[5]];
                    double toX = getGlobalX(toButton);
                    double toY = getGlobalY(toButton);

                    double[] extendedLine = extendLine(fromX, fromY, toX, toY, 50);
                    addLine(extendedLine);

                    gameOver = true;
                }

                if (round == 8) {
                    bottomLabel.setText("It's a draw!");
                    gameOver = true;
                }

                nextRound();
            }
        }
    }

    public static void main(String[] args) {
        launch();
    }

}