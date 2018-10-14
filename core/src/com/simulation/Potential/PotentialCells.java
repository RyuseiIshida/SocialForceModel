package com.simulation.Potential;

import Obstacle.Obstacle;
import org.omg.PortableServer.POA;

import javax.vecmath.Vector2f;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.ListIterator;
import java.util.Map;

public class PotentialCells {
    ArrayList<PotentialCell> potentialCells;
    ArrayList<ArrayList<PotentialCell>> matrixPotentialCells;
    ArrayList<Obstacle> obstacles;
    Vector2f scale;
    float maxPotential;
    int interval;
    int row;
    int column;

    public PotentialCells(Vector2f scale, int interval, float maxPotential) {
        potentialCells = new ArrayList<>();
        matrixPotentialCells = new ArrayList<>();
        obstacles = new ArrayList<>();
        this.maxPotential = maxPotential;
        this.scale = scale;
        this.interval = interval;

        column = 0;
        for (int i = 0; i <= scale.x; i += interval) {
            ArrayList<PotentialCell> array = new ArrayList<>();
            row = 0;
            for (int j = 0; j <= scale.y; j += interval) {
                PotentialCell cell = new PotentialCell(interval, new Vector2f(i, j), maxPotential);
                potentialCells.add(cell);
                array.add(cell);
                row++;
            }
            matrixPotentialCells.add(array);
            column++;
        }
    }

    public void setObstacle(Obstacle obstacle){
        this.obstacles.add(obstacle);
    }

    public ArrayList<Obstacle> getObstacles() {
        return obstacles;
    }

    public ArrayList<PotentialCell> getPotentialCells() {
        return potentialCells;
    }

    public ArrayList<ArrayList<PotentialCell>> getMatrixPotentialCells() {
        return matrixPotentialCells;
    }

    public Vector2f getScale() {
        return scale;
    }

    public float getMaxPotential() {
        return maxPotential;
    }

    public int getInterval() {
        return interval;
    }

    public int getRow() {
        return row;
    }

    public int getColumn() {
        return column;
    }


    public PotentialCell getPotentialCell(Vector2f targetPoint) {
        PotentialCell matchPotentialCell = null;

        for (PotentialCell cell : potentialCells) {
            if (targetPoint.x < cell.getRightTopPoint().x && targetPoint.y < cell.getRightTopPoint().y) {
                matchPotentialCell = cell;
                break;
            }
        }

        return matchPotentialCell;
    }

    public PotentialCell getPotentialCell(PotentialCell targetCell){
        PotentialCell matchPotentialCell = null;
        for (PotentialCell potentialCell : potentialCells) {
            if(targetCell.equals(potentialCell)) {
                matchPotentialCell = potentialCell;
            }
        }
        return matchPotentialCell;
    }

    public Vector2f getMatrixNumber(PotentialCell potentialCell){
        Vector2f matrixNumber  = null;
        int row;
        int column = 0;
        for (ArrayList<PotentialCell> matrixPotentialCell : matrixPotentialCells) {
            row = 0;
            for (PotentialCell cell : matrixPotentialCell) {
                if(cell.equals(potentialCell)){
                    matrixNumber = new Vector2f(column,row);
                }
                row++;
            }
            column++;
        }
        return matrixNumber;
    }

    public PotentialCell getLeftPotentialCell(PotentialCell targetCell) {
        Vector2f leftButtomPoint = targetCell.getLeftButtomPoint();
        Vector2f leftTopPoint = targetCell.getLeftTopPoint();
        PotentialCell matchPotentialCell = null;
        for (PotentialCell potentialCell : potentialCells) {
            if (leftButtomPoint.equals(potentialCell.getRightButtomPoint())
                    && leftTopPoint.equals(potentialCell.getRightTopPoint())) {
                matchPotentialCell = potentialCell;
            }
        }

        return matchPotentialCell;
    }

    public PotentialCell getRightPotentialCell(PotentialCell targetCell) {
        Vector2f rightButtomPoint = targetCell.getRightButtomPoint();
        Vector2f rightTopPoint = targetCell.getRightTopPoint();
        PotentialCell matchPotentialCell = null;
        for (PotentialCell potentialCell : potentialCells) {
            if (rightButtomPoint.equals(potentialCell.getLeftButtomPoint())
                    && rightTopPoint.equals(potentialCell.getLeftTopPoint())) {
                matchPotentialCell = potentialCell;
            }
        }

        return matchPotentialCell;
    }

    public PotentialCell getUpPotentialCell(PotentialCell targetCell) {
        ListIterator<PotentialCell> iterator = potentialCells.listIterator();
        PotentialCell matchCell = null;
        while (iterator.hasNext()) {
            PotentialCell next = iterator.next();
            if (next.equals(targetCell)) {
                matchCell = iterator.next();
                break;
            }
        }

        return matchCell;
    }

    public PotentialCell getDownPotentialCell(PotentialCell targetCell) {
        PotentialCell matchPotentialCell = null;
        Vector2f leftButtomPoint = targetCell.getLeftButtomPoint();
        Vector2f rightButtomPoint = targetCell.getRightButtomPoint();
        for (PotentialCell potentialCell : potentialCells) {
            if (leftButtomPoint.equals(potentialCell.getLeftTopPoint()) && rightButtomPoint.equals(potentialCell.getRightTopPoint())) {
                matchPotentialCell = potentialCell;
            }
        }

        return matchPotentialCell;
    }

    public PotentialCell getLeftButtomPotentialCell(PotentialCell targetCell) {
        Vector2f leftButtomPoint = targetCell.getLeftButtomPoint();
        PotentialCell matchPotentialCell = null;

        for (PotentialCell potentialCell : potentialCells) {
            if (leftButtomPoint.equals(potentialCell.getRightTopPoint())) {
                matchPotentialCell = potentialCell;
            }
        }

        return matchPotentialCell;
    }

    public PotentialCell getLeftTopPotentialCell(PotentialCell targetCell) {
        Vector2f leftTopPoint = targetCell.getLeftTopPoint();
        PotentialCell matchPotentialCell = null;

        for (PotentialCell potentialCell : potentialCells) {
            if (leftTopPoint.equals(potentialCell.getRightButtomPoint())) {
                matchPotentialCell = potentialCell;
            }
        }

        return matchPotentialCell;
    }

    public PotentialCell getRightButtomPotentialCell(PotentialCell targetCell) {
        Vector2f rightButtomPoint = targetCell.getRightButtomPoint();
        PotentialCell matchPotentialCell = null;

        for (PotentialCell potentialCell : potentialCells) {
            if (rightButtomPoint.equals(potentialCell.getLeftTopPoint())) {
                matchPotentialCell = potentialCell;
            }
        }

        return matchPotentialCell;
    }

    public PotentialCell getRightTopPotentialCell(PotentialCell targetCell) {
        Vector2f rightTopPoint = targetCell.getRightTopPoint();
        PotentialCell matchPotentialCell = null;

        for (PotentialCell potentialCell : potentialCells) {
            if (rightTopPoint.equals(potentialCell.getLeftButtomPoint())) {
                matchPotentialCell = potentialCell;
            }
        }

        return matchPotentialCell;
    }

    public ArrayList getNearPotentialCells(Vector2f targetPoint) {
        ArrayList<PotentialCell> potentialCells = new ArrayList<>();
        PotentialCell oneSelfCell = getPotentialCell(targetPoint);

        potentialCells.add(getLeftButtomPotentialCell(oneSelfCell));
        potentialCells.add(getLeftPotentialCell(oneSelfCell));
        potentialCells.add(getLeftTopPotentialCell(oneSelfCell));
        potentialCells.add(getDownPotentialCell(oneSelfCell));
        potentialCells.add(getUpPotentialCell(oneSelfCell));
        potentialCells.add(getRightButtomPotentialCell(oneSelfCell));
        potentialCells.add(getRightPotentialCell(oneSelfCell));
        potentialCells.add(getRightTopPotentialCell(oneSelfCell));

        return potentialCells;
    }

    public ArrayList getNearPotentialCells(PotentialCell oneSelfCell) {
        ArrayList<PotentialCell> potentialCells = new ArrayList<>();

        potentialCells.add(getLeftButtomPotentialCell(oneSelfCell));
        potentialCells.add(getLeftPotentialCell(oneSelfCell));
        potentialCells.add(getLeftTopPotentialCell(oneSelfCell));
        potentialCells.add(getDownPotentialCell(oneSelfCell));
        potentialCells.add(getUpPotentialCell(oneSelfCell));
        potentialCells.add(getRightButtomPotentialCell(oneSelfCell));
        potentialCells.add(getRightPotentialCell(oneSelfCell));
        potentialCells.add(getRightTopPotentialCell(oneSelfCell));

        return potentialCells;
    }

    public float totalPotential(PotentialCell targetCell){
        float obstaclePotential = 0, agentPotential = 0;
        float totalPotential;
        for (Obstacle obstacle : obstacles) {
            for (PotentialCell obstacleCell : obstacle.getObstacleCell()) {
                if(targetCell.equals(obstacleCell)){
                    obstaclePotential = obstacleCell.getPotential();
                }
            }
        }
        totalPotential = obstaclePotential + agentPotential;
        return totalPotential;
    }

    public boolean isObstacles(PotentialCell targetCell){
        for (Obstacle obstacle : obstacles) {
            for(PotentialCell potentialCell : obstacle.getObstacleCell()){
                if(targetCell.equals(potentialCell)){
                    return true;
                }
            }
        }
        return false;
    }





}
