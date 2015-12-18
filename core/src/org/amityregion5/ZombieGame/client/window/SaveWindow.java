package org.amityregion5.ZombieGame.client.window;

import org.amityregion5.ZombieGame.ZombieGame;
import org.amityregion5.ZombieGame.client.Client;
import org.amityregion5.ZombieGame.client.InputAccessor;
import org.amityregion5.ZombieGame.client.screen.InGameScreen;
import org.amityregion5.ZombieGame.common.game.model.entity.PlayerModel;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.utils.Align;

/**
 * The save window
 * @author sergeys
 *
 */
public class SaveWindow implements Screen {
	private ShapeRenderer	shapeRender			= new ShapeRenderer(); //The shape renderer
	private InGameScreen	screen; //The screen
	private GlyphLayout		glyph; //The glyph layout
	private SpriteBatch		batch				= new SpriteBatch(); //The sprite batch
	private PlayerModel		player; //The player
	private PauseWindow		window; //The window
	private InputProcessor	processor; //The input processor
	private String			saveName			= ""; //The game's current save name
	private boolean			showCursor			= false; //Should the cursor be shown
	private float			timeUntilShowCursor	= 0; //The time until the cursor is shown
	private boolean mouseWasUp = false;

	public SaveWindow(InGameScreen screen, PlayerModel player, PauseWindow pauseWindow) {
		this.screen = screen; //Set values
		glyph = new GlyphLayout();
		this.player = player;
		window = pauseWindow;

		processor = new InputAccessor() {
			@Override
			public boolean keyUp(int keycode) {
				//Allow backspace to function as a delete key
				if (keycode == Keys.BACKSPACE && saveName.length() > 0) {
					saveName = saveName.substring(0, saveName.length() - 1);
				}
				return false;
			}

			@Override
			public boolean keyTyped(char character) {
				//Add alphanumeric characters to the string when they are typed
				if (Character.isLetterOrDigit(character)) {
					saveName += character;
				}
				return true;
			}
		};

		//Add the processor the the multiplexer
		Client.inputMultiplexer.addProcessor(processor);
	}

	@Override
	public void drawScreen(float delta, Camera camera) {

		//Swap show cursor on and off every 0.4 seconds.
		if (timeUntilShowCursor <= 0) {
			timeUntilShowCursor = 0.4f;
			showCursor = !showCursor;
		}
		timeUntilShowCursor -= delta;

		//Prepare for drawing
		drawPrepare(delta);

		//Draw the main area
		drawMain(delta);

		Gdx.gl.glDisable(GL20.GL_BLEND);
		mouseWasUp = Gdx.input.isTouched();
	}

	private void drawPrepare(float delta) {
		//Update the projection matricies
		shapeRender.setProjectionMatrix(screen.getScreenProjectionMatrix());
		batch.setProjectionMatrix(screen.getScreenProjectionMatrix());

		//Enable transparency
		Gdx.gl.glEnable(GL20.GL_BLEND);
		Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

		shapeRender.begin(ShapeType.Filled);

		// Gray the entire screen
		shapeRender.setColor(0.5f, 0.5f, 0.5f, 0.2f);
		shapeRender.rect(0, 0, screen.getWidth(), screen.getHeight());

		// Main box in the center
		shapeRender.setColor(0.3f, 0.3f, 0.3f, 0.6f);
		shapeRender.rect(screen.getWidth() / 2 - 300*ZombieGame.getXScalar(), screen.getHeight() / 2 - 150*ZombieGame.getYScalar(), 600*ZombieGame.getXScalar(), 300*ZombieGame.getYScalar());

		shapeRender.end();

		shapeRender.begin(ShapeType.Line);

		shapeRender.setColor(0.9f, 0.9f, 0.9f, 0.5f);
		// Main box border
		shapeRender.rect(screen.getWidth() / 2 - 300*ZombieGame.getXScalar(), screen.getHeight() / 2 - 150*ZombieGame.getYScalar(), 600*ZombieGame.getXScalar(), 300*ZombieGame.getYScalar());

		shapeRender.end();
	}

	private void drawMain(float delta) {
		batch.begin();

		//Draw the text Save in the middle of the screen
		glyph.setText(ZombieGame.instance.mainFont, "Save", Color.WHITE, 600*ZombieGame.getXScalar(), Align.center, false);
		ZombieGame.instance.mainFont.draw(batch, glyph, screen.getWidth() / 2 - 300*ZombieGame.getXScalar(), screen.getHeight() / 2 + 150*ZombieGame.getYScalar() - glyph.height - 10*ZombieGame.getYScalar());

		//Draw the name of the save in the middle
		glyph.setText(ZombieGame.instance.mainFont, "Name: " + saveName + (showCursor ? "|" : ""), Color.WHITE, 500*ZombieGame.getXScalar(), Align.left, false);
		ZombieGame.instance.mainFont.draw(batch, glyph, screen.getWidth() / 2 - 250*ZombieGame.getXScalar(), screen.getHeight() / 2);

		//Is the back button moused over
		boolean mouseOverBack = Gdx.input.getX() > screen.getWidth() / 2 - 300*ZombieGame.getXScalar() && Gdx.input.getX() < screen.getWidth() / 2
				&& screen.getHeight() - Gdx.input.getY() > screen.getHeight() / 2 - 150*ZombieGame.getYScalar()
				&& screen.getHeight() - Gdx.input.getY() < screen.getHeight() / 2 - 150*ZombieGame.getYScalar() + 50*ZombieGame.getYScalar();

		//Draw the back button
		glyph.setText(ZombieGame.instance.mainFont, "Back", (mouseOverBack ? new Color(27 / 255f, 168 / 255f, 55 / 255f, 1f) : Color.WHITE), 280*ZombieGame.getXScalar(), Align.center,
				false);
		ZombieGame.instance.mainFont.draw(batch, glyph, screen.getWidth() / 2 - 290*ZombieGame.getXScalar(), screen.getHeight() / 2 - 150*ZombieGame.getYScalar() + 25*ZombieGame.getYScalar());

		//If the back button is pressed
		if (mouseOverBack && !Gdx.input.isTouched() && mouseWasUp) {
			//Return to the previous window
			screen.setCurrentWindow(window);
			//Dispose self
			dispose();
			return;
		}

		//Is the save button is moused over
		boolean mouseOverSave = Gdx.input.getX() > screen.getWidth() / 2 && Gdx.input.getX() < screen.getWidth() / 2 + 300*ZombieGame.getXScalar()
				&& screen.getHeight() - Gdx.input.getY() > screen.getHeight() / 2 - 150*ZombieGame.getYScalar()
				&& screen.getHeight() - Gdx.input.getY() < screen.getHeight() / 2 - 150*ZombieGame.getYScalar() + 50*ZombieGame.getYScalar();

		//Draw save and quit button
		glyph.setText(ZombieGame.instance.mainFont, "Save and Quit", (mouseOverSave ? new Color(27 / 255f, 168 / 255f, 55 / 255f, 1f) : Color.WHITE), 280*ZombieGame.getXScalar(),
				Align.center, false);
		ZombieGame.instance.mainFont.draw(batch, glyph, screen.getWidth() / 2 + 10*ZombieGame.getXScalar(), screen.getHeight() / 2 - 150*ZombieGame.getYScalar() + 25*ZombieGame.getYScalar());

		//If the save button is pressed
		if (mouseOverSave && !Gdx.input.isTouched() && mouseWasUp) {
			//Save the game
			screen.getGame().saveToFile(saveName);
			//Kill the player (with fun message)
			player.damage(Float.POSITIVE_INFINITY, null, "QUIT BUTTON SMITES YOU");
		}

		batch.end();
	}

	@Override
	public void dispose() {
		batch.dispose();
		//Remove input processor
		Client.inputMultiplexer.removeProcessor(processor);
	}
}
