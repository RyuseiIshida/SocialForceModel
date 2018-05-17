package com.simulation.socialforce;

import javax.vecmath.Vector2f;

final class CVector {

    static Vector2f direction( final Vector2f p_v1, final Vector2f p_v2 )
    {
        return normalize( sub( p_v1, p_v2 ) );
    }


    static Vector2f scale( final float p_speed, final Vector2f p_v1 )
    {
        return new Vector2f( p_v1.getX() * p_speed, p_v1.getY() * p_speed );
    }


    static Vector2f add( final Vector2f p_v1, final Vector2f p_v2 )
    {
        return new Vector2f( p_v1.getX() + p_v2.getX(), p_v1.getY() + p_v2.getY() );
    }

    static Vector2f add( final Vector2f p_v1, final Vector2f p_v2, final Vector2f p_v3 )
    {
        return new Vector2f( p_v1.getX() + p_v2.getX() + p_v3.getX(), p_v1.getY() + p_v2.getY() + p_v3.getY() );
    }


    static Vector2f sub( final Vector2f p_v1, final Vector2f p_v2 )
    {
        return new Vector2f( p_v1.getX() - p_v2.getX(), p_v1.getY() - p_v2.getY() );
    }

    static float distance( final Vector2f p_v1, final Vector2f p_v2 )
    {
        return sub( p_v1, p_v2 ).length();
    }

    static Vector2f staticObjectCentre( final Vector2f p_v1, final Vector2f p_v2 )
    {
        return new Vector2f( ( p_v1.getX() + p_v2.getX() )/2.0f , ( p_v1.getY() + p_v2.getY() )/2.0f );
    }


    static float cosdheta( final Vector2f p_v1, final Vector2f p_v2, final Vector2f p_v3 )
    {
        return ( direction( p_v1, p_v2 ).dot( sub( p_v3, p_v2 ) ) )/
                ( direction( p_v1, p_v2 ).length() * sub( p_v3, p_v2 ).length() );

    }

    static float angle( final Vector2f p_v1, final Vector2f p_v2 )
    {
        return p_v1.dot( p_v2 ) / p_v1.length() * p_v2.length();

    }

    static Vector2f perpendicular_derection( final Vector2f p_position, final CWall l_wall )
    {
        final Vector2f l_wallPoint = l_wall.getPoint1();
        final Vector2f l_walldirection = CVector.normalize( CVector.sub( l_wall.getPoint2(), l_wall.getPoint1() ) );
        final float l_check = CVector.sub( p_position, l_wallPoint ).dot( l_walldirection );

        return CVector.add( l_wallPoint, CVector.scale( l_check, l_walldirection ) );
    }

    static Vector2f truncate( final Vector2f p_vector, final float p_scalefactor ) {
        float l_check;

        l_check = p_scalefactor / p_vector.length();
        l_check = l_check < 1.0f ? 1.0f : 1/l_check;

        return CVector.scale( l_check, p_vector );
    }

    static boolean check( final Vector2f p_point, final Vector2f p_wallpoint1, final Vector2f p_wallpoint2 )
    {
        final float l_wall2Towall1 = CVector.sub( p_wallpoint2, p_wallpoint1 ).length();
        final float l_pointTowall1 = CVector.sub( p_point, p_wallpoint1 ).length();
        final float l_pointTowall2 = CVector.sub( p_wallpoint2, p_point ).length();

        return ( l_wall2Towall1 == l_pointTowall1 + l_pointTowall2 ) ? true : false;
    }

    static Vector2f normalize( final Vector2f p_vector ) {

        Vector2f l_temp = new Vector2f( 0, 0 );

        if ( p_vector.length() != 0 )
        {
            l_temp.x = p_vector.x / p_vector.length();
            l_temp.y = p_vector.y / p_vector.length();
        }

        return l_temp;
    }

    public static Vector2f distanceToWall( final Vector2f p_position, final Vector2f p_wall1, final Vector2f p_wall2 )
    {
        if( CVector.sub( p_position, p_wall1 ).dot( CVector.direction( p_wall2, p_wall1 ) ) <= 0 )
        {
            return CVector.sub( p_wall1, p_position );
        }
        else if( CVector.sub( p_position, p_wall1 ).dot( CVector.direction( p_wall2, p_wall1 ) ) > 0 && CVector.sub( p_position,
                            p_wall1 ).dot( CVector.direction( p_wall2, p_wall1 ) ) <= CVector.sub( p_wall2, p_wall1 ).length() )
        {
            return CVector.sub( CVector.sub( p_wall1, p_position ), CVector.scale( CVector.sub( p_wall1, p_position )
                            .dot( CVector.direction( p_wall2, p_wall1 ) ), CVector.direction( p_wall2, p_wall1 ) ) );
        }
        else return CVector.sub( p_wall2, p_position );

    }

}
