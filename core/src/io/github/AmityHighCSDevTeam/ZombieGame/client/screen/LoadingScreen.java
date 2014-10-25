package io.github.AmityHighCSDevTeam.ZombieGame.client.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.BitmapFont.HAlignment;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;

public class LoadingScreen implements Screen {
	
	SpriteBatch batch;
	BitmapFont calibri30;
	int dots = 0;
	float dCount = 0;

	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		batch.begin();
		
		calibri30.drawWrapped(batch, "Loading" + (dots > 0 ? "." : "") + (dots > 1 ? "." : "") + (dots > 2 ? "." : ""),
				10, 300, 780, HAlignment.CENTER);
		
		if (dCount > 0.5){
			dots++;
			dCount = 0f;
		}
		
		dCount += delta;
		
		if (dots > 3) {
			dots = 0;
		}
		
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
		
		calibri30 = generator.generateFont(parameter);
		
		generator.dispose();

		
		calibri30.setColor(1, 1, 1, 1);
		
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
		calibri30.dispose();
	}
}
