package org.amityregion5.ZombieGame.client.screen;

import org.amityregion5.ZombieGame.ZombieGame;
import org.amityregion5.ZombieGame.client.gui.GuiButton;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.utils.Align;

/**
 *
 * @author sergeys
 *
 */
public class MainMenu extends GuiScreen {
	
	private GlyphLayout glyph = new GlyphLayout();

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
		batch.setColor(1, 1, 1, 1);
		batch.draw(titleTexture, 10, camera.viewportHeight - titleHeight-10,
				camera.viewportWidth - 20, titleHeight);
		
		float y = 15;
		glyph.setText(ZombieGame.instance.mainFont, "Version " + ZombieGame.instance.version, Color.WHITE, getWidth(), Align.left, false);
		ZombieGame.instance.mainFont.draw(batch, glyph, 10, y + glyph.height/2); y += glyph.height + 15;
		
		if (ZombieGame.instance.newestVersion != null && !ZombieGame.instance.newestVersion.equals(ZombieGame.instance.version)) {
			glyph.setText(ZombieGame.instance.bigFont, "A new version is available: " + ZombieGame.instance.newestVersion, Color.WHITE, getWidth(), Align.left, false);
			ZombieGame.instance.bigFont.draw(batch, glyph, 10, y + glyph.height/2); y += glyph.height+ 15;
		}
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
		boolean[] enabled = {true, true, true, false, true};
		for (int i = 0; i<buttons.length; i++) {
			if (buttons[i] != null) {
				addButton(new GuiButton(buttonTexture, i, buttons[i],
						10*ZombieGame.getXScalar(), 
						getHeight() - titleHeight - 10 - ZombieGame.getScaledY(10 + 50 + 60*i),
						getWidth() - ZombieGame.getScaledX(20),
						ZombieGame.getScaledY(50)).setEnabled(enabled[i]));
			}
		}
	}

	@Override
	protected void buttonClicked(int id) {
		super.buttonClicked(id);
		switch (id) {
			case 0:
				// Play Game button
				ZombieGame.instance.setScreen(new SinglePlayerMenu(this));
				break;
			case 1:
				// Play Game button
				ZombieGame.instance.setScreen(new OptionMenu(this));
				break;
			case 2:
				// Play Game button
				ZombieGame.instance.setScreen(new CreditsMenu(this));
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
