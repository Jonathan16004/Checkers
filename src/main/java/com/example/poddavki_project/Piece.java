package com.example.poddavki_project;

import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Ellipse;

import static com.example.poddavki_project.CheckersApplication.TILE_SIZE;

public class Piece extends StackPane
{
    private PieceType type;

    private double mouseX, mouseY;
    private double oldX, oldY;
    private Ellipse bg;
    private Ellipse ellipse;
    public Piece(PieceType type, int x, int y)
    {
        this.type = type;

        // Sets the location of the game board and saves it to the old move
        move(x, y);

        // --------- Creates a new ellipse that will serve as the background of the game piece ---------
        bg = new Ellipse(TILE_SIZE * 0.3125, TILE_SIZE * 0.26);

        // Designs the ellipse
        bg.setFill(Color.BLACK);
        bg.setStroke(Color.BLACK);
        bg.setStrokeWidth(TILE_SIZE* 0.03);
        // Calculates the width and the height of the bg ellipse divided by 2 in order to center the ellipse
        bg.setTranslateX((TILE_SIZE - TILE_SIZE * 0.3125 * 2) / 2);
        // We are adding a slight amount in order for it to look like the ellipse's shadow
        bg.setTranslateY((TILE_SIZE - TILE_SIZE * 0.26 * 2) / 2 + TILE_SIZE * 0.07);

        // --------- Creates a new ellipse that will serve as the game piece ---------
        ellipse = new Ellipse(TILE_SIZE * 0.3125, TILE_SIZE * 0.26);

        // Designs the ellipse
        ellipse.setFill(type == PieceType.BLACK ? Color.valueOf("#252525") : Color.valueOf("#fff9f4"));
        ellipse.setStroke(Color.BLACK);
        ellipse.setStrokeWidth(TILE_SIZE* 0.03);
        // Calculates the width and the height of the bg ellipse divided by 2 in order to center the ellipse
        ellipse.setTranslateX((TILE_SIZE - TILE_SIZE * 0.3125 * 2) / 2);
        ellipse.setTranslateY((TILE_SIZE - TILE_SIZE * 0.26 * 2) / 2);

        // Adds them all to the piece group
        getChildren().addAll(bg , ellipse);

        // sets the mouse x and mouse y at mouse press
        setOnMousePressed(e-> {
            mouseX = e.getSceneX();
            mouseY = e.getSceneY();
            System.out.println("row: " + oldY + " col: " + oldX);
        });

        // Sets the new position of the piece after the drag
        setOnMouseDragged(e -> {
            relocate(e.getSceneX() - mouseX + oldX,e.getSceneY() - mouseY + oldY);
        });
    }

    // Returns the type of the piece
    public PieceType getType()
    {
        return type;
    }

    // Returns the old x position of the piece
    public double getOldX()
    {
        return oldX;
    }

    // Returns the old y position of the piece
    public double getOldY()
    {
        return oldY;
    }

    // Moves the piece to the desired position
    public void move(int x, int y)
    {
        oldX = x * TILE_SIZE;
        oldY = y * TILE_SIZE;
        relocate(oldX, oldY);
    }

    // Moves the piece to the original position
    public void abortMove()
    {
        relocate(oldX, oldY);
    }

    public void setKing()
    {
        if(type == PieceType.BLACK) type = PieceType.BLACKKING;
        else type = PieceType.WHITEKING;

        ellipse.setStroke(Color.GOLD);
        bg.setStroke(Color.valueOf("#dbb702"));
        bg.setFill(Color.valueOf("#dbb702"));
    }

}
