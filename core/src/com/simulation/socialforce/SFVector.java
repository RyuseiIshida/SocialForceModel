package com.simulation.socialforce;

import java.lang.Math;
import java.io.*;

public class SFVector{
    public double x=0, y=0, z=0;
    public SFVector(double xInput, double yInput, double zInput){
        this.x=xInput;
        this.y=yInput;
        this.z=zInput;
    }
    public SFVector(double xInput, double yInput){
        this.x=xInput;
        this.y=yInput;
    }
    public SFVector(SFVector q){
        this.x=q.x;
        this.y=q.y;
    }
    public SFVector(){

    }
    final double norm(){
        return Math.sqrt(Math.pow(this.x, 2)+Math.pow(this.y, 2)+Math.pow(this.z, 2));
    }
    final SFVector scale(double scale){
        return new SFVector(scale * this.x, scale * this.y, scale * this.z);
    }

    //Vector algebra
    //Vector addition
    public static SFVector add(SFVector a, SFVector b){
        return new SFVector(a.x + b.x, a.y + b.y, a.z + b.z);
    }
    //Vector subtraction
    public static SFVector subtract(SFVector a, SFVector b){
        return new SFVector(a.x - b.x, a.y - b.y, a.z - b.z);
    }
    //Vector dot product
    public static double dotProduct(SFVector a, SFVector b){
        return (a.x * b.x) + (a.y * b.y) + (a.z * b.z);
    }
    //Vector cross product
    public static SFVector crossProduct(SFVector a, SFVector b){
        return new SFVector(a.y*b.z - a.z*b.y , a.z*b.x - a.x*b.z , a.x*b.y - a.y*b.x);
    }
    //Direction from a to b
    public static SFVector direction(SFVector a, SFVector b){
        SFVector temp = SFVector.subtract(b, a);
        temp = temp.scale(1/SFVector.subtract(b, a).norm());
        return temp;
    }
    //Return a string of 2d cartesian coordinate
    public String toString(){
        return "(" + this.x + ", " + this.y + ")";
    }
    final void stop(){
        this.x = 0;
        this.y = 0;
    }

    public static void main(String[] args){
        SFVector x = new SFVector(1,2);
        SFVector y = new SFVector(0,0);
        System.out.println(SFVector.direction(x,y).toString());
    }
}