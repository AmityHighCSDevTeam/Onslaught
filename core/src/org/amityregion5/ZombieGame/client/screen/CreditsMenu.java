package org.amityregion5.ZombieGame.client.screen;

import java.awt.geom.Rectangle2D;

import org.amityregion5.ZombieGame.ZombieGame;
import org.amityregion5.ZombieGame.client.gui.GuiRectangle;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.utils.Align;

/**
 * The credits menu
 * @author sergeys
 */
public class CreditsMenu extends GuiScreen {

	private GlyphLayout glyph = new GlyphLayout();

	public CreditsMenu(GuiScreen prevScreen) {
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
		ZombieGame.instance.bigFont.draw(batch, "Credits", 10, getHeight() - 45, getWidth() - 20, Align.center, false);

		float x = 50;
		float y = getHeight() - 150;
		float w = getWidth() - 100;
		float extraH = 20;

		for (String s : new String[] {"Lead Programmer: Sergey Savelyev", "Artist: Ray Tian", "Voice Actor: Ray Tian"}) {
			glyph.setText(ZombieGame.instance.mainFont, s, new Color(1, 1, 1, 1), w, Align.left, false);
			ZombieGame.instance.mainFont.draw(batch, glyph, x, y + glyph.height / 2);
			y -= glyph.height;
			y -= extraH;
		}
		// smallFont.draw(batch, "Lead Programmer: Sergey Saveleyev", x, y,
		// w, Align.right, false);
	}

	@Override
	public void resize(int width, int height) {
		super.resize(width, height);
	}

	@Override
	protected void setUpScreen() {
		super.setUpScreen();

		// Register buttons
		addElement(new GuiRectangle(()->
			new Rectangle2D.Float(10*ZombieGame.getXScalar(), 10*ZombieGame.getXScalar(), getWidth() - 20*ZombieGame.getXScalar(), 50*ZombieGame.getXScalar()),
				"Back", ()->{
					ZombieGame.instance.settings.save();
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
