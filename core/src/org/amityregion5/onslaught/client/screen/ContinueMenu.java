package org.amityregion5.onslaught.client.screen;

import java.awt.geom.Rectangle2D;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.amityregion5.onslaught.Onslaught;
import org.amityregion5.onslaught.client.Client;
import org.amityregion5.onslaught.client.InputAccessor;
import org.amityregion5.onslaught.client.gui.GuiRectangle;
import org.amityregion5.onslaught.client.music.MusicHandler;
import org.amityregion5.onslaught.client.settings.Settings;
import org.amityregion5.onslaught.common.game.Game;
import org.amityregion5.onslaught.common.game.GameLoadedContainer;
import org.amityregion5.onslaught.common.helper.MathHelper;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.utils.ScissorStack;
import com.badlogic.gdx.utils.Align;

/**
 * The continue menu
 * @author sergeys
 */
public class ContinueMenu extends GuiScreen {

	private GlyphLayout		glyph		= new GlyphLayout(); //The glyph layout
	private double			scrollPos; //The scroll position
	private String			selected; //Selected game name
	private InputProcessor	processor; //Input processor
	private List<String>	fileNames; //All game names cached

	public ContinueMenu(GuiScreen prevScreen) {
		super(prevScreen);

		processor = new InputAccessor() {
			@Override
			public boolean scrolled(int amount) {
				double maxAmt = getMaxScrollAmount() - getHeight() + 200;
				scrollPos = MathHelper.clamp(0, (maxAmt > 0 ? maxAmt : 0), scrollPos + amount * 5);
				return true;
			}
		};

		Client.inputMultiplexer.addProcessor(processor);

		FileHandle file = Onslaught.instance.settingsFile.parent().child("saves");

		//Get all things in saves folder
		fileNames = Arrays.stream(file.list()).map((f) -> f.nameWithoutExtension()).collect(Collectors.toList());
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

		//Clip screen
		Rectangle clipBounds = new Rectangle(10*Onslaught.getXScalar(), 100*Onslaught.getYScalar(), getWidth() - 20*Onslaught.getXScalar(), getHeight() - 201*Onslaught.getYScalar());
		ScissorStack.pushScissors(clipBounds);

		{
			//Calculate positions and sizes
			float x = 10*Onslaught.getXScalar();
			float h = 50*Onslaught.getYScalar();
			float blankSpace = 10*Onslaught.getYScalar();
			float y = (float) ((getHeight() - 50*Onslaught.getYScalar()) - h - blankSpace + scrollPos);
			float w = getWidth() - 40*Onslaught.getXScalar();

			//Go through each file name
			for (String file : fileNames) {
				//Select if clicked on
				if (Gdx.input.isTouched() && Gdx.input.justTouched() && Gdx.input.getX() >= x && Gdx.input.getX() <= x + w
						&& getHeight() - Gdx.input.getY() >= y && getHeight() - Gdx.input.getY() <= y + h && selected != file) {
					selected = file;
				}

				batch.setColor(1, 1, 1, 1);

				//Get color to draw text with
				Color color = (selected != file ? Color.WHITE : new Color(27 / 255f, 255 / 255f, 55 / 255f, 1f));

				//If moused over make color different
				if (color == Color.WHITE) {
					if (Gdx.input.getX() > x && Onslaught.instance.height - Gdx.input.getY() > y && Gdx.input.getX() < x + w
							&& Onslaught.instance.height - Gdx.input.getY() < y + h) {
						color = new Color(27 / 255f, 168 / 255f, 55 / 255f, 1f);
					}
				}

				//Write text
				glyph.setText(Onslaught.instance.mainFont, file, color, w - 20*Onslaught.getXScalar(), Align.left, false);
				Onslaught.instance.mainFont.draw(batch, glyph, x, y + (h + glyph.height) / 2);

				//Move drawing point
				y -= (h + blankSpace);
			}
		}
		ScissorStack.popScissors();
		batch.end();
		{
			float x = getWidth() - 30*Onslaught.getXScalar();
			float y = 100*Onslaught.getYScalar();
			float w = 20*Onslaught.getXScalar();
			float h = getHeight() - 200*Onslaught.getYScalar();

			Matrix4 projM = shape.getProjectionMatrix().cpy();

			shape.setProjectionMatrix(camera.combined);

			shape.begin(ShapeType.Filled);
			// Main Scroll bar box
			shape.setColor(0.4f, 0.4f, 0.4f, 1f);
			shape.rect(x, y, w, h);

			// Main Scroll Bar
			shape.setColor(0.7f, 0.7f, 0.7f, 1f);
			shape.rect(x, y, w, h);
			shape.end();

			shape.begin(ShapeType.Line);

			shape.setColor(0.9f, 0.9f, 0.9f, 0.5f);
			// Scroll bar box border left line
			shape.rect(x, getScrollBarPos(), w, getScrollBarHeight());

			shape.end();

			shape.setProjectionMatrix(projM);
		}

		batch.begin();

		// Draw name of screen
		Onslaught.instance.bigFont.draw(batch, "Continue", 10, getHeight() - 45, getWidth() - 20, Align.center, false);
		
		if (Onslaught.instance.settings.getInput("Close_Window").isJustDown()) {
			Onslaught.instance.setScreenAndDispose(prevScreen);
		}
	}

	@Override
	public void resize(int width, int height) {
		super.resize(width, height);
	}

	@Override
	protected void setUpScreen() {
		super.setUpScreen();

		addElement(new GuiRectangle(()->
		new Rectangle2D.Float(10*Onslaught.getXScalar(), 10*Onslaught.getXScalar(), getWidth()/2 - 20*Onslaught.getXScalar(), 50*Onslaught.getXScalar()),
		"Back", (r)->{
			Settings.save(Onslaught.instance.settings);
			Onslaught.instance.setScreenAndDispose(prevScreen);
		}));
		addElement(new GuiRectangle(()->
		new Rectangle2D.Float(getWidth()/2 + 10*Onslaught.getXScalar(), 10*Onslaught.getXScalar(), getWidth()/2 - 20*Onslaught.getXScalar(), 50*Onslaught.getXScalar()),
		"Load", (r)->{
			if (selected == null) {
				return;
			}
			Onslaught.log("Loading Game");
			GameLoadedContainer glc = Game.loadFromFile(selected);
			if (!glc.errors.isEmpty()) {
				Onslaught.instance.setScreen(new FailedToLoadScreen(this, glc));
			} else {
				Onslaught.instance.setScreen(new InGameScreen(this, glc.game, false));
				glc.game.setPaused(false);
			}
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
		return 60*Onslaught.getYScalar() * (fileNames.size()) + 20*Onslaught.getYScalar();
	}

	private float getScrollBarPos() {
		double screenHeight = getHeight() - 200*Onslaught.getYScalar();
		double pos = (screenHeight - getScrollBarHeight()) * (scrollPos) / (getMaxScrollAmount() - screenHeight);
		pos = (getHeight() - 100*Onslaught.getYScalar()) - pos - getScrollBarHeight();
		// double pos = (getMaxScrollAmount() - screenHeight - scrollPos + 47);
		// if (pos < 100) {
		// pos = 100;
		// }
		return (float) pos;
	}

	private float getScrollBarHeight() {
		double screenHeight = getHeight() - 200*Onslaught.getYScalar();
		double height = (screenHeight * screenHeight) / getMaxScrollAmount();
		return (float) (height > screenHeight ? screenHeight : height);
	}
}
