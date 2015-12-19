package org.amityregion5.ZombieGame.client.screen;

import java.util.ArrayList;
import java.util.List;

import org.amityregion5.ZombieGame.client.gui.GuiElement;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;

/**
 * An base implementation of the screen
 * @author sergeys
 */
public abstract class GuiScreen implements Screen {

	// The batch for drawing the screen
	protected SpriteBatch				batch;
	protected ShapeRenderer				shape;
	// Last locations that the mouse was down
	private float						lastMouseX, lastMouseY;
	// Was the mouse down?
	private boolean						lastMouseDown	= false;
	// The buttons
	private List<GuiElement>	guiElements = new ArrayList<GuiElement>();
	// The screen that we came from
	protected GuiScreen					prevScreen;
	// The camera
	protected OrthographicCamera		camera;

	private boolean disposed = false; //Has this been disposed

	/**
	 * @param prevScreen
	 *            the screen that we came from
	 */
	public GuiScreen(GuiScreen prevScreen) {
		this.prevScreen = prevScreen;
	}

	@Override
	public void render(float delta) {
		if (disposed) {
			return;
		}
		//Set camera stuffs
		camera.setToOrtho(false);
		camera.update();
		batch.setProjectionMatrix(camera.combined);
		shape.setProjectionMatrix(camera.combined);

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
	}

	@Override
	public void show() {
		batch = new SpriteBatch(); // Create a new sprite batch
		shape = new ShapeRenderer();
		camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		
		setUpScreen();
	}

	@Override
	public void hide() {
		for (GuiElement b : guiElements) {
			b.dispose(); // Dispose all of the buttons
		}
		guiElements.clear();
	}

	@Override
	public void pause() {}

	@Override
	public void resume() {}

	@Override
	public void dispose() {
		disposed = true;
		for (GuiElement b : guiElements) {
			b.dispose(); // Dispose all of the buttons
		}
		batch.dispose();
		shape.dispose();
	}

	protected void setUpScreen() {
	}

	/**
	 * On mouse down
	 */
	protected void mouseDown(float x, float y) {}

	/**
	 * On mouse up
	 */
	protected void mouseUp(float x, float y) {}

	protected void drawScreen(float delta) {
		for (int i = 0; i<guiElements.size(); i++) {
			guiElements.get(i).draw(batch, shape); // Draw the buttons
		}
	}

	public int getWidth() {
		return Gdx.graphics.getWidth();
	}

	public int getHeight() {
		return Gdx.graphics.getHeight();
	}

	protected void addElement(GuiElement e) {
		guiElements.add(e); // Add a button
	}

	protected List<GuiElement> getGuiElements() {
		return guiElements;
	}
}
