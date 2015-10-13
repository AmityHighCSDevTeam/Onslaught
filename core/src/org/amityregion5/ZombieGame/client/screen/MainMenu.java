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
	private float		titleHeight;

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
		batch.draw(titleTexture, 10, camera.viewportHeight - titleHeight-10,
				camera.viewportWidth - 20, titleHeight);
	}

	@Override
	public void resize(int width, int height) {
		// Compute title position
		titleHeight = ZombieGame.getScaledY(titleTexture.getHeight());//(float) ((double) titleTexture.getHeight() / (double) titleTexture.getWidth() * (getWidth() - 20));

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
		String[] buttons = {"Play Game", "Options", "Credits", null, "Quit"};
		boolean[] enabled = {true, false, false, false, true};
		for (int i = 0; i<buttons.length; i++) {
			if (buttons[i] != null) {
				addButton(new GuiButton(buttonTexture, i, buttons[i],
						10*ZombieGame.getXScalar(), 
						getHeight() - titleHeight - 10 - ZombieGame.getScaledY(10 + 50 + 60*i),
						getWidth() - ZombieGame.getScaledX(20),
						ZombieGame.getScaledY(50)).setEnabled(enabled[i]));
			}
		}
		/*
		addButton(new GuiButton(buttonTexture, 0, "Play Game",
				ZombieGame.getScaledX(10), 
				ZombieGame.getScaledY(getHeight() - titleHeight - 10 - 50 - 60*0),
				ZombieGame.getScaledX(getWidth() - 20),
				ZombieGame.getScaledY(50)));
		addButton(new GuiButton(buttonTexture, 0, "Options",
				ZombieGame.getScaledX(10), 
				ZombieGame.getScaledY(getHeight() - titleHeight - 10 - 50 - 60*1),
				ZombieGame.getScaledX(getWidth() - 20),
				ZombieGame.getScaledY(50)).setEnabled(false));
		addButton(new GuiButton(buttonTexture, 0, "Credits",
				ZombieGame.getScaledX(10), 
				ZombieGame.getScaledY(getHeight() - titleHeight - 10 - 50),
				ZombieGame.getScaledX(getWidth() - 20),
				ZombieGame.getScaledY(50)).setEnabled(false));
		addButton(new GuiButton(buttonTexture, 0, "Quit",
				ZombieGame.getScaledX(10), 
				ZombieGame.getScaledY(getHeight() - titleHeight - 10 - 50),
				ZombieGame.getScaledX(getWidth() - 20),
				ZombieGame.getScaledY(50)));
				*/
		
		/*
		addButton(new GuiButton(buttonTexture, 1, "Options", 10, getHeight()
				- titleHeight - 10 - 50*ZombieGame.getYScalar() - 1*60*ZombieGame.getYScalar(), getWidth() - 20, 50*ZombieGame.getYScalar())
				.setEnabled(false));
		addButton(new GuiButton(buttonTexture, 2, "Credits", 10, getHeight()
				- titleHeight - 10 - 50*ZombieGame.getYScalar() - 2*60*ZombieGame.getYScalar(), getWidth() - 20, 50*ZombieGame.getYScalar())
				.setEnabled(false));
		addButton(new GuiButton(buttonTexture, 4, "Quit", 10, getHeight()
				- titleHeight - 10 - 50*ZombieGame.getYScalar() - 4*60*ZombieGame.getYScalar(), getWidth() - 20, 50*ZombieGame.getYScalar()));
				*/
		
		/*
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
		 */
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
