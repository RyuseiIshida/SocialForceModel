package com.simulation.Potential;

import com.simulation.Cell.Cell;

import javax.vecmath.Vector2f;
import java.util.ArrayList;

public class PotentialCell extends Cell {
    float maxPotential;
    float potencialObstacle;
    float potentialAgent;
    int interval;

    ArrayList<Object> objPotential;

    public PotentialCell(int interval, Vector2f point,float maxPotential) {
        super(interval, point);
        this.maxPotential = maxPotential;
        this.interval = interval;
        potencialObstacle = 0;
        potentialAgent = 0;
        objPotential = new ArrayList<>();
    }

    public void setPotencialObstacle(){
        potencialObstacle = maxPotential;
    }

    public void setPotentialAgent(){
        potentialAgent = maxPotential;
    }

    public float getPotential(){
        return potencialObstacle + potentialAgent;

    }

    public float getPotencialObstacle() {
        return potencialObstacle;
    }

    public float getPotencialPerson() {
        return potentialAgent;
    }

    public int getInterval() {
        return interval;
    }
}
