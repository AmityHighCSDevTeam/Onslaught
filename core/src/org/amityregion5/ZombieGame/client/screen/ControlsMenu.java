package org.amityregion5.ZombieGame.client.screen;

import java.util.Map.Entry;

import org.amityregion5.ZombieGame.ZombieGame;
import org.amityregion5.ZombieGame.client.gui.GuiButton;
import org.amityregion5.ZombieGame.client.settings.InputData;
import org.amityregion5.ZombieGame.common.helper.MathHelper;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.utils.ScissorStack;
import com.badlogic.gdx.utils.Align;

/**
 *
 * @author sergeys
 *
 */
public class ControlsMenu extends GuiScreen {

	private GlyphLayout glyph = new GlyphLayout();
	private ShapeRenderer shapeRender = new ShapeRenderer();
	private double scrollPos;
	private String selected;

	public ControlsMenu(GuiScreen prevScreen) {
		super(prevScreen);

		Gdx.input.setInputProcessor(new InputProcessor() {
			public boolean keyDown(int keycode) {
				if (selected != null) {
					ZombieGame.instance.settings.setInput(selected, new InputData(true, keycode));
					selected = null;
				}
				return false;
			}
			public boolean keyUp(int keycode) {return false;}
			public boolean keyTyped(char character) {return false;}
			public boolean touchDown(int screenX, int screenY, int pointer,
					int button) {
				if (selected != null) {
					ZombieGame.instance.settings.setInput(selected, new InputData(false, button));
				}
				return false;
			}
			public boolean touchUp(int screenX, int screenY, int pointer,
					int button) {return false;}
			public boolean touchDragged(int screenX, int screenY, int pointer) {return false;}
			public boolean mouseMoved(int screenX, int screenY) {return false;}
			public boolean scrolled(int amount) {
				double maxAmt = getMaxScrollAmount() - getHeight() + 200;
				scrollPos = MathHelper.clamp(0, (maxAmt > 0 ? maxAmt : 0), scrollPos + amount*5);
				return true;
			}
		});
	}

	// Font
	private BitmapFont	calibri30;

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
			Rectangle clipBounds = new Rectangle(10, 100, getWidth()-20, getHeight() - 201);
			//ScissorStack.calculateScissors(camera, screen.getScreenProjectionMatrix(), clipBounds, scissors);
			ScissorStack.pushScissors(clipBounds);
			batch.begin();
			Texture buttText = ZombieGame.instance.buttonTexture;

			boolean right = false;
			float h = 50;
			float blankSpace = 10;
			float y = (float) ((getHeight()-50) - h - blankSpace + scrollPos);
			float w = (getWidth()-40)/2 - 20;
			
			boolean clickOn = false;

			for (Entry<String, InputData> entry : ZombieGame.instance.settings.getEntries()){
				float x;
				if (right) {
					x = getWidth()/2 - 10;
					right = false;
				} else {
					x = 10;
					y -= (h + blankSpace);
					right = true;
				}
				if (Gdx.input.isTouched() && Gdx.input.justTouched() &&
						Gdx.input.getX()>=x && Gdx.input.getX()<=x+w &&
						getHeight()-Gdx.input.getY()>=y && getHeight()-Gdx.input.getY()<=y+h &&
						selected != entry.getKey()) {
					selected = entry.getKey();
					clickOn = true;
				}
				if (selected == entry.getKey()) {
					batch.setColor(0.75f, 0.75f, 0.75f, 1);
				}
				batch.draw(buttText, x, y, w, h);
				batch.setColor(1, 1, 1, 1);
				glyph.setText(ZombieGame.instance.mainFont, entry.getKey().replace('_', ' ') + ": " + entry.getValue().getName(), (ZombieGame.instance.settings.getSameValues(entry.getValue()) <= 1 ? Color.BLACK : Color.RED), w-20, Align.center, false);
				ZombieGame.instance.mainFont.draw(batch, glyph, x, y + (h+glyph.height)/2);
			}
			if (Gdx.input.isTouched() && Gdx.input.justTouched() && !clickOn) {
				selected = null;
			}
			batch.end();
			ScissorStack.popScissors();
		}

		{
			float x = getWidth()-30;
			float y = 100;
			float w = 20;
			float h = getHeight()-200;
			/*
			shapeRender.begin(ShapeType.Filled);

			shapeRender.setColor(r, g, b, a);

			shapeRender.end();

			shapeRender.begin(ShapeType.Line);
			shapeRender.end();
			 */

			shapeRender.begin(ShapeType.Filled);
			//Main Scroll bar box
			shapeRender.setColor(0.4f, 0.4f, 0.4f, 1f);
			shapeRender.rect(x, y, w, h);

			//Main Scroll Bar
			shapeRender.setColor(0.7f, 0.7f, 0.7f, 1f);
			shapeRender.rect(x, y, w, h);
			shapeRender.end();

			shapeRender.begin(ShapeType.Line);

			shapeRender.setColor(0.9f, 0.9f, 0.9f, 0.5f);
			//Scroll bar box border left line
			shapeRender.rect(x, getScrollBarPos(), w, getScrollBarHeight());

			shapeRender.end();

		}

		batch.begin();

		// Draw name of screen
		calibri30.draw(batch, "Controls" + (selected == null ? "" : " --Press a button to set new--"), 10, getHeight() - 45,
				getWidth() - 20, Align.center, false);
	}

	@Override
	public void resize(int width, int height) {
		super.resize(width, height);
	}

	@Override
	protected void setUpScreen() {
		super.setUpScreen();

		// Register buttons
		/*
		addButton(new GuiButton(ZombieGame.instance.buttonTexture, 0,
				"Controls", 10, getHeight() - 150, getWidth() - 20, 50));
		addButton(new GuiButton(ZombieGame.instance.buttonTexture, 1,
				"Master Volume", 10, getHeight() - 210, getWidth() - 20, 50)
				.setEnabled(false));
		 */
		addButton(new GuiButton(ZombieGame.instance.buttonTexture, -1, "Back",
				10, 10, getWidth() - 20, 50));
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
		calibri30.dispose();
		shapeRender.dispose();
	}

	private double getMaxScrollAmount() {
		return 60*((ZombieGame.instance.settings.getEntries().size()+1)/2) + 20;
	}

	private float getScrollBarPos() {
		double screenHeight = getHeight()-200;
		double pos = (screenHeight - getScrollBarHeight()) * (scrollPos)/(getMaxScrollAmount() - screenHeight);
		pos = (getHeight() - 100) - pos - getScrollBarHeight();
		//double pos = (getMaxScrollAmount() - screenHeight - scrollPos + 47);
		//if (pos < 100) {
		//	pos = 100;
		//}
		return (float) pos;
	}

	private float getScrollBarHeight() {
		double screenHeight = getHeight()-200;
		double height = (screenHeight * screenHeight)/getMaxScrollAmount();
		return (float) (height > screenHeight ? screenHeight : height);
	}
}
