package com.simulation.socialforce;

import javax.vecmath.Vector2d;

public class CWall {

    private Vector2d m_point1;
    private Vector2d m_point2;

    public CWall( final Vector2d p_point1, final Vector2d p_point2 )
    {
        m_point1 = p_point1;
        m_point2 = p_point2;
    }

    public Vector2d getPoint1() {
        return m_point1;
    }

    public Vector2d getPoint2() {
        return m_point2;
    }
}
