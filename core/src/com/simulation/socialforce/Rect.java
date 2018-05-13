package com.simulation.socialforce;

import javax.vecmath.Vector2d;

public class Rect {
    Vector2d vector;
    double x;
    double y;
    double width;
    double height;
    Vector2d leftButtom;
    Vector2d leftTop;
    Vector2d rightButtom;
    Vector2d rightTop;
    CStatic force;
    Rect(double X, double Y, double w, double h){
        x = X;
        y = Y;
        width = w;
        height = h;
        vector = new Vector2d(x,y);
        leftButtom = new Vector2d(x,y);
        leftTop = new Vector2d(x,y+h);
        rightButtom = new Vector2d(x+w,y);
        rightTop = new Vector2d(x+w,y+h);
        force = new CStatic((int)x,(int)y,(int)(x+w),(int)(y+h));
    }
}
