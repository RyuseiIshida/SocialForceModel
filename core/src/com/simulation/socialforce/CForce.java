package com.simulation.socialforce;

import javax.vecmath.Vector2f;
import javax.vecmath.Vector2f;
import java.util.ArrayList;

public class CForce {

    private static final float m_detta = 2f;
    private static final float m_lamda = 0.2f;
    private static final float m_repulsefactortoped = 2.1f;
    private static final float m_sigma = 0.3f;
    private static final float m_repulsefactortowall = 1f;
    private static final float m_R = 0.2f;


    static Vector2f drivingForce( final Vector2f p_desiredvelocity, final Vector2f p_current )
    {
        return CVector.sub( p_desiredvelocity, p_current );
    }

    static float calculateb( final CPedestrian p_self, final CPedestrian p_other )
    {
        final Vector2f l_tempvector = CVector.sub( p_self.getPosition(), p_other.getPosition() );

        final float l_tempvalue = CVector.sub( l_tempvector, CVector.scale( m_detta, p_other.getVelocity() ) ).length();

        return (float)Math.sqrt( ( l_tempvector.length() + l_tempvalue ) * ( l_tempvector.length() + l_tempvalue )
                                         - ( p_other.getSpeed() * p_other.getSpeed() ) );
    }

    static Vector2f repulseothers( final CPedestrian p_self, final CPedestrian p_other )
    {
        final Vector2f l_normvector = CVector.direction( p_other.getPosition(), p_self.getPosition() );

        final float l_temp = - calculateb( p_self, p_other ) / m_sigma;

        return CVector.scale( m_repulsefactortoped * (float)Math.exp( l_temp ), l_normvector );
    }

    static Vector2f repulseotherPed( final CPedestrian p_self, final CPedestrian p_other, ArrayList<COutput> test )
    {
        final float l_radious = p_self.getM_radius() * 0.5f + p_other.getM_radius() * 0.5f ;
        final float l_temp = l_radious - CVector.distance( p_self.getPosition(), p_other.getPosition() );
        //System.out.println( p_self + " dis " + l_temp );
        //if ( l_temp >= -4 ) {
        //test.add( new COutput( p_self.getPosition().x,p_self.getPosition().y, m_repulsefactortoped * Math.exp( l_temp / m_sigma ) * anisotropic_character( p_self.getPosition(),
               // p_other.getPosition() ) ) );
           // System.out.println( p_self.getPosition()+" self "+m_repulsefactortoped * Math.exp( l_temp / m_sigma )*anisotropic_character( p_self.getPosition(),
                   // p_other.getPosition() ) +" other "+ p_other.getPosition());
        return CVector.scale( m_repulsefactortoped * (float)Math.exp( l_temp / m_sigma ) * anisotropic_character( p_self.getPosition(),
                    p_other.getPosition() ), CVector.normalize( CVector.sub( p_self.getPosition(), p_other.getPosition() ) ) );
        //}

        //return new Vector2f( 0, 0 );
    }

    static Vector2f repulsewall( final CPedestrian p_self, final CWall p_wall, ArrayList<COutput> test )
    {
        final Vector2f l_normposition = CVector.perpendicular_derection( p_self.getPosition(), p_wall );

        if ( CVector.check( l_normposition, p_wall.getPoint1(), p_wall.getPoint2() ) )
        {
            final float l_temp = p_self.getM_radius() - CVector.distance( p_self.getPosition(), l_normposition );
            float p = m_repulsefactortowall * (float)Math.exp( l_temp / m_R );
            if ( l_temp >= - 4 ) {
                //test.add( new COutput( p_self.getPosition().x,p_self.getPosition().y, p ) );

                //System.out.println( p_self.getPosition()+" self "+m_repulsefactortowall * Math.exp( l_temp / m_R )+" wall "+ l_normposition);
                return CVector.scale( m_repulsefactortowall * (float)Math.exp( l_temp / m_R ), CVector.normalize(
                        CVector.sub( p_self.getPosition(), l_normposition ) ) );
            }
        }
        return new Vector2f(0,0);
    }

    static float anisotropic_character( final Vector2f p_v1, final Vector2f p_v2 )
    {
        return m_lamda + ( 1 -m_lamda )*( ( 1 + CVector.angle( p_v1, p_v2 ) ) * 0.5f );
    }

}
