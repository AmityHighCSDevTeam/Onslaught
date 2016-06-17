package org.amityregion5.onslaught.client.screen;

import java.awt.geom.Rectangle2D;
import java.util.Map.Entry;

import org.amityregion5.onslaught.Onslaught;
import org.amityregion5.onslaught.client.Client;
import org.amityregion5.onslaught.client.InputAccessor;
import org.amityregion5.onslaught.client.gui.GuiRectangle;
import org.amityregion5.onslaught.client.music.MusicHandler;
import org.amityregion5.onslaught.client.settings.InputData;
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
public class ControlsMenu extends GuiScreen {

	private GlyphLayout		glyph		= new GlyphLayout(); //The glyph layout
	private double			scrollPos; //The scroll position
	private String			selected; //The selected key
	private InputProcessor	processor; //The input processor
	private boolean justSet;

	public ControlsMenu(GuiScreen prevScreen) {
		super(prevScreen);

		processor = new InputAccessor() {
			@Override
			public boolean keyDown(int keycode) {
				if (selected != null) {
					//If something is currently selected set its input and deselect it
					Onslaught.instance.settings.setInput(selected, new InputData(true, keycode));
					selected = null;
					justSet = true;
				}
				return false;
			}
			@Override
			public boolean touchDown(int screenX, int screenY, int pointer, int button) {
				if (selected != null) {
					//If something is currently selected set its input (will later we deselected)
					Onslaught.instance.settings.setInput(selected, new InputData(false, button));
					justSet = true;
				}
				return false;
			}
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
		
		if (selected == null && justSet == false && Onslaught.instance.settings.getInput("Close_Window").isJustDown()) {
			Onslaught.instance.setScreenAndDispose(prevScreen);
		}
		justSet = true;

		batch.end();
		{
			//Clip screen
			Rectangle clipBounds = new Rectangle(10*Onslaught.getXScalar(), 100*Onslaught.getYScalar(), getWidth() - 20*Onslaught.getXScalar(), getHeight() - 201*Onslaught.getYScalar());
			ScissorStack.pushScissors(clipBounds);
			batch.begin();

			//Setup variables
			//Side of the screen
			boolean right = false;
			//Setup rectangle
			float h = 50*Onslaught.getYScalar();
			float blankSpace = 10*Onslaught.getYScalar();
			float y = (float) ((getHeight() - 50*Onslaught.getYScalar()) - h - blankSpace + scrollPos);
			float w = (getWidth() - 40*Onslaught.getXScalar()) / 2 - 100*Onslaught.getXScalar();

			boolean clickOn = false;

			for (Entry<String, InputData> entry : Onslaught.instance.settings.getEntries()) {
				float x;
				if (right) {
					x = getWidth() / 2 - 10*Onslaught.getXScalar() + 50*Onslaught.getXScalar();
					right = false;
				} else {
					x = 10*Onslaught.getXScalar();
					y -= (h + blankSpace);
					right = true;
				}
				if (Gdx.input.isTouched() && Gdx.input.justTouched() && Gdx.input.getX() >= x && Gdx.input.getX() <= x + w
						&& getHeight() - Gdx.input.getY() >= y && getHeight() - Gdx.input.getY() <= y + h && selected != entry.getKey()) {
					selected = entry.getKey();
					clickOn = true;
				}
				if (selected == entry.getKey()) {
					batch.setColor(0.75f, 0.75f, 0.75f, 1);
				}
				// batch.draw(buttText, x, y, w, h);
				batch.setColor(1, 1, 1, 1);

				Color color = (Onslaught.instance.settings.getSameValues(entry.getValue()) <= 1 ? Color.WHITE : Color.RED);

				if (color == Color.WHITE) {
					if (Gdx.input.getX() > x && Onslaught.instance.height - Gdx.input.getY() > y && Gdx.input.getX() < x + w
							&& Onslaught.instance.height - Gdx.input.getY() < y + h) {
						color = new Color(27 / 255f, 168 / 255f, 55 / 255f, 1f);
					}
				}

				//Draw key
				glyph.setText(Onslaught.instance.mainFont, entry.getKey().replace('_', ' ') + ": ", color, w - 20, Align.left, false);
				Onslaught.instance.mainFont.draw(batch, glyph, x, y + (h + glyph.height) / 2);
				//Draw value
				glyph.setText(Onslaught.instance.mainFont, entry.getValue().getName(), color, w - 20, Align.right, false);
				Onslaught.instance.mainFont.draw(batch, glyph, x, y + (h + glyph.height) / 2);
			}
			if (Gdx.input.isTouched() && Gdx.input.justTouched() && !clickOn) {
				selected = null;
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
		Onslaught.instance.bigFont.draw(batch, "Controls", 10, getHeight() - 45*Onslaught.getYScalar(), getWidth() - 20, Align.center, false);

		batch.end();

		if (selected != null) {
			float x = getWidth() / 2 - 150*Onslaught.getXScalar();
			float y = getHeight() / 2 - 50*Onslaught.getYScalar();
			float w = 300*Onslaught.getXScalar();
			float h = 100*Onslaught.getYScalar();

			shape.begin(ShapeType.Filled);

			shape.setColor(0.7f, 0.7f, 0.7f, 1f);

			shape.rect(x, y, w, h);

			shape.end();

			shape.begin(ShapeType.Line);

			shape.setColor(0.9f, 0.9f, 0.9f, 0.5f);

			shape.rect(x, y, w, h);

			shape.end();

			batch.begin();

			//Draw press button to set text
			glyph.setText(Onslaught.instance.mainFont, "Press a Button to set", Color.BLACK, w - 20, Align.center, false);
			Onslaught.instance.mainFont.draw(batch, glyph, x, y + (h + glyph.height) / 2);

			batch.end();
		}

		batch.begin();
	}

	@Override
	public void resize(int width, int height) {
		super.resize(width, height);
	}

	@Override
	protected void setUpScreen() {
		super.setUpScreen();

		addElement(new GuiRectangle(()->new Rectangle2D.Float(10*Onslaught.getXScalar(), 10*Onslaught.getXScalar(), getWidth() - 20*Onslaught.getXScalar(), 50*Onslaught.getXScalar()),
				"Back", (r)->{
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
