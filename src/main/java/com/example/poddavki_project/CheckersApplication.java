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



public class CheckersApplication extends Application
{
    public static final  int TILE_SIZE = 100;
    public static final  int WIDTH = 8;
    public static final int HEIGHT = 8;


    // if any problems are cause change from final to normal
    private final Tile[][] boardVisual = new Tile[WIDTH][HEIGHT];
    private final Bitboard board = new Bitboard();
    private final Group tileGroup = new Group();
    private final Group pieceGroup = new Group();

    private PieceType currentPlayer = PieceType.WHITE;

    private boolean ai = false;
    private boolean won = false;
    private final Stage stageWon = new Stage();

    public Tile[][] getBoardVisual() {
        return boardVisual;
    }

    // Function that creates a scene
    private Parent createContentDuel()
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
                    piece = makePiece(PieceType.BLACK, x ,y);
                }

                // Checks if it's the lower half of the board the tile isn't a white tile
                if(y >= 5 && (x+y) % 2 != 0)
                {
                    piece = makePiece(PieceType.WHITE, x ,y);
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


    private Parent blackWinScene() {
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

    private Parent whiteWinScene() {
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

    private Parent tieScene() {
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

    // Creates a new object of a piece
    private void aiMove(int x0, int y0, int newX, int newY, Piece piece)
    {

            MoveResult result = tryMove(piece, newX, newY);
            // PROBLEM IS HERE
            Map<Coordinate, List<List<Coordinate>>> legalMoves = board.generateLegalCaptureMovesForType(currentPlayer);
            if(board.emptyMoves(legalMoves))
            {
                if (result.getType() == MoveType.NONE)
                {
                    piece.abortMove();
                }
                else if (result.getType() == MoveType.NORMAL)
                {
                    piece.getType();
                    piece.move(newX, newY);
                    boardVisual[x0][y0].setPiece(null);
                    boardVisual[newX][newY].setPiece(piece);
                    board.MovePiece(y0, x0, newY, newX);
                    // Checks if a player is now king
                    if(board.checkKing(newY,newX,currentPlayer))
                    {
                        piece.setKing();
                        board.kingify(newY,newX);
                        System.out.println("NEW KING " + board.typeOfPiece(newY, newX));
                    }
                    currentPlayer = currentPlayer == PieceType.BLACK ? PieceType.WHITE : PieceType.BLACK;
                    System.out.println("TURN: " + currentPlayer);
                }
                else
                {
                    piece.abortMove();
                }
            }
            else if (result.getType() == MoveType.KILL)
            {
                piece.move(newX, newY);
                boardVisual[x0][y0].setPiece(null);
                boardVisual[newX][newY].setPiece(piece);
                board.MovePiece(y0, x0, newY, newX);
                Piece otherPiece = result.getPiece();
                boardVisual[toBoard(otherPiece.getOldX())][toBoard(otherPiece.getOldY())].setPiece(null);
                board.deletePiece(toBoard(otherPiece.getOldY()), toBoard(otherPiece.getOldX()));
                pieceGroup.getChildren().remove(otherPiece);
                if(board.checkKing(newY,newX,currentPlayer))
                {
                    piece.setKing();
                    board.kingify(newY,newX);
                    System.out.println("NEW KING " + board.typeOfPiece(newY, newX));
                }
                if(board.emptyMovesForPiece(board.generateLegalMovesForPieceCapture(newY, newX, piece.getType())))
                {
                    currentPlayer = currentPlayer == PieceType.BLACK ? PieceType.WHITE : PieceType.BLACK;
                }
            }
            else
            {
                piece.abortMove();
            }
            // White won
            if(board.checkWin(PieceType.WHITE))
            {
                System.out.println("WHITE WON");
                Scene whiteWon = new Scene(whiteWinScene());
                stageWon.setScene(whiteWon);
                stageWon.show();
                this.won = true;
            }
            // Black won
            else if(board.checkWin(PieceType.BLACK))
            {
                System.out.println("BLACK WON");
                Scene blackWon = new Scene(blackWinScene());
                stageWon.setScene(blackWon);
                stageWon.show();
                this.won = true;
            }
            // Tie
            else if (board.checkWin(PieceType.BLACK) && board.checkWin(PieceType.WHITE))
            {
                System.out.println("Tie");
                Scene tie = new Scene(tieScene());
                stageWon.setScene(tie);
                stageWon.show();
                this.won = true;
            }
            board.printBoard();
        System.out.println("AI");
    }

    // Creates a new object of a piece
    private Piece makePiece(PieceType type, int x, int y)
    {
        Piece piece = new Piece(type, x, y);
        piece.setOnMouseReleased(e ->
        {
            System.out.println("TURN: " + currentPlayer);
            movePiece(piece);
            // White won
            if(board.checkWin(PieceType.WHITE))
            {
                System.out.println("WHITE WON");
                Scene whiteWon = new Scene(whiteWinScene());
                stageWon.setScene(whiteWon);
                stageWon.show();
                this.won = true;
            }
            // Black won
            else if(board.checkWin(PieceType.BLACK))
            {
                System.out.println("BLACK WON");
                Scene blackWon = new Scene(blackWinScene());
                stageWon.setScene(blackWon);
                stageWon.show();
                this.won = true;
            }
            // Tie
            else if (board.checkWin(PieceType.BLACK) && board.checkWin(PieceType.WHITE))
            {
                System.out.println("Tie");
                Scene tie = new Scene(tieScene());
                stageWon.setScene(tie);
                stageWon.show();
                this.won = true;
                // :)
            }
            board.printBoard();
            while (ai && currentPlayer == PieceType.BLACK && !won)
            {
                int fromRow=0;
                int fromCol=0;
                int row=0;
                int col=0;
                Piece pieceAi = null;
                Map<Coordinate, List<List<Coordinate>>> legalMoves = board.generateLegalCaptureMovesForType(PieceType.BLACK);
                if(board.emptyMoves(legalMoves))
                {
                    legalMoves = board.generateLegalMovesForType(PieceType.BLACK);
                    if(board.emptyMoves(legalMoves))
                    {
                        System.out.println("No suitable moves.");
                        break;
                    }
                    for (int i = 0; i < legalMoves.size(); i++)
                    {
                        List<List<Coordinate>> movesForPiece = legalMoves.get(i);
                        System.out.println("Legal moves for piece number " + i + ": ");
                        for (List<Coordinate> move : movesForPiece)
                        {
                            for (Coordinate moveDir : move)
                            {
                                System.out.println("[" + moveDir.getX() + "][" + moveDir.getY() + "] from " + "[" + moveDir.getOldX() + "][" + moveDir.getOldY() + "]");
                                if(board.typeOfPiece(moveDir.getOldX(),moveDir.getOldY()) == PieceType.BLACK ||  board.typeOfPiece(moveDir.getOldX(),moveDir.getOldY()) == PieceType.BLACKKING ) {
                                    row = moveDir.getX();
                                    col = moveDir.getY();
                                    fromRow = moveDir.getOldX();
                                    fromCol = moveDir.getOldY();
                                    pieceAi = boardVisual[fromCol][fromRow].getPiece();
                                    break;
                                }
                            }
                        }
                    }
                    try {
                        aiMove(fromCol, fromRow, col, row, pieceAi);
                    } catch (Exception E) {
                        System.out.println("An error occurred: " + E.getMessage());
                    }
                }
                else
                {
                    for (int i = 0; i < legalMoves.size(); i++)
                    {
                        List<List<Coordinate>> movesForPiece = legalMoves.get(i);
                        System.out.println("Legal moves for piece number " + i + ": ");
                        for (List<Coordinate> move : movesForPiece)
                        {
                            for(Coordinate moveDir : move)
                            {
                                System.out.println("[" + moveDir.getX() + "][" + moveDir.getY() + "] from " + "[" + moveDir.getOldX() + "][" + moveDir.getOldY() + "]");
                                if(board.typeOfPiece(moveDir.getOldX(),moveDir.getOldY()) == PieceType.BLACK || board.typeOfPiece(moveDir.getOldX(),moveDir.getOldY()) == PieceType.BLACKKING ) {
                                    row = moveDir.getX();
                                    col = moveDir.getY();
                                    fromRow = moveDir.getOldX();
                                    fromCol = moveDir.getOldY();
                                    pieceAi = boardVisual[fromCol][fromRow].getPiece();
                                    if(pieceAi != null) break;
                                }
                            }
                        }
                    }
                    try {
                        aiMove(fromCol,fromRow,col,row,pieceAi);
                    } catch (Exception E) {
                        System.out.println("An error occurred: " + E.getMessage());
                    }
                }
            }
        });
        return piece;
    }

    public void movePiece(Piece piece)
    {
        System.out.println("MOVING " + currentPlayer);
        board.generateLegalCaptureMovesForType(currentPlayer);
        int newX = toBoard(piece.getLayoutX());
        int newY = toBoard(piece.getLayoutY());

        MoveResult result = tryMove(piece, newX, newY);
        int x0 = toBoard(piece.getOldX());
        int y0 = toBoard(piece.getOldY());
        Map<Coordinate, List<List<Coordinate>>> legalMoves = board.generateLegalCaptureMovesForType(currentPlayer);
        if(board.emptyMoves(legalMoves))
        {
            if (result.getType() == MoveType.NONE)
            {
                piece.abortMove();
            }
            else if (result.getType() == MoveType.NORMAL)
            {
                piece.move(newX, newY);
                boardVisual[x0][y0].setPiece(null);
                boardVisual[newX][newY].setPiece(piece);
                board.MovePiece(y0, x0, newY, newX);
                // Checks if a player is now king
                if(board.checkKing(newY,newX,currentPlayer))
                {
                    piece.setKing();
                    board.kingify(newY,newX);
                    System.out.println("NEW KING " + board.typeOfPiece(newY, newX));
                }
                currentPlayer = currentPlayer == PieceType.BLACK ? PieceType.WHITE : PieceType.BLACK;
            }
            else
            {
                piece.abortMove();
            }
        }
        else if (result.getType() == MoveType.KILL)
        {
            piece.move(newX, newY);
            boardVisual[x0][y0].setPiece(null);
            boardVisual[newX][newY].setPiece(piece);
            board.MovePiece(y0, x0, newY, newX);
            Piece otherPiece = result.getPiece();
            boardVisual[toBoard(otherPiece.getOldX())][toBoard(otherPiece.getOldY())].setPiece(null);
            board.deletePiece(toBoard(otherPiece.getOldY()), toBoard(otherPiece.getOldX()));
            pieceGroup.getChildren().remove(otherPiece);
            if(board.checkKing(newY,newX,currentPlayer))
            {
                piece.setKing();
                board.kingify(newY,newX);
                System.out.println("NEW KING " + board.typeOfPiece(newY, newX));
            }
            if(board.emptyMovesForPiece(board.generateLegalMovesForPieceCapture(newY, newX, piece.getType())))
            {
                currentPlayer = currentPlayer == PieceType.BLACK ? PieceType.WHITE : PieceType.BLACK;
            }
        }
        else
        {
            piece.abortMove();
        }
    }

    // Checks if a piece can move to a destination
    public MoveResult tryMove(Piece piece, int newX, int newY)
    {
        int x0 = toBoard(piece.getOldX());
        int y0 = toBoard(piece.getOldY());
        PieceType checkTurn = piece.getType();
        System.out.println("THE TYPE OF THE PIECE IS " + board.typeOfPiece(y0,x0));
        // PROBLEM IS HERE FOR SOME REASON
        List<List<Coordinate>> movesForPiece = board.generateLegalMovesForPiece(y0,x0,piece.getType());
        board.printLegalPieceMoves(y0,x0);
        if(piece.getType() == PieceType.WHITEKING)
        {
            checkTurn = PieceType.WHITE;
        }
        else if(piece.getType() == PieceType.BLACKKING)
        {
            checkTurn = PieceType.BLACK;
        }
        if(checkTurn == currentPlayer && !movesForPiece.isEmpty())
        {
            for (List<Coordinate> move : movesForPiece)
            {
                for(Coordinate moveDir : move)
                {
                    if(moveDir.getX() == newY && moveDir.getY() == newX)
                    {
                        // In case of a normal move
                        if(Math.abs(newX - x0) == 1)
                        {
                            System.out.println("Normal MOVE!");
                            return new MoveResult(MoveType.NORMAL);
                        }

                        else if (Math.abs(newX - x0) == 2/* && newY - y0 == piece.getType().moveDir * 2*/) {
                            int x1;
                            int y1;

                            x1 = x0 + (newX - x0) / 2;
                            y1 = y0 + (newY - y0) / 2;

                            if (boardVisual[x1][y1].hasPiece() && boardVisual[x1][y1].getPiece().getType() != piece.getType())
                            {
                                System.out.println("KILL MOVE!");
                                return new MoveResult(MoveType.KILL, boardVisual[x1][y1].getPiece());
                            }
                        }
                    }
                }
            }
        }
        System.out.println("No Move");
        return new MoveResult(MoveType.NONE);
    }

    // function takes a pixel coordinate value and converts it to a corresponding board coordinate
    private int toBoard(double pixel)
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
    private void handleAIButtonClick(Stage stage) {
        // Create a new scene for an AI
        ai = true;
        Scene aiScene = new Scene(createContentDuel());
        stage.setScene(aiScene);
    }

    private void handleDuelButtonClick(Stage stage) {
        Scene duelScene = new Scene(createContentDuel());
        stage.setScene(duelScene); // Switch to the duel scene
    }


    // Launches the app
    public static void main(String[] args)
    {
        launch();
    }
}