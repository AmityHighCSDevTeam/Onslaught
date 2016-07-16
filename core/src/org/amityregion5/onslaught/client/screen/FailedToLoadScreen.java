package org.amityregion5.onslaught.client.screen;

import java.awt.geom.Rectangle2D;
import java.util.List;
import java.util.Map.Entry;

import org.amityregion5.onslaught.Onslaught;
import org.amityregion5.onslaught.client.Client;
import org.amityregion5.onslaught.client.InputAccessor;
import org.amityregion5.onslaught.client.gui.GuiRectangle;
import org.amityregion5.onslaught.client.music.MusicHandler;
import org.amityregion5.onslaught.common.game.GameLoadedContainer;
import org.amityregion5.onslaught.common.helper.MathHelper;

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
				double maxAmt = getMaxScrollAmount() - getHeight() + 200*Onslaught.getYScalar();
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
			Rectangle clipBounds = new Rectangle(10*Onslaught.getXScalar(), 100*Onslaught.getYScalar(), getWidth() - 20*Onslaught.getXScalar(), getHeight() - 201*Onslaught.getYScalar());
			ScissorStack.pushScissors(clipBounds);
			batch.begin();

			//Setup rectangle
			float h = 50*Onslaught.getYScalar();
			float blankSpace = 51*Onslaught.getYScalar();
			float x = 10*Onslaught.getXScalar();
			float y = (float) ((getHeight() - 50*Onslaught.getYScalar()) - h - blankSpace + scrollPos);
			float w = (getWidth() - 40*Onslaught.getXScalar()) - 100*Onslaught.getXScalar();

			for (Entry<String, List<String>> entry : glc.errors.entrySet()) {
				glyph.setText(Onslaught.instance.mainFont, entry.getKey(), Color.WHITE, w - 20, Align.left, true);
				Onslaught.instance.mainFont.draw(batch, glyph, x, y + (h + glyph.height) / 2); y -= (glyph.height + 4);
				for (String str : entry.getValue()) {
					glyph.setText(Onslaught.instance.mainFont, "    " + str, Color.WHITE, w - 20, Align.left, true);
					Onslaught.instance.mainFont.draw(batch, glyph, x, y + (h + glyph.height) / 2); y -= (glyph.height + 4);
				}
				glyph.setText(Onslaught.instance.mainFont, "\n", Color.WHITE, w - 20, Align.left, false);
				Onslaught.instance.mainFont.draw(batch, glyph, x, y + (h + glyph.height) / 2); y -= (glyph.height + 4);
			}
			batch.end();
			ScissorStack.popScissors();
		}

		{
			float x = getWidth() - 30*Onslaught.getXScalar();
			float y = 100*Onslaught.getYScalar();
			float w = 20*Onslaught.getXScalar();
			float h = getHeight() - 200*Onslaught.getYScalar();

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
		Onslaught.instance.bigFont.draw(batch, "Failed to load game. Errors:", 10, getHeight() - 45*Onslaught.getYScalar(), getWidth() - 20, Align.center, false);
	}

	@Override
	public void resize(int width, int height) {
		super.resize(width, height);
	}

	@Override
	protected void setUpScreen() {
		super.setUpScreen();

		addElement(new GuiRectangle(()->new Rectangle2D.Float(10*Onslaught.getXScalar(), 10*Onslaught.getXScalar(), getWidth()/2 - 10*Onslaught.getXScalar(), 50*Onslaught.getXScalar()),
				"Back", (r)->{
					Onslaught.instance.setScreenAndDispose(prevScreen);
				}));
		
		addElement(new GuiRectangle(()->new Rectangle2D.Float(10*Onslaught.getXScalar() + getWidth()/2, 10*Onslaught.getXScalar(), getWidth()/2 - 10*Onslaught.getXScalar(), 50*Onslaught.getXScalar()),
				"Load Anyways", (r)->{
					Onslaught.instance.setScreenAndDispose(new InGameScreen(prevScreen, glc.game, false));
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
		return 60*Onslaught.getYScalar() * ((Onslaught.instance.settings.getEntries().size() + 1) / 2) + 20*Onslaught.getYScalar();
	}

	/**
	 * Get the scroll bar position
	 * @return the scroll bar position
	 */
	private float getScrollBarPos() {
		double screenHeight = getHeight() - 200*Onslaught.getYScalar();
		double pos = (screenHeight - getScrollBarHeight()) * (scrollPos) / (getMaxScrollAmount() - screenHeight);
		pos = (getHeight() - 100*Onslaught.getYScalar()) - pos - getScrollBarHeight();
		return (float) pos;
	}

	/**
	 * Get the height of the scroll bar
	 * @return the height of the scroll bar
	 */
	private float getScrollBarHeight() {
		double screenHeight = getHeight() - 200*Onslaught.getYScalar();
		double height = (screenHeight * screenHeight) / getMaxScrollAmount();
		return (float) (height > screenHeight ? screenHeight : height);
	}
}
