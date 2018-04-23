package com.simulation.socialforce;

import javax.vecmath.Vector2d;
import java.util.ArrayList;
import com.badlogic.gdx.graphics.g2d.Sprite;

/**
 * pedestrian class
 * Created by Fatema on 10/21/2016.
 */
public class CPedestrian implements IPedestrian{

    private static final double m_maxspeedfactor = 2.5;
    private static final double m_maxforce = 3.0;
    private static final double m_radius = 10;
    private Vector2d m_position;
    private Vector2d m_goal;
    private ArrayList<Vector2d> m_goals;
    private Vector2d m_velocity ;
    private double m_speed;
    private SocialForceModel l_env;
    private double m_maxspeed;
    private int m_controlossilation;
    private Sprite sprite;

    public CPedestrian( final Vector2d p_position, final double p_speed, final ArrayList<Vector2d> p_goal, final SocialForceModel p_env, Sprite sprites) {
        m_goals = p_goal;
        m_goal = p_goal.remove( 0 );
        m_position = p_position;
        m_speed = p_speed;
        m_velocity = CVector.scale( p_speed, CVector.direction( m_goal, m_position ) );
        l_env = p_env;
        m_maxspeed = p_speed * m_maxspeedfactor;
        m_controlossilation = 0;
        sprite = sprites;
        sprite.setPosition((float)m_position.x,(float)m_position.y);
    }


    public Sprite getSprite(){return sprite;}

    @Override
    public Vector2d getGoalposition() {
        return m_goal;
    }

    @Override
    public IPedestrian setGoalposition( final Vector2d p_position )
    {
        this.m_goal = p_position;
        return this;
    }

    @Override
    public Vector2d getPosition() {
        return m_position;
    }

    @Override
    public IPedestrian setPosition( final double p_x, final double p_y ) {
        this.m_position = new Vector2d( p_x, p_y );
        sprite.setPosition((float)m_position.x,(float)m_position.y);
        return this;
    }

    @Override
    public Vector2d getVelocity() {
        return m_velocity;
    }

    @Override
    public double getSpeed() {
        return m_speed;
    }


    @Override
    public IPedestrian setposX( final double p_posX ) {
        this.m_position.x = p_posX;
        sprite.setPosition((float)m_position.x,(float)m_position.y);
        return this;
    }

    @Override
    public IPedestrian setposY( final double p_posY ) {
        this.m_position.y = p_posY;
        sprite.setPosition((float)m_position.x,(float)m_position.y);
        return this;
    }

    @Override
    public Vector2d accelaration()
    {
        Vector2d l_repulsetoWall = new Vector2d( 0, 0 );
        Vector2d l_repulsetoOthers = new Vector2d( 0, 0 );
        Vector2d l_desiredVelocity = CVector.scale( this.m_maxspeed, CVector.direction( this.getGoalposition(), this.getPosition() ) );

        for ( int i = 0; i < l_env.getWallinfo().size(); i++ )
        {
            l_repulsetoWall = CVector.add( l_repulsetoWall, CForce.repulsewall( this, l_env.getWallinfo().get( i ), l_env.test ) );
        }


        for ( int i = 0; i < l_env.getPedestrianinfo().size(); i++ )
        {
            if( !l_env.getPedestrianinfo().get(i).equals( this ) )
            {
                l_repulsetoOthers = CVector.add( l_repulsetoOthers, CForce.repulseotherPed( this, l_env.getPedestrianinfo().get( i ), l_env.test ) );
            }
        }

        final Vector2d l_temp = CVector.add( CForce.drivingForce( l_desiredVelocity, this.getVelocity() ), l_repulsetoOthers );

        return CVector.truncate( CVector.add( l_temp, l_repulsetoWall ), m_maxforce );

    }


    public double getM_radius()
    {
        return m_radius;
    }


    @Override
    public IPedestrian call() throws Exception {

        final double l_check = CVector.sub( this.getGoalposition(), this.getPosition() ).length();

        if ( this.m_goals.isEmpty() ) { m_controlossilation ++; }

        if ( l_check <= this.getM_radius() * 0.5 )
        {
            this.m_velocity = new Vector2d(0, 0);
            if ( this.m_goals.size() > 0 )
            {
                this.m_goal = this.m_goals.remove( 0 );
                this.m_velocity = CVector.scale( m_maxspeed, CVector.normalize( CVector.add( this.m_velocity, this.accelaration() ) ) );
                this.m_position = CVector.add( m_position, m_velocity );
                sprite.setPosition((float)m_position.x,(float)m_position.y);
            }
        }
        else
        {
            if( m_controlossilation >= 1000 )
            {
                this.m_velocity = new Vector2d( 0, 0 );
            }
            else
            {
                this.m_velocity = CVector.scale( m_maxspeed, CVector.normalize( CVector.add( this.m_velocity, this.accelaration() ) ) );
                this.m_position = CVector.add( m_position, m_velocity );
                sprite.setPosition((float)m_position.x,(float)m_position.y);
            }
        }

        if( m_position.getX() > 800.0 ) {
            setposX( 0.0 );
            sprite.setPosition((float)m_position.x,(float)m_position.y);
        }
        if( m_position.getX() < 0.0 ) {
            setposX( 800.0 );
            sprite.setPosition((float)m_position.x,(float)m_position.y);
        }
        if( m_position.getY() > 600.0 ) {
            setposY( 0.0 );
            sprite.setPosition((float)m_position.x,(float)m_position.y);
        }
        if( m_position.getY() < 0.0 ) {
            setposY( 600.0);
            sprite.setPosition((float)m_position.x,(float)m_position.y);
        }

        return this;
    }

}
