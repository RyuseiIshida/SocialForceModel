package com.simulation.socialforce;
import java.util.*;


class SFWall{
    private SFVector end1, end2;
    public String id;
    public SFWall(SFVector x1, SFVector x2){
        this.end1=x1;
        this.end2=x2;
    }
    public SFWall(double x1, double y1, double x2, double y2){
        this.end1=new SFVector(x1, y1);
        this.end2=new SFVector(x2, y2);
    }
    public double length(){
        return Math.sqrt(Math.pow(end2.y-end1.y,2) + Math.pow(end2.x-end1.x,2));
    }
    final double distanceToPoint(SFVector point){
        return Math.abs(((end2.y - end1.y)*point.x) - ((end2.x - end1.x)*point.y)
                + (end2.x*end1.y) - (end1.x*end2.y))/this.length();
    }
    final SFVector pointClosest(SFVector point){
        SFVector AP = SFVector.subtract(point, end1);
        SFVector AB = SFVector.subtract(end2, end1);
        double magnitudeAB = Math.pow(AB.norm(),2);
        double ABAP = SFVector.dotProduct(AP, AB);
        double dist = ABAP/magnitudeAB;
        if(dist < 0) return end1;
        else if(dist > 1) return end2;
        else return SFVector.add(end1, AB.scale(dist));
    }
}