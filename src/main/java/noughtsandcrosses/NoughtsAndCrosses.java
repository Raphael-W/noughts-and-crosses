package noughtsandcrosses;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
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

    // VARIABLES
    Player xPlayer = new Player('X');
    Player oPlayer = new Player('O');

    int gridSize = 3;

    // ELEMENTS
    public Label bottomLabel;
    public Label scoreLabel;
    public Button resetButton;

    private GameBoardUI gameBoardUI;

    public int getSquareSide() {
        return Math.min(sceneWidth, sceneHeight - 70);
    }

    public Scene createGameScene(Stage stage) {
        gameBoardUI = new GameBoardUI(xPlayer, oPlayer, getSquareSide(), gridPadding, gridSize);

        bottomLabel = new Label();
        scoreLabel = new Label();
        scoreLabel.getStyleClass().add("score-label");

        var bottomPane = new VBox(bottomLabel, scoreLabel);
        var root = new VBox(gameBoardUI.getPane(), bottomPane);

        resetButton = new Button("Reset");
        resetButton.setOnAction(e -> {
            gameBoardUI.reset();
            resetButton.setText("Reset");
        });

        Button closeButton = new Button("X");
        closeButton.setOnAction(e -> {
            gameBoardUI.cancelAIMoves();
            stage.setScene(createMenuScene(stage));
        });

        var uiPane = new AnchorPane(resetButton, closeButton);
        uiPane.setMouseTransparent(false);
        uiPane.setPickOnBounds(false);

        AnchorPane.setTopAnchor(resetButton, 20.0);
        AnchorPane.setLeftAnchor(resetButton, 20.0);

        AnchorPane.setTopAnchor(closeButton, 20.0);
        AnchorPane.setRightAnchor(closeButton, 20.0);


        var stack = new StackPane(root, uiPane);
        var scene = new Scene(stack, sceneWidth, sceneHeight);

        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("styles.css")).toExternalForm());

        VBox.setMargin(bottomPane, new Insets(20));
        root.setAlignment(Pos.CENTER);
        bottomPane.setAlignment(Pos.BOTTOM_CENTER);

        gameBoardUI.setOnTurnChange(e -> setTurnLabel(getPlayer(e)));
        gameBoardUI.setOnWin(e -> setWinLabel(getPlayer(e)));
        gameBoardUI.setOnDraw(this::setDrawLabel);
        gameBoardUI.start();

        setTurnLabel(gameBoardUI.getPlayer());
        updateScoreLabel();

        return scene;
    }

    private HBox buildPlayerRow(Player player) {
        Label playerLabel = new Label(player + ":");
        playerLabel.setPrefWidth(70);

        TextField nameField = new TextField();
        nameField.getStyleClass().add("player-input");
        nameField.setPromptText("Name");
        HBox.setHgrow(nameField, Priority.ALWAYS);
        nameField.setUserData(player.getSymbol());
        nameField.textProperty().addListener((obs, oldVal, newVal) -> {
            if ((char) nameField.getUserData() == 'X') xPlayer.setName(newVal);
            if ((char) nameField.getUserData() == 'O') oPlayer.setName(newVal);
        });

        CheckBox aiBox = new CheckBox("AI");
        aiBox.setUserData(player.getSymbol());
        aiBox.setOnAction(e -> {
            boolean checked = aiBox.isSelected();
            if ((char) aiBox.getUserData() == 'X') xPlayer.setIsAI(checked);
            if ((char) aiBox.getUserData() == 'O') oPlayer.setIsAI(checked);
            nameField.setDisable(checked);

            if (checked) {
                nameField.setText("AI");
            }
            else {
                nameField.setText("");
            };
        });

        HBox row = new HBox(10, playerLabel, nameField, aiBox);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(10, 16, 10, 16));
        row.setMaxWidth(420);
        return row;
    }

    public Scene createMenuScene(Stage stage) {
        AnchorPane root = new AnchorPane();

        Label title = new Label("Menu");
        Label subtitle = new Label("Configure players and game below");
        subtitle.getStyleClass().add("subtitle");

        VBox titleGroup = new VBox(4, title, subtitle);
        titleGroup.setAlignment(Pos.CENTER);

        StackPane topCenter = new StackPane(titleGroup);
        AnchorPane.setTopAnchor(topCenter, 20.0);
        AnchorPane.setLeftAnchor(topCenter, 0.0);
        AnchorPane.setRightAnchor(topCenter, 0.0);

        ToggleGroup sizeGroup = new ToggleGroup();
        Label gridLabel = new Label("Grid:");
        HBox.setMargin(gridLabel, new Insets(0, 12, 0, 0));
        HBox sizeSelector = new HBox(8, gridLabel);
        VBox.setMargin(sizeSelector, new Insets(20, 0, 0, 0));

        for (int i = 3; i <= 6; i++) {
            ToggleButton btn = new ToggleButton(i + " x " + i);
            btn.setUserData(i);
            btn.setOnAction(e -> gridSize = (int) ((ToggleButton) e.getSource()).getUserData());
            btn.setToggleGroup(sizeGroup);
            btn.setPrefWidth(80);
            if (i == 3) btn.setSelected(true); // default
            sizeSelector.getChildren().add(btn);
        }

        VBox playerRows = new VBox(12, buildPlayerRow(xPlayer), buildPlayerRow(oPlayer), sizeSelector);
        playerRows.setAlignment(Pos.CENTER);

        StackPane middle = new StackPane(playerRows);
        AnchorPane.setTopAnchor(middle, 0.0);
        AnchorPane.setBottomAnchor(middle, 0.0);
        AnchorPane.setLeftAnchor(middle, 40.0);
        AnchorPane.setRightAnchor(middle, 40.0);

        // --- Play button (bottom-right) ---
        Button playBtn = new Button("Play");
        playBtn.getStyleClass().add("filled");
        AnchorPane.setBottomAnchor(playBtn, 32.0);
        AnchorPane.setRightAnchor(playBtn, 32.0);
        playBtn.setOnAction(e -> {
            if (!xPlayer.getName().isEmpty() && !oPlayer.getName().isEmpty()) stage.setScene(createGameScene(stage));
        });

        root.getChildren().addAll(topCenter, middle, playBtn);

        Scene scene = new Scene(root, sceneWidth, sceneHeight);
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("styles.css")).toExternalForm());
        return scene;
    }

    public void play() {

    }

    @Override
    public void start(Stage stage) {
        Scene menuScene = createMenuScene(stage);

        stage.setScene(menuScene);
        stage.setResizable(false);
        stage.setTitle("Noughts and Crosses");
        stage.show();
    }

    public void setTurnLabel(Player player) {
        if (player.isAI()) bottomLabel.setText(player.getName() + " is thinking...");
        else bottomLabel.setText(player.getName() + "'s turn");
    }

    public void setWinLabel(Player player) {
        bottomLabel.setText(player.getName() + " won!");
        resetButton.setText("Rematch");
        player.incWin();
        updateScoreLabel();
    }

    public void updateScoreLabel() {
        scoreLabel.setText(xPlayer.getName() + ": " + xPlayer.getWins() + "       " + oPlayer.getName() + ": " + oPlayer.getWins());
    }

    public void setDrawLabel() {
        bottomLabel.setText("It's a draw!");
        resetButton.setText("Rematch");

    }

    public Player getPlayer(char player) {
        if (player == 'X') return xPlayer;
        return oPlayer;
    }

    public static void main(String[] args) {
        launch();
    }

}