package org.amityregion5.ZombieGame.client.screen;

import java.awt.geom.Rectangle2D;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.amityregion5.ZombieGame.ZombieGame;
import org.amityregion5.ZombieGame.client.Client;
import org.amityregion5.ZombieGame.client.InputAccessor;
import org.amityregion5.ZombieGame.client.gui.GuiRectangle;
import org.amityregion5.ZombieGame.client.music.MusicHandler;
import org.amityregion5.ZombieGame.common.game.Game;
import org.amityregion5.ZombieGame.common.game.GameLoadedContainer;
import org.amityregion5.ZombieGame.common.helper.MathHelper;

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

		FileHandle file = ZombieGame.instance.settingsFile.parent().child("saves");

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
		Rectangle clipBounds = new Rectangle(10*ZombieGame.getXScalar(), 100*ZombieGame.getYScalar(), getWidth() - 20*ZombieGame.getXScalar(), getHeight() - 201*ZombieGame.getYScalar());
		ScissorStack.pushScissors(clipBounds);

		{
			//Calculate positions and sizes
			float x = 10*ZombieGame.getXScalar();
			float h = 50*ZombieGame.getYScalar();
			float blankSpace = 10*ZombieGame.getYScalar();
			float y = (float) ((getHeight() - 50*ZombieGame.getYScalar()) - h - blankSpace + scrollPos);
			float w = getWidth() - 40*ZombieGame.getXScalar();

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
					if (Gdx.input.getX() > x && ZombieGame.instance.height - Gdx.input.getY() > y && Gdx.input.getX() < x + w
							&& ZombieGame.instance.height - Gdx.input.getY() < y + h) {
						color = new Color(27 / 255f, 168 / 255f, 55 / 255f, 1f);
					}
				}

				//Write text
				glyph.setText(ZombieGame.instance.mainFont, file, color, w - 20*ZombieGame.getXScalar(), Align.left, false);
				ZombieGame.instance.mainFont.draw(batch, glyph, x, y + (h + glyph.height) / 2);

				//Move drawing point
				y -= (h + blankSpace);
			}
		}
		ScissorStack.popScissors();
		batch.end();
		{
			float x = getWidth() - 30*ZombieGame.getXScalar();
			float y = 100*ZombieGame.getYScalar();
			float w = 20*ZombieGame.getXScalar();
			float h = getHeight() - 200*ZombieGame.getYScalar();

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
		ZombieGame.instance.bigFont.draw(batch, "Continue", 10, getHeight() - 45, getWidth() - 20, Align.center, false);
	}

	@Override
	public void resize(int width, int height) {
		super.resize(width, height);
	}

	@Override
	protected void setUpScreen() {
		super.setUpScreen();

		addElement(new GuiRectangle(()->
		new Rectangle2D.Float(10*ZombieGame.getXScalar(), 10*ZombieGame.getXScalar(), getWidth()/2 - 20*ZombieGame.getXScalar(), 50*ZombieGame.getXScalar()),
		"Back", (r)->{
			ZombieGame.instance.settings.save();
			ZombieGame.instance.setScreenAndDispose(prevScreen);
		}));
		addElement(new GuiRectangle(()->
		new Rectangle2D.Float(getWidth()/2 + 10*ZombieGame.getXScalar(), 10*ZombieGame.getXScalar(), getWidth()/2 - 20*ZombieGame.getXScalar(), 50*ZombieGame.getXScalar()),
		"Load", (r)->{
			if (selected == null) {
				return;
			}
			ZombieGame.log("Loading Game");
			GameLoadedContainer glc = Game.loadFromFile(selected);
			if (!glc.errors.isEmpty()) {
				ZombieGame.instance.setScreen(new FailedToLoadScreen(this, glc));
			} else {
				ZombieGame.instance.setScreen(new InGameScreen(this, glc.game, false));
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
		return 60*ZombieGame.getYScalar() * (fileNames.size()) + 20*ZombieGame.getYScalar();
	}

	private float getScrollBarPos() {
		double screenHeight = getHeight() - 200*ZombieGame.getYScalar();
		double pos = (screenHeight - getScrollBarHeight()) * (scrollPos) / (getMaxScrollAmount() - screenHeight);
		pos = (getHeight() - 100*ZombieGame.getYScalar()) - pos - getScrollBarHeight();
		// double pos = (getMaxScrollAmount() - screenHeight - scrollPos + 47);
		// if (pos < 100) {
		// pos = 100;
		// }
		return (float) pos;
	}

	private float getScrollBarHeight() {
		double screenHeight = getHeight() - 200*ZombieGame.getYScalar();
		double height = (screenHeight * screenHeight) / getMaxScrollAmount();
		return (float) (height > screenHeight ? screenHeight : height);
	}
}
