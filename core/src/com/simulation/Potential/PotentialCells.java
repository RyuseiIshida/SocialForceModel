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
    int cellInterval;
    int row;
    int column;

    public PotentialCells(Vector2f scale, int cellInterval, float maxPotential) {
        potentialCells = new ArrayList<>();
        matrixPotentialCells = new ArrayList<>();
        obstacles = new ArrayList<>();
        this.maxPotential = maxPotential;
        this.scale = scale;
        this.cellInterval = cellInterval;
        initCell();
    }

    public void initCell(){
        column = 0;
        for (int i = 0; i <= scale.x; i += cellInterval) {
            ArrayList<PotentialCell> array = new ArrayList<>();
            row = 0;
            for (int j = 0; j <= scale.y; j += cellInterval) {
                PotentialCell cell = new PotentialCell(cellInterval, new Vector2f(i, j), maxPotential);
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

    public PotentialCell getPotentialCell(Vector2f targetPoint) {
        PotentialCell matchPotentialCell = null;

        for (PotentialCell cell : potentialCells) {
            if (targetPoint.x < cell.getRightTopPoint().x && targetPoint.y < cell.getRightTopPoint().y) {
                matchPotentialCell = cell;
                break;
            }
        }
        if(matchPotentialCell == null){
            System.out.println("getPotentialCell = NULL");
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
        if(matchPotentialCell == null){
            System.out.println("getPotentialCell = NULL");
        }
        return matchPotentialCell;
    }

    public PotentialCell getPotentialCell(int i, int j){
        return matrixPotentialCells.get(i).get(j);
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
        if(matrixNumber == null){
            System.out.println("getMatrixNumber == NUll");
        }
        return matrixNumber;
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
}
