
package com.simulation.socialforce;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

import javax.vecmath.Vector2d;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Random;
import java.util.stream.IntStream;

public class SocialForceModel extends ApplicationAdapter {
    private Texture personImage;
    private Texture wallImage;
    private Texture exitImage;
    private SpriteBatch batch;
    private OrthographicCamera camera;
    private Array<Sprite> walls;
    private Sprite exit;

    private boolean FLAG = false;

    private Random rand = new Random();
    private static final double m_GaussianMean = 1.34;
    private static final double m_GaussianStandardDeviation = 0.26;
    private ArrayList<CPedestrian> m_pedestrian = new ArrayList<CPedestrian>();
    private ArrayList<CStatic> m_wall = new ArrayList<>( 2 );
    private ArrayList<CWall> m_walledge = new ArrayList<>( );
    public ArrayList<COutput> test = new ArrayList<>();

    @Override
    public void create () {
        personImage = new Texture(Gdx.files.internal("person.png"));
        wallImage = new Texture(Gdx.files.internal("black_texture.png"));
        exitImage = new Texture(Gdx.files.internal("exit.png"));

        camera = new OrthographicCamera();
        camera.setToOrtho(false, 800, 480);
        batch = new SpriteBatch();

        exit = new Sprite(exitImage);
        exit.setPosition(100, 480/2 - 32);

        walls = new Array<Sprite>();
        spawnWall();
    }

    private void spawnAgent(Vector3 pos){
        m_pedestrian.add( new CPedestrian( new Vector2d(pos.x-16, pos.y-16),
                1* m_GaussianStandardDeviation + m_GaussianMean, new CGoal( 0, 480/2+20, 0, 480/2+20).get_goals(), this, new Sprite(personImage)) );
    }

    private void spawnWall(){

        //描画用スプライト
        for(int i = 150; i < 750; i++){
            Sprite wall = new Sprite(wallImage);
            wall.setPosition(i, 30);
            walls.add(wall);
            Sprite wall2 = new Sprite(wallImage);
            wall2.setPosition(i, 450);
            walls.add(wall2);
        }

        for(int i = 30; i < 450; i++){
            if(!(i>200 && i<250)) {
                Sprite wall = new Sprite(wallImage);
                wall.setPosition(150, i);
                wall.setRotation(90);
                walls.add(wall);
            }
            Sprite wall2 = new Sprite(wallImage);
            wall2.setPosition(750, i);
            wall2.setRotation(90);
            walls.add(wall2);
        }

        // テスト障害物
        for(int i = 200; i < 250; i++){
            Sprite testwall = new Sprite(wallImage);
            testwall.setPosition(300,i);
            testwall.setRotation(90);
            walls.add(testwall);
        }

        //force
        //m_wall.add( new CStatic( 300,200,1,50) );
        m_wall.add( new CStatic( 150,30,1,150) ); // exit したライン
        m_wall.add( new CStatic( 150,230,1,450) ); // exit うえライン

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
        for(Sprite wall: walls) wall.draw(batch);
        //exit.draw(batch);
        batch.end();

        // クリックされたとき
        if(Gdx.input.justTouched()) {
            Vector3 touchPos = new Vector3();
            touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
            camera.unproject(touchPos);
            spawnAgent(touchPos);
        }

        // スペースを押されたとき
        if(Gdx.input.isKeyJustPressed(Input.Keys.SPACE)){
            if(FLAG) FLAG = false;
            else FLAG = true;
        }

        if(FLAG) {
          update();
        }
    }


    @Override
    public void dispose () {
        personImage.dispose();
        wallImage.dispose();
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
