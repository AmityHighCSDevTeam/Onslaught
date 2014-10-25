package io.github.AmityHighCSDevTeam.ZombieGame.client.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;

public class MainMenu implements Screen {

	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(1, 1, 1, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
	}
	@Override
	public void resize(int width, int height) {
	}
	@Override
	public void show() {
		//FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/myfont.ttf"));
		//FreeTypeFontParameter parameter = new FreeTypeFontParameter();
		//parameter.size = 12;
		//BitmapFont font12 = generator.generateFont(parameter); // font size 12 pixels
		//generator.dispose(); // don't forget to dispose to avoid memory leaks!
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
	}
}
