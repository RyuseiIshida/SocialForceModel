package com.simulation.socialforce;

import javax.swing.*;
import javax.vecmath.Vector2d;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.util.ArrayList;
import java.util.Random;
import java.util.stream.IntStream;


public class CEnvironment{

    private Graphics2D graphics2d;
    private Random rand = new Random();
    private static final double m_GaussianMean = 1.34;
    private static final double m_GaussianStandardDeviation = 0.26;
    private ArrayList<CPedestrian> m_pedestrian = new ArrayList<CPedestrian>();
    private ArrayList<CStatic> m_wall = new ArrayList<>( 2 );
    private ArrayList<CWall> m_walledge = new ArrayList<>( );
    public ArrayList<COutput> test = new ArrayList<>();

    public CEnvironment() {


        //first test

        m_wall.add( new CStatic( 0, 190, 170, 150 ) );
        m_wall.add( new CStatic( 600, 190, 182, 150 ) );



        m_wall.forEach( i->
        {
            m_walledge.add(i.getwall1());
            m_walledge.add(i.getwall2());
            m_walledge.add(i.getwall3());
            m_walledge.add(i.getwall4());
        }
        );



        //first test
/*
        IntStream.range( 0, 150 )
                .forEach( i -> m_pedestrian.add( new CPedestrian( new Vector2d( 0 + ( Math.random() * 100 ), 100 + ( Math.random() * 50 ) ),
                        rand.nextGaussian() * m_GaussianStandardDeviation + m_GaussianMean, new CGoal( 415, 250, 0, 100 ).get_goals(), this ) ) );

        IntStream.range( 0, 150 )
                .forEach( i -> m_pedestrian.add( new CPedestrian( new Vector2d( 400 + ( Math.random() * 100 ), 100 + ( Math.random() * 50 ) ),
                        rand.nextGaussian() * m_GaussianStandardDeviation + m_GaussianMean, new CGoal( 310, 350, 550, 200 ).get_goals(), this ) ) );

*/
    }



    public ArrayList<CPedestrian> getPedestrianinfo()
    {
        return m_pedestrian;
    }


    public ArrayList<CWall> getWallinfo()
    {
        return m_walledge;
    }

    public void update() {

            m_pedestrian.stream()
                    .parallel()
                    .forEach( j ->
                    {
                        try
                        {
                            j.call();
                        }
                        catch ( final Exception l_exception ) {}
                    });
    }
}
