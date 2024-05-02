package com.example.poddavki_project;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class View extends Application implements IView
{
    public Presenter presenter;
    public View()
    {
        presenter = new Presenter(this);
    }
    public static final  int TILE_SIZE = 100;
    public static final  int WIDTH = 8;
    public static final int HEIGHT = 8;


    // if any problems are cause change from final to normal
    public final Tile[][] boardVisual = new Tile[WIDTH][HEIGHT];
    public final Group tileGroup = new Group();
    public final Group pieceGroup = new Group();

    public PieceType currentPlayer = PieceType.WHITE;

    public boolean ai = false;
    public boolean won = false;
    public final Stage stageWon = new Stage();

    // Function that creates a scene
    public Parent createContentDuel()
    {
        Pane root = new Pane();

        // Sets the size of the game board
        root.setPrefSize(WIDTH * TILE_SIZE, HEIGHT * TILE_SIZE);

        root.getChildren().addAll(tileGroup, pieceGroup);

        // Sets the colors of the tile
        for (int y = 0; y < HEIGHT; y++)
        {
            for (int x = 0; x < WIDTH; x++)
            {
                // Sets a tile for each area of the board and sets the accordingly
                Tile tile = new Tile((x+y) % 2 == 0, x, y);

                boardVisual[x][y] = tile;
                // Adds a tile to the group of tiles
                tileGroup.getChildren().add(tile);
                Piece piece = null;

                // Checks if it's the upper half of the board the tile isn't a white tile
                if(y <= 2 && (x+y) % 2 != 0)
                {
                    piece = presenter.makePiece(PieceType.BLACK, x ,y);
                }

                // Checks if it's the lower half of the board the tile isn't a white tile
                if(y >= 5 && (x+y) % 2 != 0)
                {
                    piece = presenter.makePiece(PieceType.WHITE, x ,y);
                }

                // Adds the piece to the current tile if it isn't null
                if(piece != null)
                {
                    tile.setPiece(piece);

                    // Adds the piece to the group of pieces
                    pieceGroup.getChildren().add(piece);
                }
            }
        }
        return root;
    }


    public Parent blackWinScene() {
        Pane root = new Pane();
        root.setPrefSize(WIDTH * TILE_SIZE, HEIGHT * TILE_SIZE);

        // Create a button to indicate black winning
        Button blackWonButton = new Button("BLACK WON");
        blackWonButton.setStyle("-fx-background-color: #121212; -fx-text-fill: #e8c309; -fx-font-size: 40px; -fx-background-radius: 5px; -fx-border-color: #e8c309; -fx-border-width: 4px;");
        blackWonButton.setPrefSize(300, 300);
        blackWonButton.setLayoutX((WIDTH * TILE_SIZE - blackWonButton.getPrefWidth()) / 2);
        blackWonButton.setLayoutY((HEIGHT * TILE_SIZE - blackWonButton.getPrefHeight()) / 2);

        // Add the button to the root pane
        root.getChildren().add(blackWonButton);

        return root;
    }

    public Parent whiteWinScene() {
        Pane root = new Pane();
        root.setPrefSize(WIDTH * TILE_SIZE, HEIGHT * TILE_SIZE);

        // Create a button to indicate black winning
        Button blackWonButton = new Button("WHITE WON");
        blackWonButton.setStyle("-fx-background-color: #E8E8E8; -fx-text-fill: #e8c309; -fx-font-size: 40px; -fx-background-radius: 5px; -fx-border-color: #e8c309; -fx-border-width: 4px;");
        blackWonButton.setPrefSize(300, 300);
        blackWonButton.setLayoutX((WIDTH * TILE_SIZE - blackWonButton.getPrefWidth()) / 2);
        blackWonButton.setLayoutY((HEIGHT * TILE_SIZE - blackWonButton.getPrefHeight()) / 2);

        // Add the button to the root pane
        root.getChildren().add(blackWonButton);

        return root;
    }

    public Parent tieScene() {
        Pane root = new Pane();
        root.setPrefSize(WIDTH * TILE_SIZE, HEIGHT * TILE_SIZE);

        // Create a button to indicate black winning
        Button blackWonButton = new Button("TIE");
        blackWonButton.setStyle("-fx-background-color: #949494; -fx-text-fill: #e8c309; -fx-font-size: 40px; -fx-background-radius: 5px; -fx-border-color: #e8c309; -fx-border-width: 4px;");
        blackWonButton.setPrefSize(300, 300);
        blackWonButton.setLayoutX((WIDTH * TILE_SIZE - blackWonButton.getPrefWidth()) / 2);
        blackWonButton.setLayoutY((HEIGHT * TILE_SIZE - blackWonButton.getPrefHeight()) / 2);

        // Add the button to the root pane
        root.getChildren().add(blackWonButton);

        return root;
    }

    public int toBoard(double pixel)
    {
        return (int)(pixel + TILE_SIZE / 2) / TILE_SIZE;
    }
    public void start(Stage stage)
    {
// Create buttons with inline styles
        Button aiButton = new Button("🤖");
        aiButton.setStyle("-fx-background-color: #3f51b5; -fx-text-fill: white; -fx-font-size: 40px; -fx-background-radius: 5px; -fx-border-color: black; -fx-border-width: 3px;");

        Button duelButton = new Button("⚔");
        duelButton.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-font-size: 40px; -fx-background-radius: 5px; -fx-border-color: black; -fx-border-width: 3px;");

        stage.setMinHeight(HEIGHT * TILE_SIZE); // Set minimum height
        stage.setMinWidth(WIDTH * TILE_SIZE);  // Set minimum width

// Create a layout to hold the buttons with inline styles
        HBox buttonLayout = new HBox(100); // 10 for spacing between buttons
        buttonLayout.getChildren().addAll(aiButton, duelButton);
        buttonLayout.setStyle("-fx-alignment: center;");

        // Set initial scene to a temporary "main menu" with the buttons
        Scene scene = new Scene(buttonLayout);
        stage.setTitle("Poddavki");
        stage.setScene(scene);
        stage.show();

        // Handle button actions
        aiButton.setOnAction(e -> handleAIButtonClick(stage));
        duelButton.setOnAction(e -> handleDuelButtonClick(stage));
    }
    public void handleAIButtonClick(Stage stage) {
        // Create a new scene for an AI
        ai = true;
        Scene aiScene = new Scene(createContentDuel());
        stage.setScene(aiScene);
    }

    public void handleDuelButtonClick(Stage stage) {
        Scene duelScene = new Scene(createContentDuel());
        stage.setScene(duelScene); // Switch to the duel scene
    }

    public static void main(String[] args)
    {
        launch();
    }

}
