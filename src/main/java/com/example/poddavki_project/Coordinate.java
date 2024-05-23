package com.example.poddavki_project;

public class Coordinate {
    private int x;
    private int y;
    private int oldX;
    private int oldY;

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
    public void setY(int y) {
        this.y = y;
    }
    public void setX(int x) {
        this.x = x;
    }
}
