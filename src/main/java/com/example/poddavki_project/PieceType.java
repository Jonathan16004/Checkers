package com.example.poddavki_project;

public enum PieceType
{
    BLACK(1), WHITE(-1), BLACKKING(2), WHITEKING(-1), NONE (0);
    final int moveDir;
    PieceType(int moveDir)
    {
        this.moveDir = moveDir;
    }
}
