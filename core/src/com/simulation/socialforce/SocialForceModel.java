
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
import com.simulation.ifcparser.IfcParser;

import javax.vecmath.Vector2f;
import java.util.*;


public class SocialForceModel extends ApplicationAdapter {
    private Texture personImage;
    private Texture exitImage;
    private SpriteBatch batch;
    private ShapeRenderer shapeRenderer;
    private OrthographicCamera camera;
    private Parameter parameter = new Parameter();
    private IfcParser ifcParser;
    public ArrayList<CPedestrian> m_pedestrian = new ArrayList<>();
    private ArrayList<CWall> m_walledge = new ArrayList<>();
    private ArrayList<Sprite> exit = new ArrayList<>();
    private boolean isStart = false; //スペースボタン
    private boolean isGoalInfo = false; //Fボタン
    private Vector2f initVec = new Vector2f(0,0);
    public ArrayList<COutput> test = new ArrayList<>();
    public static double step = 0;

    @Override
    public void create () {
        personImage = new Texture(Gdx.files.internal("person.png"));
        exitImage = new Texture(Gdx.files.internal("exit.png"));
        camera = new OrthographicCamera();
        camera.setToOrtho(false, parameter.scale.x, parameter.scale.y);
        batch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();
        //getIfc();
        spawnExit();
        spawnWall();
        spawnRect();
        spawnInitAgent();
    }

    private void getIfc() throws Exception{
        try{
            ifcParser = new IfcParser(parameter.IfcModelPath);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void spawnInitAgent(){
        for (int i = 0; i < 100; i++) {
            //ランダムな方向を向いた歩行者を追加
            Vector2f initPos = new Vector2f(MathUtils.random(50,1500),MathUtils.random(50,900));
            float initDirectionX = MathUtils.random(initPos.x - 1, initPos.x + 1);
            float initDirectionY = MathUtils.random(initPos.y - 1, initPos.y + 1);
            m_pedestrian.add(new CPedestrian(this,false, initPos,
                    1, new Vector2f(initDirectionX, initDirectionY),new Sprite(personImage)));
        }
//        for (int i = 0; i < m_pedestrian.size(); i++) {
//            for (int j = 0; j < m_pedestrian.size(); j++) {
//                if(m_pedestrian.get(j).getSprite().getBoundingRectangle().overlaps(m_pedestrian.get(i).getSprite().getBoundingRectangle()))
//                    m_pedestrian.remove(j);
//            }
//        }
        //m_pedestrian.add(new CPedestrian(this,true,new Vector2f(850,400),1,new Vector2f(parameter.exitVec.get(0)),new Sprite(personImage)));
        m_pedestrian.add(new CPedestrian(this,false,new Vector2f(1000,100),1,new Vector2f(1000,-10),new Sprite(personImage)));
        //m_pedestrian.add(new CPedestrian(this,true,new Vector2f()))
        //m_pedestrian.add(new CPedestrian(this,true,new Vector2f(600,450),1,new Vector2f(parameter.exitVec.get(0)),new Sprite(personImage)));

    }

    private void spawnAgent(Vector3 pos){
        //m_pedestrian.add( new CPedestrian( new Vector2f(pos.x, pos.y),
        //        1, new CGoal( 0, 0, 0, 0).get_goals(), this, new Sprite(personImage)) );
        //ゴール情報あり
        if(isGoalInfo){
            m_pedestrian.add(new CPedestrian(this,true,new Vector2f(pos.x, pos.y),
                    1, new Vector2f(parameter.exitVec.get(0).x,parameter.exitVec.get(0).y), new Sprite(personImage)) );
        }
        //ゴール情報なし
        else {
            //m_pedestrian.add(new CPedestrian(false,new Vector2f(pos.x, pos.y),
            //        1, new CGoal( pos.x+initVec.getX(), pos.y+initVec.getY(), pos.x+initVec.getX(), pos.y+initVec.getY()).get_goals(), this, new Sprite(personImage)) );

            //ランダムな方向を向いた歩行者を追加
            float initDirectionX = MathUtils.random(pos.x - 1, pos.x + 1);
            float initDirectionY = MathUtils.random(pos.y - 1, pos.y + 1);
            m_pedestrian.add(new CPedestrian(this,false, new Vector2f(pos.x, pos.y),
                    1, new Vector2f(initDirectionX, initDirectionY),new Sprite(personImage)));
        }
    }

    private void spawnExit(){
        for (Vector2f exitvec : parameter.exitVec) {
            Sprite spexit = new Sprite(exitImage);
            spexit.setPosition(exitvec.x-16,exitvec.y-16);
            exit.add(spexit);
        }
    }

    private void spawnWall(){
        for (CStatic cStatic : parameter.m_wall) {
            m_walledge.add(cStatic.getwall1());
            m_walledge.add(cStatic.getwall2());
            m_walledge.add(cStatic.getwall3());
            m_walledge.add(cStatic.getwall4());
        }
    }

    private void spawnRect(){
        for (Rect rect : parameter.arrayRect) {
            m_walledge.add(rect.force.getwall1());
            m_walledge.add(rect.force.getwall2());
            m_walledge.add(rect.force.getwall3());
            m_walledge.add(rect.force.getwall4());
        }
    }

    @Override
    public void render () {
        step++;

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
        for (CStatic cStatic : parameter.m_wall) {
            shapeRenderer.line(cStatic.getX1(),cStatic.getY1(),cStatic.getX2(),cStatic.getY2());
        }

        for(CPedestrian agent: m_pedestrian){
            //agentの向きライン描画
            float goalDegree = agent.getPedestrianDegree();
            shapeRenderer.setColor(Color.BLACK);
            shapeRenderer.arc(agent.getPosition().x,agent.getPosition().y,13,goalDegree,0.8f);
            // 出口情報あり
            if(agent.getisExitInfo()){
                shapeRenderer.setColor(Color.RED);
                shapeRenderer.circle(agent.getPosition().x,agent.getPosition().y, 10);
            }

        }
        shapeRenderer.end();


        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(new Color(0, 1, 0, 0.1f));
        for(CPedestrian agent: m_pedestrian){
            // 視野範囲の描写
            float goaltheta = agent.getPedestrianDegree();
            goaltheta -= parameter.view_phi_theta/2;
            shapeRenderer.setColor(new Color(0, 1, 0, 0.1f));
            shapeRenderer.arc((float)agent.getPosition().x,(float)agent.getPosition().y,parameter.view_dmax,goaltheta,parameter.view_phi_theta);
        }

        //障害物の描画
        shapeRenderer.setColor(Color.GRAY);
        for(Rect rect: parameter.arrayRect) shapeRenderer.rect((float)rect.x,(float)rect.y,(float)rect.width,(float)rect.height);
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
//            //ステップ毎のエージェントの行動
//            for (CPedestrian cPedestrian : m_pedestrian) {
//                //ゴールが視界に入っているか
//                cPedestrian.setGoalposition(getTargetExit(cPedestrian));
//                //ゴールを知っていない場合,視界内にいるゴールを目指すエージェントに向かう
//                if(cPedestrian.getisExitInfo()==false){
//                    cPedestrian.setGoalposition(getTargetPedestrian_turn(cPedestrian));
//                    cPedestrian.setGoalposition(getTargetPedestrian(cPedestrian));
//                    if (step % 50 == 0) {
//                        int randomx = MathUtils.random(-200,200);
//                        int randomy = MathUtils.random(-200,200);
//                        cPedestrian.setGoalposition(new Vector2f(cPedestrian.getPosition().x+randomx,cPedestrian.getPosition().y+randomy));
//                    }
//                }
//                //getSubGoal(cPedestrian);
//                setSubGoal(cPedestrian);
//            }
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


}
