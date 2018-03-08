package com.simulation.socialforce;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;

import java.util.Random;


public class SocialForceModel extends ApplicationAdapter {
    private Texture personImage;
    private Texture wallImage;
    private SpriteBatch batch;
    private OrthographicCamera camera;
    private Rectangle person;
    private Array<Sprite> agents;
    private Array<Sprite> walls;

    @Override
    public void create () {
        personImage = new Texture(Gdx.files.internal("person.png"));
        wallImage = new Texture(Gdx.files.internal("wall.png"));

        camera = new OrthographicCamera();
        camera.setToOrtho(false, 800, 480);
        batch = new SpriteBatch();

        person = new Rectangle();
        person.x = 800/2 - 32/2;
        person.y = 480/2 - 32/2;
        person.width = 32;
        person.height = 32;

        agents = new Array<Sprite>();
        spawnAgent();
        walls = new Array<Sprite>();
        spawnWall();
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
        for(int i = 150; i < 750; i += 32){
            Sprite wall = new Sprite(wallImage);
            wall.setPosition(i, 12);
            walls.add(wall);
            Sprite wall2 = new Sprite(wallImage);
            wall2.setPosition(i, 454);
            walls.add(wall2);
        }

        for(int i = 25; i < 450; i += 32){
            if(i>215 && i<248){
            }
            else {
                Sprite wall = new Sprite(wallImage);
                wall.setPosition(132, i);
                wall.setRotation(90);
                walls.add(wall);
            }
        }

        for(int i = 25; i < 450; i += 32){
            Sprite wall2 = new Sprite(wallImage);
            wall2.setPosition(745, i);
            wall2.setRotation(90);
            walls.add(wall2);
        }

    }

    @Override
    public void render () {
        Gdx.gl.glClearColor(255, 255, 255, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camera.update();
        batch.setProjectionMatrix(camera.combined);

        batch.begin();
        for(Sprite agent: agents){ agent.draw(batch); }
        for(Sprite wall: walls){ wall.draw(batch); }
        batch.end();

        if(Gdx.input.isTouched()) { // クリックでエージェント生成
            System.out.println("touched");
            Vector3 touchPos = new Vector3();
            touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
            camera.unproject(touchPos);
            Sprite agent = new Sprite(personImage);
            agent.setPosition(touchPos.x-32/2, touchPos.y-32/2);
            agents.add(agent);
        }
    }


    @Override
    public void dispose () {
        personImage.dispose();
        batch.dispose();
    }
}
