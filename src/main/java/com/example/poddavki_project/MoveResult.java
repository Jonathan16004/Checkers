package com.example.poddavki_project;

public class MoveResult
{

    private MoveType type;
    private Piece piece;

    // In case of a killing move
    public MoveResult(MoveType type, Piece piece)
    {
        this.type = type;
        this.piece = piece;
    }
    // In case of a normal move
    public MoveResult(MoveType type)
    {
        this(type,null);
    }

    // Returns the type of the move
    public MoveType getType()
    {
        return type;
    }
    // Returns the piece
    public Piece getPiece()
    {
        return piece;
    }
}
