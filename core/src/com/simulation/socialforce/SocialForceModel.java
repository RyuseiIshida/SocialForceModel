
package com.simulation.socialforce;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;

import javax.vecmath.Vector2f;
import java.util.*;
import java.util.Map;
import java.util.Map.Entry;

import static java.lang.Math.pow;
import static java.lang.Math.random;
import static java.lang.Math.sqrt;

public class SocialForceModel extends ApplicationAdapter {
    private Texture personImage;
    private Texture exitImage;
    private SpriteBatch batch;
    private ShapeRenderer shapeRenderer;
    private OrthographicCamera camera;
    private ArrayList<CPedestrian> m_pedestrian = new ArrayList<>();
    private ArrayList<CStatic> m_wall = new ArrayList<>( 2 );
    private ArrayList<CWall> m_walledge = new ArrayList<>();
    private ArrayList<Sprite> exit = new ArrayList<>();
    private boolean isStart = false; //スペースボタン
    private boolean isGoalInfo = false; //Fボタン
    private Vector2f initVec = new Vector2f(0,0);

    private int step = 0;
    private int num = 0;

    private static final double m_GaussianMean = 1.34;
    private static final double m_GaussianStandardDeviation = 0.26;
    private static final float view_phi_theta = 360;
    private static final float view_dmax = 800;
    //private final ArrayList<Vector2f> exitVec = new ArrayList<>(Arrays.asList(new Vector2f(30,230),new Vector2f(700, 230)));
    private final ArrayList<Vector2f> exitVec = new ArrayList<>(Arrays.asList(new Vector2f(500, 600)));
    //private final ArrayList<Rect> arrayRect = new ArrayList<>();
    private final ArrayList<Rect> arrayRect = new ArrayList<>(Arrays.asList(new Rect(300,200,400,300)));
    private final CStatic wallDownLine     = new CStatic(0,0,0,0);
    private final CStatic wallUpLine       = new CStatic(0,0,0,0);
    private final CStatic wallRightLine    = new CStatic(0,0,0,0);
    private final CStatic wallexitDownLine = new CStatic(0,0,0,0);
    private final CStatic wallexitUpLine   = new CStatic(0,0,0,0);
    //private final CStatic wallDownLine     = new CStatic(150,30,750,30);
    //private final CStatic wallUpLine       = new CStatic(150,450,750,450);
    //private final CStatic wallRightLine    = new CStatic(750,30,750,450);
    //private final CStatic wallexitDownLine = new CStatic(150,30,150,200);
    //private final CStatic wallexitUpLine   = new CStatic(150,250,150,450);
    public ArrayList<COutput> test = new ArrayList<>();

    @Override
    public void create () {
        personImage = new Texture(Gdx.files.internal("person.png"));
        exitImage = new Texture(Gdx.files.internal("exit.png"));
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 800*2, 480*2);
        batch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();
        spawnExit();
        spawnWall();
        spawnRect();
    }

    private void spawnInitAgent(){
        m_pedestrian.add( new CPedestrian(true,new Vector2f(200, 80),
                1, new Vector2f(100,100), this, new Sprite(personImage)) );
    }

    private void spawnAgent(Vector3 pos){
        //m_pedestrian.add( new CPedestrian( new Vector2f(pos.x, pos.y),
        //        1, new CGoal( 0, 0, 0, 0).get_goals(), this, new Sprite(personImage)) );
        //ゴール情報あり
        if(isGoalInfo){
            m_pedestrian.add( new CPedestrian(true,new Vector2f(pos.x, pos.y),
                    1, new CGoal(exitVec.get(0).x,exitVec.get(0).y,exitVec.get(0).x,exitVec.get(0).y).get_goals(), this, new Sprite(personImage)) );
        }
        //ゴール情報なし
        else {
            //m_pedestrian.add(new CPedestrian(false,new Vector2f(pos.x, pos.y),
            //        1, new CGoal( pos.x+initVec.getX(), pos.y+initVec.getY(), pos.x+initVec.getX(), pos.y+initVec.getY()).get_goals(), this, new Sprite(personImage)) );

            //ランダムな方向を向いた歩行者を追加
            float initDirectionX = MathUtils.random(pos.x - 1, pos.x + 1);
            float initDirectionY = MathUtils.random(pos.y - 1, pos.y + 1);
            m_pedestrian.add(new CPedestrian(false, new Vector2f(pos.x, pos.y),
                    1, new CGoal(initDirectionX, initDirectionY, initDirectionX, initDirectionY).get_goals(), this, new Sprite(personImage)));
        }
    }

    private void spawnExit(){
        for (Vector2f exitvec : exitVec) {
            Sprite spexit = new Sprite(exitImage);
            spexit.setPosition(exitvec.x-16,exitvec.y-16);
            exit.add(spexit);
        }
    }

    private void spawnWall(){
        m_wall.add(wallDownLine);
        m_wall.add(wallUpLine);
        m_wall.add(wallRightLine);
        m_wall.add(wallexitDownLine);
        m_wall.add(wallexitUpLine);
        for (CStatic cStatic : m_wall) {
            m_walledge.add(cStatic.getwall1());
            m_walledge.add(cStatic.getwall2());
            m_walledge.add(cStatic.getwall3());
            m_walledge.add(cStatic.getwall4());
        }
    }

    private void spawnRect(){
        for (Rect rect : arrayRect) {
            m_walledge.add(rect.force.getwall1());
            m_walledge.add(rect.force.getwall2());
            m_walledge.add(rect.force.getwall3());
            m_walledge.add(rect.force.getwall4());
        }
    }

    @Override
    public void render () {


        Gdx.gl.glClearColor(255, 255, 255, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camera.update();
        batch.setProjectionMatrix(camera.combined);

        //描画
        batch.begin();
        for(CPedestrian agent: m_pedestrian) agent.getSprite().draw(batch);
        for (Sprite spexit: exit) {
            spexit.draw(batch);
        }
        batch.end();

        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        //壁の描画
        shapeRenderer.setColor(0,0,0,0);
        shapeRenderer.line(wallDownLine.getX1(),wallDownLine.getY1(),wallDownLine.getX2(),wallDownLine.getY2());
        shapeRenderer.line(wallUpLine.getX1(),wallUpLine.getY1(),wallUpLine.getX2(),wallUpLine.getY2());
        shapeRenderer.line(wallRightLine.getX1(),wallRightLine.getY1(),wallRightLine.getX2(),wallRightLine.getY2());
        shapeRenderer.line(wallexitDownLine.getX1(),wallexitDownLine.getY1(),wallexitDownLine.getX2(),wallexitDownLine.getY2());
        shapeRenderer.line(wallexitUpLine.getX1(),wallexitUpLine.getY1(),wallexitUpLine.getX2(),wallexitUpLine.getY2());

        for(CPedestrian agent: m_pedestrian){
            //agentの向きライン描画
            float goaltheta = getTheta(agent.getPosition().x,agent.getPosition().y,agent.getGoalposition().x,agent.getGoalposition().y);
            shapeRenderer.setColor(Color.BLACK);
            shapeRenderer.arc((float)agent.getPosition().x,(float)agent.getPosition().y,13,goaltheta,0.8f);
            // 出口情報あり
            if(agent.getisExitInfo()){
                shapeRenderer.setColor(Color.RED);
                shapeRenderer.circle((float)agent.getPosition().x,(float)agent.getPosition().y, 10);
            }

        }
        shapeRenderer.end();


        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(new Color(0, 1, 0, 0.1f));
        for(CPedestrian agent: m_pedestrian){
            // 視野範囲の描写
            //ゴールに対する角度θを求める
            float goaltheta = getTheta(agent.getPosition().x,agent.getPosition().y,agent.getGoalposition().x,agent.getGoalposition().y);
            goaltheta -= view_phi_theta/2;
            shapeRenderer.setColor(new Color(0, 1, 0, 0.1f));
            shapeRenderer.arc((float)agent.getPosition().x,(float)agent.getPosition().y,view_dmax,goaltheta,view_phi_theta);
        }

        //障害物の描画
        shapeRenderer.setColor(Color.GRAY);
        for(Rect rect: arrayRect) shapeRenderer.rect((float)rect.x,(float)rect.y,(float)rect.width,(float)rect.height);
        shapeRenderer.end();
        Gdx.gl.glDisable(GL20.GL_BLEND);


        // クリックされたとき
        if(Gdx.input.justTouched()) {
            Vector3 touchPos = new Vector3();
            touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
            camera.unproject(touchPos);
            System.out.println("touchPos = " + touchPos);
            spawnAgent(touchPos);
        }


        if(Gdx.input.isKeyJustPressed(Input.Keys.SPACE)){
            if(isStart) isStart = false;
            else isStart = true;
        }
        else if(Gdx.input.isKeyJustPressed(Input.Keys.F)){
            if(isGoalInfo) isGoalInfo = false;
            else  isGoalInfo = true;
        }
        else if(Gdx.input.isKeyJustPressed(Input.Keys.P))
            for(int i =0; i<100; i++) spawnAgent(new Vector3(400, 240, 0));
        else if(Gdx.input.isKeyJustPressed(Input.Keys.D))
            m_pedestrian.removeAll(m_pedestrian);
        else if(Gdx.input.isKeyJustPressed(Input.Keys.UP))
            initVec.set(0,1);
        else if(Gdx.input.isKeyJustPressed(Input.Keys.DOWN))
            initVec.set(0,-1);
        else if(Gdx.input.isKeyJustPressed(Input.Keys.RIGHT))
            initVec.set(1,0);
        else if(Gdx.input.isKeyJustPressed(Input.Keys.LEFT))
            initVec.set(-1,0);



        //ゴールについたら消す → exitImageと重なったら削除
        Iterator<CPedestrian> cPedestrianIterator = m_pedestrian.iterator();
        while (cPedestrianIterator.hasNext()) {
            CPedestrian next =  cPedestrianIterator.next();
            for (Sprite sprite : exit) {
                if(next.getSprite().getBoundingRectangle().overlaps(sprite.getBoundingRectangle()))
                    cPedestrianIterator.remove();
            }
        }

        if(isStart){
            update();
            //ステップ毎のエージェントの行動
            for (CPedestrian cPedestrian : m_pedestrian) {
                //ゴールが視界に入っているか
                cPedestrian.setGoalposition(getTargetExit(cPedestrian));
                //ゴールを知っていない場合,視界内にいるゴールを目指すエージェントに向かう
                if(cPedestrian.getisExitInfo()==false){
                    cPedestrian.setGoalposition(getTargetPedestrian_turn(cPedestrian));
                    cPedestrian.setGoalposition(getTargetPedestrian(cPedestrian));
                    if (step % 50 == 0) {
                        int randomx = MathUtils.random(-200,200);
                        int randomy = MathUtils.random(-200,200);
                        cPedestrian.setGoalposition(new Vector2f(cPedestrian.getPosition().x+randomx,cPedestrian.getPosition().y+randomy));
                    }
                }
                //getSubGoal(cPedestrian);
                setSubGoal(cPedestrian);
            }
        }
        if(step==0) spawnInitAgent();
        step++;
    }


    @Override
    public void dispose () {
        personImage.dispose();
        exitImage.dispose();
        batch.dispose();
    }

    public ArrayList<CPedestrian> getPedestrianinfo()
    {
        return m_pedestrian;
    }

    public ArrayList<CWall> getWallinfo()
    {
        return m_walledge;
    }

    public void update() {

        m_pedestrian.stream()
                .parallel()
                .forEach( j ->
                {
                    try
                    {
                        j.call();
                    }
                    catch ( final Exception l_exception ) {}
                });
    }

    public int getDistance(float x1, float y1, float x2, float y2) {
        float distance = (float)Math.sqrt((x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1));
        return (int) distance;
    }

    public float getTheta(float x1, float y1, float x2, float y2){
        float radian = (float)Math.atan2(y2-y1, x2-x1);
        float degree = (float)(radian * 180d / Math.PI);
        //if(degree<0) degree = 360 + degree;
        return (float)degree;
    }

    public Vector2f getTargetPedestrian_turn(CPedestrian ped){
        Vector2f pedvec = new Vector2f(ped.getGoalposition().x,ped.getGoalposition().y);
        for (CPedestrian mvec : m_pedestrian) {
            int distance = getDistance(ped.getPosition().x,ped.getPosition().y,mvec.getPosition().x,mvec.getPosition().y);
            //対象 - 向かっている方向 = delta_x
            //view_phi-theta/2 - delta_x > 0 -> 重なっている
            float delta_x = getTheta(ped.getPosition().x,ped.getPosition().y,mvec.getPosition().x,mvec.getPosition().y)
                    - getTheta(ped.getPosition().x,ped.getPosition().y,ped.getGoalposition().x,ped.getGoalposition().y);
            if(delta_x < 0) delta_x *= -1;
            if(mvec.getisExitInfo() && view_dmax >= distance && view_phi_theta/2 - delta_x >= 0 ){
                float d = getDistance(mvec.getPosition().x,mvec.getPosition().y,mvec.getGoalposition().x,mvec.getGoalposition().y);
                float tmpx = mvec.getGoalposition().x / d;
                float tmpy = mvec.getGoalposition().y / d;
                pedvec.set(pedvec.x+tmpx,pedvec.y+tmpy);
            }
        }
        return pedvec;
    }

    public Vector2f getTargetPedestrian(CPedestrian ped){
        Vector2f pedvec = new Vector2f(ped.getGoalposition().x,ped.getGoalposition().y);
        for (CPedestrian mvec : m_pedestrian) {
            int distance = getDistance(ped.getPosition().x,ped.getPosition().y,mvec.getPosition().x,mvec.getPosition().y);
            //対象 - 向かっている方向 = delta_x
            //view_phi-theta/2 - delta_x > 0 -> 重なっている
            float delta_x = getTheta(ped.getPosition().x,ped.getPosition().y,mvec.getPosition().x,mvec.getPosition().y)
                    - getTheta(ped.getPosition().x,ped.getPosition().y,ped.getGoalposition().x,ped.getGoalposition().y);
            if(delta_x < 0) delta_x *= -1;
            if(mvec.getisExitInfo() && view_dmax >= distance && view_phi_theta/2 - delta_x >= 0 ) pedvec.set(mvec.getPosition().x,mvec.getPosition().y);
        }
        return pedvec;
    }

    public Vector2f getTargetExit(CPedestrian ped){
        Vector2f exitvec = new Vector2f(ped.getGoalposition().x,ped.getGoalposition().y);
        for (Vector2f  vec: exitVec) {
            int distance = getDistance(ped.getPosition().x,ped.getPosition().y,vec.x,vec.y);
            //対象 - 向かっている方向 = delta_x
            //view_phi-theta/2 - delta_x > 0 -> 重なっている
            float delta_x = getTheta(ped.getPosition().x,ped.getPosition().y,vec.x,vec.y)
                - getTheta(ped.getPosition().x,ped.getPosition().y,ped.getGoalposition().x,ped.getGoalposition().y);
            if(delta_x < 0) delta_x *= -1;
            if(view_dmax >= distance && view_phi_theta/2 - delta_x >= 0 ){
                exitvec.set(vec.x,vec.y);
                ped.setExitInfo(true);
            }
        }
    return exitvec;
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
        for(Rect rect : arrayRect){
            //交差判定
            if(judgeIntersectedRect(ped,rect)){
                Map<Vector2f, Float> goalDis = new HashMap<>();
                goalDis.put(rect.leftButtom, (float) getDistance(ped.getGoalposition().x, ped.getGoalposition().y, rect.leftButtom.x, rect.leftButtom.y));
                goalDis.put(rect.leftTop, (float) getDistance(ped.getGoalposition().x, ped.getGoalposition().y, rect.leftTop.x, rect.leftTop.y));
                goalDis.put(rect.rightButtom, (float) getDistance(ped.getGoalposition().x, ped.getGoalposition().y, rect.rightButtom.x, rect.rightButtom.y));
                goalDis.put(rect.rightTop, (float) getDistance(ped.getGoalposition().x, ped.getGoalposition().y, rect.rightTop.x, rect.rightTop.y));
                List<Entry<Vector2f, Float>> list_entries = new ArrayList<Entry<Vector2f, Float>>(goalDis.entrySet());
                Collections.sort(list_entries, new Comparator<Entry<Vector2f, Float>>() {
                    public int compare(Entry<Vector2f, Float> obj1, Entry<Vector2f, Float> obj2) {
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
        for (Rect rect : arrayRect) {
            float pedDegree = getTheta(ped.getPosition().x, ped.getPosition().y,ped.getGoalposition().x,ped.getGoalposition().y);
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
            float rect_leftButtom = getTheta(ped.getPosition().x,ped.getPosition().y,rect.leftButtom.x,rect.leftButtom.y);
            //System.out.println("rect_leftButtom = " + rect_leftButtom);
            float rect_leftTop = getTheta(ped.getPosition().x,ped.getPosition().y,rect.leftTop.x,rect.leftTop.y);
            //System.out.println("rect_leftTop = " + rect_leftTop);
            float rect_rightButtom = getTheta(ped.getPosition().x,ped.getPosition().y,rect.rightButtom.x,rect.rightButtom.y);
            //System.out.println("rect_rightButtom = " + rect_rightButtom);
            float rect_rightTop = getTheta(ped.getPosition().x,ped.getPosition().y,rect.rightTop.x,rect.rightTop.y);
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
                float tmpDegree0 = getTheta(ped.getPosition().x,ped.getPosition().y,tmpRect.get(0).x,tmpRect.get(0).y);
                float tmpDegree1 = getTheta(ped.getPosition().x,ped.getPosition().y,tmpRect.get(1).x,tmpRect.get(1).y);
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
