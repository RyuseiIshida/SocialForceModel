package com.simulation.Potential.Obstacle;

import com.simulation.Potential.PotentialCell;
import com.simulation.Potential.PotentialMap;

import javax.vecmath.Vector2f;
import java.util.ArrayList;

public class Obstacle {
    PotentialMap potentialMap;
    ArrayList<PotentialCell> obstacleCellMap;
    float valuePotential;
    int cellInterval;

    public Obstacle(PotentialMap potentialMap, float valuePotential, int x, int y, int w, int h){
        this.potentialMap = potentialMap;
        this.obstacleCellMap = new ArrayList<>();
        this.valuePotential = valuePotential;
        this.cellInterval = potentialMap.getCellInterval();
        setShapeObstacle(x,y,w,h);
    }

    public void setShapeObstacle(Vector2f pos, int w, int h) {
        PotentialCell startPoint = potentialMap.getPotentialCell(pos);
        PotentialCell diagonalPoint = potentialMap.getPotentialCell(new Vector2f(pos.x + w, pos.y + h));
        Vector2f startMatrixNumber = potentialMap.getMatrixNumber(startPoint);
        Vector2f diagonalMatrixNumber = potentialMap.getMatrixNumber(diagonalPoint);
        ArrayList<ArrayList<PotentialCell>> matrixCell = potentialMap.getMatrixPotentialCells();

        for (int i = (int) startMatrixNumber.x; i < (int) diagonalMatrixNumber.x; i++) {
            for (int j = (int) startMatrixNumber.y; j < (int) diagonalMatrixNumber.y; j++) {
                matrixCell.get(i).get(j).setObstaclePotential(valuePotential);
                obstacleCellMap.add(matrixCell.get(i).get(j));
            }
        }
    }

    public void setShapeObstacle(int x, int y, int w, int h) {
        setShapeObstacle(new Vector2f(x, y), w, h);
    }

    public void setShapeCircle(Vector2f pos, int w, int h) {
    }

}