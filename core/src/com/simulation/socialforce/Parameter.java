package com.simulation.socialforce;

import javax.vecmath.Vector2f;
import java.util.ArrayList;
import java.util.Arrays;

public class Parameter {
    public String IfcModelPath = "/Users/rys9469/Documents/build_data/10-20room.ifc";
    public static final Vector2f scale = new Vector2f(800*2, 480*2);
    public static final int CELL_INTERVAL = 10;
    public static final int initPedNum = 50;
    public static final double m_GaussianMean = 1.34;
    public static final double m_GaussianStandardDeviation = 0.26;
    public static final boolean view_Renderer = true;
    public static final float view_phi_theta = 120;
    public static final float view_dmax = 400;
    public final ArrayList<Vector2f> exitVec = new ArrayList<>(Arrays.asList(new Vector2f(1500,810)));
    public final ArrayList<Rect> arrayRect = new ArrayList<>();
//    public final ArrayList<Rect> arrayRect = new ArrayList<>(Arrays.asList(wakumigi,wakuhidari,wakuue,wakushita,wakuuehidari,wakuuemigi,aida1,aida2,aida3,aida4));
    private static final CStatic wallDownLine     = new CStatic(0,0,(int)scale.x,0);
    private static final CStatic wallUpLine       = new CStatic(0,(int)scale.y,(int)scale.x,(int)scale.y);
    private static final CStatic wallLeftLine     = new CStatic(0,0,0,(int)scale.y);
    private static final CStatic wallRightLine    = new CStatic((int)scale.x,0,(int)scale.x,(int)scale.y);
    //private final CStatic exitLeftLine = new CStatic((int)exitVec.get(0).x-300,(int)exitVec.get(0).y-50,(int)exitVec.get(0).x-50,(int)exitVec.get(0).y-50);
    //private final CStatic exitRightLine = new CStatic((int)exitVec.get(0).x+50,(int)exitVec.get(0).y-50,(int)exitVec.get(0).x+300,(int)exitVec.get(0).y-50);
    public static final ArrayList<CStatic> m_wall = new ArrayList<>(Arrays.asList(wallDownLine,wallUpLine,wallRightLine,wallLeftLine));
}
