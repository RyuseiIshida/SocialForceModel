package com.simulation.socialforce;

import javax.vecmath.Vector2f;
import java.util.concurrent.Callable;

public interface IPedestrian extends Callable<IPedestrian>{


    /**
     * returns the current goal position
     * @return goal position
     **/
    Vector2f getGoalposition();

    /**
     * set the goal position
     * @return the object itself
     **/
    IPedestrian setGoalposition(Vector2f p_position);

    /**
     * returns the current position
     * @return current position
     **/
    Vector2f getPosition();

    /**
     * set the current position
     * @return the object itself
     **/
    IPedestrian setPosition(final float p_x, final float p_y);

    /**
     * returns the current velocity
     * @return current velocity
     **/
    Vector2f getVelocity();

    /**
     * returns the current speed
     * @return current speed
     **/
    float getSpeed();

    /**
     * set the current position's X
     * @return the object itself
     **/
    IPedestrian setposX(final float p_posX);

    /**
     * set the current position's Y
     * @return the object itself
     **/
    IPedestrian setposY(final float p_posY);

    /**
     * calculate accelaration
     * @return accelaration
     **/
    Vector2f accelaration();

}
