package com.simulation.socialforce;

import com.simulation.Potential.Obstacle.Obstacle;
import com.simulation.Potential.PotentialManager;
import com.simulation.Potential.PotentialMap;

import javax.vecmath.Vector2f;
import java.util.ArrayList;
import java.util.Arrays;

public class Parameter {
    public String IfcModelPath = "/Users/rys9469/Documents/build_data/10-20room.ifc";
    public static final Vector2f scale = new Vector2f(800 * 2, 480 * 2);
    public static final int STEPINTERVAL = 60;//step60 ＝ 1second
    public static final int CELL_INTERVAL = 10;
    public static final int MAXPOTENTIAL = 5;
    public static final int initPedNum = 50;
    public static final int goalPed = 20;
    public static final float judgeFollowNum = 5;
    public static final float judgeFollowNum2 = 1;
    public static final double m_GaussianMean = 1.34;
    public static final double m_GaussianStandardDeviation = 0.26;
    public static boolean view_Renderer = false;
    public static final float view_phi_theta = 120;
    public static final float view_dmax = 400;

    public static ArrayList<Vector2f> exitVec = new ArrayList<>(Arrays.asList(new Vector2f(60, 40)));
    //public static final ArrayList<Vector2f> exitVec = new ArrayList<>(Arrays.asList(new Vector2f(600, 40)));

    public static PotentialManager potentialManager = new PotentialManager();
    public static final float VALUEOBSTACLEPOTENTIAL = 9;


    public static final ArrayList<CStatic> m_wall = new ArrayList<>(Arrays.asList());
}
