package com.tps.opengl;

public class Circle {
    private float x, y;
    private int color;
    private int radius;

    public Circle(float x, float y, int color, int radius) {
        this.x = x;
        this.y = y;
        this.color = color;
        this.radius = radius;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public void setX(float X){
        this.x = X;
    }

    public void setY(float Y){
        this.y = Y;
    }

    public int getColor() {
        return color;
    }

    public int getRadius() {
        return radius;
    }

    public void move(float deltaX, float deltaY) {
        x += deltaX;
        y += deltaY;
    }
    public boolean collidesWith(Circle otherCircle) {
        float dx = this.getX() - otherCircle.getX();
        float dy = this.getY() - otherCircle.getY();
        float distance = (float) Math.sqrt(dx * dx + dy * dy);

        return distance < this.getRadius() + otherCircle.getRadius();
    }

}
