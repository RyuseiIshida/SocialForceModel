
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
import com.badlogic.gdx.utils.Array;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Random;

public class SocialForceModel extends ApplicationAdapter {
    private Texture personImage;
    private Texture wallImage;
    private Texture exitImage;
    private SpriteBatch batch;
    private OrthographicCamera camera;
    private Array<Sprite> agents;
    private Array<Sprite> walls;
    private Sprite exit;

    private SFVector sfvector;
    private SFWaypoint sfwaypoint;
    private SFAgent sfagent;
    private LinkedList<SFAgent> sfagents;
    private int id = 0;

    private boolean FLAG = false;

    @Override
    public void create () {
        personImage = new Texture(Gdx.files.internal("person.png"));
        wallImage = new Texture(Gdx.files.internal("black_texture.png"));
        exitImage = new Texture(Gdx.files.internal("exit.png"));

        camera = new OrthographicCamera();
        camera.setToOrtho(false, 800, 480);
        batch = new SpriteBatch();

        agents = new Array<Sprite>();
        sfagents = new LinkedList<SFAgent>();
        //spawnAgent();
        walls = new Array<Sprite>();
        spawnWall();
        exit = new Sprite(exitImage);
        exit.setPosition(125, 480/2 - 32);
    }

    private void spawnAgent(){
        /* ランダム生成
        Random rand = new Random();
        int randomNumber;
        for(int i = 0; i < 10; i++) {
            randomNumber = rand.nextInt(6) * 100;
            System.out.println(randomNumber);
            Sprite agent = new Sprite(personImage);
            agent.setPosition(randomNumber, randomNumber);
            agents.add(agent);
        }
        */
    }

    private void spawnWall(){
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
    }

    @Override
    public void render () {
        //シミュレーション開始判定
        if(Gdx.input.isKeyJustPressed(Input.Keys.SPACE)){
            if(FLAG) FLAG = false;
            else FLAG = true;
        }

        Gdx.gl.glClearColor(255, 255, 255, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camera.update();
        batch.setProjectionMatrix(camera.combined);

        batch.begin();
        //for(Sprite agent: agents) agent.draw(batch);
        for(SFAgent sfagent: sfagents) sfagent.getSprite().draw(batch);
        for(Sprite wall: walls) wall.draw(batch);
        exit.draw(batch);
        batch.end();

        if(Gdx.input.justTouched()) { // クリックでエージェント生成
            Vector3 touchPos = new Vector3();
            touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
            camera.unproject(touchPos);
            sfvector = new SFVector(touchPos.x - 32/2, touchPos.y - 32/2);
            sfwaypoint = new SFWaypoint("goal", 125, 480/2 - 32);
            LinkedList<SFWaypoint> destination = new LinkedList<SFWaypoint>();
            destination.add(sfwaypoint);
            sfagent = new SFAgent(id,1, sfvector,destination, sfvector,1, new Sprite(personImage));
            sfagent.printInfo();
            sfagents.add(sfagent);
            for(SFAgent sfagent: sfagents) System.out.println(sfagent.getPos());
            id++;

            //Sprite agent = new Sprite(personImage);
            //agent.setPosition(touchPos.x-32/2, touchPos.y-32/2);
            //agent.setPosition((float)(sfagent.getPos().x, (float)sfagent.getPos().y);
            /*
            //agentが置いてある場所には上書きして置く
            Rectangle rect = agent.getBoundingRectangle();
            Iterator<Sprite> iter = agents.iterator();
            while(iter.hasNext()){
                Sprite tmpsprite = iter.next();
                Rectangle tmprect = tmpsprite.getBoundingRectangle();
                if(tmprect.overlaps(rect)) iter.remove();
            }
            */
            //agents.add(agent);
        }

        // agentの移動ルール
        if(FLAG) {
            sfagent.move(sfagents);//agentと一致させなければならない
            /*
            for (Sprite agent : agents) {
                double speed = 1;
                float sx = agent.getX();
                float sy = agent.getY();
                int ex = 125, ey = 480 / 2 - 32;
                double angleRad = Math.atan2(ey - sy, ex - sx);
                sx += Math.cos(angleRad) * speed;
                sy += Math.sin(angleRad) * speed;
                agent.setPosition(sx, sy);
            }*/
        }

    }


    @Override
    public void dispose () {
        personImage.dispose();
        batch.dispose();
    }
}