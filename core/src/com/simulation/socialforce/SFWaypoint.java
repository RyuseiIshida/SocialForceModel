package com.simulation.socialforce;

public class SFWaypoint extends SFVector{
    public SFWaypoint(){
        super();
        this.id="";
        setEndpoints();
    }
    public SFWaypoint(String id, double xInput, double yInput){
        super(xInput, yInput);
        this.id=id;
        setEndpoints();
    }
    public SFWaypoint(String id, double xInput, double yInput, double r){
        super(xInput, yInput);
        this.id=id;
        this.dx=r;
        this.dy=r;
        setEndpoints();
    }
    public SFWaypoint(String id, double xInput, double yInput, double dxInput, double dyInput){
        super(xInput, yInput);
        this.dx=dxInput;
        this.dy=dyInput;
        this.id=id;
        setEndpoints();
    }
    public SFWaypoint(String id, SFVector e1, SFVector e2, SFVector e3, SFVector e4){
        super(SFVector.add(
                SFVector.add(e1,e4).scale(1/2),
                SFVector.add(e2,e3).scale(1/2)
        ));
        this.e1=e1;
        this.e2=e2;
        this.e3=e3;
        this.e4=e4;
        this.id=id;
    }
    public String id;

    //rectangular waypoint
    //dx and dy determines length from center point x,y
    public double dx=0, dy=0;
    //endpoints e1 being the topleft, e2 top right, e3 bot left, e4 bot right
    public SFVector e1, e2, e3, e4;
    public void setEndpoints(){
        e1 = new SFVector(x-dx, y+dy);
        e2 = new SFVector(x+dx, y+dy);
        e3 = new SFVector(x-dx, y-dy);
        e4 = new SFVector(x+dx, y-dy);
    }
    final boolean inWaypoint(SFVector point){
        //using perpendicular projections
        SFVector AM, AB, AD;
        AM = SFVector.subtract(point, e1); //AM
        AB = SFVector.subtract(e2, e1); //AB
        AD = SFVector.subtract(e3, e1); //AD

        if((0 < SFVector.dotProduct(AM, AB) &&
                SFVector.dotProduct(AM, AB) <= SFVector.dotProduct(AB, AB))&&
                (0 < SFVector.dotProduct(AM, AD) &&
                        SFVector.dotProduct(AM, AD) <= SFVector.dotProduct(AD, AD)))
        {
            return true;
        }
        else return false;
    }
}