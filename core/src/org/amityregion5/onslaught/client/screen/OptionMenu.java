package org.amityregion5.onslaught.client.screen;

import java.awt.geom.Rectangle2D;

import org.amityregion5.onslaught.Onslaught;
import org.amityregion5.onslaught.client.Client;
import org.amityregion5.onslaught.client.gui.GuiRectangle;
import org.amityregion5.onslaught.client.music.MusicHandler;
import org.amityregion5.onslaught.client.settings.Settings;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.utils.Align;

/**
 * The class representing the options menu
 * 
 * @author sergeys
 */
public class OptionMenu extends GuiScreen {

	private GlyphLayout glyph = new GlyphLayout();
	
	private Texture dot;
	
	private float xSplit = 400;

	public OptionMenu(GuiScreen prevScreen) {
		super(prevScreen);
		Pixmap map = new Pixmap(1,1,Format.RGBA8888);
		map.drawPixel(0, 0, 0xA9A9A9FF);//RGBA
		dot = new Texture(map);
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
		Onslaught.instance.bigFont.draw(batch, "Options", 10, getHeight() - 45, getWidth() - 20, Align.center, false);

		//Rectangle for options menu
		float x = 10*Onslaught.getXScalar();
		float y = getHeight() - 210*Onslaught.getYScalar();
		float w = getWidth() - 20*Onslaught.getXScalar();
		float h = 50*Onslaught.getYScalar();

		//Button texture
		Texture buttText = dot;

		{
			Color c = batch.getColor();
			batch.setColor(1, 1, 1, 1);
			//Draw volume text
			glyph.setText(Onslaught.instance.mainFont, "Master Volume: " + ((int) (Onslaught.instance.settings.getMasterVolume() * 10000)) / 100f + "%",
					Color.WHITE, w, Align.left, false);
			Onslaught.instance.mainFont.draw(batch, glyph, x, y + (h + glyph.height) / 2);
			batch.setColor(c);

			//Displace x position
			x += xSplit*Onslaught.getXScalar();
			w = getWidth() - x - 50*Onslaught.getXScalar();

			batch.setColor(1, 1, 1, 1);
			//Change color if moused over
			if (mX >= x && mX <= x + w && mY >= y && mY <= y + h) {
				batch.setColor(new Color(27 / 255f, 168 / 255f, 55 / 255f, 1f));
			}
			//Draw the Slider using two buttons because i'm lazy to make a shape renderer
			batch.draw(buttText, x, y + h / 2 - 1, w, 2);
			batch.draw(buttText, x + (float) (w * Onslaught.instance.settings.getMasterVolume()) - 1, y, 2, h);
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
					Onslaught.instance.settings.setMasterVolume(maxVolume);
				}
			}
			
			y -= h + 10*Onslaught.getYScalar();
		}	x = 10*Onslaught.getXScalar();
		{
			Color c = batch.getColor();
			batch.setColor(1, 1, 1, 1);
			//Draw volume text
			glyph.setText(Onslaught.instance.mainFont, "UI Scale: " + ((int) (Onslaught.instance.settings.getUiScale() * 100)) / 100f,
					Color.WHITE, w, Align.left, false);
			Onslaught.instance.mainFont.draw(batch, glyph, x, y + (h + glyph.height) / 2);
			batch.setColor(c);

			//Displace x position
			x += xSplit*Onslaught.getXScalar();
			w = getWidth() - x - 50*Onslaught.getXScalar();

			batch.setColor(1, 1, 1, 1);
			//Change color if moused over
			if (mX >= x && mX <= x + w && mY >= y && mY <= y + h) {
				batch.setColor(new Color(27 / 255f, 168 / 255f, 55 / 255f, 1f));
			}
			
			double deviation = 0.5;
			
			//Draw the Slider using two buttons because i'm lazy to make a shape renderer
			batch.draw(buttText, x, y + h / 2 - 1, w, 2);
			batch.draw(buttText, x + (float) (w * (Onslaught.instance.settings.getUiScale()-1+deviation)/deviation/2) - 1, y, 2, h);
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
					double uiScale = (mX / w) * deviation*2 + 1 - deviation;
					mX = Gdx.input.getX();
					Onslaught.instance.settings.setUiScale(uiScale);
				}
			} else {
				if (Client.mouseJustReleased()) {
					Onslaught.instance.resize(getWidth(), getHeight());
				}
			}
			
			y -= h + 10*Onslaught.getYScalar();
		}	x = 10*Onslaught.getXScalar();
		{
			Color c = batch.getColor();
			batch.setColor(1, 1, 1, 1);
			//Draw volume text
			glyph.setText(Onslaught.instance.mainFont, "Ammo Circle Radius: " + ((int)(Onslaught.instance.settings.getARadius()*100)/100f),
					Color.WHITE, w, Align.left, false);
			Onslaught.instance.mainFont.draw(batch, glyph, x, y + (h + glyph.height) / 2);
			batch.setColor(c);

			//Displace x position
			x += xSplit*Onslaught.getXScalar();
			w = getWidth() - x - 50*Onslaught.getXScalar();

			batch.setColor(1, 1, 1, 1);
			//Change color if moused over
			if (mX >= x && mX <= x + w && mY >= y && mY <= y + h) {
				batch.setColor(new Color(27 / 255f, 168 / 255f, 55 / 255f, 1f));
			}
			//Draw the Slider using two buttons because i'm lazy to make a shape renderer
			batch.draw(buttText, x, y + h / 2 - 1, w, 2);
			batch.draw(buttText, x + (float) (w * Onslaught.instance.settings.getARadius()/100) - 1, y, 2, h);
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
					double rad = mX / w * 100;
					mX = Gdx.input.getX();
					Onslaught.instance.settings.setARadius(rad);
					batch.end();
					Gdx.gl.glEnable(GL20.GL_BLEND);
					shape.begin(ShapeType.Filled);
					shape.setColor(1, 0, 0, (float)Onslaught.instance.settings.getAAlpha());
					shape.circle(Gdx.input.getX(), getHeight() - Gdx.input.getY(), (float)Onslaught.instance.settings.getARadius());
					shape.end();
					Gdx.gl.glDisable(GL20.GL_BLEND);
					batch.begin();
				}
			}
			
			y -= h + 10*Onslaught.getYScalar();
		}	x = 10*Onslaught.getXScalar();
		{
			Color c = batch.getColor();
			batch.setColor(1, 1, 1, 1);
			//Draw volume text
			glyph.setText(Onslaught.instance.mainFont, "Ammo Circle Opacity: " + ((int)(Onslaught.instance.settings.getAAlpha()*100)/100f),
					Color.WHITE, w, Align.left, false);
			Onslaught.instance.mainFont.draw(batch, glyph, x, y + (h + glyph.height) / 2);
			batch.setColor(c);

			//Displace x position
			x += xSplit*Onslaught.getXScalar();
			w = getWidth() - x - 50*Onslaught.getXScalar();

			batch.setColor(1, 1, 1, 1);
			//Change color if moused over
			if (mX >= x && mX <= x + w && mY >= y && mY <= y + h) {
				batch.setColor(new Color(27 / 255f, 168 / 255f, 55 / 255f, 1f));
			}
			//Draw the Slider using two buttons because i'm lazy to make a shape renderer
			batch.draw(buttText, x, y + h / 2 - 1, w, 2);
			batch.draw(buttText, x + (float) (w * Onslaught.instance.settings.getAAlpha()) - 1, y, 2, h);
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
					double alpha = mX / w;
					mX = Gdx.input.getX();
					Onslaught.instance.settings.setAAlpha(alpha);
					batch.end();
					Gdx.gl.glEnable(GL20.GL_BLEND);
					shape.begin(ShapeType.Filled);
					shape.setColor(1, 0, 0, (float)Onslaught.instance.settings.getAAlpha());
					shape.circle(Gdx.input.getX(), getHeight() - Gdx.input.getY(), (float)Onslaught.instance.settings.getARadius());
					shape.end();
					Gdx.gl.glDisable(GL20.GL_BLEND);
					batch.begin();
				}
			}
			
			y -= h + 10*Onslaught.getYScalar();
		}	x = 10*Onslaught.getXScalar();
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
				"Controls", (r)->{
					Onslaught.instance.setScreen(new ControlsMenu(this));
				}));
		addElement(new GuiRectangle(()->new Rectangle2D.Float(10, getHeight() - 450*Onslaught.getYScalar(), getWidth()/2 - 20, 50*Onslaught.getYScalar()), "Automatic Ammo Buying:", (r)->{}, Align.left, false));
		addElement(new GuiRectangle(()->new Rectangle2D.Float(10, getHeight() - 450*Onslaught.getYScalar(), getWidth() - 20, 50*Onslaught.getYScalar()),
				(Onslaught.instance.settings.isAutoBuy() ? "Currently Enabled" : "Currently Disabled"), (r)->{
					Onslaught.instance.settings.setAutoBuy(!Onslaught.instance.settings.isAutoBuy());
					r.setText((Onslaught.instance.settings.isAutoBuy() ? "Currently Enabled" : "Currently Disabled"));
				}));
		addElement(new GuiRectangle(()->new Rectangle2D.Float(10*Onslaught.getXScalar(), 10*Onslaught.getXScalar(), getWidth() - 20*Onslaught.getXScalar(), 50*Onslaught.getXScalar()),
				"Back", (r)->{
					Settings.save(Onslaught.instance.settings);
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
		dot.dispose();
	}
}
