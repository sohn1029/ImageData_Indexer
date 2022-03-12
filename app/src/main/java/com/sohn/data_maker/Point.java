package com.sohn.data_maker;

public
class Point {

    int pointX;
    int pointY;
    int radius ;


    public Point(int pointX, int pointY, int radius) {
        this.pointX = pointX;
        this.pointY = pointY;
        this.radius = radius;
    }

    public int getCircleX() {
        return pointX;
    }

    public void setCircleX(int pointX) {
        this.pointX = pointX;
    }

    public int getCircleY() {
        return pointY;
    }

    public void setCircleY(int pointY) {
        this.pointY = pointY;
    }

    public int getRadius() {
        return radius;
    }

    public void setRadius(int radius) {
        this.radius = radius;
    }
}
