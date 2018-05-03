
package com.simulation.socialforce;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector3;

import javax.vecmath.Vector2d;
import java.util.ArrayList;
import java.util.Iterator;

import static java.lang.Math.pow;

public class SocialForceModel extends ApplicationAdapter {
    private Texture personImage;
    private Texture exitImage;
    private SpriteBatch batch;
    private ShapeRenderer shapeRenderer;
    private OrthographicCamera camera;
    private Sprite exit;
    private static final double m_GaussianMean = 1.34;
    private static final double m_GaussianStandardDeviation = 0.26;
    private ArrayList<CPedestrian> m_pedestrian = new ArrayList<>();
    private ArrayList<CStatic> m_wall = new ArrayList<>( 2 );
    private ArrayList<CWall> m_walledge = new ArrayList<>( );
    public ArrayList<COutput> test = new ArrayList<>();
    private boolean isSpaceButton = false;

    private final CStatic  wallDownLine     = new CStatic(150,30,750,30);
    private final CStatic  wallUpLine       = new CStatic(150,450,750,450);
    private final CStatic  wallRightLine    = new CStatic(750,30,750,450);
    private final CStatic  wallexitDownLine = new CStatic(150,30,150,200);
    private final CStatic  wallexitUpLine   = new CStatic(150,250,150,450);


    @Override
    public void create () {
        personImage = new Texture(Gdx.files.internal("person.png"));
        exitImage = new Texture(Gdx.files.internal("exit.png"));
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 800, 480);
        batch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();
        spawnWall();
    }

    private void spawnAgent(Vector3 pos){
        m_pedestrian.add( new CPedestrian( new Vector2d(pos.x, pos.y),
                1, new CGoal( -10, 230, 0, 240).get_goals(), this, new Sprite(personImage)) );
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
        //exit.draw(batch);
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

        //agentの向きライン描画
        shapeRenderer.setColor(((float) 0.9), ((float) 0), ((float) 0),1);
        for(CPedestrian agent: m_pedestrian){
            float delta_x =  (float)agent.getGoalposition().getX() - (float)agent.getPosition().getX();
            float delta_y = (float)agent.getGoalposition().getY() - (float)agent.getPosition().getY();
            float length = (float)Math.sqrt(pow(delta_x,2) + pow(delta_y,2));
            delta_x = delta_x/length * 15 + (float)agent.getPosition().getX(); //15はエージェントの体分
            delta_y = delta_y/length * 15 + (float)agent.getPosition().getY();
            shapeRenderer.line(((float) agent.getPosition().x),(float)agent.getPosition().y,
                    delta_x,delta_y);

        }
        shapeRenderer.end();

        // クリックされたとき
        if(Gdx.input.justTouched()) {
            Vector3 touchPos = new Vector3();
            touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
            camera.unproject(touchPos);
            System.out.println(touchPos);
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
