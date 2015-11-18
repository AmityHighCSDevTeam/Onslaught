package org.amityregion5.ZombieGame.client.screen;

import org.amityregion5.ZombieGame.ZombieGame;
import org.amityregion5.ZombieGame.client.gui.GuiButton;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.badlogic.gdx.utils.Align;

/**
 * @author sergeys
 */
public class OptionMenu extends GuiScreen {

	private GlyphLayout glyph = new GlyphLayout();

	public OptionMenu(GuiScreen prevScreen) {
		super(prevScreen);
	}

	// Font
	private BitmapFont calibri30;

	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(50f / 255f, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT); // Clear screen

		super.render(delta);
	}

	@Override
	protected void drawScreen(float delta) {
		super.drawScreen(delta);

		int mX = Gdx.input.getX();
		int mY = getHeight() - Gdx.input.getY();

		// Draw name of screen
		calibri30.draw(batch, "Options", 10, getHeight() - 45, getWidth() - 20, Align.center, false);

		float x = 10;
		float y = getHeight() - 210;
		float w = getWidth() - 20;
		float h = 50;

		Texture buttText = ZombieGame.instance.buttonTexture;

		Color c = batch.getColor();
		batch.setColor(1, 1, 1, 1);
		glyph.setText(ZombieGame.instance.mainFont, "Master Volume: " + ((int) (ZombieGame.instance.settings.getMasterVolume() * 10000)) / 100f + "%",
				Color.WHITE, w, Align.left, false);
		ZombieGame.instance.mainFont.draw(batch, glyph, x, y + (h + glyph.height) / 2);
		batch.setColor(c);

		x += 300 + 10;
		w = getWidth() - x - 50;

		batch.setColor(1, 1, 1, 1);
		if (mX >= x && mX <= x + w && mY >= y && mY <= y + h) {
			batch.setColor(new Color(27 / 255f, 168 / 255f, 55 / 255f, 1f));
		}
		batch.draw(buttText, x, y + h / 2 - 1, w, 2);
		batch.draw(buttText, x + (float) (w * ZombieGame.instance.settings.getMasterVolume()) - 1, y, 2, h);
		batch.setColor(c);

		if (Gdx.input.isTouched()) {
			if (mX >= x && mX <= x + w && mY >= y && mY <= y + h) {
				mX -= x;
				double maxVolume = mX / w;
				ZombieGame.instance.settings.setMasterVolume(maxVolume);
			}
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
		addButton(new GuiButton(ZombieGame.instance.buttonTexture, 0, "Controls", 10, getHeight() - 150, getWidth() - 20, 50));
		addButton(new GuiButton(ZombieGame.instance.buttonTexture, -1, "Back", 10, 10, getWidth() - 20, 50));
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
				ZombieGame.instance.settings.save();
				dispose();
				ZombieGame.instance.setScreen(prevScreen);
				break;
			case 0:
				// Controls button
				ZombieGame.instance.setScreen(new ControlsMenu(this));
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
		calibri30.dispose();
	}
}
