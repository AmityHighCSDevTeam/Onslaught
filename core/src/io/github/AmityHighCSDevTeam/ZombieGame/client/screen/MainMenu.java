package io.github.AmityHighCSDevTeam.ZombieGame.client.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;

public class MainMenu implements Screen {
	
	SpriteBatch batch;
	BitmapFont arial;
	
	public MainMenu() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(1, 1, 1, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		batch.begin();
		
		arial.drawWrapped(batch, "This is the Arial font from a ttf file of it. Converted to font size 30.", 10, 590, 780);
		
		batch.end();
	}
	@Override
	public void resize(int width, int height) {
	}
	@Override
	public void show() {
		
		
		FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("font/Calibri.ttf"));
		
		FreeTypeFontParameter parameter = new FreeTypeFontParameter();
		parameter.size = 30;
		
		arial = generator.generateFont(parameter);
		
		generator.dispose();

		
		arial.setColor(0, 0, 0, 1);
		
		batch = new SpriteBatch();
	}
	@Override
	public void hide() {
	}
	@Override
	public void pause() {
	}
	@Override
	public void resume() {
	}
	@Override
	public void dispose() {
		batch.dispose();
		arial.dispose();
	}
}
