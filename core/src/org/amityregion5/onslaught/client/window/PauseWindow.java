package org.amityregion5.onslaught.client.window;

import org.amityregion5.onslaught.Onslaught;
import org.amityregion5.onslaught.client.screen.InGameScreen;
import org.amityregion5.onslaught.client.screen.OptionMenu;
import org.amityregion5.onslaught.common.game.model.entity.PlayerModel;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.utils.Align;

/**
 * The pause window
 * @author sergeys
 *
 */
public class PauseWindow implements Screen {
	private ShapeRenderer	shapeRender	= new ShapeRenderer(); //The shape renderer
	private InGameScreen	screen; //The screen
	private GlyphLayout		glyph; //The glyph layout
	private SpriteBatch		batch		= new SpriteBatch(); //The sprite batch
	private PlayerModel		player; //The player
	private boolean mouseWasUp = false;

	public PauseWindow(InGameScreen screen, PlayerModel player) {
		this.screen = screen; //Set variables
		glyph = new GlyphLayout();
		this.player = player;
	}

	@Override
	public void drawScreen(float delta, Camera camera) {
		//Prepare to draw
		drawPrepare(delta);

		//Draw
		drawMain(delta);

		//Disable blending
		Gdx.gl.glDisable(GL20.GL_BLEND);
		
		mouseWasUp = Gdx.input.isTouched();
	}

	private void drawPrepare(float delta) {
		//Update projection matricies
		shapeRender.setProjectionMatrix(screen.getScreenProjectionMatrix());
		batch.setProjectionMatrix(screen.getScreenProjectionMatrix());

		//Enable blending
		Gdx.gl.glEnable(GL20.GL_BLEND);
		Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

		shapeRender.begin(ShapeType.Filled);

		// Gray the entire screen
		shapeRender.setColor(0.5f, 0.5f, 0.5f, 0.2f);
		shapeRender.rect(0, 0, screen.getWidth(), screen.getHeight());

		// Main box in the center
		shapeRender.setColor(0.3f, 0.3f, 0.3f, 0.6f);
		shapeRender.rect(screen.getWidth() / 2 - 200*Onslaught.getXScalar(), screen.getHeight() / 2 - 150*Onslaught.getYScalar(), 400*Onslaught.getXScalar(), 250*Onslaught.getYScalar());

		shapeRender.end();

		shapeRender.begin(ShapeType.Line);

		shapeRender.setColor(0.9f, 0.9f, 0.9f, 0.5f);
		// Main box border
		shapeRender.rect(screen.getWidth() / 2 - 200*Onslaught.getXScalar(), screen.getHeight() / 2 - 150*Onslaught.getYScalar(), 400*Onslaught.getXScalar(), 250*Onslaught.getYScalar());

		shapeRender.end();
	}

	private void drawMain(float delta) {
		batch.begin();

		//Draw paused text
		glyph.setText(Onslaught.instance.mainFont, "Paused", Color.WHITE, 400*Onslaught.getXScalar(), Align.center, false);
		Onslaught.instance.mainFont.draw(batch, glyph, screen.getWidth() / 2 - 200*Onslaught.getXScalar(), screen.getHeight() / 2 + glyph.height / 2 + 75*Onslaught.getYScalar());

		//Is the quit button moused over
		boolean mouseOverQuit = Gdx.input.getX() > screen.getWidth() / 2 - 200*Onslaught.getXScalar() && Gdx.input.getX() < screen.getWidth() / 2
				&& screen.getHeight() - Gdx.input.getY() > screen.getHeight() / 2 - 100*Onslaught.getYScalar() && screen.getHeight() - Gdx.input.getY() < screen.getHeight() / 2 - 50*Onslaught.getYScalar();

		//Draw the quit button
		glyph.setText(Onslaught.instance.mainFont, "Quit", (mouseOverQuit ? new Color(27 / 255f, 168 / 255f, 55 / 255f, 1f) : Color.WHITE), 180*Onslaught.getXScalar(), Align.center,
				false);
		Onslaught.instance.mainFont.draw(batch, glyph, screen.getWidth() / 2 - 190*Onslaught.getXScalar(), screen.getHeight() / 2 + glyph.height / 2 - 75*Onslaught.getYScalar());

		//If quit pressed
		if (mouseOverQuit && !Gdx.input.isTouched() && mouseWasUp) {
			//Kill the player
			player.damage(Float.POSITIVE_INFINITY, null, "QUIT BUTTON SMITES YOU");
		}

		//Is the save button moused over
		boolean mouseOverSave = Gdx.input.getX() > screen.getWidth() / 2 && Gdx.input.getX() < screen.getWidth() / 2 + 200*Onslaught.getXScalar()
				&& screen.getHeight() - Gdx.input.getY() > screen.getHeight() / 2 - 100*Onslaught.getYScalar() && screen.getHeight() - Gdx.input.getY() < screen.getHeight() / 2 - 50*Onslaught.getYScalar();

		boolean canSave = screen.getGame().canSaveGame();

		//Draw save button
		glyph.setText(Onslaught.instance.mainFont, "Save and Quit", canSave ? (mouseOverSave ? new Color(27 / 255f, 168 / 255f, 55 / 255f, 1f) : Color.WHITE) : Color.DARK_GRAY, 180*Onslaught.getXScalar(),
				Align.center, false);
		Onslaught.instance.mainFont.draw(batch, glyph, screen.getWidth() / 2 + 10*Onslaught.getXScalar(), screen.getHeight() / 2 + glyph.height / 2 - 75*Onslaught.getYScalar());

		//If save button pressed
		if (mouseOverSave && !Gdx.input.isTouched() && mouseWasUp && canSave) {
			//Open save window
			screen.setCurrentWindow(new SaveWindow(screen, player, this));
		}

		//Is the save button moused over
		boolean mouseOverSettings = Gdx.input.getX() > screen.getWidth() / 2 - 200*Onslaught.getXScalar() && Gdx.input.getX() < screen.getWidth() / 2 + 200*Onslaught.getXScalar()
				&& screen.getHeight() - Gdx.input.getY() > screen.getHeight() / 2 - 150*Onslaught.getYScalar() && screen.getHeight() - Gdx.input.getY() < screen.getHeight() / 2 - 100*Onslaught.getYScalar();
				
		//Draw save button
		glyph.setText(Onslaught.instance.mainFont, "Options", (mouseOverSettings ? new Color(27 / 255f, 168 / 255f, 55 / 255f, 1f) : Color.WHITE), 2*180*Onslaught.getXScalar(),
				Align.center, false);
		Onslaught.instance.mainFont.draw(batch, glyph, screen.getWidth() / 2 - 190*Onslaught.getXScalar(), screen.getHeight() / 2 + glyph.height / 2 - 125*Onslaught.getYScalar());

		//If save button pressed
		if (mouseOverSettings && !Gdx.input.isTouched() && mouseWasUp) {
			//Open save window
			Onslaught.instance.setScreen(new OptionMenu(screen));
		}

		batch.end();
	}

	@Override
	public void dispose() {
		//Unpause when disposed
		screen.getGame().setPaused(false);
		batch.dispose();
	}
	
	@Override
	public boolean pauseIfOpenAsWindow() {
		return true;
	}
}
