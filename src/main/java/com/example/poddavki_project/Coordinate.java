package com.example.poddavki_project;

public class Coordinate {
    private final int x;
    private final int y;
    private final int oldX;
    private final int oldY;

    public Coordinate(int x, int y, int oldX, int oldY) {
        this.x = x;
        this.y = y;
        this.oldX = oldX;
        this.oldY = oldY;
    }

    public Coordinate(int x, int y) {
        this.x = x;
        this.y = y;
        this.oldX = -1;
        this.oldY = -1;
    }

    // Getter methods
    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
    public int getOldX() {
        return oldX;
    }
    public int getOldY() {
        return oldY;
    }
}
