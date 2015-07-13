package org.amityregion5.ZombieGame.client.screen;

import org.amityregion5.ZombieGame.ZombieGame;
import org.amityregion5.ZombieGame.client.gui.GuiButton;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;

/**
 *
 * @author sergeys
 *
 */
public class MainMenu extends GuiScreen {

	public MainMenu() {
		super(null);
	}

	// Title image
	private Texture	titleTexture;
	// Title position
	private int		titleHeight;

	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(50f / 255f, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT); // Clear screen

		super.render(delta);
	}

	@Override
	protected void drawScreen(float delta) {
		super.drawScreen(delta);

		// Draw picture
		batch.draw(titleTexture, 10, getHeight() - titleHeight - 10,
				getWidth() - 20, titleHeight);
	}

	@Override
	public void resize(int width, int height) {
		// Compute title position
		titleHeight = (int) Math.round((double) titleTexture.getHeight()
				/ (double) titleTexture.getWidth() * (getWidth() - 20));

		super.resize(width, height);
	}

	@Override
	public void show() {
		super.show();
		// Initialize the title texture
		titleTexture = new Texture(
				Gdx.files.internal("images/ZombieGameTitle.png"));

	}

	@Override
	protected void setUpScreen() {
		super.setUpScreen();

		// Get the button texture
		Texture buttonTexture = ZombieGame.instance.buttonTexture;

		// Add all of the buttons
		addButton(new GuiButton(buttonTexture, 0, "Play Game", 10, getHeight()
				- titleHeight - 10 - 50, getWidth() - 20, 50));
		addButton(new GuiButton(buttonTexture, 1, "Options", 10, getHeight()
				- titleHeight - 10 - 50 - 60, getWidth() - 20, 50)
				.setEnabled(false));
		addButton(new GuiButton(buttonTexture, 2, "Credits", 10, getHeight()
				- titleHeight - 10 - 50 - 60 - 60, getWidth() - 20, 50)
				.setEnabled(false));
		addButton(new GuiButton(buttonTexture, 4, "Quit", 10, getHeight()
				- titleHeight - 10 - 50 - 60 - 60 - 60 - 60, getWidth() - 20, 50));
	}

	@Override
	protected void buttonClicked(int id) {
		super.buttonClicked(id);
		switch (id) {
			case 0:
				// Play Game button
				ZombieGame.instance.setScreen(new PlayGameMenu(this));
				break;
			case 4:
				// Quit button
				Gdx.app.exit();
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
		batch.dispose(); // Clear up memory
		titleTexture.dispose();
	}
}
