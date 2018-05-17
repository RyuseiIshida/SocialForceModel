package com.simulation.socialforce;

import javax.vecmath.Vector2f;
import java.util.ArrayList;
import com.badlogic.gdx.graphics.g2d.Sprite;

/**
 * pedestrian class
 * Created by Fatema on 10/21/2016.
 */
public class CPedestrian implements IPedestrian{

    private static final float m_maxspeedfactor = 2.5f;
    private static final float m_maxforce = 0.1f;
    private static final float m_radius = 20f;
    private Vector2f m_position;
    private Vector2f m_goal;
    private ArrayList<Vector2f> m_goals;
    private ArrayList<Vector2f> subGoal;
    private Vector2f m_velocity ;
    private float m_speed;
    private SocialForceModel l_env;
    private float m_maxspeed;
    private int m_controlossilation;
    private Sprite sprite;
    private boolean aisExitInfo;

    public CPedestrian(boolean isExitInfo,final Vector2f p_position, final float p_speed, Vector2f p_goal, final SocialForceModel p_env, Sprite sprites) {
        m_goal = p_goal;
        m_position = p_position;
        m_speed = p_speed;
        m_velocity = CVector.scale( p_speed, CVector.direction( m_goal, m_position ) );
        l_env = p_env;
        m_maxspeed = p_speed * m_maxspeedfactor;
        m_controlossilation = 0;
        aisExitInfo = isExitInfo;
        sprite = sprites;
        sprite.setPosition(m_position.x-32/2,m_position.y-32/2);
    }

    public void setSubGoal(){

    };

    public void setExitInfo(boolean bool) { aisExitInfo = bool;}
    public boolean getisExitInfo(){ return aisExitInfo;}
    public Sprite getSprite(){return sprite;}

    @Override
    public Vector2f getGoalposition() {
        return m_goal;
    }

    @Override
    public IPedestrian setGoalposition(  Vector2f p_position )
    {
        this.m_goal = p_position;
        return this;
    }

    @Override
    public Vector2f getPosition() {
        return m_position;
    }

    @Override
    public IPedestrian setPosition( final float p_x, final float p_y ) {
        this.m_position = new Vector2f( p_x, p_y );
        sprite.setPosition(m_position.x-16,m_position.y-16);
        return this;
    }

    @Override
    public Vector2f getVelocity() {
        return m_velocity;
    }

    @Override
    public float getSpeed() {
        return m_speed;
    }


    @Override
    public IPedestrian setposX( final float p_posX ) {
        this.m_position.x = p_posX;
        sprite.setPosition(m_position.x-16,m_position.y-16);
        return this;
    }

    @Override
    public IPedestrian setposY( final float p_posY ) {
        this.m_position.y = p_posY;
        sprite.setPosition(m_position.x-16,m_position.y-16);
        return this;
    }

    @Override
    public Vector2f accelaration()
    {
        Vector2f l_repulsetoWall = new Vector2f( 0, 0 );
        Vector2f l_repulsetoOthers = new Vector2f( 0, 0 );
        Vector2f l_desiredVelocity = CVector.scale( this.m_maxspeed, CVector.direction( this.getGoalposition(), this.getPosition() ) );

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

        final Vector2f l_temp = CVector.add( CForce.drivingForce( l_desiredVelocity, this.getVelocity() ), l_repulsetoOthers );

        return CVector.truncate( CVector.add( l_temp, l_repulsetoWall ), m_maxforce );

    }


    public float getM_radius()
    {
        return m_radius;
    }


    @Override
    public IPedestrian call() throws Exception {

        final float l_check = CVector.sub( this.getGoalposition(), this.getPosition() ).length();

        //if ( this.m_goals.isEmpty() ) { m_controlossilation ++; }

        if ( l_check <= this.getM_radius() * 0.5 )
        {
            this.m_velocity = new Vector2f(0, 0);
            if ( this.m_goals.size() > 0 )
            {
                this.m_goal = this.m_goals.remove( 0 );
                this.m_velocity = CVector.scale( m_maxspeed, CVector.normalize( CVector.add( this.m_velocity, this.accelaration())));
                this.m_position = CVector.add( m_position, m_velocity );
                sprite.setPosition(m_position.x-16,m_position.y-16);
            }
        }
        else
        {
            if( m_controlossilation >= 1000 )
            {
                this.m_velocity = new Vector2f( 0, 0 );
            }
            else
            {
                this.m_velocity = CVector.scale( m_maxspeed, CVector.normalize( CVector.add( this.m_velocity, this.accelaration() ) ) );
                this.m_position = CVector.add( m_position, m_velocity );
                sprite.setPosition(m_position.x-16,m_position.y-16);
            }
        }

        if( m_position.getX() > 1440.0 ) {
            setposX( 0.0f );
            sprite.setPosition(m_position.x-16,m_position.y-16);
        }
        if( m_position.getX() < 0.0 ) {
            setposX( 800.0f );
            sprite.setPosition(m_position.x-16,m_position.y-16);
        }
        if( m_position.getY() > 900.0 ) {
            setposY( 0.0f );
            sprite.setPosition(m_position.x-16,m_position.y-16);
        }
        if( m_position.getY() < 0.0 ) {
            setposY( 600.0f);
            sprite.setPosition(m_position.x-16,m_position.y-16);
        }

        return this;
    }

}
