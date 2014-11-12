package io.github.AmityHighCSDevTeam.ZombieGame.client.screen;

import io.github.AmityHighCSDevTeam.ZombieGame.ZombieGame;
import io.github.AmityHighCSDevTeam.ZombieGame.client.gui.GuiButton;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.BitmapFont.HAlignment;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;

public class PlayGameMenu extends GuiScreen {
	
	public PlayGameMenu(GuiScreen prevScreen) {
		super(prevScreen);
	}

	private BitmapFont calibri30;
	
	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor((50f / 255f), 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		super.render(delta);
	}
	
	@Override
	protected void drawScreen(float delta) {
		super.drawScreen(delta);
		
		calibri30.drawWrapped(batch, "Play Game", 10, getHeight() - 45, getWidth() - 20, HAlignment.CENTER);
	}
	
	@Override
	public void resize(int width, int height) {
		super.resize(width, height);
		
		if (!getButtons().isEmpty()) {
			getButtons().get(0).setXYWH(10,
					getHeight() - 150,
					getWidth() - 20, 50);
			getButtons().get(1).setXYWH(10,
					getHeight() - 210,
					getWidth() - 20, 50);
			getButtons().get(2).setXYWH(10,
					10,
					getWidth() - 20, 50);
		}
	}
	
	@Override
	public void show() {
		super.show();
		FreeTypeFontGenerator generator = new FreeTypeFontGenerator(
				Gdx.files.internal("font/Calibri.ttf"));
		
		FreeTypeFontParameter parameter = new FreeTypeFontParameter();
		parameter.size = 40;
		
		calibri30 = generator.generateFont(parameter);
		
		generator.dispose();
		
		calibri30.setColor(1, 1, 1, 1);
		
		addButton(new GuiButton(ZombieGame.instance.buttonTexture, 0, "Singleplayer", 10, getHeight() - 150, getWidth() - 20, 50));
		addButton(new GuiButton(ZombieGame.instance.buttonTexture, 1, "Multiplayer", 10, getHeight() - 210, getWidth() - 20, 50).setEnabled(false));
		addButton(new GuiButton(ZombieGame.instance.buttonTexture, 2, "Back", 10, 10, getWidth() - 20, 50));
	}
	
	@Override
	protected void buttonClicked(int id) {
		super.buttonClicked(id);
		switch (id) {
			case 2:
				ZombieGame.instance.setScreen(prevScreen);
				break;
		}
	}
	
	@Override
	public void hide() {
		super.hide();
	}
	
	@Override
	public void pause() {
		super.pause();
	}
	
	@Override
	public void resume() {
		super.resume();
	}
	
	@Override
	public void dispose() {
		super.dispose();
		batch.dispose();
		calibri30.dispose();
	}
}
