package org.amityregion5.onslaught.client.screen;

import java.awt.geom.Rectangle2D;

import org.amityregion5.onslaught.Onslaught;
import org.amityregion5.onslaught.client.gui.GuiRectangle;
import org.amityregion5.onslaught.client.music.MusicHandler;
import org.amityregion5.onslaught.common.game.tutorial.TutorialGame;

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
		Onslaught.instance.bigFont.draw(batch, "Play Game", 10, getHeight() - 45*Onslaught.getYScalar(), getWidth() - 20, Align.center, false);
		
		if (Onslaught.instance.settings.getInput("Close_Window").isJustDown()) {
			Onslaught.instance.setScreenAndDispose(prevScreen);
		}
	}

	@Override
	public void resize(int width, int height) {
		super.resize(width, height);
	}

	@Override
	protected void setUpScreen() {
		super.setUpScreen();

		// Register buttons
		addElement(new GuiRectangle(()->new Rectangle2D.Float(10, getHeight() - 150*Onslaught.getYScalar(), getWidth() - 20, 50*Onslaught.getYScalar()),
				"Singleplayer", (r)->{
					Onslaught.instance.setScreen(new SinglePlayerMenu(this));
				}));
		addElement(new GuiRectangle(()->new Rectangle2D.Float(10, getHeight() - 210*Onslaught.getYScalar(), getWidth() - 20, 50*Onslaught.getYScalar()),
				"Tutorial", (r)->{
					Onslaught.instance.setScreen(new InGameScreen(this, new TutorialGame(), true));
				}));
		addElement(new GuiRectangle(()->new Rectangle2D.Float(10*Onslaught.getXScalar(), 10*Onslaught.getXScalar(), getWidth() - 20*Onslaught.getXScalar(), 50*Onslaught.getXScalar()),
				"Back", (r)->{
					Onslaught.instance.setScreenAndDispose(prevScreen);
				}));
	}

	@Override
	public void show() {
		super.show();
		MusicHandler.setMusicPlaying(MusicHandler.menuMusic);
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
