
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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Random;

public class SocialForceModel extends ApplicationAdapter {
    private Texture personImage;
    private Texture wallImage;
    private Texture exitImage;
    private SpriteBatch batch;
    private OrthographicCamera camera;
    private Array<Sprite> walls;
    private Sprite exit;

    private SFVector sfvector;
    private SFAgent sfagent;
    private LinkedList<SFAgent> sfagents;
    private int id = 0;
    private int i = 0;

    private SFWall sfwall;
    private LinkedList<SFWall> sfwalls = new LinkedList<SFWall>();

    private boolean FLAG = false;

    private SFWaypoint sfwaypoint = new SFWaypoint("goal", 100, 480/2 - 32);
    private LinkedList<SFWaypoint> destination = new LinkedList<SFWaypoint>();

    //スピード　
    private SFVector max_vel = new SFVector(5,5);

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

        sfagents = new LinkedList<SFAgent>();
        SFWaypoint sfwaypoint = new SFWaypoint("goal", 125, 480/2 - 32);
        walls = new Array<Sprite>();
        spawnWall();
    }

    private void spawnAgent(Vector3 pos){
        ++i;
        sfagent = new SFAgent(id,1, new SFVector(pos.x-32/2,pos.y-32/2),destination,max_vel,1, new Sprite(personImage));
        sfagents.add(sfagent);
        destination.add(sfwaypoint);

    }

    private void moveAgent(){
        Iterator<SFAgent> iterator = sfagents.iterator();
        while(iterator.hasNext()){
            SFAgent sfagent = iterator.next();
            sfagent.move(sfagents, sfwalls);
            if(sfagent.getSprite().getBoundingRectangle().overlaps(exit.getBoundingRectangle())){
                iterator.remove();
            }
        }
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

        //SFWall

        sfwall = new SFWall(150,30,750,30); //したライン
        sfwalls.add(sfwall);
        sfwall = new SFWall(150,450,750,450); //うえライン
        sfwalls.add(sfwall);
        sfwall = new SFWall(150,30,150,200); // exit したライン
        sfwalls.add(sfwall);
        sfwall = new SFWall(150,250,150,450); // exit うえライン
        sfwalls.add(sfwall);
        sfwall = new SFWall(750,30,750,450); // みぎライン
        sfwalls.add(sfwall);

        /*
        sfwall = new SFWall(300,200,300,250);
        sfwalls.add(sfwall);
        sfwall = new SFWall(300,200,301,200);
        sfwalls.add(sfwall);
        sfwall = new SFWall(300,250,301,250);
        sfwalls.add(sfwall);
        sfwall = new SFWall(3001,200,301,250);
        sfwalls.add(sfwall);
        */
    }

    @Override
    public void render () {


        Gdx.gl.glClearColor(255, 255, 255, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camera.update();
        batch.setProjectionMatrix(camera.combined);

        //描画
        batch.begin();
        for(SFAgent sfagent: sfagents) sfagent.getSprite().draw(batch);
        for(Sprite wall: walls) wall.draw(batch);
        exit.draw(batch);
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
          moveAgent();
        }
    }


    @Override
    public void dispose () {
        personImage.dispose();
        wallImage.dispose();
        exitImage.dispose();
        batch.dispose();
    }
}