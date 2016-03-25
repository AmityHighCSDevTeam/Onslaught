package org.amityregion5.ZombieGame.client.screen;

import java.awt.geom.Rectangle2D;
import java.util.List;

import org.amityregion5.ZombieGame.ZombieGame;
import org.amityregion5.ZombieGame.client.gui.GuiRectangle;
import org.amityregion5.ZombieGame.client.music.MusicHandler;
import org.amityregion5.ZombieGame.common.game.Game;
import org.amityregion5.ZombieGame.common.game.GameRegistry;
import org.amityregion5.ZombieGame.common.game.difficulty.Difficulty;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.utils.Align;

/**
 * The class representing the new game menu
 * @author sergeys
 */
public class NewGameMenu extends GuiScreen {

	//The cached difficulties
	List<Difficulty> diffs = GameRegistry.difficulties;

	public NewGameMenu(GuiScreen prevScreen) {
		super(prevScreen);
		//Sort the cached difficulties
		diffs.sort((a, b) -> (int) ((a.getOverallMultiplier() - b.getOverallMultiplier()) * 10));
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
		ZombieGame.instance.bigFont.draw(batch, "New Game", 10, getHeight() - 45*ZombieGame.getYScalar(), getWidth() - 20, Align.center, false);
	}

	@Override
	public void resize(int width, int height) {
		super.resize(width, height);
	}

	@Override
	protected void setUpScreen() {
		super.setUpScreen();

		// Register buttons
		{
			int i = 0;
			for (Difficulty d : diffs) {
				final int iVal = i;
				addElement(new GuiRectangle(()->new Rectangle2D.Float(10, getHeight() - (150 + 60 * iVal)*ZombieGame.getYScalar(), getWidth() - 20, 50*ZombieGame.getYScalar()),
						d.getHumanName(), (r)->{
							ZombieGame.instance.setScreen(new InGameScreen(this,
									new Game(diffs.get(iVal), true,
											ZombieGame.instance.isCheatModeAllowed && Gdx.input.isKeyPressed(Keys.SHIFT_LEFT) && Gdx.input.isKeyPressed(Keys.ALT_LEFT)),
									true));
						}));
				i++;
			}
		}
		addElement(new GuiRectangle(()->new Rectangle2D.Float(10*ZombieGame.getXScalar(), 10*ZombieGame.getXScalar(), getWidth() - 20*ZombieGame.getXScalar(), 50*ZombieGame.getXScalar()),
				"Back", (r)->{
					ZombieGame.instance.setScreenAndDispose(prevScreen);
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
