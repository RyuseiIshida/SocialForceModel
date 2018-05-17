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
    //private final ArrayList<Vector2f> exitVec = new ArrayList<>(Arrays.asList(new Vector2f(30,230),new Vector2f(700, 230)));
    public final ArrayList<Vector2f> exitVec = new ArrayList<>(Arrays.asList(new Vector2f(500, 600)));
    public final ArrayList<Rect> arrayRect = new ArrayList<>();
    //public final ArrayList<Rect> arrayRect = new ArrayList<>(Arrays.asList(new Rect(300,200,400,300)));
    public final CStatic wallDownLine     = new CStatic(0,0,0,0);
    public final CStatic wallUpLine       = new CStatic(0,0,0,0);
    public final CStatic wallRightLine    = new CStatic(0,0,0,0);
    public final CStatic wallexitDownLine = new CStatic(0,0,0,0);
    public final CStatic wallexitUpLine   = new CStatic(0,0,0,0);
//    private final CStatic wallDownLine     = new CStatic(0,0,800*2,0);
//    private final CStatic wallUpLine       = new CStatic(0,480*2,800*2,480*2);
//    private final CStatic wallLeftLine = new CStatic(0,0,0,480*2);
//    private final CStatic wallRightLine    = new CStatic(800*2,0,800*2,480*2);

    //private final CStatic wallDownLine     = new CStatic(150,30,750,30);
    //private final CStatic wallUpLine       = new CStatic(150,450,750,450);
    //private final CStatic wallRightLine    = new CStatic(750,30,750,450);
    //private final CStatic wallexitDownLine = new CStatic(150,30,150,200);
    //private final CStatic wallexitUpLine   = new CStatic(150,250,150,450);
}
