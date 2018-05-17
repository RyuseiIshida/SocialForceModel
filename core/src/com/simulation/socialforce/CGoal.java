package com.simulation.socialforce;

import javax.vecmath.Vector2f;
import java.util.ArrayList;

public class CGoal {

    private ArrayList<Vector2f> m_goals = new ArrayList<>();

    public CGoal( final float l_x, final float l_x1, final float l_x2, final float l_x3  )
    {
        //m_goals.add( new Vector2f( l_x , 20 + ( Math.random() * 100 )) );
        //m_goals.add( new Vector2f( l_x , 160 + ( Math.random() * 60 ) ) );//20
        //m_goals.add( new Vector2f( l_x, 265 ) );
        //m_goals.add( new Vector2f( l_x2 + ( 20 + Math.random() * l_x3 ) , 400 + ( Math.random() * 140 ) ) );
        m_goals.add( new Vector2f( l_x , l_x1) );
        m_goals.add( new Vector2f( l_x , l_x1) );
        m_goals.add( new Vector2f( l_x , l_x1) );
    }
    public ArrayList<Vector2f> get_goals()
    {
        return m_goals;
    }
}
