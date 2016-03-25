package org.amityregion5.ZombieGame.client.screen;

import java.awt.geom.Rectangle2D;

import org.amityregion5.ZombieGame.ZombieGame;
import org.amityregion5.ZombieGame.client.Client;
import org.amityregion5.ZombieGame.client.InputAccessor;
import org.amityregion5.ZombieGame.client.gui.GuiRectangle;
import org.amityregion5.ZombieGame.client.music.MusicHandler;
import org.amityregion5.ZombieGame.common.helper.MathHelper;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.utils.ScissorStack;
import com.badlogic.gdx.utils.Align;

/**
 * The credits menu
 * @author sergeys
 */
public class CreditsMenu extends GuiScreen {

	private GlyphLayout glyph = new GlyphLayout();
	private double			scrollPos, maxHeight; //The scroll position
	private InputProcessor	processor; //The input processor

	public CreditsMenu(GuiScreen prevScreen) {
		super(prevScreen);

		processor = new InputAccessor() {
			@Override
			public boolean scrolled(int amount) {
				//Scroll
				double maxAmt = getMaxScrollAmount() - getHeight() + 200*ZombieGame.getYScalar();
				scrollPos = MathHelper.clamp(0, (maxAmt > 0 ? maxAmt : 0), scrollPos + amount * 5);
				return true;
			}
		};

		Client.inputMultiplexer.addProcessor(processor);
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
		ZombieGame.instance.bigFont.draw(batch, "Credits", 10*ZombieGame.getXScalar(), getHeight() - 45*ZombieGame.getYScalar(), getWidth() - 20*ZombieGame.getXScalar(), Align.center, false);

		{
			float x = 50*ZombieGame.getXScalar();
			float y = (float) (getHeight() - 150*ZombieGame.getYScalar() + scrollPos);
			float w = getWidth() - 100*ZombieGame.getXScalar();
			float extraH = 20*ZombieGame.getYScalar();
			batch.flush();
			ScissorStack.pushScissors(new Rectangle(x, 100*ZombieGame.getYScalar(), w, getHeight() - 200*ZombieGame.getYScalar()));
			
			double maxHe = 0;

			for (String s : new String[] {"Lead Programmer: Sergey Savelyev", "Artist: Ray Tian", "Voice Actor: Ray Tian", 
					"\n",
					"\"Ossuary 6 - Air\" Kevin MacLeod (incompetech.com)\nLicensed under Creative Commons: By Attribution 3.0 License\nhttp://creativecommons.org/licenses/by/3.0/",
					"\"Gloom Horizon\" Kevin MacLeod (incompetech.com)\nLicensed under Creative Commons: By Attribution 3.0 License\nhttp://creativecommons.org/licenses/by/3.0/",
					"\"The Complex\" Kevin MacLeod (incompetech.com)\nLicensed under Creative Commons: By Attribution 3.0 License\nhttp://creativecommons.org/licenses/by/3.0/",
					"\n",
					"\"Escape\"",
					"\"Steamtech Mayhem\"",
					"by Eric Matyas",
					"www.soundimage.org"}) {
				glyph.setText(ZombieGame.instance.mainFont, s, new Color(1, 1, 1, 1), w, Align.left, false);
				ZombieGame.instance.mainFont.draw(batch, glyph, x, y + glyph.height / 2);
				maxHe += glyph.height + extraH;
				y -= glyph.height;
				y -= extraH;
			}
			batch.flush();
			ScissorStack.popScissors();
			
			maxHeight = maxHe;
		}


		{
			batch.end();
			float x = getWidth() - 30*ZombieGame.getXScalar();
			float y = 100*ZombieGame.getYScalar();
			float w = 20*ZombieGame.getXScalar();
			float h = getHeight() - 200*ZombieGame.getYScalar();

			Matrix4 projM = shape.getProjectionMatrix().cpy();

			shape.setProjectionMatrix(camera.combined);

			shape.begin(ShapeType.Filled);
			shape.setColor(0.4f, 0.4f, 0.4f, 1f);
			shape.rect(x, y, w, h);

			// Secondary Scroll Bar
			shape.setColor(0.7f, 0.7f, 0.7f, 1f);
			shape.rect(x, getScrollBarPos(), w, getScrollBarHeight());

			shape.end();
			shape.begin(ShapeType.Line);

			shape.setColor(0.9f, 0.9f, 0.9f, 0.5f);
			// Scroll bar box border left line
			shape.rect(x, y, w, h);

			shape.end();

			shape.setProjectionMatrix(projM);
			batch.begin();
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
		addElement(new GuiRectangle(()->
		new Rectangle2D.Float(10*ZombieGame.getXScalar(), 10*ZombieGame.getXScalar(), getWidth() - 20*ZombieGame.getXScalar(), 50*ZombieGame.getXScalar()),
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
		Client.inputMultiplexer.removeProcessor(processor);
	}

	private double getMaxScrollAmount() {
		return maxHeight;
	}

	/**
	 * Get the scroll bar position
	 * @return the scroll bar position
	 */
	private float getScrollBarPos() {
		double screenHeight = getHeight() - 200*ZombieGame.getYScalar();
		double pos = (screenHeight - getScrollBarHeight()) * (scrollPos) / (getMaxScrollAmount() - screenHeight);
		pos = (getHeight() - 100*ZombieGame.getYScalar()) - pos - getScrollBarHeight();
		return (float) pos;
	}

	/**
	 * Get the height of the scroll bar
	 * @return the height of the scroll bar
	 */
	private float getScrollBarHeight() {
		double screenHeight = getHeight() - 200*ZombieGame.getYScalar();
		double height = (screenHeight * screenHeight) / getMaxScrollAmount();
		return (float) (height > screenHeight ? screenHeight : height);
	}
}
