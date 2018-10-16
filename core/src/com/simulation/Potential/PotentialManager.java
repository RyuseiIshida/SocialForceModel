package com.simulation.Potential;

import com.badlogic.gdx.math.Vector2;
import com.simulation.Potential.Obstacle.Obstacle;
import com.simulation.socialforce.CPedestrian;
import com.simulation.socialforce.Parameter;

import javax.vecmath.Vector2f;
import java.util.ArrayList;

public class PotentialManager {
    private static PotentialMap envPotentialMap;
    private static PotentialMap obstaclePotentialMap;
    private static PotentialMap goalPotentialMap;
    private static PotentialMap agentPotentialMap;
    private static ArrayList<Obstacle> obstacles = new ArrayList<>();
    private static Vector2f scale = Parameter.scale;
    private static int cellInterval = Parameter.CELL_INTERVAL;
    private static float maxValuePotential = Parameter.MAXPOTENTIAL;
    private static float obstacleValuePotential = Parameter.VALUEOBSTACLEPOTENTIAL;
    private static ArrayList<Vector2f> goals = Parameter.exitVec;

    public PotentialManager() {
        envPotentialMap = new PotentialMap(scale, cellInterval, maxValuePotential);
        //setObstacle(400, 500, 50, 50);
        //setObstacle(400, 500, 30, 100);
        //setObstacle(530, 600, 60,30);
        setObstacle(500, 500,100,100);
        //setObstacle(400,550,100,30);
        setGoalPotentialMap();
        setdtObstaclePotentialMap();
    }

    public void setObstacle(int x, int y, int width, int height) {
        obstaclePotentialMap = new PotentialMap(scale, cellInterval, maxValuePotential);
        Obstacle obstacle = new Obstacle(obstaclePotentialMap, obstacleValuePotential, x, y, width, height);
        setObstaclePotentialMap(obstacle);
        obstacles.add(obstacle);
    }

    public void setObstacle(Vector2f vec1, Vector2f vec2) {
        float d_x = Math.abs(vec1.x - vec2.x);
        float d_y = Math.abs(vec1.y - vec2.y);
        setObstacle((int) vec1.x, (int) vec1.x, (int) d_x, (int) d_y);
    }


    private void setObstaclePotentialMap(Obstacle obstacle){
        for(PotentialCell potentialCell : obstacle.getObstacleCellMap()){
            Vector2f  matrixNumber = obstaclePotentialMap.getMatrixNumber(potentialCell);
            obstaclePotentialMap.getMatrixPotentialCells(matrixNumber.x, matrixNumber.y).setObstaclePotential(obstacleValuePotential);
        }
    }

    public static void setdtObstaclePotentialMap(){
        Vector2f matrixNumber;
        for (Obstacle obstacle : obstacles) {
            for (PotentialCell cell : obstacle.getObstacleCellMap()) {
                matrixNumber = obstaclePotentialMap.getMatrixNumber(cell);
                for (int i = (int)matrixNumber.x - 1; i <= (int)matrixNumber.x + 1; i++) {
                    for (int j = (int)matrixNumber.y - 1; j <= (int)matrixNumber.y + 1; j++) {
                        obstaclePotentialMap.getMatrixPotentialCells(i,j).setObstaclePotential(1);
                    }
                }
            }
        }
    }


    public static void setPedObstacleMap(CPedestrian ped) {
        //近傍の値を合わせる
        ArrayList<Vector2f> neighboringCellsNumber = ped.getNeighboringCellsNumber();
        //obstacleマップのmatrizNumberをセットする
        for (Vector2f matrix : neighboringCellsNumber) {
            float obstaclePotential = obstaclePotentialMap.getMatrixPotentialCells(matrix.x, matrix.y).getObstaclePotential();
            ped.getMyPotentialMap().getMatrixPotentialCells(matrix.x, matrix.y).setObstaclePotential(obstaclePotential);
        }
    }


    public static ArrayList<Obstacle> getObstacles() { //描画用
        return obstacles;
    }



    public static void setGoalPotentialMap() {
        goalPotentialMap = new PotentialMap(scale, cellInterval, maxValuePotential);
        for (PotentialCell cell : goalPotentialMap.getPotentialCells()) {
            for (Vector2f goal : goals) {
                float distance = getLength(goal, cell.getCenterPoint());
                float nomalize = getLength(goal, scale);
                distance = distance / nomalize;
                cell.setGoalPotential(distance - 1);
            }
        }
    }

    public static float getLength(Vector2f vec1, Vector2f vec2) {
        return(float)Math.sqrt((vec1.x - vec2.x) * (vec1.x - vec2.x) + (vec1.y - vec2.y) * (vec1.y - vec2.y));
    }

    public static void setPedGoalPotentialMap(CPedestrian ped) {
        ArrayList<Vector2f> neighboringCellsNumber = ped.getNeighboringCellsNumber();
        for (Vector2f matrix : neighboringCellsNumber) {
            float goalPotential = goalPotentialMap.getMatrixPotentialCells(matrix.x, matrix.y).getGoalPotential();
            ped.getMyPotentialMap().getMatrixPotentialCells(matrix.x, matrix.y).setGoalPotential(goalPotential);
        }
    }

    public static float EnvValuePotential(PotentialCell cell) {
        return envPotentialMap.getPotentialCell(cell).getPotential();
    }
}
