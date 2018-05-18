package com.simulation.socialforce;

import javax.vecmath.Vector2f;
import java.util.ArrayList;
import java.util.Arrays;

public class Parameter {
    public Vector2f scale = new Vector2f(800*2, 480*2);
    public static final double m_GaussianMean = 1.34;
    public static final double m_GaussianStandardDeviation = 0.26;
    public static final float view_phi_theta = 120;
    public static final float view_dmax = 400;
    public final ArrayList<Vector2f> exitVec = new ArrayList<>(Arrays.asList(new Vector2f(800, 800)));
    public final ArrayList<Rect> arrayRect = new ArrayList<>();
    //public final ArrayList<Rect> arrayRect = new ArrayList<>(Arrays.asList(new Rect(300,200,400,300)));
    private final CStatic wallDownLine     = new CStatic(10,10,(int)scale.x-10,10);
    private final CStatic wallUpLine       = new CStatic(10,(int)scale.y-10,(int)scale.x-10,(int)scale.y-10);
    private final CStatic wallLeftLine     = new CStatic(10,10,10,(int)scale.y-10);
    private final CStatic wallRightLine    = new CStatic((int)scale.x-10,10,(int)scale.x-10,(int)scale.y-10);
    //private final CStatic exitLeftLine = new CStatic((int)exitVec.get(0).x-300,(int)exitVec.get(0).y-50,(int)exitVec.get(0).x-50,(int)exitVec.get(0).y-50);
    //private final CStatic exitRightLine = new CStatic((int)exitVec.get(0).x+50,(int)exitVec.get(0).y-50,(int)exitVec.get(0).x+300,(int)exitVec.get(0).y-50);
    public final ArrayList<CStatic> m_wall = new ArrayList<>(Arrays.asList(wallDownLine,wallUpLine,wallRightLine,wallLeftLine));
}
