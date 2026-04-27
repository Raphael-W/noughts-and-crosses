package uk.ac.soton.comp1206;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.Objects;

/**
 * JavaFX App
 */
public class NoughtsAndCrosses extends Application {
    // CONSTANTS
    private final int gridPadding = 50;
    private final int sceneWidth = 640;
    private final int sceneHeight = 480;

    // ELEMENTS
    public Label bottomLabel;
    public Label scoreLabel;
    public Button resetButton;

    private GameBoardUI gameBoardUI;

    public int getSquareSide() {
        return Math.min(sceneWidth, sceneHeight - 70);
    }

    @Override
    public void start(Stage stage) {
        gameBoardUI = new GameBoardUI(getSquareSide(), gridPadding, 6);

        bottomLabel = new Label();
        scoreLabel = new Label();
        scoreLabel.getStyleClass().add("score-label");

        var bottomPane = new VBox(bottomLabel, scoreLabel);
        var root = new VBox(gameBoardUI.getPane(), bottomPane);

        resetButton = new Button("Reset");
        resetButton.getStyleClass().add("reset-button");
        resetButton.setOnAction(e -> {
            gameBoardUI.reset();
            resetButton.setText("Reset");
        });

        var uiPane = new AnchorPane(resetButton);
        AnchorPane.setTopAnchor(resetButton, 20.0);
        AnchorPane.setLeftAnchor(resetButton, 20.0);
        uiPane.setMouseTransparent(false);
        uiPane.setPickOnBounds(false);

        var stack = new StackPane(root, uiPane);
        var scene = new Scene(stack, sceneWidth, sceneHeight);

        VBox.setMargin(bottomPane, new Insets(20));
        root.setAlignment(Pos.CENTER);
        bottomPane.setAlignment(Pos.BOTTOM_CENTER);
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("styles.css")).toExternalForm());

        gameBoardUI.setOnTurnChange(this::setTurnLabel);
        gameBoardUI.setOnWin(this::setWinLabel);
        gameBoardUI.setOnDraw(this::setDrawLabel);

        setTurnLabel(gameBoardUI.getPlayer());
        updateScoreLabel();

        stage.setScene(scene);
        stage.setResizable(false);
        stage.setTitle("Noughts and Crosses");
        stage.show();
    }

    public void setTurnLabel(Character player) {
        bottomLabel.setText(player + "'s turn");
    }

    public void setWinLabel(Character player) {
        bottomLabel.setText(player + " won!");
        updateScoreLabel();
        resetButton.setText("Rematch");
    }

    public void updateScoreLabel() {
        scoreLabel.setText("X: " + gameBoardUI.getXWins() + "       O: " + gameBoardUI.getOWins());
    }

    public void setDrawLabel() {
        bottomLabel.setText("It's a draw!");
    }

    public static void main(String[] args) {
        launch();
    }

}