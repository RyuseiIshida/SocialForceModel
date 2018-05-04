
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
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

import javax.vecmath.Vector2d;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import static java.lang.Math.pow;
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
    private boolean isSpaceButton = false;

    private static final double m_GaussianMean = 1.34;
    private static final double m_GaussianStandardDeviation = 0.26;
    private static final float view_phi_theta = 120;
    private static final float view_dmax = 100;
    private final ArrayList<Vector2d> exitVec = new ArrayList<>(Arrays.asList(new Vector2d(30,230), new Vector2d(700, 230)));
    private final CStatic wallDownLine     = new CStatic(150,30,750,30);
    private final CStatic wallUpLine       = new CStatic(150,450,750,450);
    private final CStatic wallRightLine    = new CStatic(750,30,750,450);
    private final CStatic wallexitDownLine = new CStatic(150,30,150,200);
    private final CStatic wallexitUpLine   = new CStatic(150,250,150,450);

    public ArrayList<COutput> test = new ArrayList<>();

    @Override
    public void create () {
        personImage = new Texture(Gdx.files.internal("person.png"));
        exitImage = new Texture(Gdx.files.internal("exit.png"));
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 800, 480);
        batch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();
        spawnExit();
        spawnWall();
    }

    private void spawnAgent(Vector3 pos){
        //m_pedestrian.add( new CPedestrian( new Vector2d(pos.x, pos.y),
        //        1, new CGoal( -10, 230, 0, 240).get_goals(), this, new Sprite(personImage)) );
        m_pedestrian.add( new CPedestrian( new Vector2d(pos.x, pos.y),
                1, new CGoal( pos.x, pos.y, pos.x, pos.y).get_goals(), this, new Sprite(personImage)) );
        for (CPedestrian cPedestrian : m_pedestrian) {
            cPedestrian.setGoalposition(exitVec.get(MathUtils.random(1)));
        }

    }

    private void spawnExit(){
        for (Vector2d exitvec : exitVec) {
            Sprite spexit = new Sprite(exitImage);
            spexit.setPosition((float)exitvec.x-16,(float)exitvec.y-16);
            exit.add(spexit);
        }
    }

    private void spawnWall(){
        m_wall.add(wallDownLine);
        m_wall.add(wallUpLine);
        m_wall.add(wallRightLine);
        m_wall.add(wallexitDownLine);
        m_wall.add(wallexitUpLine);
        m_wall.forEach( i->
                {
                    m_walledge.add(i.getwall1());
                    m_walledge.add(i.getwall2());
                    m_walledge.add(i.getwall3());
                    m_walledge.add(i.getwall4());
                }
        );
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
            float delta_x =  (float)agent.getGoalposition().getX() - (float)agent.getPosition().getX();
            float delta_y = (float)agent.getGoalposition().getY() - (float)agent.getPosition().getY();
            float length = (float)Math.sqrt(pow(delta_x,2) + pow(delta_y,2));
            delta_x = delta_x/length * 10 + (float)agent.getPosition().getX(); //15はエージェントの体分
            delta_y = delta_y/length * 10 + (float)agent.getPosition().getY();
            //agentの向きライン描画
            shapeRenderer.line(((float) agent.getPosition().x),(float)agent.getPosition().y, delta_x,delta_y);
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
            shapeRenderer.arc((float) agent.getPosition().x,(float)agent.getPosition().y,view_dmax,goaltheta,view_phi_theta);
            isEyeLap(agent.getPosition().x,agent.getPosition().y,agent.getGoalposition().x,agent.getGoalposition().y);

        }
        shapeRenderer.end();
        Gdx.gl.glDisable(GL20.GL_BLEND);


        // クリックされたとき
        if(Gdx.input.justTouched()) {
            Vector3 touchPos = new Vector3();
            touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
            camera.unproject(touchPos);
            spawnAgent(touchPos);
        }

        // スペースを押されたとき
        if(Gdx.input.isKeyJustPressed(Input.Keys.SPACE)){
            if(isSpaceButton) isSpaceButton = false;
            else isSpaceButton = true;
        }

        if(isSpaceButton) {
            update();
            Iterator<CPedestrian> iterator = m_pedestrian.iterator();
            while(iterator.hasNext()) {
                CPedestrian agent = iterator.next();
                if(agent.getPosition().x < 5) iterator.remove();
            }
        }

        // Pを押された時
        if(Gdx.input.isKeyJustPressed(Input.Keys.P)) {
            for(int i =0; i<100; i++) spawnAgent(new Vector3(600, 240, 0));
        }

        //ゴールについたら消す → exitImageと重なったら削除
        Iterator<CPedestrian> cPedestrianIterator = m_pedestrian.iterator();
        while (cPedestrianIterator.hasNext()) {
            CPedestrian next =  cPedestrianIterator.next();
            for (Sprite sprite : exit) {
                if(next.getSprite().getBoundingRectangle().overlaps(sprite));
            }
        }
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

    public int getDistance(double x1, double y1, double x2, double y2) {
        double distance = Math.sqrt((x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1));
        return (int) distance;
    }

    public void isEyeLap(double x1, double y1, double x2, double y2){
        double degree = getTheta(x1,y1,x2,y2);
        int distance = getDistance(x1,y1,x2,y2);
        //if(degree<view_phi_theta && distance<view_dmax) System.out.println("Yes");
        //else System.out.println("No");
        //if(degree<view_phi_theta && distance<view_dmax) return true;
        //else return false;
    }

    public ArrayList<CPedestrian> isAgentLap(double x1, double y1, double x2, double y2){
        double delta_x;
        ArrayList<CPedestrian> langeMember = new ArrayList<>();
        for (CPedestrian cPedestrian : m_pedestrian) {
            //オブジェクト - 向かっている方向 = delta_x
            // view_phi-theta - delta_x > 0 -> 重なっている
            delta_x = getTheta(x1,y1,cPedestrian.getPosition().x,cPedestrian.getPosition().y) - getTheta(x1,y1,x2,y2);
            if(delta_x > 0) delta_x *= -1;
            if (view_phi_theta - delta_x > 0) langeMember.add(cPedestrian);
        }
        return langeMember;
    }

    public float getTheta(double x1, double y1, double x2, double y2){
        double radian = Math.atan2(y2-y1, x2-x1);
        double degree = radian * 180d / Math.PI;
        if(degree<0) degree = 360 + degree;
        return (float)degree;
    }
}
