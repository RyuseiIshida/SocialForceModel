package com.simulation.Potential;

import com.simulation.Potential.Obstacle.Obstacle;
import com.simulation.socialforce.CPedestrian;
import com.simulation.socialforce.Parameter;

import javax.vecmath.Vector2f;
import java.util.ArrayList;

public class PotentialManager {
    private static PotentialMap envPotentialMap = Parameter.potentialMap;
    private static ArrayList<Vector2f> exits = Parameter.exitVec;
    private static ArrayList<PotentialMap> potentialMaps;
    private static ArrayList<Obstacle> obstacles = new ArrayList<>();
    private static Vector2f scale = Parameter.scale;
    private static int cellInterval = Parameter.CELL_INTERVAL;
    private static float maxValuePotential = Parameter.MAXPOTENTIAL;
    private static float obstacleValuePotential = Parameter.VALUEOBSTACLEPOTENTIAL;


    public PotentialManager(){
        setObstacle(400,400,300, 300);
    }

    //obstacleの生成
    public static void setObstacle(int x, int y, int width, int height){
        Obstacle obstacle = new Obstacle(envPotentialMap, obstacleValuePotential, x, y, width, height);
        obstacles.add(obstacle);
    }

    public static ArrayList<Obstacle> getObstacles(){
        return obstacles;
    }

    public static void setObstacleMap(CPedestrian ped){
        PotentialMap agentMap =  ped.getMyPotentialMap();
        for (PotentialCell potentialCell : agentMap.getPotentialCells()) {
            Vector2f matrixNumber = agentMap.getMatrixNumber(potentialCell);
            float value = envPotentialMap.getPotentialCell((int)matrixNumber.x, (int)matrixNumber.y).getObstaclePotential();
            potentialCell.setObstaclePotential(value);
        }
    }

    public static void setGoalPotentialMap(CPedestrian ped){
        float distance = ped.getDistance(ped.getPosition(), ped.getGoalposition());
        float nomalize = ped.getDistance(0, 0, scale.x, scale.y);
        distance = distance / nomalize;
        for (PotentialCell potentialCell : ped.getMyPotentialMap().getPotentialCells()) {
            potentialCell.setGoalPotential(distance - 1);
        }
    }


    public static float EnvValuePotential(PotentialCell cell){
        return envPotentialMap.getPotentialCell(cell).getPotential();
    }
}
