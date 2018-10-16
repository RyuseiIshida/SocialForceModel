package com.simulation.socialforce;

import javax.vecmath.Vector2f;
import java.util.*;

import com.simulation.Potential.Obstacle.Obstacle;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.MathUtils;
import com.simulation.Potential.PotentialCell;
import com.simulation.Potential.PotentialManager;
import com.simulation.Potential.PotentialMap;

import static java.lang.Math.*;


public class CPedestrian implements IPedestrian {
    private Parameter parameter = new Parameter();
    private static final float m_maxspeedfactor = 2.5f;//最高速度
    private static final float m_maxforce = 0.1f;
    private static final float m_radius = 23f;
    private Vector2f m_position;
    private Vector2f m_goal;
    private ArrayList<Vector2f> m_goals;
    private Vector2f m_velocity;
    private Vector2f tmpGoal;
    private float m_speed;
    private SocialForceModel l_env;
    private float m_maxspeed;
    private Sprite sprite;
    private boolean aisExitInfo;
    private boolean included;
    private String stateTag;
    private CPedestrian myleader;
    private ArrayList<CPedestrian> myfollower;

    private PotentialMap myPotentialMap;
    private ArrayList<Vector2f> neighboringCellsNumber;
    private boolean MOVEFLAG = true;


    public CPedestrian(SocialForceModel p_env, boolean isExitInfo, final Vector2f p_position, final float p_speed, Vector2f p_goal, Sprite sprites) {
        l_env = p_env;
        m_goals = new ArrayList<>(Arrays.asList(p_goal));
        m_goal = m_goals.get(m_goals.size() - 1);
        m_position = p_position;
        m_speed = p_speed;
        m_velocity = CVector.scale(p_speed, CVector.direction(m_goal, m_position));
        m_maxspeed = p_speed * m_maxspeedfactor;
        aisExitInfo = isExitInfo;
        if (aisExitInfo) stateTag = "GoExit";
        stateTag = "";
        sprite = sprites;
        sprite.setPosition(m_position.x - 32 / 2, m_position.y - 32 / 2);
        stateTag = "";
        included = false;
        myfollower = new ArrayList<>();
        myPotentialMap = new PotentialMap(Parameter.scale, Parameter.CELL_INTERVAL, Parameter.MAXPOTENTIAL);
    }

    public PotentialMap getMyPotentialMap() {
        return myPotentialMap;
    }

    public void setExitInfo(boolean bool) {
        aisExitInfo = bool;
    }

    public boolean getisExitInfo() {
        return aisExitInfo;
    }

    public Sprite getSprite() {
        return sprite;
    }

    public void addMyfollower(CPedestrian ped) {
        this.myfollower.add(ped);
    }

    @Override
    public Vector2f getGoalposition() {
        return m_goal;
    }

    @Override
    public IPedestrian setGoalposition(Vector2f p_position) {
        this.m_goal = p_position;
        this.m_goals.add(p_position);
        return this;
    }

    @Override
    public Vector2f getPosition() {
        return m_position;
    }

    @Override
    public IPedestrian setPosition(final float p_x, final float p_y) {
        this.m_position = new Vector2f(p_x, p_y);
        sprite.setPosition(m_position.x - 16, m_position.y - 16);
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
    public IPedestrian setposX(final float p_posX) {
        this.m_position.x = p_posX;
        sprite.setPosition(m_position.x - 16, m_position.y - 16);
        return this;
    }

    @Override
    public IPedestrian setposY(final float p_posY) {
        this.m_position.y = p_posY;
        sprite.setPosition(m_position.x - 16, m_position.y - 16);
        return this;
    }

    @Override
    public Vector2f accelaration() //加速度ベクトル
    {
        Vector2f l_repulsetoWall = new Vector2f(0, 0); //壁からの逆ベクトル
        Vector2f l_repulsetoOthers = new Vector2f(0, 0); //他のエージェントからの逆ベクトル
        Vector2f l_desiredVelocity = CVector.scale(this.m_maxspeed, CVector.direction(this.getGoalposition(), this.getPosition())); //目的地へ向かうベクトル

        //壁からの逆ベクトルを算出
        for (int i = 0; i < l_env.getWallinfo().size(); i++) {
            l_repulsetoWall = CVector.add(l_repulsetoWall, CForce.repulsewall(this, l_env.getWallinfo().get(i)));
        }

        //他のエージェントからの逆ベクトルを算出
        for (int i = 0; i < l_env.getPedestrianinfo().size(); i++) {
            if (!l_env.getPedestrianinfo().get(i).equals(this)) {
                l_repulsetoOthers = CVector.add(l_repulsetoOthers, CForce.repulseotherPed(this, l_env.getPedestrianinfo().get(i)));
            }
        }

        //たぶん全てのベクトルを足していると思う
        final Vector2f l_temp = CVector.add(CForce.drivingForce(l_desiredVelocity, this.getVelocity()), l_repulsetoOthers);
        //Vector2f l_temp = CVector.add(CForce.drivingForce(l_desiredVelocity, this.getVelocity()), l_repulsetoOthers);


        return CVector.truncate(CVector.add(l_temp, l_repulsetoWall), m_maxforce);
    }


    public float getM_radius() {
        return m_radius;
    }

    public void setStateTag(String str) {
        this.stateTag = str;
    }

    public String getStateTag() {
        return this.stateTag;
    }

    public void setMyleader(CPedestrian ped) {
        System.out.println("leaderセット");
        this.myleader = ped;
    }

    public CPedestrian getMyleader() {
        return this.myleader;
    }


    @Override
    public IPedestrian call() throws Exception {

        /*-----意思決定-----------------------------------------------------------------------------------------------*/
        //出口を知っているか
        //出口はあるか?
        this.setTargetExit();

        //追従者の行動
        if (this.stateTag == "follow") {
            if (this.getDistance(this.m_position, this.myleader.getPosition()) > 30) {
                this.m_goal = this.myleader.getPosition();
            } else {
                this.m_goal = this.getPosition();
                this.m_velocity = this.myleader.m_velocity;
            }


            if (this.getDistance(this.m_position, this.myleader.getPosition()) > 200) {
                this.stateTag = "";
            }


        }


        if (this.getisExitInfo() == false && !(this.stateTag == "follow")) {
            if (l_env.step % Parameter.STEPINTERVAL == 0) {
                if (this.getisExitInfo() == false) {
                    if (!(this.stateTag == "follow")) {
                        switch (MathUtils.random(0, 2)) {
                            case 0:
                                this.multi_people_following();
                                break;
                            case 1:
                                this.randomWalk2();
                                break;
                            case 2:
                                this.m_goal = new Vector2f(this.m_position.x, this.m_position.y);
                                break;
                            case 3:
                                lookAround();
                                break;
                        }
                    }
                }
            }
        }



        /*-----------------------------------------------------------------------------------------------------------*/

        if (!(stateTag == "GoExit") && !(stateTag == "follow") && !this.aisExitInfo) {
            this.wall_turn3();
        }

        //もし初期目標地点がスケール外は全て削除
        if (this.m_goal.x < 0 || this.m_goal.x > Parameter.scale.x || this.m_goal.y < 0 || this.m_goal.y > Parameter.scale.y) {
            this.m_goals.clear();
        }

        //this.checkObstacle();
        moveCell();


        final float l_check = CVector.sub(this.getGoalposition(), this.getPosition()).length(); //ゴールとの距離
        //final float l_check = CVector.sub( this.m_goals.get(0), this.getPosition() ).length(); //ゴールとの距離

        if (l_check <= this.getM_radius() * 0.1) //ゴールについたかの判断
        {
            this.m_velocity = new Vector2f(0, 0); //スピードベクトルを0にする
            if (this.m_goals.size() > 0) //もしゴール集合が残っているなら
            {
                this.m_goal = this.m_goals.remove(this.m_goals.size() - 1); //ゴール集合を一つ削除した新しいゴールを追加する
                this.m_velocity = CVector.scale(m_maxspeed, CVector.normalize(CVector.add(this.m_velocity, this.accelaration()))); //正規化された速度ベクトルをスケールにより調整して速度ベクトル代入
                this.m_position = CVector.add(m_position, m_velocity); //現在の場所
                sprite.setPosition(m_position.x - 16, m_position.y - 16);
            }
        } else { //ゴールについてない場合
            this.m_velocity = CVector.scale(m_maxspeed, CVector.normalize(CVector.add(this.m_velocity, this.accelaration())));
            this.m_position = CVector.add(m_position, m_velocity);
            sprite.setPosition(m_position.x - 16, m_position.y - 16);
        }
        return this;
    }


    //集団を追従
    public void multi_people_following() {
        ArrayList<CPedestrian> multiPed = new ArrayList<>();
        M:
        for (CPedestrian ped : l_env.getPedestrianinfo()) {
            if (!(this.equals(ped))) {
                int distance = getPedDistance(ped);
                float delta_x = getPedDegree(ped) - getDegree(this.m_position, this.m_goal);
                Math.abs(delta_x);
                if (parameter.view_dmax >= distance && parameter.view_phi_theta / 2 - delta_x >= 0) {
                    multiPed.add(ped);
                    if (multiPed.size() >= Parameter.judgeFollowNum && !(ped.getStateTag() == "follow")) {
                        //if( multiPed.size() >= Parameter.judgeFollowNum){
                        this.m_goal = ped.getPosition();
                        if (distance < 200) {
                            for (CPedestrian mulped : multiPed) {
                                if (mulped.getisExitInfo()) {
                                    this.stateTag = "follow";
                                    this.myleader = ped;
                                    this.m_goal = myleader.getPosition();
                                    ped.addMyfollower(this);
                                    break M;
                                }
                            }
                            int minDis = 0;
                            int tmpDis;
                            CPedestrian tmpPed = ped;
                            for (CPedestrian mulped : multiPed) {
                                if (minDis == 0) {
                                    minDis = this.getPedDistance(mulped);
                                }
                                tmpDis = this.getPedDistance(mulped);
                                if (minDis < tmpDis) {
                                    minDis = tmpDis;
                                    tmpPed = mulped;
                                }
                            }
                            this.stateTag = "follow";
                            this.myleader = tmpPed;
                            //this.myleader = ped;
                            this.m_goal = myleader.getPosition();
                            ped.setStateTag("leader");
                            ped.addMyfollower(this);
                            break;
                        }
                    } else {
                        randomWalk2();
                    }
                }
            }
        }
    }


    //集団を追従
    public void multi_people_following2() {
        Map<CPedestrian, Integer> goal_peds = new HashMap<>();
        int distance;
        for (CPedestrian ped : l_env.getPedestrianinfo()) {
            if (!(this.equals(ped))) {
                distance = this.getPedDistance(ped);
                if (distance > parameter.view_dmax) {
                    goal_peds.put(ped, distance);
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
    public void randomWalk1() {
        int randomx = MathUtils.random(-200, 200);
        int randomy = MathUtils.random(-200, 200);
        this.m_goal = new Vector2f(this.m_position.x + randomx, this.m_position.y + randomy);
    }

    //ランダムウォーク2 完全なランダム
    public void randomWalk2() {
        this.stateTag = "random";
        //System.out.println("random to allclear");
        this.m_goals.clear();
        this.m_goal = new Vector2f(MathUtils.random(Parameter.scale.x), MathUtils.random(Parameter.scale.y));
    }

    //周りを見渡す
    public void lookAround() {
        //System.out.println("clear to allclear");
        this.m_goals.clear();
        ArrayList<Vector2f> directions = new ArrayList<>(Arrays.asList(
                new Vector2f(this.m_position.x + 1, this.m_position.y + 1),
                new Vector2f(this.m_position.x + 1, this.m_position.y - 1),
                new Vector2f(this.m_position.x - 1, this.m_position.y + 1),
                new Vector2f(this.m_position.x - 1, this.m_position.y - 1)

        ));
        for (int i = 4; i >= 0; i--) {
            int random = MathUtils.random(i);
            this.setGoalposition(new Vector2f(directions.get(random)));
            directions.remove(random);
        }
    }


    public void wall_turn3() {
        for (CStatic wall : Parameter.m_wall) {
            if (judgeIntersected(m_position.x, m_position.y, viewDegreeVec2().x, viewDegreeVec2().y, wall.getX1(), wall.getY1(), wall.getX2(), wall.getY2())) {
                int randomx = MathUtils.random(-200, 200);
                int randomy = MathUtils.random(-200, 200);
                this.m_goal = new Vector2f(this.m_position.x + randomx, this.m_position.y + randomy);
            }
        }
    }


    public int getDistance(float x1, float y1, float x2, float y2) {
        float distance = (float) Math.sqrt((x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1));
        return (int) distance;
    }

    public int getDistance(Vector2f v1, Vector2f v2) {
        return this.getDistance(v1.x, v1.y, v2.x, v2.y);
    }

    public int getPedDistance(CPedestrian ped) {
        return this.getDistance(this.m_position, ped.getPosition());
    }

    public float getDegree(float x1, float y1, float x2, float y2) {
        float radian = (float) Math.atan2(y2 - y1, x2 - x1);
        float degree = (float) (radian * 180d / Math.PI);
        return degree;
    }

    public float getDegree(Vector2f v1, Vector2f v2) {
        return getDegree(v1.x, v1.y, v2.x, v2.y);
    }

    public float getPedDegree(CPedestrian ped) {
        return getDegree(this.m_position, ped.getPosition());
    }

    public float getPedestrianDegree() {
        return getDegree(this.m_position.x, this.m_position.y, this.m_goal.x, this.m_goal.y);
    }


    public void setTargetExit() {
        for (Vector2f vec : parameter.exitVec) {
            int distance = getDistance(m_position.x, m_position.y, vec.x, vec.y);
            //対象 - 向かっている方向 = delta_x
            //view_phi-theta/2 - delta_x > 0 -> 重なっている
            float delta_x = getDegree(m_position.x, m_position.y, vec.x, vec.y)
                    - getDegree(m_position.x, m_position.y, m_goal.x, m_goal.y);
            if (delta_x < 0) delta_x *= -1;
            if (parameter.view_dmax >= distance && parameter.view_phi_theta / 2 - delta_x >= 0) {
                this.m_goal = vec;
                this.setExitInfo(true);
                this.stateTag = "GoExit";
            }
        }
    }

    public boolean judgeIntersected(float ax, float ay, float bx, float by, float cx, float cy, float dx, float dy) {
        //線分交差判定 https://qiita.com/ykob/items/ab7f30c43a0ed52d16f2
        float ta = (cx - dx) * (ay - cy) + (cy - dy) * (cx - ax);
        float tb = (cx - dx) * (by - cy) + (cy - dy) * (cx - bx);
        float tc = (ax - bx) * (cy - ay) + (ay - by) * (ax - cx);
        float td = (ax - bx) * (dy - ay) + (ay - by) * (ax - dx);
        //return 線分が交差 -> true, 交差していない -> false
        return tc * td < 0 && ta * tb < 0;
    }

    public void setNeighboringCells() { //問題なし
        PotentialCell posCell = myPotentialMap.getPotentialCell(m_position);
        Vector2f matrixNumber = myPotentialMap.getMatrixNumber(posCell);
        int range = 1;
        neighboringCellsNumber = new ArrayList<>();
        for (float i = matrixNumber.x - range; i <= matrixNumber.x + range; i++) {
            i = i < 0 ? 0 : i;
            for (float j = matrixNumber.y - range; j <= matrixNumber.y + range; j++) {
                j = j < 0 ? 0 : j;
                if (!(i == matrixNumber.x && j == matrixNumber.y)) { //自分のセルは省く
                    neighboringCellsNumber.add(new Vector2f(i, j));
                }
            }
        }
    }

    public ArrayList<Vector2f> getNeighboringCellsNumber() {
        return neighboringCellsNumber;
    }

    public void moveCell() {
        setNeighboringCells();//近傍を調べる
        PotentialManager.setPedObstacleMap(this); //近く障害物情報
        PotentialManager.setPedGoalPotentialMap(this); //ゴールのポテンシャルをゲット
        if(aisExitInfo) {
            float minPotential = 100;
            PotentialCell moveCell = null;
            for (Vector2f matrixNumber : neighboringCellsNumber) {
                if (minPotential == 100) {
                    minPotential = myPotentialMap.getMatrixPotentialCells(matrixNumber.x, matrixNumber.y).getPotential();
                    moveCell = myPotentialMap.getMatrixPotentialCells(matrixNumber.x, matrixNumber.y);
                }
                if (minPotential > myPotentialMap.getMatrixPotentialCells(matrixNumber.x, matrixNumber.y).getPotential()) {
                    minPotential = myPotentialMap.getMatrixPotentialCells(matrixNumber.x, matrixNumber.y).getPotential();
                    moveCell = myPotentialMap.getMatrixPotentialCells(matrixNumber.x, matrixNumber.y);
                }
            }
            m_goal = moveCell.getCenterPoint();
        }
    }



    public float getGoalRadian() {
        double degree = this.getPedestrianDegree();
        double rad = (degree * Math.PI) / 180;
        return (float) rad;
    }

    //視野最大方向のベクトルを返す
    public Vector2f viewDegreeVec() {
        //底辺a = 斜辺c * cos
        float c = Parameter.view_dmax;
        float cos = (float) Math.cos(Math.toRadians(this.getPedestrianDegree()));
        float a = c * cos;
        //対辺b = 斜辺c * sin
        float sin = (float) Math.sin(Math.toRadians(this.getPedestrianDegree()));
        float b = c * sin;
        float result_x = a + this.m_position.x;
        float result_y = b + this.m_position.y;
        return new Vector2f(result_x, result_y);
    }

    //視野
    public Vector2f viewDegreeVec2() {
        //底辺a = 斜辺c * cos
        float c = 100;
        float cos = (float) Math.cos(Math.toRadians(this.getPedestrianDegree()));
        float a = c * cos;
        //対辺b = 斜辺c * sin
        float sin = (float) Math.sin(Math.toRadians(this.getPedestrianDegree()));
        float b = c * sin;
        float result_x = a + this.m_position.x;
        float result_y = b + this.m_position.y;
        return new Vector2f(result_x, result_y);
    }

}


