package com.simulation.socialforce;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;


public class SocialForceModel extends ApplicationAdapter {
    private Texture personImage;
	private SpriteBatch batch;
	private OrthographicCamera camera;
	private Rectangle person;

	@Override
	public void create () {

		personImage = new Texture(Gdx.files.internal("person.png"));

		camera = new OrthographicCamera();
		camera.setToOrtho(false, 800, 480);
		batch = new SpriteBatch();

        person = new Rectangle();
        person.x = 400;
        person.y = 240;
        person.width = 64;
        person.height = 64;

	}

	@Override
	public void render () {
		Gdx.gl.glClearColor(255, 255, 255, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		camera.update();
		batch.setProjectionMatrix(camera.combined);

		batch.begin();
		    batch.draw(personImage, person.x, person.y);
		batch.end();
	}
	
	@Override
	public void dispose () {
	    personImage.dispose();
		batch.dispose();
	}
}
