package org.amityregion5.ZombieGame.client.window;

import org.amityregion5.ZombieGame.ZombieGame;
import org.amityregion5.ZombieGame.client.screen.InGameScreen;
import org.amityregion5.ZombieGame.common.game.model.entity.PlayerModel;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.utils.Align;

public class PauseWindow implements Screen {
	private ShapeRenderer	shapeRender	= new ShapeRenderer();
	private InGameScreen	screen;
	private GlyphLayout		glyph;
	private SpriteBatch		batch		= new SpriteBatch();
	private PlayerModel		player;

	public PauseWindow(InGameScreen screen, PlayerModel player) {
		this.screen = screen;
		glyph = new GlyphLayout();
		this.player = player;
	}

	@Override
	public void drawScreen(float delta, Camera camera) {
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
		shapeRender.rect(screen.getWidth() / 2 - 200, screen.getHeight() / 2 - 100, 400, 200);

		shapeRender.end();

		shapeRender.begin(ShapeType.Line);

		shapeRender.setColor(0.9f, 0.9f, 0.9f, 0.5f);
		// Main box border
		shapeRender.rect(screen.getWidth() / 2 - 200, screen.getHeight() / 2 - 100, 400, 200);

		shapeRender.end();
	}

	private void drawMain(float delta) {
		batch.begin();

		glyph.setText(ZombieGame.instance.mainFont, "Paused", Color.WHITE, 400, Align.center, false);
		ZombieGame.instance.mainFont.draw(batch, glyph, screen.getWidth() / 2 - 200,
				screen.getHeight() / 2 + glyph.height / 2 + 75);

		boolean mouseOverQuit = Gdx.input.getX() > screen.getWidth() / 2 - 200
				&& Gdx.input.getX() < screen.getWidth() / 2
				&& screen.getHeight() - Gdx.input.getY() > screen.getHeight() / 2 - 100
				&& screen.getHeight() - Gdx.input.getY() < screen.getHeight() / 2 - 50;

		glyph.setText(ZombieGame.instance.mainFont, "Quit",
				(mouseOverQuit ? new Color(27 / 255f, 168 / 255f, 55 / 255f, 1f) : Color.WHITE), 180, Align.center,
				false);
		ZombieGame.instance.mainFont.draw(batch, glyph, screen.getWidth() / 2 - 190,
				screen.getHeight() / 2 + glyph.height / 2 - 75);

		if (mouseOverQuit && Gdx.input.isTouched()) {
			player.damage(Float.POSITIVE_INFINITY, null, "QUIT BUTTON SMITES YOU");
		}

		boolean mouseOverSave = Gdx.input.getX() > screen.getWidth() / 2
				&& Gdx.input.getX() < screen.getWidth() / 2 + 200
				&& screen.getHeight() - Gdx.input.getY() > screen.getHeight() / 2 - 100
				&& screen.getHeight() - Gdx.input.getY() < screen.getHeight() / 2 - 50;

		glyph.setText(ZombieGame.instance.mainFont, "Save and Quit",
				(mouseOverSave ? new Color(27 / 255f, 168 / 255f, 55 / 255f, 1f) : Color.WHITE), 180, Align.center,
				false);
		ZombieGame.instance.mainFont.draw(batch, glyph, screen.getWidth() / 2 + 10,
				screen.getHeight() / 2 + glyph.height / 2 - 75);

		if (mouseOverSave && Gdx.input.isTouched()) {
			screen.setCurrentWindow(new SaveWindow(screen, player, this));
		}

		batch.end();
	}

	@Override
	public void dispose() {
		screen.getGame().setPaused(false);
		batch.dispose();
	}
}
