package com.simulation.socialforce;

import javax.vecmath.Vector2f;

public class Rect {
    Vector2f vector;
    float x;
    float y;
    float width;
    float height;
    Vector2f leftButtom;
    Vector2f leftTop;
    Vector2f rightButtom;
    Vector2f rightTop;
    CStatic force;
    Rect(float X, float Y, float w, float h){
        x = X;
        y = Y;
        width = w;
        height = h;
        vector = new Vector2f(x,y);
        leftButtom = new Vector2f(x,y);
        leftTop = new Vector2f(x,y+h);
        rightButtom = new Vector2f(x+w,y);
        rightTop = new Vector2f(x+w,y+h);
        force = new CStatic((int)x,(int)y,(int)(x+w),(int)(y+h));
    }
}
