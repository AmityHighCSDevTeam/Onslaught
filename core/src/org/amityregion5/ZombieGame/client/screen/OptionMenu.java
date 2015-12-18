package org.amityregion5.ZombieGame.client.screen;

import org.amityregion5.ZombieGame.ZombieGame;
import org.amityregion5.ZombieGame.client.gui.GuiButton;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.utils.Align;

/**
 * The class representing the options menu
 * 
 * @author sergeys
 */
public class OptionMenu extends GuiScreen {

	private GlyphLayout glyph = new GlyphLayout();
	
	private boolean wasUIMouseDown = false;

	public OptionMenu(GuiScreen prevScreen) {
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

		//Mouse positions
		float mX = Gdx.input.getX();
		float mY = getHeight() - Gdx.input.getY();

		// Draw name of screen
		ZombieGame.instance.bigFont.draw(batch, "Options", 10, getHeight() - 45, getWidth() - 20, Align.center, false);

		//Rectangle for options menu
		float x = 10*ZombieGame.getXScalar();
		float y = getHeight() - 210*ZombieGame.getYScalar();
		float w = getWidth() - 20*ZombieGame.getXScalar();
		float h = 50*ZombieGame.getYScalar();

		//Button texture
		Texture buttText = ZombieGame.instance.buttonTexture;

		{
			Color c = batch.getColor();
			batch.setColor(1, 1, 1, 1);
			//Draw volume text
			glyph.setText(ZombieGame.instance.mainFont, "Master Volume: " + ((int) (ZombieGame.instance.settings.getMasterVolume() * 10000)) / 100f + "%",
					Color.WHITE, w, Align.left, false);
			ZombieGame.instance.mainFont.draw(batch, glyph, x, y + (h + glyph.height) / 2);
			batch.setColor(c);

			//Displace x position
			x += 310*ZombieGame.getXScalar();
			w = getWidth() - x - 50*ZombieGame.getXScalar();

			batch.setColor(1, 1, 1, 1);
			//Change color if moused over
			if (mX >= x && mX <= x + w && mY >= y && mY <= y + h) {
				batch.setColor(new Color(27 / 255f, 168 / 255f, 55 / 255f, 1f));
			}
			//Draw the Slider using two buttons because i'm lazy to make a shape renderer
			batch.draw(buttText, x, y + h / 2 - 1, w, 2);
			batch.draw(buttText, x + (float) (w * ZombieGame.instance.settings.getMasterVolume()) - 1, y, 2, h);
			batch.setColor(c);

			//If the mouse is down set the master volume
			if (Gdx.input.isTouched()) {
				if (mY >= y && mY <= y + h) {
					mX -= x;
					if (mX > w) {
						mX = w;
					} else if (mX < 0) {
						mX = 0;
					}
					double maxVolume = mX / w;
					mX = Gdx.input.getX();
					ZombieGame.instance.settings.setMasterVolume(maxVolume);
				}
			}
			
			y -= h + 10*ZombieGame.getYScalar();
		}	x = 10*ZombieGame.getXScalar();
		{
			Color c = batch.getColor();
			batch.setColor(1, 1, 1, 1);
			//Draw volume text
			glyph.setText(ZombieGame.instance.mainFont, "UI Scale: " + ((int) (ZombieGame.instance.settings.getUiScale() * 100)) / 100f,
					Color.WHITE, w, Align.left, false);
			ZombieGame.instance.mainFont.draw(batch, glyph, x, y + (h + glyph.height) / 2);
			batch.setColor(c);

			//Displace x position
			x += 310*ZombieGame.getXScalar();
			w = getWidth() - x - 50*ZombieGame.getXScalar();

			batch.setColor(1, 1, 1, 1);
			//Change color if moused over
			if (mX >= x && mX <= x + w && mY >= y && mY <= y + h) {
				batch.setColor(new Color(27 / 255f, 168 / 255f, 55 / 255f, 1f));
			}
			
			double deviation = 0.5;
			
			//Draw the Slider using two buttons because i'm lazy to make a shape renderer
			batch.draw(buttText, x, y + h / 2 - 1, w, 2);
			batch.draw(buttText, x + (float) (w * (ZombieGame.instance.settings.getUiScale()-1+deviation)/deviation/2) - 1, y, 2, h);
			batch.setColor(c);

			//If the mouse is down set the master volume
			if (Gdx.input.isTouched()) {
				wasUIMouseDown = true;
				if (mY >= y && mY <= y + h) {
					mX -= x;
					if (mX > w) {
						mX = w;
					} else if (mX < 0) {
						mX = 0;
					}
					double uiScale = (mX / w) * deviation*2 + 1 - deviation;
					mX = Gdx.input.getX();
					ZombieGame.instance.settings.setUiScale(uiScale);
				}
			} else {
				if (wasUIMouseDown) {
					ZombieGame.instance.resize(getWidth(), getHeight());
				}
				wasUIMouseDown = false;
			}
			
			y -= h + 10*ZombieGame.getYScalar();
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
		addButton(new GuiButton(ZombieGame.instance.buttonTexture, 0, "Controls", 10, getHeight() - 150*ZombieGame.getYScalar(), getWidth() - 20, 50*ZombieGame.getYScalar()));
		addButton(new GuiButton(ZombieGame.instance.buttonTexture, -1, "Back", 10, 10*ZombieGame.getYScalar(), getWidth() - 20, 50*ZombieGame.getYScalar()));
	}

	@Override
	public void show() {
		super.show();
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
	}
}
