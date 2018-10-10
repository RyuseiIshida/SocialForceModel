package com.simulation.Cell;

import javax.vecmath.Vector2f;
import java.util.ArrayList;

public class Cell {
    private int cellInterval;
    private Vector2f leftButtomPoint;
    private Vector2f leftTopPoint;
    private Vector2f rightButtomPoint;
    private Vector2f rightTopPoint;
    private Vector2f centerPoint;

    public Cell() {
    }

    public Cell(int interval, Vector2f point) {
        this.cellInterval = interval;
        leftButtomPoint = new Vector2f(point);
        leftTopPoint = new Vector2f(point.x, point.y + interval);
        rightButtomPoint = new Vector2f(point.x + interval, point.y);
        rightTopPoint = new Vector2f(point.x + interval, point.y + interval);
        centerPoint = new Vector2f(point.x + interval / 2, point.y + interval / 2);
    }


    public Vector2f getLeftButtomPoint() {
        return leftButtomPoint;
    }

    public Vector2f getLeftTopPoint() {
        return leftTopPoint;
    }

    public Vector2f getRightButtomPoint() {
        return rightButtomPoint;
    }

    public Vector2f getRightTopPoint() {
        return rightTopPoint;
    }

    public Vector2f getCenterPoint() {
        return centerPoint;
    }

    public ArrayList getCellPoints() {
        ArrayList<Vector2f> cellPoints = new ArrayList<>();
        cellPoints.add(getLeftButtomPoint());
        cellPoints.add(getLeftTopPoint());
        cellPoints.add(getRightButtomPoint());
        cellPoints.add(getRightTopPoint());
        return cellPoints;
    }

}
