package com.example.poddavki_project;

public class Model implements IModel
{
    public Bitboard board;
    public Ai ai;
    public Model()
    {
        this.board = new Bitboard();
    }
    public Bitboard GetBitboard() {
        return board;
    }
}
