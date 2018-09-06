package com.simulation.socialforce;

import javax.vecmath.Vector2f;
import java.util.ArrayList;
import java.util.Arrays;

public class Parameter {
    public String IfcModelPath = "/Users/rys9469/Documents/build_data/10-20room.ifc";
    public Vector2f scale = new Vector2f(800*2, 480*2);
    public static final double m_GaussianMean = 1.34;
    public static final double m_GaussianStandardDeviation = 0.26;
    public static final float view_phi_theta = 120;
    public static final float view_dmax = 800;
    public final ArrayList<Vector2f> exitVec = new ArrayList<>(Arrays.asList(new Vector2f(100, 810),new Vector2f(1500,810)));
    //public final ArrayList<Rect> arrayRect = new ArrayList<>();
    public final Rect wakumigi = new Rect(1500,80,10,700);
    public final Rect wakuhidari = new Rect(100,80,10,700);
    public final Rect wakuue = new Rect(100,900,1400,10);
    public final Rect wakuuehidari = new Rect(100,850,10,50);
    public final Rect wakuuemigi = new Rect(1500,850,10,60);
    public final Rect wakushita = new Rect(100,80,1400,10);
    public final Rect aida1 = new Rect(100,700,300,10);
    public final Rect aida2 = new Rect(500,700,600,10);
    public final Rect aida3 = new Rect(1200,700,300,10);
    public final Rect aida4 = new Rect(790,80,10,620);
    public final ArrayList<Rect> arrayRect = new ArrayList<>(Arrays.asList(wakumigi,wakuhidari,wakuue,wakushita,wakuuehidari,wakuuemigi,aida1,aida2,aida3,aida4));

    private final CStatic wallDownLine     = new CStatic(10,10,(int)scale.x-10,10);
    private final CStatic wallUpLine       = new CStatic(10,(int)scale.y-10,(int)scale.x-10,(int)scale.y-10);
    private final CStatic wallLeftLine     = new CStatic(10,10,10,(int)scale.y-10);
    private final CStatic wallRightLine    = new CStatic((int)scale.x-10,10,(int)scale.x-10,(int)scale.y-10);
    //private final CStatic exitLeftLine = new CStatic((int)exitVec.get(0).x-300,(int)exitVec.get(0).y-50,(int)exitVec.get(0).x-50,(int)exitVec.get(0).y-50);
    //private final CStatic exitRightLine = new CStatic((int)exitVec.get(0).x+50,(int)exitVec.get(0).y-50,(int)exitVec.get(0).x+300,(int)exitVec.get(0).y-50);
    public final ArrayList<CStatic> m_wall = new ArrayList<>(Arrays.asList(wallDownLine,wallUpLine,wallRightLine,wallLeftLine));
}
