package org.amityregion5.ZombieGame.client.screen;

import java.awt.geom.Rectangle2D;
import java.util.List;
import java.util.Map.Entry;

import org.amityregion5.ZombieGame.ZombieGame;
import org.amityregion5.ZombieGame.client.Client;
import org.amityregion5.ZombieGame.client.InputAccessor;
import org.amityregion5.ZombieGame.client.gui.GuiRectangle;
import org.amityregion5.ZombieGame.client.music.MusicHandler;
import org.amityregion5.ZombieGame.common.game.GameLoadedContainer;
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
 * The controls menu
 * @author sergeys
 */
public class FailedToLoadScreen extends GuiScreen {

	private GlyphLayout		glyph		= new GlyphLayout(); //The glyph layout
	private double			scrollPos; //The scroll position
	private InputProcessor	processor; //The input processor
	private GameLoadedContainer glc;

	public FailedToLoadScreen(GuiScreen prevScreen, GameLoadedContainer glc) {
		super(prevScreen);
		
		this.glc = glc;

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

		batch.end();
		{
			//Clip screen
			Rectangle clipBounds = new Rectangle(10*ZombieGame.getXScalar(), 100*ZombieGame.getYScalar(), getWidth() - 20*ZombieGame.getXScalar(), getHeight() - 201*ZombieGame.getYScalar());
			ScissorStack.pushScissors(clipBounds);
			batch.begin();

			//Setup rectangle
			float h = 50*ZombieGame.getYScalar();
			float blankSpace = 51*ZombieGame.getYScalar();
			float x = 10*ZombieGame.getXScalar();
			float y = (float) ((getHeight() - 50*ZombieGame.getYScalar()) - h - blankSpace + scrollPos);
			float w = (getWidth() - 40*ZombieGame.getXScalar()) - 100*ZombieGame.getXScalar();

			for (Entry<String, List<String>> entry : glc.errors.entrySet()) {
				glyph.setText(ZombieGame.instance.mainFont, entry.getKey(), Color.WHITE, w - 20, Align.left, true);
				ZombieGame.instance.mainFont.draw(batch, glyph, x, y + (h + glyph.height) / 2); y -= (glyph.height + 4);
				for (String str : entry.getValue()) {
					glyph.setText(ZombieGame.instance.mainFont, "    " + str, Color.WHITE, w - 20, Align.left, true);
					ZombieGame.instance.mainFont.draw(batch, glyph, x, y + (h + glyph.height) / 2); y -= (glyph.height + 4);
				}
				glyph.setText(ZombieGame.instance.mainFont, "\n", Color.WHITE, w - 20, Align.left, false);
				ZombieGame.instance.mainFont.draw(batch, glyph, x, y + (h + glyph.height) / 2); y -= (glyph.height + 4);
			}
			batch.end();
			ScissorStack.popScissors();
		}

		{
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
		}

		batch.begin();

		// Draw name of screen
		ZombieGame.instance.bigFont.draw(batch, "Failed to load game. Errors:", 10, getHeight() - 45*ZombieGame.getYScalar(), getWidth() - 20, Align.center, false);
	}

	@Override
	public void resize(int width, int height) {
		super.resize(width, height);
	}

	@Override
	protected void setUpScreen() {
		super.setUpScreen();

		addElement(new GuiRectangle(()->new Rectangle2D.Float(10*ZombieGame.getXScalar(), 10*ZombieGame.getXScalar(), getWidth()/2 - 10*ZombieGame.getXScalar(), 50*ZombieGame.getXScalar()),
				"Back", (r)->{
					ZombieGame.instance.setScreenAndDispose(prevScreen);
				}));
		
		addElement(new GuiRectangle(()->new Rectangle2D.Float(10*ZombieGame.getXScalar() + getWidth()/2, 10*ZombieGame.getXScalar(), getWidth()/2 - 10*ZombieGame.getXScalar(), 50*ZombieGame.getXScalar()),
				"Load Anyways", (r)->{
					ZombieGame.instance.setScreenAndDispose(new InGameScreen(prevScreen, glc.game, false));
					glc.game.setPaused(false);
				}).setEnabled(glc.canBeLoaded));
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
		return 60*ZombieGame.getYScalar() * ((ZombieGame.instance.settings.getEntries().size() + 1) / 2) + 20*ZombieGame.getYScalar();
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
