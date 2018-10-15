package com.simulation.Cell;

import com.simulation.socialforce.Parameter;

import javax.vecmath.Vector2f;
import java.util.ArrayList;

public class Cells {
    private ArrayList<Cell> cells;


    public Cells(Vector2f scale, int cellInterval) {
        cells = new ArrayList<>();
        for (int i = 0; i <= scale.x; i += cellInterval) {
            for (int j = 0; j <= scale.y; j += cellInterval) {
                cells.add(new Cell(cellInterval, new Vector2f(i, j)));
            }
        }
    }

    public ArrayList<Cell> getCells() {
        return cells;
    }

    public Cell getCell(Vector2f targetPoint) {
        Cell matchCell = null;
        for (Cell cell : cells) {
            if (targetPoint.x < cell.getRightTopPoint().x && targetPoint.y < cell.getRightTopPoint().y) {
                matchCell = cell;
                break;
            }
        }
        return matchCell;
    }
}
