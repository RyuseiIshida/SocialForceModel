package com.simulation.socialforce;

class Test{
    public static void main(String [] args){
        double xA = 0;
        double yA = 0;
        double xB = -1;
        double yB = -1;
        double arcLineMargineRadian = getRedian(xA,yA,xB,yB);
        System.out.println("radian = " + arcLineMargineRadian);
        double degree = arcLineMargineRadian * 180d / Math.PI;
        System.out.println("degree = " + degree);
    }
    public static double getRedian(double x, double y, double x2, double y2){
        double radian = Math.atan2(y2-y, x2-x);
        return radian;
    }
}
