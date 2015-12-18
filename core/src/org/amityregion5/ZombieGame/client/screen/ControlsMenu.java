package org.amityregion5.ZombieGame.client.screen;

import java.util.Map.Entry;

import org.amityregion5.ZombieGame.ZombieGame;
import org.amityregion5.ZombieGame.client.Client;
import org.amityregion5.ZombieGame.client.InputAccessor;
import org.amityregion5.ZombieGame.client.gui.GuiButton;
import org.amityregion5.ZombieGame.client.settings.InputData;
import org.amityregion5.ZombieGame.common.helper.MathHelper;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
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
	private ShapeRenderer	shapeRender	= new ShapeRenderer(); //The shape renderer
	private double			scrollPos; //The scroll position
	private String			selected; //The selected key
	private InputProcessor	processor; //The input processor

	public ControlsMenu(GuiScreen prevScreen) {
		super(prevScreen);

		processor = new InputAccessor() {
			@Override
			public boolean keyDown(int keycode) {
				if (selected != null) {
					//If something is currently selected set its input and deselect it
					ZombieGame.instance.settings.setInput(selected, new InputData(true, keycode));
					selected = null;
				}
				return false;
			}
			@Override
			public boolean touchDown(int screenX, int screenY, int pointer, int button) {
				if (selected != null) {
					//If something is currently selected set its input (will later we deselected)
					ZombieGame.instance.settings.setInput(selected, new InputData(false, button));
				}
				return false;
			}
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

			//Setup variables
			//Side of the screen
			boolean right = false;
			//Setup rectangle
			float h = 50*ZombieGame.getYScalar();
			float blankSpace = 10*ZombieGame.getYScalar();
			float y = (float) ((getHeight() - 50*ZombieGame.getYScalar()) - h - blankSpace + scrollPos);
			float w = (getWidth() - 40*ZombieGame.getXScalar()) / 2 - 100*ZombieGame.getXScalar();

			boolean clickOn = false;

			for (Entry<String, InputData> entry : ZombieGame.instance.settings.getEntries()) {
				float x;
				if (right) {
					x = getWidth() / 2 - 10*ZombieGame.getXScalar() + 50*ZombieGame.getXScalar();
					right = false;
				} else {
					x = 10*ZombieGame.getXScalar();
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

				Color color = (ZombieGame.instance.settings.getSameValues(entry.getValue()) <= 1 ? Color.WHITE : Color.RED);

				if (color == Color.WHITE) {
					if (Gdx.input.getX() > x && ZombieGame.instance.height - Gdx.input.getY() > y && Gdx.input.getX() < x + w
							&& ZombieGame.instance.height - Gdx.input.getY() < y + h) {
						color = new Color(27 / 255f, 168 / 255f, 55 / 255f, 1f);
					}
				}

				//Draw key
				glyph.setText(ZombieGame.instance.mainFont, entry.getKey().replace('_', ' ') + ": ", color, w - 20, Align.left, false);
				ZombieGame.instance.mainFont.draw(batch, glyph, x, y + (h + glyph.height) / 2);
				//Draw value
				glyph.setText(ZombieGame.instance.mainFont, entry.getValue().getName(), color, w - 20, Align.right, false);
				ZombieGame.instance.mainFont.draw(batch, glyph, x, y + (h + glyph.height) / 2);
			}
			if (Gdx.input.isTouched() && Gdx.input.justTouched() && !clickOn) {
				selected = null;
			}
			batch.end();
			ScissorStack.popScissors();
		}

		{
			float x = getWidth() - 30*ZombieGame.getXScalar();
			float y = 100*ZombieGame.getYScalar();
			float w = 20*ZombieGame.getXScalar();
			float h = getHeight() - 200*ZombieGame.getYScalar();
			
			Matrix4 projM = shapeRender.getProjectionMatrix().cpy();
			
			shapeRender.setProjectionMatrix(camera.combined);

			shapeRender.begin(ShapeType.Filled);
			shapeRender.setColor(0.4f, 0.4f, 0.4f, 1f);
			shapeRender.rect(x, y, w, h);

			// Secondary Scroll Bar
			shapeRender.setColor(0.7f, 0.7f, 0.7f, 1f);
			shapeRender.rect(x, getScrollBarPos(), w, getScrollBarHeight());

			shapeRender.end();
			shapeRender.begin(ShapeType.Line);

			shapeRender.setColor(0.9f, 0.9f, 0.9f, 0.5f);
			// Scroll bar box border left line
			shapeRender.rect(x, y, w, h);

			shapeRender.end();

			shapeRender.setProjectionMatrix(projM);
		}

		batch.begin();

		// Draw name of screen
		ZombieGame.instance.bigFont.draw(batch, "Controls", 10, getHeight() - 45*ZombieGame.getYScalar(), getWidth() - 20, Align.center, false);

		batch.end();

		if (selected != null) {
			float x = getWidth() / 2 - 150*ZombieGame.getXScalar();
			float y = getHeight() / 2 - 50*ZombieGame.getYScalar();
			float w = 300*ZombieGame.getXScalar();
			float h = 100*ZombieGame.getYScalar();

			shapeRender.begin(ShapeType.Filled);

			shapeRender.setColor(0.7f, 0.7f, 0.7f, 1f);

			shapeRender.rect(x, y, w, h);

			shapeRender.end();

			shapeRender.begin(ShapeType.Line);

			shapeRender.setColor(0.9f, 0.9f, 0.9f, 0.5f);

			shapeRender.rect(x, y, w, h);

			shapeRender.end();

			batch.begin();

			//Draw press button to set text
			glyph.setText(ZombieGame.instance.mainFont, "Press a Button to set", Color.BLACK, w - 20, Align.center, false);
			ZombieGame.instance.mainFont.draw(batch, glyph, x, y + (h + glyph.height) / 2);

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

		addButton(new GuiButton(ZombieGame.instance.buttonTexture, -1, "Back", 10, 10, getWidth() - 20, 50));
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
				dispose();
				ZombieGame.instance.setScreen(prevScreen);
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
		shapeRender.dispose();
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
