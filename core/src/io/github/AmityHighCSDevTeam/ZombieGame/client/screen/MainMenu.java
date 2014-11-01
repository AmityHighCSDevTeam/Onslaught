package io.github.AmityHighCSDevTeam.ZombieGame.client.screen;

import io.github.AmityHighCSDevTeam.ZombieGame.client.gui.GuiButton;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;

public class MainMenu extends GuiScreen {
	
	BitmapFont calibri30;
	Texture buttonTexture, titleTexture;
	int titleHeight;
	
	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor((50f / 255f), 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		super.render(delta);
	}
	
	@Override
	protected void drawScreen(float delta) {
		super.drawScreen(delta);
		
		calibri30.draw(batch, "FPS: " + Gdx.graphics.getFramesPerSecond(), 10, 33);
		batch.draw(titleTexture, 10, (getHeight() - titleHeight) - 10,
				getWidth() - 20, titleHeight);
	}
	
	@Override
	public void resize(int width, int height) {
		super.resize(width, height);
		titleHeight = (int) Math.round((double) titleTexture.getHeight()
				/ (double) titleTexture.getWidth() * (getWidth() - 20));
		
		if (!getButtons().isEmpty()) {
			getButtons().get(0).setXYWH(10,
					getHeight() - titleHeight - 10 - 50, getWidth() - 20, 50);
			getButtons().get(1).setXYWH(10,
					getHeight() - titleHeight - 10 - 50 - 60, getWidth() - 20,
					50);
			getButtons().get(2).setXYWH(10,
					getHeight() - titleHeight - 10 - 50 - 60 - 60,
					getWidth() - 20, 50);
			getButtons().get(3).setXYWH(10,
					getHeight() - titleHeight - 10 - 50 - 60 - 60 - 60,
					getWidth() - 20, 50);
		}
	}
	
	@Override
	public void show() {
		super.show();
		FreeTypeFontGenerator generator = new FreeTypeFontGenerator(
				Gdx.files.internal("font/Calibri.ttf"));
		
		FreeTypeFontParameter parameter = new FreeTypeFontParameter();
		parameter.size = 30;
		
		calibri30 = generator.generateFont(parameter);
		
		generator.dispose();
		
		calibri30.setColor(1, 1, 1, 1);
		
		buttonTexture = new Texture(Gdx.files.internal("images/button.png"));
		titleTexture = new Texture(
				Gdx.files.internal("images/ZombieGameTitle.png"));
		
		addButton(new GuiButton(buttonTexture, 0, "Play Game", 10, getHeight()
				- titleHeight - 10 - 50, getWidth() - 20, 50));
		addButton(new GuiButton(buttonTexture, 1, "Options", 10, getHeight()
				- titleHeight - 10 - 50 - 60, getWidth() - 20, 50));
		addButton(new GuiButton(buttonTexture, 2, "Credits", 10, getHeight()
				- titleHeight - 10 - 50 - 60 - 60, getWidth() - 20, 50));
		addButton(new GuiButton(buttonTexture, 3, "Quit", 10, getHeight()
				- titleHeight - 10 - 50 - 60 - 60 - 60, getWidth() - 20, 50));
	}
	
	@Override
	protected void buttonClicked(int id) {
		super.buttonClicked(id);
		switch (id) {
			case 3:
				Gdx.app.exit();
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
		buttonTexture.dispose();
		titleTexture.dispose();
	}
}
