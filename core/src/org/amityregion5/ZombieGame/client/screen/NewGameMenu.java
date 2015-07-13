package org.amityregion5.ZombieGame.client.screen;

import org.amityregion5.ZombieGame.ZombieGame;
import org.amityregion5.ZombieGame.client.gui.GuiButton;
import org.amityregion5.ZombieGame.common.game.Difficulty;
import org.amityregion5.ZombieGame.common.game.Game;
import com.badlogic.gdx.Gdx;
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

	public NewGameMenu(GuiScreen prevScreen) {
		super(prevScreen);
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
			for (Difficulty d : Difficulty.getSortedArray()) {
				addButton(new GuiButton(ZombieGame.instance.buttonTexture, i,
						d.getLocName(), 10, getHeight() - 150 - 60 * i,
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
		FreeTypeFontGenerator generator = new FreeTypeFontGenerator(
				Gdx.files.internal("font/Calibri.ttf"));

		FreeTypeFontParameter parameter = new FreeTypeFontParameter();
		parameter.size = 40;

		calibri30 = generator.generateFont(parameter);

		generator.dispose();

		calibri30.setColor(1, 1, 1, 1);

	}

	@Override
	protected void buttonClicked(int id) {
		super.buttonClicked(id);
		switch (id) {
			case -1:
				// Back button
				ZombieGame.instance.setScreen(prevScreen);
				break;
			default:
				ZombieGame.instance
						.setScreen(new InGameScreen(this, new Game()));
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
