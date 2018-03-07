package com.simulation.socialforce;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;


public class SocialForceModel extends ApplicationAdapter {
    private Texture personImage;
    private Texture wallImage;
    private SpriteBatch batch;
    private OrthographicCamera camera;
    private Rectangle person;
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

        walls = new Array<Sprite>();
        spawnWall();
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
                System.out.println("kita");
            }
            else {
                Sprite wall = new Sprite(wallImage);
                wall.setPosition(132, i);
                wall.setRotation(90);
                walls.add(wall);
                Sprite wall2 = new Sprite(wallImage);
                wall2.setPosition(745, i);
                wall2.setRotation(90);
                walls.add(wall2);
            }
        }

    }

    @Override
    public void render () {
        Gdx.gl.glClearColor(255, 255, 255, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camera.update();
        batch.setProjectionMatrix(camera.combined);

        batch.begin();
        batch.draw(personImage, person.x, person.y);
        for(Sprite wall: walls){
            wall.draw(batch);
        }
        batch.end();
    }

    @Override
    public void dispose () {
        personImage.dispose();
        batch.dispose();
    }
}
