package com.simulation.socialforce;

import javax.vecmath.Vector2f;


public class CStatic {

    private final int m_X1;
    private final int m_Y1;
    private final int m_X2;
    private final int m_Y2;
    private final int m_width;
    private final int m_height;

    public CStatic(final int p_x1, final int p_y1, final int p_x2, final int p_y2) {
        m_X1 = p_x1;
        m_Y1 = p_y1;
        m_X2 = p_x2;
        m_Y2 = p_y2;
        m_width = p_x2 - p_x1;
        m_height = p_y2 - p_y1;
    }

    public final int getX1() {
        return m_X1;
    }

    public final int getY1() {
        return m_Y1;
    }

    public final int getX2() {
        return m_X2;
    }

    public final int getY2() {
        return m_Y2;
    }

    public final CWall getwall1() {
        return new CWall(new Vector2f(m_X1, m_Y1), new Vector2f(m_X1 + m_width, m_Y1));
    }

    public final CWall getwall2() {
        return new CWall(new Vector2f(m_X1, m_Y1), new Vector2f((m_X1), (m_Y1 + m_height)));
    }


    public final CWall getwall3() {
        return new CWall(new Vector2f((m_X1 + m_width), (m_Y1 + m_height)), new Vector2f(m_X1 + m_width, m_Y1));
    }

    public final CWall getwall4() {
        return new CWall(new Vector2f((m_X1 + m_width), (m_Y1 + m_height)), new Vector2f((m_X1), (m_Y1 + m_height)));
    }

    public final int getWidth() {
        return m_width;
    }


    public final int getHeight() {
        return m_height;
    }
}
