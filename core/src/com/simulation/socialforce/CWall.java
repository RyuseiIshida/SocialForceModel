package com.simulation.socialforce;

import javax.vecmath.Vector2f;

public class CWall {

    private Vector2f m_point1;
    private Vector2f m_point2;

    public CWall(final Vector2f p_point1, final Vector2f p_point2) {
        m_point1 = p_point1;
        m_point2 = p_point2;
    }

    public Vector2f getPoint1() {
        return m_point1;
    }

    public Vector2f getPoint2() {
        return m_point2;
    }
}
