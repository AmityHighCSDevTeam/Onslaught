package org.amityregion5.ZombieGame.client.screen;

import java.awt.geom.Rectangle2D;

import org.amityregion5.ZombieGame.ZombieGame;
import org.amityregion5.ZombieGame.client.gui.GuiRectangle;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.utils.Align;

/**
 * A screen representing the single player menu
 * @author sergeys
 */
public class SinglePlayerMenu extends GuiScreen {

	public SinglePlayerMenu(GuiScreen prevScreen) {
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
		ZombieGame.instance.bigFont.draw(batch, "Single Player", 10, getHeight() - 45*ZombieGame.getYScalar(), getWidth() - 20, Align.center, false);
	}

	@Override
	public void resize(int width, int height) {
		super.resize(width, height);
	}

	@Override
	protected void setUpScreen() {
		super.setUpScreen();

		// Register buttons
		addElement(new GuiRectangle(()->new Rectangle2D.Float(10*ZombieGame.getXScalar(), getHeight() - 150*ZombieGame.getYScalar(), getWidth() - 20*ZombieGame.getXScalar(), 50*ZombieGame.getYScalar()),
				"New Game", ()->{
					ZombieGame.instance.setScreen(new NewGameMenu(this));
				}));
		addElement(new GuiRectangle(()->new Rectangle2D.Float(10, getHeight() - 210*ZombieGame.getYScalar(), getWidth() - 20, 50*ZombieGame.getYScalar()),
				"Continue", ()->{
					ZombieGame.instance.setScreen(new ContinueMenu(this));
				}));
		addElement(new GuiRectangle(()->new Rectangle2D.Float(10*ZombieGame.getXScalar(), 10*ZombieGame.getXScalar(), getWidth() - 20*ZombieGame.getXScalar(), 50*ZombieGame.getXScalar()),
				"Back", ()->{
					ZombieGame.instance.setScreenAndDispose(prevScreen);
				}));
	}

	@Override
	public void show() {
		super.show();
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
	}
}
