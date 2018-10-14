package com.simulation.Potential;

import com.simulation.Cell.Cell;

import javax.vecmath.Vector2f;
import java.util.ArrayList;

public class PotentialCell extends Cell {
    float maxPotential;
    float potencialObstacle;
    float potentialAgent;
    float potentialGoal;
    float totalPotential;
    int interval;

    ArrayList<Object> objPotential;

    public PotentialCell(int interval, Vector2f point,float maxPotential) {
        super(interval, point);
        this.maxPotential = maxPotential;
        this.interval = interval;
        potencialObstacle = 0;
        potentialAgent = 0;
        potentialGoal = 0;
        totalPotential = 0;
        objPotential = new ArrayList<>();
    }

    public void setPotencialObstacle(){
        potencialObstacle = maxPotential;
        setTotalPotential();
    }

    public void setPotentialAgent(){
        potentialAgent = maxPotential;
        setTotalPotential();
    }

    public void setPotentialGoal(float distance){
        potentialGoal = distance;
        setTotalPotential();
    }

    public void setTotalPotential() {
        totalPotential = potencialObstacle + potentialAgent + potentialGoal;
    }

    public void addTotalPotential(float potential){
        totalPotential += potential;
    }

    public float getPotential(){
        return totalPotential;

    }

    public float getPotencialObstacle() {
        return potencialObstacle;
    }

    public float getPotencialPerson() {
        return potentialAgent;
    }

    public float getPotentialGoal() {
        return potentialGoal;
    }

    public int getInterval() {
        return interval;
    }
}
