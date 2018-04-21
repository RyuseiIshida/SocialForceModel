package com.simulation.socialforce;

import com.badlogic.gdx.graphics.g2d.Sprite;

import java.lang.Math;
import java.util.*;

public class SFAgent{
    private SFVector pos, vel, max_vel;
    private int id;
    private double tau, mass = 1;
    private Sprite sprite;    private SFWall closestWall;
    private SFVector closestWallPoint;
    //parameters

    private double b = 0.1;
    private double interactionFactor = 0.35;
    private double n = 2, n_prime = 3;

    private LinkedList<SFWaypoint> destination = new LinkedList<SFWaypoint>();
    public SFAgent(int id, double tau, SFVector pos, LinkedList<SFWaypoint> path,
                   SFVector max_vel, double mass, Sprite sprite)
    {
        this.id = id;
        this.tau = tau;
        this.pos = pos;
        this.destination = path; //ゴール
        this.max_vel = max_vel; //速度
        this.mass = mass;
        this.sprite = sprite;
        sprite.setPosition((float)pos.x, (float)pos.y);
        //initialize velocity to direction of first waypoint
        if(path.size()>0) vel = SFVector.direction(this.pos, path.getFirst()).scale(this.max_vel.norm());
        else vel = new SFVector(0,0);
    }

    public void move(LinkedList<SFAgent> agents, LinkedList<SFWall> walls){
        if(destination.size()<1) return;
        else{
            //movement is euler method
            //p = p + v * h
            pos = SFVector.add(pos, vel.scale(tau));
            //v = 0.5 * v + a * h;
            SFVector accel = SFVector.add(SFVector.add(desiredForce(), wallForce(walls)), pedForce(agents));
            vel = SFVector.add(vel.scale(0.5), accel.scale(tau));
            if(this.destination.getFirst().inWaypoint(this.pos)) destination.removeFirst();
            this.sprite.setPosition((float)pos.x, (float)pos.y);
        }
    }

    /*
    public void move(LinkedList<SFAgent> agents){
        for(SFAgent agent: agents){
            agent.pos.x -= 1;
            agent.sprite.setPosition((float)agent.pos.x,(float)agent.pos.y);
            System.out.println(agent.pos.x);

        }
    }
    */

    public void printInfo(){
        System.out.println("ID: " + id);
        System.out.println("POS: " + pos.toString());
        System.out.println("VEL: " + vel.toString());
        System.out.println("---------");
    }
    public String getInfo(){
        return new String("ID: " + id + "\n" +
                "POS: " + pos.toString() + "\n" +
                "VEL: " + vel.toString() + "\n" +
                "---------\n");
    }
    public SFVector desiredForce(){
        SFVector desired;
        desired = SFVector.direction(this.pos, this.destination.getFirst()).scale(this.max_vel.norm());
        desired = SFVector.subtract(desired, this.vel);
        desired = desired.scale(this.mass/this.tau);
        if(desired.norm()>this.max_vel.norm()) desired = this.max_vel;
        desired = desired.scale(mass);
        return desired;
    }
    final double distanceToAgent(SFAgent x){
        return SFVector.subtract(x.pos, this.pos).norm();
    }

    public SFVector getPos(){
        return pos;
    }

    public Sprite getSprite(){
        return this.sprite;
    }

    public SFVector wallForce(LinkedList<SFWall> allWalls){
        SFVector sumForce = new SFVector(0, 0);
        //find walls close to the point
        double a = Double.MAX_VALUE;
        for(SFWall temp: allWalls){
            if(temp.distanceToPoint(this.pos)>1000) {
                System.out.println("Continue distanceToPoint = " + temp.distanceToPoint(this.pos));
                continue;
            }
            else{
                System.out.println("Else distanceToPoint = " + temp.distanceToPoint(this.pos));
                double distance = temp.distanceToPoint(this.pos);
                if(distance<a){
                    a = distance;
                    closestWall = temp;
                    closestWallPoint = temp.pointClosest(this.pos);
                }
                SFVector tempForce;
                tempForce = SFVector.direction(temp.pointClosest(this.pos), this.pos);
                tempForce = tempForce.scale(Math.exp(-temp.distanceToPoint(this.pos)/b));
                sumForce = SFVector.add(sumForce, tempForce);
            }
        }
        System.out.println(closestWall.distanceToPoint(this.pos));
        sumForce = sumForce.scale(closestWall.distanceToPoint(this.pos));
        return sumForce;
    }

    public SFVector pedForce(LinkedList<SFAgent> allAgents){
        SFVector sumForce = new SFVector(0, 0);
        for(SFAgent temp : allAgents){
            if(distanceToAgent(temp)>64) continue;
            else if(temp == this) continue;
            else{
                SFVector tempForce = new SFVector(0,0);
                double interactionStrength = interactionFactor*SFVector.subtract(temp.vel, this.vel).norm();
                double distanceFactor = -(Math.exp(-distanceToAgent(temp) / interactionStrength));
                SFVector interactionT = SFVector.direction(temp.vel, this.vel);
                double theta = Math.acos(SFVector.dotProduct(interactionT, SFVector.subtract(temp.pos, this.pos))/
                        (interactionT.norm()*SFVector.subtract(temp.pos, this.pos).norm()));

                double interactionScale = Math.exp(-(Math.pow(n_prime*interactionStrength*theta, 2)));
                SFVector interactionN = new SFVector(-interactionT.y, interactionT.x);
                double normalScale = Math.exp(-(Math.pow(n*interactionStrength*theta, 2)));

                interactionT = interactionT.scale(interactionScale);
                interactionN = interactionN.scale(normalScale);

                tempForce = SFVector.add(interactionT, interactionN);

                tempForce = tempForce.scale(distanceFactor);
                sumForce = SFVector.add(sumForce, tempForce);
            }
        }
        return sumForce;
    }
}