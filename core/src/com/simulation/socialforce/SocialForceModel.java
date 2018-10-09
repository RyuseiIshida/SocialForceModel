
package com.simulation.socialforce;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.simulation.ifcparser.IfcParser;

import javax.vecmath.Vector2f;
import java.util.*;

public class SocialForceModel extends ApplicationAdapter {
    private Texture personImage;
    private Texture exitImage;
    private BitmapFont bitmapFont;
    private SpriteBatch batch;
    private ShapeRenderer shapeRenderer;
    private OrthographicCamera camera;
    private Parameter parameter = new Parameter();
    private IfcParser ifcParser;
    private ArrayList<CPedestrian> m_pedestrian = new ArrayList<>();
    private ArrayList<CWall> m_walledge = new ArrayList<>();
    private ArrayList<Sprite> exit = new ArrayList<>();
    private boolean isStart = false; //スペースボタン
    private boolean isGoalInfo = false; //Fボタン
    private Vector2f initVec = new Vector2f(0, 0);
    public ArrayList<Double> GoalTime = new ArrayList<>();
    public static double step = 0;


    @Override
    public void create() {
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
        bitmapFont = new BitmapFont();
        bitmapFont.setColor(Color.BLACK);
        bitmapFont.getData().setScale(2);
    }


    private void getIfc() throws Exception {
        try {
            ifcParser = new IfcParser(parameter.IfcModelPath);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void spawnInitAgent() {
        CPedestrian ped;
        boolean DETERMINE = false;
        for (int i = 0; m_pedestrian.size() < Parameter.initPedNum; i++) {
            //ランダムな方向を向いた歩行者を追加
            Vector2f initPos = new Vector2f(MathUtils.random(180, 1400), MathUtils.random(100, 800));
            float initDirectionX = MathUtils.random(initPos.x - 1, initPos.x + 1);
            float initDirectionY = MathUtils.random(initPos.y - 1, initPos.y + 1);

            ped = i < Parameter.goalPed
                    ? new CPedestrian(this, true, initPos, 1, Parameter.exitVec.get(0), new Sprite(personImage))
                    : new CPedestrian(this, false, initPos, 1, new Vector2f(initDirectionX, initDirectionY), new Sprite(personImage));

            if (i == 0) {
                m_pedestrian.add(ped);
            }
            for (CPedestrian cPedestrian : m_pedestrian) {
                int distance = this.getDistance(ped.getPosition().x, ped.getPosition().y, cPedestrian.getPosition().x, cPedestrian.getPosition().y);
                if (distance > 30) {
                    DETERMINE = true;
                } else {
                    DETERMINE = false;
                    break;
                }
            }
            if (DETERMINE) {
                m_pedestrian.add(ped);
            }

        }
        //m_pedestrian.add(new CPedestrian(this,true,new Vector2f(600,450),1,new Vector2f(parameter.exitVec.get(0)),new Sprite(personImage)));
    }

    private void spawnAgent(Vector3 pos) {
        //ゴール情報あり
        if (isGoalInfo) {
            m_pedestrian.add(new CPedestrian(this, true, new Vector2f(pos.x, pos.y),
                    1, new Vector2f(parameter.exitVec.get(0).x, parameter.exitVec.get(0).y), new Sprite(personImage)));
        }
        //ゴール情報なし
        else {
            //ランダムな方向を向いた歩行者を追加
            float initDirectionX = MathUtils.random(pos.x - 1, pos.x + 1);
            float initDirectionY = MathUtils.random(pos.y - 1, pos.y + 1);
            m_pedestrian.add(new CPedestrian(this, false, new Vector2f(pos.x, pos.y),
                    1, new Vector2f(initDirectionX, initDirectionY), new Sprite(personImage)));
        }
    }

    private void spawnExit() {
        for (Vector2f exitvec : parameter.exitVec) {
            Sprite spexit = new Sprite(exitImage);
            spexit.setPosition(exitvec.x - 16, exitvec.y - 16);
            exit.add(spexit);
        }
    }

    private void spawnWall() {
        for (CStatic cStatic : parameter.m_wall) {
            m_walledge.add(cStatic.getwall1());
            m_walledge.add(cStatic.getwall2());
            m_walledge.add(cStatic.getwall3());
            m_walledge.add(cStatic.getwall4());
        }
    }

    private void spawnRect() {
        for (Rect rect : parameter.arrayRect) {
            m_walledge.add(rect.getForce().getwall1());
            m_walledge.add(rect.getForce().getwall2());
            m_walledge.add(rect.getForce().getwall3());
            m_walledge.add(rect.getForce().getwall4());
        }
    }

    @Override
    public void render() {
        if (isStart) {
            update();
            step++;
        }
        if (m_pedestrian.isEmpty() && isStart) {
            //System.out.println("総避難完了時間 = " + GoalTime.get(GoalTime.size()-1));
            //System.out.println(GoalTime);
        }

        //描画用の初期設定
        Gdx.gl.glClearColor(255, 255, 255, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        camera.update();
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        {
            bitmapFont.draw(batch, "time " + String.format("%.2f", step / 60), Parameter.scale.x - 200, Parameter.scale.y - 10);
            bitmapFont.draw(batch, "pedestrian = " + String.format(String.valueOf(m_pedestrian.size())), Parameter.scale.x - 450, Parameter.scale.y - 10);
            m_pedestrian.forEach(ped -> ped.getSprite().draw(batch));
            exit.forEach(spexit -> spexit.draw(batch));
        }
        batch.end();

        shapeRenderer.setProjectionMatrix(camera.combined);

        //塗りつぶし
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        {
            for (CPedestrian ped : m_pedestrian) {
                // 出口情報あり
                if (ped.getisExitInfo()) {
                    shapeRenderer.setColor(Color.RED);
                    shapeRenderer.circle(ped.getPosition().x, ped.getPosition().y, 10);
                    //shapeRenderer.rect((float)rect.x,(float)rect.y,(float)rect.width,(float)rect.height);
                    shapeRenderer.arc(ped.getPosition().x, ped.getPosition().y, 10, 0, 360);
                }
                if (ped.getStateTag() == "follow") {
                    shapeRenderer.setColor(Color.BLUE);
                    shapeRenderer.circle(ped.getPosition().x, ped.getPosition().y, 10);
                }
                //  if(ped.getStateTag() == "leader"){
                //      shapeRenderer.setColor(Color.GREEN);
                //      shapeRenderer.circle(ped.getPosition().x, ped.getPosition().y, 10);
                //  }
                if (ped.getStateTag() == "random") {
                    shapeRenderer.setColor(Color.BLACK);
                    shapeRenderer.circle(ped.getPosition().x, ped.getPosition().y, 10);
                }
            }
        }
        shapeRenderer.end();

        // 線
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        {
            shapeRenderer.setColor(Color.BLACK);
            for (CPedestrian agent : m_pedestrian) {
                //agentの向き
                shapeRenderer.arc(agent.getPosition().x, agent.getPosition().y, 13, agent.getPedestrianDegree(), 0.8f);
                //リーダーとフォロワーの結ぶライン
                if (agent.getStateTag() == "follow") {
                    shapeRenderer.line(agent.getPosition().x, agent.getPosition().y,
                            agent.getMyleader().getPosition().x, agent.getMyleader().getPosition().y);
                }
            }

            //壁
            shapeRenderer.setColor(Color.BLACK);
            parameter.m_wall.forEach(wall -> shapeRenderer.line(wall.getX1(), wall.getY1(), wall.getX2(), wall.getY2()));
            //障害物
            shapeRenderer.setColor(Color.GRAY);
            parameter.arrayRect.forEach(rect -> shapeRenderer.rect(rect.getLeftButtom().x, rect.getLeftButtom().y, rect.getWidth(), rect.getHeight()));
        }
        shapeRenderer.end();


        //薄い色
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(new Color(0, 1, 0, 0.1f));
        if (Parameter.view_Renderer) {
            for (CPedestrian agent : m_pedestrian) {
                // 視野範囲の描写
                float goaltheta = agent.getPedestrianDegree();
                goaltheta -= parameter.view_phi_theta / 2;
                shapeRenderer.setColor(new Color(0, 1, 0, 0.1f));
                shapeRenderer.arc((float) agent.getPosition().x, (float) agent.getPosition().y, parameter.view_dmax, goaltheta, parameter.view_phi_theta);
            }
        }
        shapeRenderer.end();
        Gdx.gl.glDisable(GL20.GL_BLEND);


        // クリックされたとき
        if (Gdx.input.justTouched()) {
            Vector3 touchPos = new Vector3();
            touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
            camera.unproject(touchPos);
            System.out.println("touchPos = " + touchPos);
            spawnAgent(touchPos);
        }


        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            if (isStart) isStart = false;
            else isStart = true;
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.F)) {
            if (isGoalInfo) isGoalInfo = false;
            else isGoalInfo = true;
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.P)) {
            spawnInitAgent();
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.D)) {
            m_pedestrian.removeAll(m_pedestrian);
            step = 0;
            GoalTime.clear();
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.UP))
            initVec.set(0, 1);
        else if (Gdx.input.isKeyJustPressed(Input.Keys.DOWN))
            initVec.set(0, -1);
        else if (Gdx.input.isKeyJustPressed(Input.Keys.RIGHT))
            initVec.set(1, 0);
        else if (Gdx.input.isKeyJustPressed(Input.Keys.LEFT))
            initVec.set(-1, 0);
        else if (Gdx.input.isKeyJustPressed(Input.Keys.V)) {
            if (Parameter.view_Renderer) {
                Parameter.view_Renderer = false;
            } else {
                Parameter.view_Renderer = true;
            }
        }

        //ゴールについたら消す → exitImageと重なったら削除
        Iterator<CPedestrian> cPedestrianIterator = m_pedestrian.iterator();
        while (cPedestrianIterator.hasNext()) {
            CPedestrian next = cPedestrianIterator.next();
            for (Sprite sprite : exit) {
                if (next.getSprite().getBoundingRectangle().overlaps(sprite.getBoundingRectangle())) {
                    GoalTime.add(step / 60);
                    cPedestrianIterator.remove();
                    System.out.println("[" + GoalTime.size() + "] " + "GoalTime = " + String.format("%.2f", step / 60));
                }
            }
        }
    }

    @Override
    public void dispose() {
        personImage.dispose();
        exitImage.dispose();
        batch.dispose();
    }

    public ArrayList<CPedestrian> getPedestrianinfo() {
        return m_pedestrian;
    }

    public ArrayList<CWall> getWallinfo() {
        return m_walledge;
    }

    public void update() {

        m_pedestrian.stream()
                .parallel()
                .forEach(j ->
                {
                    try {
                        j.call();
                    } catch (final Exception l_exception) {
                    }
                });
    }


    public int getDistance(float x1, float y1, float x2, float y2) {
        float distance = (float) Math.sqrt((x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1));
        return (int) distance;
    }
}




