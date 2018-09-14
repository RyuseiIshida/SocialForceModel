package com.simulation.socialforce;

import javax.vecmath.Vector2f;
import java.util.*;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.MathUtils;
import com.sun.tools.doclets.formats.html.SourceToHTMLConverter;

public class CPedestrian implements IPedestrian{
    private Parameter parameter = new Parameter();
    private static final float m_maxspeedfactor = 2.5f;
    private static final float m_maxforce = 0.1f;
    private static final float m_radius = 23f;
    private Vector2f m_position;
    private Vector2f m_goal;
    private ArrayList<Vector2f> m_goals;
    private Vector2f m_velocity;
    private float m_speed;
    private SocialForceModel l_env;
    private float m_maxspeed;
    private int m_controlossilation;
    private Sprite sprite;
    private boolean aisExitInfo;

    private String stateTag;

    public CPedestrian(SocialForceModel p_env,boolean isExitInfo,final Vector2f p_position, final float p_speed, Vector2f p_goal,Sprite sprites) {
        l_env = p_env;
        m_goals = new ArrayList<>(Arrays.asList(p_goal));
        m_goal = m_goals.get(m_goals.size()-1);
        m_position = p_position;
        m_speed = p_speed;
        m_velocity = CVector.scale( p_speed, CVector.direction( m_goal, m_position ) );
        m_maxspeed = p_speed * m_maxspeedfactor;
        m_controlossilation = 0;
        aisExitInfo = isExitInfo;
        if(aisExitInfo) stateTag = "GoExit";
        stateTag = "";
        sprite = sprites;
        sprite.setPosition(m_position.x-32/2,m_position.y-32/2);
        stateTag = "";
    }


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
        this.m_goals.add(p_position);
//        System.out.print("[ ");
//        for (Vector2f mGoal : m_goals) {
//            System.out.print(mGoal);
//        }
//        System.out.println(" ]");
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


        /*-----意思決定書き込み部分-------------------------------------------------------------------------------------*/
        //ルール
        //出口はあるか
        if(this.getisExitInfo()==false) {
            //出口はあるか?
            this.setTargetExit();
            //step60 ＝ 1second
            if (l_env.step % 120 == 0) {
                if (this.getisExitInfo() == false) {
                    switch (MathUtils.random(0, 4)) {
                        //出口を知っている人が周りにいるか
                        //getTargetPedestrian_turn(l_env.m_pedestrian);
                        //getTargetPedestrian(l_env.m_pedestrian);
                        case 0:
                            multi_people_following(l_env.m_pedestrian);
                            break;
                        case 1:
                            //ランダムに歩く
                            //System.out.println("random");
                            randomWalk2();
                            break;
                        case 3:
                            //何もしない
                            //System.out.println("non_walk");
                            break;
                        case 4:
                            //周りを見渡す
                            //System.out.println("lookaround");
                            lookAround();

                    }
                    //multi_people_following(l_env.m_pedestrian);
                }
                this.wall_turn2();
            }
        }
        /*-----------------------------------------------------------------------------------------------------------*/

        //もし初期目標地点がスケール外は全て削除
        if(this.m_goal.x < 0 || this.m_goal.x > Parameter.scale.x || this.m_goal.y < 0 || this.m_goal.y > Parameter.scale.y){
            //System.out.println("allClear");
            this.m_goals.clear();
        }

        this.setSubGoal();
//        if(this.aisExitInfo==false) {
//            System.out.println("goalpos = " + this.getGoalposition() + "pos = " + this.getPosition());
//        }
        final float l_check = CVector.sub( this.getGoalposition(), this.getPosition() ).length();
        //fSystem.out.println("l_check = " + l_check);
        //if ( this.m_goals.isEmpty() ) { m_controlossilation ++; }

        if ( l_check <= this.getM_radius() * 0.5 )
        {
            this.m_velocity = new Vector2f(0, 0);
            if ( this.m_goals.size() > 0 )
            {
                this.m_goal = this.m_goals.remove(this.m_goals.size()-1);
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

        return this;
    }

    public int getDistance(float x1, float y1, float x2, float y2) {
        float distance = (float)Math.sqrt((x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1));
        return (int) distance;
    }

    public float getDegree(float x1, float y1, float x2, float y2){
        float radian = (float)Math.atan2(y2-y1, x2-x1);
        float degree = (float)(radian * 180d / Math.PI);
        //if(degree<0) degree = 360 + degree;
        return degree;
    }



    public float getPedestrianDegree(){
        return getDegree(this.m_position.x,this.m_position.y,this.m_goal.x,this.m_goal.y);
    }



    public void getTargetPedestrian_turn(ArrayList<CPedestrian> m_pedestrian){
        for (CPedestrian mvec : m_pedestrian) {
            int distance = getDistance(m_position.x,m_position.y,mvec.getPosition().x,mvec.getPosition().y);
            //対象 - 向かっている方向 = delta_x
            //view_phi-theta/2 - delta_x > 0 -> 重なっている
            float delta_x = getDegree(m_position.x,m_position.y,mvec.getPosition().x,mvec.getPosition().y)
                    - getDegree(m_position.x,m_position.y,m_goal.x,m_goal.y);
            if(delta_x < 0) delta_x *= -1;
            if(mvec.getisExitInfo() && parameter.view_dmax >= distance && parameter.view_phi_theta/2 - delta_x >= 0 ){
                float d = getDistance(mvec.getPosition().x,mvec.getPosition().y,mvec.getGoalposition().x,mvec.getGoalposition().y);
                float tmpx = mvec.getGoalposition().x / d;
                float tmpy = mvec.getGoalposition().y / d;
                System.out.println("Pedestrian_turn");
                this.setGoalposition(new Vector2f(this.m_goal.x+tmpx,this.m_goal.y+tmpy));
            }
        }
    }

    public void getTargetPedestrian(ArrayList<CPedestrian> m_pedestrian){
        for (CPedestrian mvec : m_pedestrian) {
            int distance = getDistance(m_position.x,m_position.y,mvec.getPosition().x,mvec.getPosition().y);
            //対象 - 向かっている方向 = delta_x
            //view_phi-theta/2 - delta_x > 0 -> 重なっている
            float delta_x = getDegree(m_position.x,m_position.y,mvec.getPosition().x,mvec.getPosition().y)
                    - getDegree(m_position.x,m_position.y,m_position.x,m_position.y);
            if(delta_x < 0) delta_x *= -1;
            if(mvec.getisExitInfo() && parameter.view_dmax >= distance && parameter.view_phi_theta/2 - delta_x >= 0 ) {
                System.out.println("getTargetPedestrian");
                this.setGoalposition(new Vector2f(mvec.getPosition().x, mvec.getPosition().y));
                this.stateTag = "followExitPedestrian";
            }
        }
    }


    public void multi_people_following(ArrayList<CPedestrian> m_pedestrian){
        int count = 0;
        ArrayList<CPedestrian> multiPed = new ArrayList<>();
        for (CPedestrian mvec : m_pedestrian) {
            int distance = getDistance(m_position.x,m_position.y,mvec.getPosition().x,mvec.getPosition().y);
            //対象 - 向かっている方向 = delta_x
            //view_phi-theta/2 - delta_x > 0 -> 重なっている
            float delta_x = getDegree(m_position.x,m_position.y,mvec.getPosition().x,mvec.getPosition().y)
                    - getDegree(m_position.x,m_position.y,m_position.x,m_position.y);
            if(delta_x < 0) delta_x *= -1;
            if(parameter.view_dmax >= distance && parameter.view_phi_theta/2 - delta_x >= 0 ) {
                count++;
                multiPed.add(mvec);
                if(count>=3){
                    //this.setGoalposition(multiPed.get(MathUtils.random(multiPed.size())).getPosition());
                    //this.setGoalposition(new Vector2f(mvec.getPosition().x, mvec.getPosition().y));
                    this.m_goal = new Vector2f(mvec.getPosition().x, mvec.getPosition().y);
                    this.stateTag = "followMultiPedestrian";
                    //System.out.println("Multi_Follow = " + count);
                }
            }
        }
        //count = 0;多分いらない
    }

    public void setTargetExit(){
        for (Vector2f  vec: parameter.exitVec) {
            int distance = getDistance(m_position.x,m_position.y,vec.x,vec.y);
            //対象 - 向かっている方向 = delta_x
            //view_phi-theta/2 - delta_x > 0 -> 重なっている
            float delta_x = getDegree(m_position.x,m_position.y,vec.x,vec.y)
                    - getDegree(m_position.x,m_position.y,m_goal.x,m_goal.y);
            if(delta_x < 0) delta_x *= -1;
            if(parameter.view_dmax >= distance && parameter.view_phi_theta/2 - delta_x >= 0 ){
                this.m_goals.clear();
                this.setGoalposition(vec);
                this.setExitInfo(true);
                this.stateTag = "GoExit";
            }
        }
    }
    public boolean judgeIntersected(float ax,float ay,float bx,float by,float cx,float cy,float dx,float dy){
        //線分交差判定 https://qiita.com/ykob/items/ab7f30c43a0ed52d16f2
        float ta = (cx - dx) * (ay - cy) + (cy - dy) * (cx - ax);
        float tb = (cx - dx) * (by - cy) + (cy - dy) * (cx - bx);
        float tc = (ax - bx) * (cy - ay) + (ay - by) * (ax - cx);
        float td = (ax - bx) * (dy - ay) + (ay - by) * (ax - dx);
        //return 線分が交差 -> true, 交差していない -> false
        return tc * td < 0 && ta * tb < 0;
    }

    public boolean judgeIntersectedRect(Rect rect){
        if(judgeIntersected(m_position.x,m_position.y,
                m_goal.x,m_goal.y, rect.x,rect.y,rect.leftButtom.x,rect.leftButtom.y))
            return true;
        else if(judgeIntersected(m_position.x,m_position.y,
                m_goal.x,m_goal.y, rect.x,rect.y,rect.leftTop.x,rect.leftTop.y))
            return true;
        else if(judgeIntersected(m_position.x,m_position.y,
                m_goal.x,m_goal.y, rect.x,rect.y,rect.rightButtom.x,rect.rightButtom.y))
            return true;
        else if(judgeIntersected(m_position.x,m_position.y,
                m_goal.x,m_goal.y, rect.x,rect.y,rect.rightTop.x,rect.rightTop.y))
            return true;
        else return false;
    }

    public void setSubGoal(){
        Vector2f goalVec;
        for(Rect rect : parameter.arrayRect){
            //交差判定
            if(judgeIntersectedRect(rect)){
                Map<Vector2f, Float> goalDis = new HashMap<>();
                //各4点とゴールとの距離をvalueに入れる
                goalDis.put(rect.leftButtom, (float) getDistance(m_goal.x,m_goal.y, rect.leftButtom.x, rect.leftButtom.y));
                goalDis.put(rect.leftTop, (float) getDistance(m_goal.x,m_goal.y, rect.leftTop.x, rect.leftTop.y));
                goalDis.put(rect.rightButtom, (float) getDistance(m_goal.x,m_goal.y, rect.rightButtom.x, rect.rightButtom.y));
                goalDis.put(rect.rightTop, (float) getDistance(m_goal.x,m_goal.y, rect.rightTop.x, rect.rightTop.y));
                List<Map.Entry<Vector2f, Float>> list_entries = new ArrayList<Map.Entry<Vector2f, Float>>(goalDis.entrySet());
                Collections.sort(list_entries, new Comparator<Map.Entry<Vector2f, Float>>() {
                    public int compare(Map.Entry<Vector2f, Float> obj1, Map.Entry<Vector2f, Float> obj2) {
                        return obj1.getValue().compareTo(obj2.getValue());
                    }
                });
                //最もゴールから近い2点を選ぶ
                //そのうち最も歩行者に近い点をゴールとする
                int tmpGoal1 = getDistance(m_position.x,m_position.y, list_entries.get(1).getKey().x, list_entries.get(0).getKey().y);
                int tmpGoal2 = getDistance(m_position.x,m_position.y, list_entries.get(0).getKey().x, list_entries.get(1).getKey().y);
                //if (tmpGoal1 < tmpGoal2) goalVec = list_entries.get(1).getKey();
                //else goalVec = list_entries.get(2).getKey();
                //if (tmpGoal1 < tmpGoal2) this.setGoalposition(list_entries.get(0).getKey());
                if(tmpGoal1>tmpGoal2) {
                    System.out.println("tmpGoal");
                    this.setGoalposition(new Vector2f(list_entries.get(0).getKey().x - 50, list_entries.get(0).getKey().y));
                }
                //else this.setGoalposition(list_entries.get(1).getKey());
                else this.setGoalposition(new Vector2f(list_entries.get(1).getKey().x-50, list_entries.get(1).getKey().y));
                //ped.setSubGoalposition(new Vector2f(goalVec.x+50,goalVec.y));
                //ゴールベクトルが重なっていたら
                //if (judgeIntersectedRect(ped,rect) setSubGoal(ped);
                //else ped.setGoalposition(goalVec);
            }
        }
    }

    public void changeGoal(CPedestrian ped){
        float distance = getDistance(ped.getPosition().x,ped.getPosition().y,ped.getGoalposition().x,ped.getGoalposition().y);
        if (distance > 30) {
            // next();
        }
    }

    //視野最大方向に壁があった場合に
    public void wall_turn(){
            if(judgeIntersected(m_position.x,m_position.y, viewDegreeVec().x, viewDegreeVec().y, wall.getX1(),wall.getY1(),wall.getX2(),wall.getY2())) {
            //if(judgeIntersected(m_position.x,m_position.y, m_goal.x, m_goal.y, wall.getX1(),wall.getY1(),wall.getX2(),wall.getY2())) {
                    //System.out.println("wall_turn");
                int randomx = MathUtils.random(-200, 200);
                int randomy = MathUtils.random(-200, 200);
                this.setGoalposition(new Vector2f(this.m_position.x + randomx, this.m_position.y + randomy));

//                float delta_x = this.m_position.x - this.m_goal.x;
//                if(delta_x < 0) delta_x *= -1;
//                float delta_y = this.m_position.y - this.m_goal.y;
//                if(delta_y < 0) delta_y *= -1;
//
//                if(this.getPedestrianDegree() > 0) //向いている方向がプラスなら
//                    this.m_goal = new Vector2f(this.m_goal.x-delta_x,this.m_goal.y-delta_y);
//                else //マイナスなら
//                    this.m_goal = new Vector2f(this.m_goal.y+delta_x,this.m_goal.y+delta_y);
//                this.m_goal = this.m_goals.remove(this.m_goals.size()-1);

        }
    }

//    //視野最大方向に壁があった場合に
//    public void wall_turn2(){
//        System.out.println("wall_turn2d");
//        for (CStatic wall : Parameter.m_wall) {
//                if(judgeIntersected(m_position.x,m_position.y, m_goal.x, m_goal.y, wall.getX1(),wall.getY1(),wall.getX2(),wall.getY2())) {
//                //System.out.println("wall_turn");
//                int randomx = MathUtils.random(-200, 200);
//                int randomy = MathUtils.random(-200, 200);
//                this.setGoalposition(new Vector2f(this.m_position.x + randomx, this.m_position.y + randomy));
//
////                float delta_x = this.m_position.x - this.m_goal.x;
////                if(delta_x < 0) delta_x *= -1;
////                float delta_y = this.m_position.y - this.m_goal.y;
////                if(delta_y < 0) delta_y *= -1;
////
////                if(this.getPedestrianDegree() > 0) //向いている方向がプラスなら
////                    this.m_goal = new Vector2f(this.m_goal.x-delta_x,this.m_goal.y-delta_y);
////                else //マイナスなら
////                    this.m_goal = new Vector2f(this.m_goal.y+delta_x,this.m_goal.y+delta_y);
////                this.m_goal = this.m_goals.remove(this.m_goals.size()-1);
//
//            }
//        }
//    }
    //視野最大方向に壁があった場合に
    public void wall_turn2(){
        System.out.println("wall_turn2d");
        for (CStatic wall : Parameter.m_wall) {
            if(judgeIntersected(m_position.x,m_position.y, m_goal.x, m_goal.y, wall.getX1(),wall.getY1(),wall.getX2(),wall.getY2())) {
                //System.out.println("wall_turn");
                int randomx = MathUtils.random(-200, 200);
                int randomy = MathUtils.random(-200, 200);
                this.setGoalposition(new Vector2f(this.m_position.x + randomx, this.m_position.y + randomy));

//                float delta_x = this.m_position.x - this.m_goal.x;
//                if(delta_x < 0) delta_x *= -1;
//                float delta_y = this.m_position.y - this.m_goal.y;
//                if(delta_y < 0) delta_y *= -1;
//
//                if(this.getPedestrianDegree() > 0) //向いている方向がプラスなら
//                    this.m_goal = new Vector2f(this.m_goal.x-delta_x,this.m_goal.y-delta_y);
//                else //マイナスなら
//                    this.m_goal = new Vector2f(this.m_goal.y+delta_x,this.m_goal.y+delta_y);
//                this.m_goal = this.m_goals.remove(this.m_goals.size()-1);

            }
        }
    }

    public float getGoalRadian(){
        double degree = this.getPedestrianDegree();
        double rad = (degree * Math.PI) / 180;
        return (float)rad;
    }

    //視野最大方向のベクトルを返す
    public Vector2f viewDegreeVec(){
        //底辺a = 斜辺c * cos
        float c = Parameter.view_dmax;
        float cos = (float)Math.cos(Math.toRadians(this.getPedestrianDegree()));
        float a = c*cos;
        //対辺b = 斜辺c * sin
        float sin = (float)Math.sin(Math.toRadians(this.getPedestrianDegree()));
        float b = c*sin;
        float result_x =a+this.m_position.x;
        float result_y =b+this.m_position.y;
        return new Vector2f(result_x,result_y);
    }

    //ランダムウォーク1 周りをランダムに
    public void randomWalk1(){
        int randomx = MathUtils.random(-200, 200);
        int randomy = MathUtils.random(-200, 200);
        this.m_goal = new Vector2f(this.m_position.x + randomx, this.m_position.y + randomy);
        //this.m_goals.clear();
        //this.setGoalposition(new Vector2f(this.m_position.x + randomx, this.m_position.y + randomy));
        this.wall_turn();
    }
    //ランダムウォーク2 完全なランダム
    public void randomWalk2(){
        this.m_goal = new Vector2f(MathUtils.random(Parameter.scale.x),MathUtils.random(Parameter.scale.y));
        //this.m_goals.clear();
        //this.setGoalposition(new Vector2f(MathUtils.random(Parameter.scale.x),MathUtils.random(Parameter.scale.y)));
        this.wall_turn();
    }
    public void lookAround(){
        this.m_goals.clear();
        ArrayList<Vector2f> directions = new ArrayList<>(Arrays.asList(
                new Vector2f(this.m_position.x+1, this.m_position.y+1),
                new Vector2f(this.m_position.x+1, this.m_position.y-1),
                new Vector2f(this.m_position.x-1, this.m_position.y+1),
                new Vector2f(this.m_position.x-1, this.m_position.y-1)

                ));
        for (int i = 4; i >= 0; i--) {
            int random = MathUtils.random(i);
            this.setGoalposition(new Vector2f(directions.get(random)));
            directions.remove(random);
        }
    }

}

