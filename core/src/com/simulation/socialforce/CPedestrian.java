package com.simulation.socialforce;

import javax.vecmath.Vector2f;
import java.util.*;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.MathUtils;

public class CPedestrian implements IPedestrian{
    private Parameter parameter = new Parameter();
    private static final float m_maxspeedfactor = 2.5f;
    private static final float m_maxforce = 0.1f;
    private static final float m_radius = 20f;
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
        sprite = sprites;
        sprite.setPosition(m_position.x-32/2,m_position.y-32/2);
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

        //ルール
        //出口はあるか
        this.setTargetExit();
        if(this.getisExitInfo()==false) {
            //出口を知っている人が周りにいるか
            getTargetPedestrian_turn(l_env.m_pedestrian);
            getTargetPedestrian(l_env.m_pedestrian);
            //ランダムに歩1く
            if (l_env.step % 50 == 0) {
                System.out.println("kita");
                int randomx = MathUtils.random(-200, 200);
                int randomy = MathUtils.random(-200, 200);
                this.setGoalposition(new Vector2f(this.m_position.x + randomx, this.m_position.y + randomy));
            }
        }

        final float l_check = CVector.sub( this.getGoalposition(), this.getPosition() ).length();

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
            if(mvec.getisExitInfo() && parameter.view_dmax >= distance && parameter.view_phi_theta/2 - delta_x >= 0 ) this.setGoalposition(new Vector2f(mvec.getPosition().x,mvec.getPosition().y));
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
                this.setGoalposition(vec);
                this.setExitInfo(true);
            }
        }
    }
    public boolean judgeIntersected(float ax,float ay,float bx,float by,float cx,float cy,float dx,float dy){
        float ta = (cx - dx) * (ay - cy) + (cy - dy) * (cx - ax);
        float tb = (cx - dx) * (by - cy) + (cy - dy) * (cx - bx);
        float tc = (ax - bx) * (cy - ay) + (ay - by) * (ax - cx);
        float td = (ax - bx) * (dy - ay) + (ay - by) * (ax - dx);
        return tc * td < 0 && ta * tb < 0;
        // return tc * td <= 0 && ta * tb <= 0; // 端点を含む場合
    }

    public boolean judgeIntersectedRect(CPedestrian ped,Rect rect){
        if(judgeIntersected(ped.getPosition().x,ped.getPosition().y,
                ped.getGoalposition().x,ped.getGoalposition().y,
                rect.x,rect.y,rect.leftButtom.x,rect.leftButtom.y))
            return true;
        else if(judgeIntersected(ped.getPosition().x,ped.getPosition().y,
                ped.getGoalposition().x,ped.getGoalposition().y,
                rect.x,rect.y,rect.leftTop.x,rect.leftTop.y))
            return true;
        else if(judgeIntersected(ped.getPosition().x,ped.getPosition().y,
                ped.getGoalposition().x,ped.getGoalposition().y,
                rect.x,rect.y,rect.rightButtom.x,rect.rightButtom.y))
            return true;
        else if(judgeIntersected(ped.getPosition().x,ped.getPosition().y,
                ped.getGoalposition().x,ped.getGoalposition().y,
                rect.x,rect.y,rect.rightTop.x,rect.rightTop.y))
            return true;
        else return false;
    }

    public void setSubGoal(CPedestrian ped){
        Vector2f goalVec;
        for(Rect rect : parameter.arrayRect){
            //交差判定
            if(judgeIntersectedRect(ped,rect)){
                Map<Vector2f, Float> goalDis = new HashMap<>();
                goalDis.put(rect.leftButtom, (float) getDistance(ped.getGoalposition().x, ped.getGoalposition().y, rect.leftButtom.x, rect.leftButtom.y));
                goalDis.put(rect.leftTop, (float) getDistance(ped.getGoalposition().x, ped.getGoalposition().y, rect.leftTop.x, rect.leftTop.y));
                goalDis.put(rect.rightButtom, (float) getDistance(ped.getGoalposition().x, ped.getGoalposition().y, rect.rightButtom.x, rect.rightButtom.y));
                goalDis.put(rect.rightTop, (float) getDistance(ped.getGoalposition().x, ped.getGoalposition().y, rect.rightTop.x, rect.rightTop.y));
                List<Map.Entry<Vector2f, Float>> list_entries = new ArrayList<Map.Entry<Vector2f, Float>>(goalDis.entrySet());
                Collections.sort(list_entries, new Comparator<Map.Entry<Vector2f, Float>>() {
                    public int compare(Map.Entry<Vector2f, Float> obj1, Map.Entry<Vector2f, Float> obj2) {
                        return obj1.getValue().compareTo(obj2.getValue());
                    }
                });
                //最もゴールから近い点をのぞいて近い2点を選ぶ
                //そのうち最も歩行者に近い点をゴールとする
                int tmpGoal1 = getDistance(ped.getPosition().x, ped.getPosition().y, list_entries.get(1).getKey().x, list_entries.get(1).getKey().y);
                int tmpGoal2 = getDistance(ped.getPosition().x, ped.getPosition().y, list_entries.get(2).getKey().x, list_entries.get(2).getKey().y);
                if (tmpGoal1 < tmpGoal2) goalVec = list_entries.get(1).getKey();
                else goalVec = list_entries.get(2).getKey();
                //ped.setSubGoalposition(new Vector2f(goalVec.x+50,goalVec.y));
                //ゴールベクトルが重なっていたら
                //if (judgeIntersectedRect(ped,rect) setSubGoal(ped);
                //else ped.setGoalposition(goalVec);
            }
        }
    }
    public void getSubGoal(CPedestrian ped){
        for (Rect rect : parameter.arrayRect) {
            float pedDegree = getDegree(ped.getPosition().x, ped.getPosition().y,ped.getGoalposition().x,ped.getGoalposition().y);
            //rect.leftButtom = (300.0, 400.0)
            //rect.leftTop = (300.0, 430.0)
            //rect.rightButtom = (700.0, 400.0)
            //rect.rightTop = (700.0, 430.0)
            //System.out.println("pedDegree = " + pedDegree);
            //pedDegree = 71
            //rect_leftButtom = 151
            //rect_leftTop = 142
            //rect_rightButtom = 16
            //rect_rightTop = 22
            float rect_leftButtom = getDegree(ped.getPosition().x,ped.getPosition().y,rect.leftButtom.x,rect.leftButtom.y);
            //System.out.println("rect_leftButtom = " + rect_leftButtom);
            float rect_leftTop = getDegree(ped.getPosition().x,ped.getPosition().y,rect.leftTop.x,rect.leftTop.y);
            //System.out.println("rect_leftTop = " + rect_leftTop);
            float rect_rightButtom = getDegree(ped.getPosition().x,ped.getPosition().y,rect.rightButtom.x,rect.rightButtom.y);
            //System.out.println("rect_rightButtom = " + rect_rightButtom);
            float rect_rightTop = getDegree(ped.getPosition().x,ped.getPosition().y,rect.rightTop.x,rect.rightTop.y);
            //System.out.println("rect_rightTop = " + rect_rightTop);

            ArrayList<Vector2f> tmpRect = new ArrayList<>();
            if(pedDegree > rect_leftButtom) tmpRect.add(rect.leftButtom);
            if(pedDegree > rect_leftTop); tmpRect.add(rect.leftTop);
            if(pedDegree > rect_rightButtom); tmpRect.add(rect.rightButtom);
            if(pedDegree > rect_rightTop); tmpRect.add(rect.rightTop);
            // tmpRect.size() -> 0または4ならば 当たっていない
            if(tmpRect.size() == 1){ ped.setGoalposition(tmpRect.get(0));
                System.out.println("1てん");
            }
            if(tmpRect.size() == 2){ //角度が小さい方に進む
                System.out.println("2てん");
                float tmpDegree0 = getDegree(ped.getPosition().x,ped.getPosition().y,tmpRect.get(0).x,tmpRect.get(0).y);
                float tmpDegree1 = getDegree(ped.getPosition().x,ped.getPosition().y,tmpRect.get(1).x,tmpRect.get(1).y);
                if(tmpDegree0 < tmpDegree1) ped.setGoalposition(tmpRect.get(0));
                else ped.setGoalposition(tmpRect.get(1));
            }
            if(tmpRect.size() == 3){ //関係のないmaxシータをサブゴールにする //一番近い点
                System.out.println("3てん");
                //float values[] = {rect_leftButtom,rect_leftTop,rect_rightButtom,rect_rightTop};
                float values[] = {
                        getDistance(ped.getPosition().x,ped.getPosition().y,rect.leftButtom.x,rect.leftButtom.y),
                        getDistance(ped.getPosition().x,ped.getPosition().y,rect.leftTop.x,rect.leftTop.y),
                        getDistance(ped.getPosition().x,ped.getPosition().y,rect.rightButtom.x,rect.rightButtom.y),
                        getDistance(ped.getPosition().x,ped.getPosition().y,rect.rightTop.x,rect.rightTop.y),
                };
                float max = values[0];
                float min = values[0];
                int max_index = 0;
                int min_index = 0;
                for (int i = 1; i<values.length; i++) {
//                    if(max < values[i]){
//                        max = values[i];
//                        max_index = i;
//                    }
                    if(min > values[i]){
                        min = values[i];
                        min_index = i;
                    }
                }
                switch (min_index) {
                    case 0:
                        //ped.setGoalposition(rect.leftButtom);
                        ped.setGoalposition(new Vector2f(rect.leftButtom.x-50, rect.leftButtom.y+30));
                        break;
                    case 1:
                        //ped.setGoalposition(rect.leftTop);
                        ped.setGoalposition(new Vector2f(rect.leftTop.x-30,rect.leftTop.y+30));
                        break;
                    case 2:
                        //ped.setGoalposition(rect.rightButtom);
                        ped.setGoalposition(new Vector2f(rect.rightButtom.x+50, rect.rightButtom.y+30));
                        break;
                    default:
                        //ped.setGoalposition(rect.rightTop);
                        ped.setGoalposition(new Vector2f(rect.rightTop.x+30, rect.rightTop.y+30));
                }
            }
            //else System.out.println("当たっていない");
        }
    }
    public void changeGoal(CPedestrian ped){
        float distance = getDistance(ped.getPosition().x,ped.getPosition().y,ped.getGoalposition().x,ped.getGoalposition().y);
        if (distance > 30) {
            //   next();
        }
    }

}
