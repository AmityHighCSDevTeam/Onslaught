package org.amityregion5.ZombieGame.client.screen;

import java.util.HashMap;

import org.amityregion5.ZombieGame.client.gui.GuiButton;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

/**
 *
 * @author sergeys
 *
 */
public abstract class GuiScreen implements Screen {

	// The batch for drawing the screen
	protected SpriteBatch				batch;
	// Last locations that the mouse was down
	private float						lastMouseX, lastMouseY;
	// Was the mouse down?
	private boolean						lastMouseDown	= false;
	// The buttons
	private HashMap<Integer, GuiButton>	buttons			= new HashMap<Integer, GuiButton>();
	// The screen that we came from
	protected GuiScreen					prevScreen;
	// The camera
	protected OrthographicCamera	camera;

	private boolean disposed = false;

	/**
	 *
	 * @param prevScreen
	 *            the screen that we came from
	 */
	public GuiScreen(GuiScreen prevScreen) {
		this.prevScreen = prevScreen;
	}

	@Override
	public void render(float delta) {

		camera.setToOrtho(false);
		camera.update();
		batch.setProjectionMatrix(camera.combined);

		// Get mouse down position
		Vector2 touchPos = new Vector2();
		touchPos.set(Gdx.input.getX(), Gdx.input.getY());

		// Is the screen touched (mouse currently down)
		if (Gdx.input.isTouched()) {
			// Record mouse pos
			lastMouseX = touchPos.x;
			lastMouseY = touchPos.y;

			// If the mouse was up register this as a mouseDown
			if (!lastMouseDown) {
				lastMouseDown = true;
				if (!disposed) {
					mouseDown(touchPos.x, touchPos.y);
				}
			}
		} else {
			// If the mouse was down register this as a mouseUp
			if (lastMouseDown) {
				lastMouseDown = false;
				if (!disposed) {
					mouseUp(lastMouseX, lastMouseY);
				}
			}
		}

		// Start drawing
		batch.begin();

		// Call other methods to draw
		if (!disposed) {
			drawScreen(delta);
		}

		// Finish drawing
		batch.end();
	}

	@Override
	public void resize(int width, int height) {
		setUpScreen(); // Used for repositioning buttons
	}

	@Override
	public void show() {
		batch = new SpriteBatch(); // Create a new sprite batch
		camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
	}

	@Override
	public void hide() {
	}

	@Override
	public void pause() {
	}

	@Override
	public void resume() {
	}

	@Override
	public void dispose() {
		disposed = true;
		for (GuiButton b : buttons.values()) {
			b.dispose(); // Dispose all of the buttons
		}
	}

	protected void setUpScreen() {
		for (GuiButton b : buttons.values()) {
			b.dispose(); // Dispose all of the buttons
		}
		buttons.clear(); // Clear the hashmap
	}

	/**
	 * Override this to get buttonClicks
	 */
	protected void buttonClicked(int id) {
	}

	/**
	 * On mouse down
	 */
	protected void mouseDown(float x, float y) {
	}

	/**
	 * On mouse up
	 */
	protected void mouseUp(float x, float y) {
		for (GuiButton b : buttons.values()) {
			if (b.getBoundingRectangle().contains(x, getHeight() - y)) {
				buttonClicked(b.getID()); // Register a general button clicked
				// event
			}
		}
	}

	protected void drawScreen(float delta) {
		for (GuiButton b : buttons.values()) {
			b.draw(batch); // Draw the buttons
		}
	}

	public int getWidth() {
		return Gdx.graphics.getWidth();
	}

	public int getHeight() {
		return Gdx.graphics.getHeight();
	}

	protected void addButton(GuiButton button) {
		buttons.put(button.getID(), button); // Add a button
	}

	protected HashMap<Integer, GuiButton> getButtons() {
		return buttons;
	}
}
