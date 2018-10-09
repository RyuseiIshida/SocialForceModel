package com.simulation.socialforce;

import javax.vecmath.Vector2f;
import java.util.ArrayList;

// getCellPointでスケール以上の数字が渡された時のエラー文を書く

public class Cell {
    private float endx = Parameter.scale.x;
    private float endy = Parameter.scale.y;
    private int cell_interval = Parameter.CELL_INTERVAL;
    private ArrayList<Vector2f> end_cellPoints = new ArrayList<>();
    private ArrayList<Vector2f> center_cellPoints = new ArrayList<>();

    Cell() {
//        for(int i=cell_interval; i<=endx; i+=cell_interval){
//            for(int j=cell_interval; j<=endy; j+=cell_interval){
        for (int i = cell_interval; i <= 30; i += cell_interval) {
            for (int j = cell_interval; j <= 30; j += cell_interval) {
                end_cellPoints.add(new Vector2f(i, j));
            }
        }

        for (int i = cell_interval; i <= 30; i += cell_interval) {
            for (int j = cell_interval; j <= 30; j += cell_interval) {
                center_cellPoints.add(new Vector2f(i - cell_interval / 2, j - cell_interval / 2));
            }
        }
        System.out.println(center_cellPoints);
    }


    public Vector2f getCellPoint(Vector2f point) {
        Vector2f match_point = new Vector2f();
        for (Vector2f end_cellPoint : end_cellPoints) {
            if (end_cellPoint.x - point.x > 0 && end_cellPoint.y - point.y > 0) {
                match_point.set(end_cellPoint.x - cell_interval / 2, end_cellPoint.y - cell_interval / 2);
                break;
            }
        }
        return match_point;
    }

    public static void main(String[] args) {
        Cell cell = new Cell();
        System.out.println(cell.getCellPoint(new Vector2f(24, 3)));
    }

}
