package com.example.poddavki_project;

import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class Tile extends Rectangle
{
    private  Piece piece;

    public Tile(boolean light, int x, int y)
    {
        // Sets width and height of the tile
        setWidth(CheckersApplication.TILE_SIZE);
        setHeight(CheckersApplication.TILE_SIZE);

        // Sets position on the game board
        relocate(x * CheckersApplication.TILE_SIZE, y * CheckersApplication.TILE_SIZE);

        // Sets the colors of the tiles
        setFill(light ? Color.valueOf("D8D8D8") : Color.valueOf("#4D4D4D"));
    }

    // Checks if there's a piece
    public boolean hasPiece()
    {
        return piece != null;
    }

    // Returns the piece
    public Piece getPiece()
    {
        return piece;
    }

    // Sets the piece
    public void setPiece(Piece piece)
    {
        this.piece = piece;
    }
}
