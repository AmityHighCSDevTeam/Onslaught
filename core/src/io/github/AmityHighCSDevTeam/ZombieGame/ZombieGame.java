package io.github.AmityHighCSDevTeam.ZombieGame;

import io.github.AmityHighCSDevTeam.ZombieGame.client.screen.LoadingScreen;
import io.github.AmityHighCSDevTeam.ZombieGame.client.screen.MainMenu;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;

public class ZombieGame extends Game {

	public static BitmapFont buttonFont;

	@Override
	public void create () {
		setScreen(new LoadingScreen());

		Gdx.app.postRunnable(new Runnable() {
			@Override
			public void run() {
				FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("font/Calibri.ttf"));

				FreeTypeFontParameter parameter = new FreeTypeFontParameter();
				parameter.size = 24;

				buttonFont = generator.generateFont(parameter);

				generator.dispose();

				buttonFont.setColor(0, 0, 0, 1);
				
				setScreen(new MainMenu());
			}
		});
	}
	@Override
	public void render () {
		super.render();
	}
	@Override
	public void dispose() {
		super.dispose();
		buttonFont.dispose();
	}
	@Override
	public void pause() {
		super.pause();
	}	
	@Override
	public void resize(int width, int height) {
		super.resize(width, height);
	}	
	@Override
	public void resume() {
		super.resume();
	}
}
