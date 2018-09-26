package com.simulation.socialforce;

import javax.vecmath.Vector2f;

public class Rect {
    //Vector2f vector;
    //float x;
    //float y;
    private int width;
    private int height;
    private Vector2f leftButtom;
    private Vector2f leftTop;
    private Vector2f rightButtom;
    private Vector2f rightTop;
    private CStatic force;
    Rect(float x, float y, int w, int h){
        //x = X;
        //y = Y;
        width = w;
        height = h;
        //vector = new Vector2f(x,y);
        leftButtom = new Vector2f(x,y);
        leftTop = new Vector2f(x,y+h);
        rightButtom = new Vector2f(x+w,y);
        rightTop = new Vector2f(x+w,y+h);
        force = new CStatic((int)x,(int)y,(int)(x+w),(int)(y+h));
    }

    public int getWidth(){
        return this.width;
    }

    public int getHeight(){
        return this.height;
    }

    public Vector2f getLeftButtom(){
        return this.leftButtom;
    }

    public Vector2f getLeftTop(){
        return this.leftTop;
    }

    public Vector2f getRightButtom(){
        return this.rightButtom;
    }

    public Vector2f getRightTop(){
        return rightTop;
    }

    public CStatic getForce(){
        return force;
    }

}
