package com.simulation.Potential;

import com.simulation.Potential.Cell.Cell;

import javax.vecmath.Vector2f;

public class PotentialCell extends Cell {
    float maxPotential;
    float obstaclePotential;
    float agentPotential;
    float goalPotential;
    float totalPotential;
    int interval;

    public PotentialCell(int interval, Vector2f point,float maxPotential) {
        super(interval, point);
        this.maxPotential = maxPotential;
        this.interval = interval;
        obstaclePotential = 0;
        agentPotential = 0;
        goalPotential = 0;
        totalPotential = 0;
    }

    public void setObstaclePotential(float value){
        obstaclePotential = value;
    }

    public void setAgentPotential(float value){
        agentPotential = value;
    }

    public void setGoalPotential(float value){
        goalPotential = value;
    }


    public float getPotential(){
        totalPotential = obstaclePotential + goalPotential + agentPotential;
        //totalPotential = totalPotential > maxPotential ? maxPotential : totalPotential;
        return totalPotential;
    }

    public float getObstaclePotential() {
        return obstaclePotential;
    }

    public float getAgentPotential() {
        return agentPotential;
    }

    public float getGoalPotential() {
        return goalPotential;
    }

    public int getInterval() {
        return interval;
    }
}