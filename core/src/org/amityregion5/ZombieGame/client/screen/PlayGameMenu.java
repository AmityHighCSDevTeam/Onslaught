package org.amityregion5.ZombieGame.client.screen;

import org.amityregion5.ZombieGame.ZombieGame;
import org.amityregion5.ZombieGame.client.gui.GuiButton;
import org.amityregion5.ZombieGame.common.game.tutorial.TutorialGame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.utils.Align;

/**
 * The screen representing the play game menu
 * @author sergeys
 */
public class PlayGameMenu extends GuiScreen {

	public PlayGameMenu(GuiScreen prevScreen) {
		super(prevScreen);
	}

	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(50f / 255f, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT); // Clear screen

		super.render(delta);
	}

	@Override
	protected void drawScreen(float delta) {
		super.drawScreen(delta);

		// Draw name of screen
		ZombieGame.instance.bigFont.draw(batch, "Play Game", 10, getHeight() - 45*ZombieGame.getYScalar(), getWidth() - 20, Align.center, false);
	}

	@Override
	public void resize(int width, int height) {
		super.resize(width, height);
	}

	@Override
	protected void setUpScreen() {
		super.setUpScreen();

		// Register buttons
		addButton(new GuiButton(ZombieGame.instance.buttonTexture, 0, "Singleplayer", 10, getHeight() - 150*ZombieGame.getYScalar(), getWidth() - 20, 50*ZombieGame.getYScalar()));
		addButton(new GuiButton(ZombieGame.instance.buttonTexture, 1, "Tutorial", 10, getHeight() - 210*ZombieGame.getYScalar(), getWidth() - 20, 50*ZombieGame.getYScalar()));
		addButton(new GuiButton(ZombieGame.instance.buttonTexture, 2, "Back", 10, 10*ZombieGame.getYScalar(), getWidth() - 20, 50*ZombieGame.getYScalar()));
	}

	@Override
	public void show() {
		super.show();
	}

	@Override
	protected void buttonClicked(int id) {
		super.buttonClicked(id);
		switch (id) {
			case 0:
				ZombieGame.instance.setScreen(new SinglePlayerMenu(this));
				break;
			case 1:
				ZombieGame.instance.setScreen(new InGameScreen(this, new TutorialGame(), true));
				break;
			case 2:
				// Back button
				dispose();
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
		batch.dispose(); // Clear memory
	}
}
