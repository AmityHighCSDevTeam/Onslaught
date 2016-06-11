package org.amityregion5.ZombieGame.client.screen;

import java.awt.geom.Rectangle2D;

import org.amityregion5.ZombieGame.ZombieGame;
import org.amityregion5.ZombieGame.client.Client;
import org.amityregion5.ZombieGame.client.gui.GuiRectangle;
import org.amityregion5.ZombieGame.client.music.MusicHandler;
import org.amityregion5.ZombieGame.client.settings.Settings;

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
		ZombieGame.instance.bigFont.draw(batch, "Options", 10, getHeight() - 45, getWidth() - 20, Align.center, false);

		//Rectangle for options menu
		float x = 10*ZombieGame.getXScalar();
		float y = getHeight() - 210*ZombieGame.getYScalar();
		float w = getWidth() - 20*ZombieGame.getXScalar();
		float h = 50*ZombieGame.getYScalar();

		//Button texture
		Texture buttText = dot;

		{
			Color c = batch.getColor();
			batch.setColor(1, 1, 1, 1);
			//Draw volume text
			glyph.setText(ZombieGame.instance.mainFont, "Master Volume: " + ((int) (ZombieGame.instance.settings.getMasterVolume() * 10000)) / 100f + "%",
					Color.WHITE, w, Align.left, false);
			ZombieGame.instance.mainFont.draw(batch, glyph, x, y + (h + glyph.height) / 2);
			batch.setColor(c);

			//Displace x position
			x += xSplit*ZombieGame.getXScalar();
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
			x += xSplit*ZombieGame.getXScalar();
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
				if (Client.mouseJustReleased()) {
					ZombieGame.instance.resize(getWidth(), getHeight());
				}
			}
			
			y -= h + 10*ZombieGame.getYScalar();
		}	x = 10*ZombieGame.getXScalar();
		{
			Color c = batch.getColor();
			batch.setColor(1, 1, 1, 1);
			//Draw volume text
			glyph.setText(ZombieGame.instance.mainFont, "Ammo Circle Radius: " + ((int)(ZombieGame.instance.settings.getARadius()*100)/100f),
					Color.WHITE, w, Align.left, false);
			ZombieGame.instance.mainFont.draw(batch, glyph, x, y + (h + glyph.height) / 2);
			batch.setColor(c);

			//Displace x position
			x += xSplit*ZombieGame.getXScalar();
			w = getWidth() - x - 50*ZombieGame.getXScalar();

			batch.setColor(1, 1, 1, 1);
			//Change color if moused over
			if (mX >= x && mX <= x + w && mY >= y && mY <= y + h) {
				batch.setColor(new Color(27 / 255f, 168 / 255f, 55 / 255f, 1f));
			}
			//Draw the Slider using two buttons because i'm lazy to make a shape renderer
			batch.draw(buttText, x, y + h / 2 - 1, w, 2);
			batch.draw(buttText, x + (float) (w * ZombieGame.instance.settings.getARadius()/100) - 1, y, 2, h);
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
					ZombieGame.instance.settings.setARadius(rad);
					batch.end();
					Gdx.gl.glEnable(GL20.GL_BLEND);
					shape.begin(ShapeType.Filled);
					shape.setColor(1, 0, 0, (float)ZombieGame.instance.settings.getAAlpha());
					shape.circle(Gdx.input.getX(), getHeight() - Gdx.input.getY(), (float)ZombieGame.instance.settings.getARadius());
					shape.end();
					Gdx.gl.glDisable(GL20.GL_BLEND);
					batch.begin();
				}
			}
			
			y -= h + 10*ZombieGame.getYScalar();
		}	x = 10*ZombieGame.getXScalar();
		{
			Color c = batch.getColor();
			batch.setColor(1, 1, 1, 1);
			//Draw volume text
			glyph.setText(ZombieGame.instance.mainFont, "Ammo Circle Opacity: " + ((int)(ZombieGame.instance.settings.getAAlpha()*100)/100f),
					Color.WHITE, w, Align.left, false);
			ZombieGame.instance.mainFont.draw(batch, glyph, x, y + (h + glyph.height) / 2);
			batch.setColor(c);

			//Displace x position
			x += xSplit*ZombieGame.getXScalar();
			w = getWidth() - x - 50*ZombieGame.getXScalar();

			batch.setColor(1, 1, 1, 1);
			//Change color if moused over
			if (mX >= x && mX <= x + w && mY >= y && mY <= y + h) {
				batch.setColor(new Color(27 / 255f, 168 / 255f, 55 / 255f, 1f));
			}
			//Draw the Slider using two buttons because i'm lazy to make a shape renderer
			batch.draw(buttText, x, y + h / 2 - 1, w, 2);
			batch.draw(buttText, x + (float) (w * ZombieGame.instance.settings.getAAlpha()) - 1, y, 2, h);
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
					ZombieGame.instance.settings.setAAlpha(alpha);
					batch.end();
					Gdx.gl.glEnable(GL20.GL_BLEND);
					shape.begin(ShapeType.Filled);
					shape.setColor(1, 0, 0, (float)ZombieGame.instance.settings.getAAlpha());
					shape.circle(Gdx.input.getX(), getHeight() - Gdx.input.getY(), (float)ZombieGame.instance.settings.getARadius());
					shape.end();
					Gdx.gl.glDisable(GL20.GL_BLEND);
					batch.begin();
				}
			}
			
			y -= h + 10*ZombieGame.getYScalar();
		}	x = 10*ZombieGame.getXScalar();
	}

	@Override
	public void resize(int width, int height) {
		super.resize(width, height);
	}

	@Override
	protected void setUpScreen() {
		super.setUpScreen();

		// Register buttons
		addElement(new GuiRectangle(()->new Rectangle2D.Float(10, getHeight() - 150*ZombieGame.getYScalar(), getWidth() - 20, 50*ZombieGame.getYScalar()),
				"Controls", (r)->{
					ZombieGame.instance.setScreen(new ControlsMenu(this));
				}));
		addElement(new GuiRectangle(()->new Rectangle2D.Float(10, getHeight() - 450*ZombieGame.getYScalar(), getWidth()/2 - 20, 50*ZombieGame.getYScalar()), "Automatic Ammo Buying:", (r)->{}, Align.left, false));
		addElement(new GuiRectangle(()->new Rectangle2D.Float(10, getHeight() - 450*ZombieGame.getYScalar(), getWidth() - 20, 50*ZombieGame.getYScalar()),
				(ZombieGame.instance.settings.isAutoBuy() ? "Currently Enabled" : "Currently Disabled"), (r)->{
					ZombieGame.instance.settings.setAutoBuy(!ZombieGame.instance.settings.isAutoBuy());
					r.setText((ZombieGame.instance.settings.isAutoBuy() ? "Currently Enabled" : "Currently Disabled"));
				}));
		addElement(new GuiRectangle(()->new Rectangle2D.Float(10*ZombieGame.getXScalar(), 10*ZombieGame.getXScalar(), getWidth() - 20*ZombieGame.getXScalar(), 50*ZombieGame.getXScalar()),
				"Back", (r)->{
					Settings.save(ZombieGame.instance.settings);
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
		dot.dispose();
	}
}
