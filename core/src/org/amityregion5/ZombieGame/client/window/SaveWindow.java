package org.amityregion5.ZombieGame.client.window;

import org.amityregion5.ZombieGame.ZombieGame;
import org.amityregion5.ZombieGame.client.Client;
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

public class SaveWindow implements Screen {
	private ShapeRenderer	shapeRender			= new ShapeRenderer();
	private InGameScreen	screen;
	private GlyphLayout		glyph;
	private SpriteBatch		batch				= new SpriteBatch();
	private PlayerModel		player;
	private PauseWindow		window;
	private InputProcessor	processor;
	private String			saveName			= "";
	private boolean			showCursor			= false;
	private float			timeUntilShowCursor	= 0;

	public SaveWindow(InGameScreen screen, PlayerModel player, PauseWindow pauseWindow) {
		this.screen = screen;
		glyph = new GlyphLayout();
		this.player = player;
		window = pauseWindow;

		processor = new InputProcessor() {
			@Override
			public boolean keyDown(int keycode) {
				return false;
			}

			@Override
			public boolean keyUp(int keycode) {
				if (keycode == Keys.BACKSPACE && saveName.length() > 0) {
					saveName = saveName.substring(0, saveName.length() - 1);
				}
				return false;
			}

			@Override
			public boolean keyTyped(char character) {
				if (Character.isLetterOrDigit(character)) {
					saveName += character;
				}
				return true;
			}

			@Override
			public boolean touchDown(int screenX, int screenY, int pointer, int button) {
				return false;
			}

			@Override
			public boolean touchUp(int screenX, int screenY, int pointer, int button) {
				return false;
			}

			@Override
			public boolean touchDragged(int screenX, int screenY, int pointer) {
				return false;
			}

			@Override
			public boolean mouseMoved(int screenX, int screenY) {
				return false;
			}

			@Override
			public boolean scrolled(int amount) {
				return false;
			}
		};

		Client.inputMultiplexer.addProcessor(processor);
	}

	@Override
	public void drawScreen(float delta, Camera camera) {

		if (timeUntilShowCursor <= 0) {
			timeUntilShowCursor = 0.4f;
			showCursor = !showCursor;
		}
		timeUntilShowCursor -= delta;

		drawPrepare(delta);

		drawMain(delta);

		Gdx.gl.glDisable(GL20.GL_BLEND);
	}

	private void drawPrepare(float delta) {
		shapeRender.setProjectionMatrix(screen.getScreenProjectionMatrix());
		batch.setProjectionMatrix(screen.getScreenProjectionMatrix());

		Gdx.gl.glEnable(GL20.GL_BLEND);
		Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

		shapeRender.begin(ShapeType.Filled);

		// Gray the entire screen
		shapeRender.setColor(0.5f, 0.5f, 0.5f, 0.2f);
		shapeRender.rect(0, 0, screen.getWidth(), screen.getHeight());

		// Main box in the center
		shapeRender.setColor(0.3f, 0.3f, 0.3f, 0.6f);
		shapeRender.rect(screen.getWidth() / 2 - 300, screen.getHeight() / 2 - 150, 600, 300);

		shapeRender.end();

		shapeRender.begin(ShapeType.Line);

		shapeRender.setColor(0.9f, 0.9f, 0.9f, 0.5f);
		// Main box border
		shapeRender.rect(screen.getWidth() / 2 - 300, screen.getHeight() / 2 - 150, 600, 300);

		shapeRender.end();
	}

	private void drawMain(float delta) {
		batch.begin();

		glyph.setText(ZombieGame.instance.mainFont, "Save", Color.WHITE, 600, Align.center, false);
		ZombieGame.instance.mainFont.draw(batch, glyph, screen.getWidth() / 2 - 300, screen.getHeight() / 2 + 150 - glyph.height - 10);

		glyph.setText(ZombieGame.instance.mainFont, "Name: " + saveName + (showCursor ? "|" : ""), Color.WHITE, 500, Align.left, false);
		ZombieGame.instance.mainFont.draw(batch, glyph, screen.getWidth() / 2 - 250, screen.getHeight() / 2);

		boolean mouseOverBack = Gdx.input.getX() > screen.getWidth() / 2 - 300 && Gdx.input.getX() < screen.getWidth() / 2
				&& screen.getHeight() - Gdx.input.getY() > screen.getHeight() / 2 - 150
				&& screen.getHeight() - Gdx.input.getY() < screen.getHeight() / 2 - 150 + 50;

		glyph.setText(ZombieGame.instance.mainFont, "Back", (mouseOverBack ? new Color(27 / 255f, 168 / 255f, 55 / 255f, 1f) : Color.WHITE), 280, Align.center,
				false);
		ZombieGame.instance.mainFont.draw(batch, glyph, screen.getWidth() / 2 - 290, screen.getHeight() / 2 - 150 + 25);

		if (mouseOverBack && Gdx.input.isTouched()) {
			screen.setCurrentWindow(window);
			dispose();
			return;
		}

		boolean mouseOverSave = Gdx.input.getX() > screen.getWidth() / 2 && Gdx.input.getX() < screen.getWidth() / 2 + 300
				&& screen.getHeight() - Gdx.input.getY() > screen.getHeight() / 2 - 150
				&& screen.getHeight() - Gdx.input.getY() < screen.getHeight() / 2 - 150 + 50;

		glyph.setText(ZombieGame.instance.mainFont, "Save and Quit", (mouseOverSave ? new Color(27 / 255f, 168 / 255f, 55 / 255f, 1f) : Color.WHITE), 280,
				Align.center, false);
		ZombieGame.instance.mainFont.draw(batch, glyph, screen.getWidth() / 2 + 10, screen.getHeight() / 2 - 150 + 25);

		if (mouseOverSave && Gdx.input.isTouched()) {
			screen.getGame().saveToFile(saveName);
			player.damage(Float.POSITIVE_INFINITY, null, "QUIT BUTTON SMITES YOU");
		}

		batch.end();
	}

	@Override
	public void dispose() {
		batch.dispose();
		Client.inputMultiplexer.removeProcessor(processor);
	}
}
