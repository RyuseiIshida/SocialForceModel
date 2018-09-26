package com.simulation.socialforce;

import javax.vecmath.Vector2f;
import java.lang.reflect.Array;
import java.util.*;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

import static java.lang.Math.*;



public class CPedestrian implements IPedestrian{
    private Parameter parameter = new Parameter();
    private static final float m_maxspeedfactor = 2.5f;//最高速度
    private static final float m_maxforce = 0.1f;
    private static final float m_radius = 23f;
    private Vector2f m_position;
    private Vector2f m_goal;
    private ArrayList<Vector2f> m_goals;
    private Vector2f m_velocity;
    private float m_speed;
    private SocialForceModel l_env;
    private float m_maxspeed;
    private Sprite sprite;
    private boolean aisExitInfo;
    private String stateTag;
    private boolean included;
    private CPedestrian myleader;
    private ArrayList<CPedestrian> myfollower;


    public CPedestrian(SocialForceModel p_env,boolean isExitInfo,final Vector2f p_position, final float p_speed, Vector2f p_goal,Sprite sprites) {
        l_env = p_env;
        m_goals = new ArrayList<>(Arrays.asList(p_goal));
        m_goal = m_goals.get(m_goals.size()-1);
        m_position = p_position;
        m_speed = p_speed;
        m_velocity = CVector.scale( p_speed, CVector.direction( m_goal, m_position ) );
        m_maxspeed = p_speed * m_maxspeedfactor;
        aisExitInfo = isExitInfo;
        if(aisExitInfo) stateTag = "GoExit";
        stateTag = "";
        sprite = sprites;
        sprite.setPosition(m_position.x-32/2,m_position.y-32/2);
        stateTag = "";
        included = false;
        myfollower = new ArrayList<>();
    }


    public void setExitInfo(boolean bool) { aisExitInfo = bool;}
    public boolean getisExitInfo(){ return aisExitInfo;}
    public Sprite getSprite(){return sprite;}
    public void addMyfollower(CPedestrian ped){
        this.myfollower.add(ped);
    }

    @Override
    public Vector2f getGoalposition() {
        return m_goal;
    }

    @Override
    public IPedestrian setGoalposition(  Vector2f p_position )
    {
        this.m_goal = p_position;
        this.m_goals.add(p_position);
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
    public Vector2f accelaration() //加速度ベクトル
    {
        Vector2f l_repulsetoWall = new Vector2f( 0, 0 ); //壁からの逆ベクトル
        Vector2f l_repulsetoOthers = new Vector2f( 0, 0 ); //他のエージェントからの逆ベクトル
        Vector2f l_desiredVelocity = CVector.scale( this.m_maxspeed, CVector.direction( this.getGoalposition(), this.getPosition() ) ); //目的地へ向かうベクトル

        //壁からの逆ベクトルを算出
        for ( int i = 0; i < l_env.getWallinfo().size(); i++ )
        {
            l_repulsetoWall = CVector.add( l_repulsetoWall, CForce.repulsewall( this, l_env.getWallinfo().get( i )) );
        }

        //他のエージェントからの逆ベクトルを算出
        for ( int i = 0; i < l_env.getPedestrianinfo().size(); i++ )
        {
            if( !l_env.getPedestrianinfo().get(i).equals( this ) )
            {
                l_repulsetoOthers = CVector.add( l_repulsetoOthers, CForce.repulseotherPed( this, l_env.getPedestrianinfo().get( i )) );
            }
        }

        //たぶん全てのベクトルを足していると思う
        //final Vector2f l_temp = CVector.add( CForce.drivingForce( l_desiredVelocity, this.getVelocity() ), l_repulsetoOthers );
        Vector2f l_temp = CVector.add( CForce.drivingForce( l_desiredVelocity, this.getVelocity() ), l_repulsetoOthers );

        //Boidベクトルを計算
        this.view();
        Vec rule1 = separation();
        Vec rule2 = alignment();
        Vec rule3 = cohesion();
        rule1.mult(2.0); //パラメータ2.5
        rule2.mult(1.5); //パラメータ1.5
        rule3.mult(1.3); //パラメータ1.3
        Vector2f rule1f = new Vector2f((float)rule1.x,(float)rule1.y);
        //Vector2f rule2f = new Vector2f((float)rule2.x,(float)rule2.y);
        //Vector2f rule3f = new Vector2f((float)rule3.x,(float)rule2.y);
        //applyForce(migrate);
        Vector2f boidVector = new Vector2f();
        //boidVector.add(rule1f,rule2f);
        //boidVector.add(boidVector,rule3f);
        //l_temp.add(l_temp, boidVector);
        //l_temp.add(l_temp, rule1f);

        return CVector.truncate( CVector.add( l_temp, l_repulsetoWall ), m_maxforce );
    }


    public float getM_radius()
    {
        return m_radius;
    }
    public void setStateTag(String str){
        this.stateTag = str;
    }
    public String getStateTag() {
        return this.stateTag;
    }

    public void setMyleader(CPedestrian ped){
        System.out.println("leaderセット");
        this.myleader = ped;
    }
    public CPedestrian getMyleader(){
        return this.myleader;
    }


    @Override
    public IPedestrian call() throws Exception {


        /*-----意思決定-----------------------------------------------------------------------------------------------*/
        //出口を知っているか
        //出口はあるか?
        this.setTargetExit();

        //追従者の行動
        if( this.stateTag == "follow") {
            if (this.getDistance(this.m_position, this.myleader.getPosition()) > 30) {
                this.m_goal = this.myleader.getPosition();
            } else {
                this.m_goal = this.getPosition();
                this.m_velocity = this.myleader.m_velocity;
            }


            if(this.getDistance(this.m_position, this.myleader.getPosition()) > 200){
                //this.stateTag = "";
            }
        }


        if(this.getisExitInfo()==false && !(this.stateTag =="follow")){
            if (l_env.step % Parameter.STEPINTERVAL == 0) {
                if (this.getisExitInfo() == false) {
                    if( !(this.stateTag == "follow") ) {
                    //if( !(this.stateTag == "follow") && !(this.stateTag == "leader") ) {
                            switch (MathUtils.random(0, 2)) {
                            case 0:
                                //if(!(this.stateTag == "leader")) multi_people_following();
                                this.multi_people_following2();
                                break;
                            case 1:
                                //ランダムに歩く
                                this.randomWalk2();
                                break;
                            case 2:
                                //何もしない
                                this.m_goal = new Vector2f(this.m_position.x, this.m_position.y);
                                //this.m_goal = this.m_velocity;
                                break;
                            case 3:
                                //周りを見渡す
                                lookAround();
                                break;
                        }
                    }
                }
            }
        }



        /*-----------------------------------------------------------------------------------------------------------*/

        if(!(stateTag=="GoExit") && !(stateTag=="follow") && !this.aisExitInfo){
            //this.wall_turn();
            //this.wall_turn2();
            this.wall_turn3();
        }

        //もし初期目標地点がスケール外は全て削除
        if(this.m_goal.x < 0 || this.m_goal.x > Parameter.scale.x || this.m_goal.y < 0 || this.m_goal.y > Parameter.scale.y){
            //System.out.println("allClear");
            this.m_goals.clear();
        }

        //this.setSubGoal();
//        if(this.aisExitInfo==false) {
//            System.out.println("goalpos = " + this.getGoalposition() + "pos = " + this.getPosition());
//        }

        final float l_check = CVector.sub( this.getGoalposition(), this.getPosition() ).length(); //ゴールとの距離
        //System.out.println("l_check = " + l_check);

        if ( l_check <= this.getM_radius() * 0.8 ) //ゴールについたかの判断
        {
            this.m_velocity = new Vector2f(0, 0); //スピードベクトルを0にする
            if ( this.m_goals.size() > 0 ) //もしゴール集合が残っているなら
            {
                this.m_goal = this.m_goals.remove(this.m_goals.size()-1); //ゴール集合を一つ削除した新しいゴールを追加する
                this.m_velocity = CVector.scale( m_maxspeed, CVector.normalize( CVector.add( this.m_velocity, this.accelaration()))); //正規化された速度ベクトルをスケールにより調整して速度ベクトル代入
                this.m_position = CVector.add( m_position, m_velocity ); //現在の場所
                sprite.setPosition(m_position.x-16,m_position.y-16);
            }
        } else { //ゴールについてない場合
            this.m_velocity = CVector.scale(m_maxspeed, CVector.normalize(CVector.add(this.m_velocity, this.accelaration())));
            this.m_position = CVector.add(m_position, m_velocity);
            sprite.setPosition(m_position.x - 16, m_position.y - 16);
        }
        return this;
    }


    //集団を追従
    public void multi_people_following(){
        int count = 0;
        ArrayList<CPedestrian> multiPed = new ArrayList<>();
        for (CPedestrian ped : l_env.getPedestrianinfo()) {
            if (!(this.equals(ped))) {
                int distance = getPedDistance(ped);
                float delta_x = getPedDegree(ped) - getDegree(this.m_position, this.m_goal);
                if (delta_x < 0) delta_x *= -1;
                if (parameter.view_dmax >= distance && parameter.view_phi_theta / 2 - delta_x >= 0) {
                    count++;
                    multiPed.add(ped);
                    if (count >= Parameter.judgeFollowNum) {
                        //for (CPedestrian cPedestrian : myfollower) {
                            //if (!cPedestrian.equals(ped)) {
                                this.m_goal = ped.getPosition();
                                if (distance < 250) {
                                    this.stateTag = "follow";
                                    this.myleader = ped;
                                    this.m_goal = myleader.getPosition();
                                    ped.setStateTag("leader");
                                    ped.addMyfollower(this);
                                    break;
                                }

                            //}
                        //}


                    }
                    else {
                        randomWalk2();
                    }
                }
            }
        }
    }


    //集団を追従
    public void multi_people_following2(){
        Map<CPedestrian,Integer> goal_peds = new HashMap<>();
        int distance;
        for (CPedestrian ped : l_env.getPedestrianinfo()) {
            if( !(this.equals(ped)) ){
                distance = this.getPedDistance(ped);
                if (distance > parameter.view_dmax) {
                    goal_peds.put(ped,distance);
                    if (goal_peds.size() > parameter.judgeFollowNum2) {

                        this.stateTag = "follow";
                        this.myleader = ped;
                        this.m_goal = ped.getGoalposition();
                        break;
                    }
                }
            }
        }
    }


    //ランダムウォーク1 周りをランダムに
    public void randomWalk1(){
        int randomx = MathUtils.random(-200, 200);
        int randomy = MathUtils.random(-200, 200);
        this.m_goal = new Vector2f(this.m_position.x + randomx, this.m_position.y + randomy);
    }

    //ランダムウォーク2 完全なランダム
    public void randomWalk2(){
        this.stateTag = "random";
        this.m_goals.clear();
        this.m_goal = new Vector2f(MathUtils.random(Parameter.scale.x),MathUtils.random(Parameter.scale.y));
    }

    //周りを見渡す
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


    //視野最大方向に壁があった場合に
    public void wall_turn(){
        for (CStatic wall : Parameter.m_wall) {
            if(judgeIntersected(m_position.x,m_position.y, viewDegreeVec().x, viewDegreeVec().y, wall.getX1(),wall.getY1(),wall.getX2(),wall.getY2())) {
                //if(judgeIntersected(m_position.x,m_position.y, m_goal.x, m_goal.y, wall.getX1(),wall.getY1(),wall.getX2(),wall.getY2())) {
                System.out.println("wall_turn");
                int randomx = MathUtils.random(-200, 200);
                int randomy = MathUtils.random(-200, 200);
                this.m_goal = new Vector2f(this.m_position.x + randomx, this.m_position.y + randomy);
            }
        }
    }

    //移動経路先に壁があった場合に
    public void wall_turn2(){
        for (CStatic wall : Parameter.m_wall) {
            if(judgeIntersected(m_position.x,m_position.y, m_goal.x, m_goal.y, wall.getX1(),wall.getY1(),wall.getX2(),wall.getY2())) {
                System.out.println("wall_turn2");
                int randomx = MathUtils.random(-200, 200);
                int randomy = MathUtils.random(-200, 200);
                this.m_goal =  new Vector2f(this.m_position.x + randomx, this.m_position.y + randomy);
            }
        }
    }


    public void wall_turn3(){
        for (CStatic wall : Parameter.m_wall) {
            if(judgeIntersected(m_position.x,m_position.y, viewDegreeVec2().x, viewDegreeVec2().y, wall.getX1(),wall.getY1(),wall.getX2(),wall.getY2())) {
                int randomx = MathUtils.random(-200, 200);
                int randomy = MathUtils.random(-200, 200);
                this.m_goal = new Vector2f(this.m_position.x + randomx, this.m_position.y + randomy);
            }
        }
    }









    public int getDistance(float x1, float y1, float x2, float y2) {
        float distance = (float)Math.sqrt((x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1));
        return (int) distance;
    }

    public int getDistance(Vector2f v1, Vector2f v2){
        return this.getDistance(v1.x, v1.y, v2.x, v2.y);
    }

    public int getPedDistance(CPedestrian ped){
        return this.getDistance(this.m_position, ped.getPosition());
    }

    public float getDegree(float x1, float y1, float x2, float y2){
        float radian = (float)Math.atan2(y2-y1, x2-x1);
        float degree = (float)(radian * 180d / Math.PI);
        return degree;
    }

    public float getDegree(Vector2f v1, Vector2f v2){
        return getDegree(v1.x, v1.y, v2.x, v2.y);
    }

    public float getPedDegree(CPedestrian ped){
        return getDegree(this.m_position, ped.getPosition());
    }


    public float getPedestrianDegree(){
        return getDegree(this.m_position.x,this.m_position.y,this.m_goal.x,this.m_goal.y);
    }



    public void getTargetPedestrian_turn(){
        for (CPedestrian mvec : l_env.getPedestrianinfo()) {
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

    public void getTargetPedestrian(){
        for (CPedestrian ped : l_env.getPedestrianinfo()) {
            int distance = getDistance(m_position.x,m_position.y,ped.getPosition().x,ped.getPosition().y);
            //対象 - 向かっている方向 = delta_x
            //view_phi-theta/2 - delta_x > 0 -> 重なっている
            float delta_x = getDegree(m_position.x,m_position.y,ped.getPosition().x,ped.getPosition().y)
                    - getDegree(m_position.x,m_position.y,m_position.x,m_position.y);
            if(delta_x < 0) delta_x *= -1;
            if(ped.getisExitInfo() && parameter.view_dmax >= distance && parameter.view_phi_theta/2 - delta_x >= 0 ) {
                System.out.println("getTargetPedestrian");
                //this.setGoalposition(new Vector2f(mvec.getPosition().x, mvec.getPosition().y));
                this.stateTag = "followExitPedestrian";
                this.m_goal = ped.getVelocity();
            }
        }
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
                this.m_goal = vec;
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

//    public boolean judgeIntersectedRect(Rect rect){
//        if(judgeIntersected(m_position.x,m_position.y,
//                m_goal.x,m_goal.y, rect.x,rect.y,rect.leftButtom.x,rect.leftButtom.y))
//            return true;
//        else if(judgeIntersected(m_position.x,m_position.y,
//                m_goal.x,m_goal.y, rect.x,rect.y,rect.leftTop.x,rect.leftTop.y))
//            return true;
//        else if(judgeIntersected(m_position.x,m_position.y,
//                m_goal.x,m_goal.y, rect.x,rect.y,rect.rightButtom.x,rect.rightButtom.y))
//            return true;
//        else if(judgeIntersected(m_position.x,m_position.y,
//                m_goal.x,m_goal.y, rect.x,rect.y,rect.rightTop.x,rect.rightTop.y))
//            return true;
//        else return false;
//    }

//    public void setSubGoal(){
//        Vector2f goalVec;
//        for(Rect rect : parameter.arrayRect){
//            //交差判定
//            if(judgeIntersectedRect(rect)){
//                Map<Vector2f, Float> goalDis = new HashMap<>();
//                //各4点とゴールとの距離をvalueに入れる
//                goalDis.put(rect.leftButtom, (float) getDistance(m_goal.x,m_goal.y, rect.leftButtom.x, rect.leftButtom.y));
//                goalDis.put(rect.leftTop, (float) getDistance(m_goal.x,m_goal.y, rect.leftTop.x, rect.leftTop.y));
//                goalDis.put(rect.rightButtom, (float) getDistance(m_goal.x,m_goal.y, rect.rightButtom.x, rect.rightButtom.y));
//                goalDis.put(rect.rightTop, (float) getDistance(m_goal.x,m_goal.y, rect.rightTop.x, rect.rightTop.y));
//                List<Map.Entry<Vector2f, Float>> list_entries = new ArrayList<Map.Entry<Vector2f, Float>>(goalDis.entrySet());
//                Collections.sort(list_entries, new Comparator<Map.Entry<Vector2f, Float>>() {
//                    public int compare(Map.Entry<Vector2f, Float> obj1, Map.Entry<Vector2f, Float> obj2) {
//                        return obj1.getValue().compareTo(obj2.getValue());
//                    }
//                });
//                //最もゴールから近い2点を選ぶ
//                //そのうち最も歩行者に近い点をゴールとする
//                int tmpGoal1 = getDistance(m_position.x,m_position.y, list_entries.get(1).getKey().x, list_entries.get(0).getKey().y);
//                int tmpGoal2 = getDistance(m_position.x,m_position.y, list_entries.get(0).getKey().x, list_entries.get(1).getKey().y);
//                //if (tmpGoal1 < tmpGoal2) goalVec = list_entries.get(1).getKey();
//                //else goalVec = list_entries.get(2).getKey();
//                //if (tmpGoal1 < tmpGoal2) this.setGoalposition(list_entries.get(0).getKey());
//                if(tmpGoal1>tmpGoal2) {
//                    System.out.println("tmpGoal");
//                    this.setGoalposition(new Vector2f(list_entries.get(0).getKey().x - 50, list_entries.get(0).getKey().y));
//                }
//                //else this.setGoalposition(list_entries.get(1).getKey());
//                else this.setGoalposition(new Vector2f(list_entries.get(1).getKey().x-50, list_entries.get(1).getKey().y));
//                //ped.setSubGoalposition(new Vector2f(goalVec.x+50,goalVec.y));
//                //ゴールベクトルが重なっていたら
//                //if (judgeIntersectedRect(ped,rect) setSubGoal(ped);
//                //else ped.setGoalposition(goalVec);
//            }
//        }
//    }

    public void changeGoal(CPedestrian ped){
        float distance = getDistance(ped.getPosition().x,ped.getPosition().y,ped.getGoalposition().x,ped.getGoalposition().y);
        if (distance > 30) {
            // next();
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

    //視野
    public Vector2f viewDegreeVec2(){
        //底辺a = 斜辺c * cos
        float c = 100;
        float cos = (float)Math.cos(Math.toRadians(this.getPedestrianDegree()));
        float a = c*cos;
        //対辺b = 斜辺c * sin
        float sin = (float)Math.sin(Math.toRadians(this.getPedestrianDegree()));
        float b = c*sin;
        float result_x =a+this.m_position.x;
        float result_y =b+this.m_position.y;
        return new Vector2f(result_x,result_y);
    }



    //Vec seek(Vec target) {
    Vec seek(Vec target){
        //Vec steer = Vec.sub(target, location);
        Vec steer = Vec.sub(target,new Vec(this.getPosition().x, this.getPosition().y));
        steer.normalize();
        //steer.mult(maxSpeed);
        steer.mult(1);
        //steer.sub(velocity);
        steer.sub(new Vec(this.getVelocity().x,this.getVelocity().y));
        //steer.limit(maxForce);
        steer.limit(1);
        return steer;
    }


    //ボイドの近傍内か判断
    void view() {
        double sightDistance = 100;
        double peripheryAngle = PI * 0.85;

        //for (Boid b : boids) {
        for (CPedestrian b : l_env.getPedestrianinfo()) {
            b.included = false;

            if (b == this)
                continue;

            //double d = Vec.dist(location, b.location); //対象との距離
            double d = Vec.dist(new Vec(this.getPosition().x,this.getPosition().y), new Vec(b.getPosition().x,b.getPosition().y));
            if (d <= 0 || d > sightDistance) //対象との距離が近傍判断距離内か?
                continue;

            //Vec lineOfSight = Vec.sub(b.location, location);
            Vec lineOfSight = Vec.sub(new Vec(b.m_position.x, b.m_position.y), new Vec(this.m_position.x,this.m_position.y));

            //double angle = Vec.angleBetween(lineOfSight, velocity); //角度
            double angle = Vec.angleBetween(lineOfSight, new Vec(this.getVelocity().x,this.getVelocity().y));
            if (angle < peripheryAngle) {
                b.included = true;
                //System.out.println("included!");
            }
        }
    }


    //separation(引き離し) 衝突の回避
    //Vec separation(List<Boid> boids) {
    Vec separation() {
        double desiredSeparation = 100; //目的回避判定の距離

        Vec steer = new Vec(0, 0); //操縦
        int count = 0;
        //for (Boid b : boids) {
        for(CPedestrian b : l_env.getPedestrianinfo()){
            if (!b.included)
                continue;
            //double d = Vec.dist(location, b.location);//対象との距離
            double d = Vec.dist(new Vec(this.getPosition().x,this.getPosition().y), new Vec(b.getPosition().x,b.getPosition().y));//対象との距離a
            if ((d > 0) && (d < desiredSeparation)) {
                //Vec diff = Vec.sub(location, b.location); //逆ベクトル
                Vec diff = Vec.sub(new Vec(this.getPosition().x,this.getPosition().y), new Vec(b.getPosition().x,b.getPosition().y)); //逆ベクトル
                diff.normalize();//逆ベクトルの正規化
                diff.div(d);        //距離による重みを追加
                steer.add(diff); //方向ベクトルを追加
                count++;
            }
        }
        if (count > 0) {
            steer.div(count); //対象となった分だけ重みを調整(平均化)
        }

        if (steer.mag() > 0) {
            steer.normalize(); //操縦ベクトルを正規化
            //steer.mult(maxSpeed); //正規化されたベクトルにスピードを追加
            steer.mult(1);
            //steer.sub(velocity); //前のステップの速度を見て調整する
            steer.sub(new Vec(this.getVelocity().x, this.getVelocity().y)); //前のステップの速度を見て調整する
            //steer.limit(maxForce);
            steer.limit(1);
            return steer;
        }
        return new Vec(0, 0);
    }

    //alignment(整列) 群の中心に向かう
    //Vec alignment(List<Boid> boids) {
    Vec alignment() {
        double preferredDist = 50; //近傍判断距離

        Vec steer = new Vec(0, 0);
        int count = 0;

        //for (Boid b : boids) {
        for (CPedestrian b : l_env.getPedestrianinfo()) {
            if (!b.included)
                continue;

            //double d = Vec.dist(location, b.location); //対象との距離
            double d = Vec.dist(new Vec(this.getPosition().x,this.getPosition().y), new Vec(b.getPosition().x,b.getPosition().y));//対象との距離a
            if ((d > 0) && (d < preferredDist)) { //対象との距離が近傍判断距離内か?
                //steer.add(b.velocity); // 前ステップの速度ベクトルを追加
                steer.add(new Vec(b.getVelocity().x,b.getVelocity().y)); // 前ステップの速度ベクトルを追加
                count++;
            }
        }

        if (count > 0) {
            steer.div(count); //平均化
            steer.normalize(); //正規化
            //steer.mult(maxSpeed); //正規化されたベクトルにスピードを追加
            //steer.sub(velocity); //前ステップの速度ベクトルを調整する
            //steer.limit(maxForce); //速度ベクトルの調整
            steer.mult(1); //正規化されたベクトルにスピードを追加
            steer.sub(new Vec(this.getVelocity().x,this.getVelocity().y)); //前ステップの速度ベクトルを調整する
            steer.limit(1); //速度ベクトルの調整
        }
        return steer;
    }

    //Cohesion(結合) 向きを合わせる
    //Vec cohesion(List<Boid> boids) {
    Vec cohesion() {
        double preferredDist = 50; //近傍判断距離

        Vec target = new Vec(0, 0);
        int count = 0;

        //for (Boid b : boids) {
        for(CPedestrian b : l_env.getPedestrianinfo()){
            if (!b.included)
                continue;

            //double d = Vec.dist(location, b.location); //対象との距離
            double d = Vec.dist(new Vec(this.getPosition().x,this.getPosition().y),new Vec(b.getPosition().x,b.getPosition().y));
            if ((d > 0) && (d < preferredDist)) { //対象との距離が近傍判断距離内か?
                //target.add(b.location); //ターゲットの位置を追加
                target.add(new Vec(b.getPosition().x,b.getPosition().y));
                count++;
            }
        }
        if (count > 0) {
            target.div(count); //ターゲット位置ベクトルの平均化
            return seek(target);
        }
        return target;
    }
}


class Vec {
    double x, y;

    Vec() {
    }

    Vec(double x, double y) {
        this.x = x;
        this.y = y;
    }

    void add(Vec v) {
        x += v.x;
        y += v.y;
    }

    void sub(Vec v) {
        x -= v.x;
        y -= v.y;
    }

    void div(double val) {
        x /= val;
        y /= val;
    }

    void mult(double val) {
        x *= val;
        y *= val;
    }

    double mag() { //ベクトルの大きさ(絶対値)
        return sqrt(pow(x, 2) + pow(y, 2));
    }

    double dot(Vec v) {
        return x * v.x + y * v.y;
    }

    void normalize() { //正規化
        double mag = mag();
        if (mag != 0) {
            x /= mag;
            y /= mag;
        }
    }

    void limit(double lim) {
        double mag = mag();
        if (mag != 0 && mag > lim) {
            x *= lim / mag;
            y *= lim / mag;
        }
    }

    double heading() {
        return atan2(y, x);
    }

    static Vec sub(Vec v, Vec v2) {
        return new Vec(v.x - v2.x, v.y - v2.y);
    }

    static double dist(Vec v, Vec v2) {
        return sqrt(pow(v.x - v2.x, 2) + pow(v.y - v2.y, 2));
    }

    static double angleBetween(Vec v, Vec v2) {
        return acos(v.dot(v2) / (v.mag() * v2.mag()));
    }
}