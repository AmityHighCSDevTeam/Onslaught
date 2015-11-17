package org.amityregion5.ZombieGame.client.screen;

import java.util.List;

import org.amityregion5.ZombieGame.ZombieGame;
import org.amityregion5.ZombieGame.client.gui.GuiButton;
import org.amityregion5.ZombieGame.common.game.Difficulty;
import org.amityregion5.ZombieGame.common.game.Game;
import org.amityregion5.ZombieGame.common.game.GameRegistry;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.badlogic.gdx.utils.Align;

/**
 *
 * @author sergeys
 *
 */
public class NewGameMenu extends GuiScreen {
	
	List<Difficulty> diffs = GameRegistry.difficulties;	

	public NewGameMenu(GuiScreen prevScreen) {
		super(prevScreen);
		diffs.sort((a,b)->(int)((a.getOverallMultiplier()-b.getOverallMultiplier())*10));
	}

	// Font
	private BitmapFont	calibri30;

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
		calibri30.draw(batch, "New Game", 10, getHeight() - 45,
				getWidth() - 20, Align.center, false);
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
				addButton(new GuiButton(ZombieGame.instance.buttonTexture, i,
						d.getHumanName(), 10, getHeight() - 150 - 60 * i,
						getWidth() - 20, 50));
				i++;
			}
		}
		addButton(new GuiButton(ZombieGame.instance.buttonTexture, -1, "Back",
				10, 10, getWidth() - 20, 50));
	}

	@Override
	public void show() {
		super.show();

		// Create the font
		FreeTypeFontGenerator generator = ZombieGame.instance.fontGenerator;

		FreeTypeFontParameter parameter = new FreeTypeFontParameter();
		parameter.size = 36;

		calibri30 = generator.generateFont(parameter);

		calibri30.setColor(1, 1, 1, 1);

	}

	@Override
	protected void buttonClicked(int id) {
		super.buttonClicked(id);
		switch (id) {
			case -1:
				// Back button
				dispose();
				ZombieGame.instance.setScreen(prevScreen);
				break;
			default:
				ZombieGame.instance
						.setScreen(new InGameScreen(this, new Game(diffs.get(id), true, ZombieGame.instance.isCheatModeAllowed &&
								Gdx.input.isKeyPressed(Keys.SHIFT_LEFT) && Gdx.input.isKeyPressed(Keys.ALT_LEFT)), true));
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
		calibri30.dispose();
	}
}
