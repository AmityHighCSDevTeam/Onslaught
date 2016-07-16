package org.amityregion5.onslaught.client.screen;

import java.awt.geom.Rectangle2D;
import java.util.List;

import org.amityregion5.onslaught.Onslaught;
import org.amityregion5.onslaught.client.Client;
import org.amityregion5.onslaught.client.gui.GuiRectangle;
import org.amityregion5.onslaught.client.music.MusicHandler;
import org.amityregion5.onslaught.client.settings.Settings;
import org.amityregion5.onslaught.common.game.difficulty.Difficulty;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.utils.Align;

/**
 * The credits menu
 * @author sergeys
 */
public class ScoreMenu extends GuiScreen {

	private GlyphLayout glyph = new GlyphLayout();
	
	private Difficulty diff;
	private double score;

	public ScoreMenu(GuiScreen prevScreen, Difficulty diff, double score) {
		super(prevScreen);
		this.diff = diff;
		this.score = score;
		
		Onslaught.instance.settings.addScore(diff, score);
		Settings.save(Onslaught.instance.settings);
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

		// Draw name of screen
		Onslaught.instance.bigFont.draw(batch, "You died.", 10, getHeight() - 45, getWidth() - 20, Align.center, false);

		float x = 50*Onslaught.getXScalar();
		float y = getHeight() - 150*Onslaught.getYScalar();
		float w = getWidth() - 100*Onslaught.getXScalar();
		float extraH = 20*Onslaught.getYScalar();
		
		glyph.setText(Onslaught.instance.mainFont, "You got a score of " + ((int)(score*100))/100f + " on " + diff.getHumanName() +"\n\nHere is the top ten list for this difficulty:", new Color(1, 1, 1, 1), w, Align.center, false);
		Onslaught.instance.mainFont.draw(batch, glyph, x, y + glyph.height / 2);
		y -= glyph.height + extraH;
		
		List<Double> scores = Onslaught.instance.settings.getTop10ScoresForDiff(diff);
		for (int i=0; i<scores.size(); i++) {
			int place = i+1;
			
			glyph.setText(Onslaught.instance.mainFont, place + ": " + ((int)(scores.get(i)*100))/100f, (scores.get(i) == score ? Client.greenColor : new Color(1, 1, 1, 1)), w, Align.left, false);
			Onslaught.instance.mainFont.draw(batch, glyph, x, y + glyph.height / 2);
			y -= glyph.height + extraH;
		}
		
		if (Onslaught.instance.settings.getInput("Close_Window").isJustDown()) {
			Onslaught.instance.setScreenAndDispose(prevScreen);
		}
	}

	@Override
	public void resize(int width, int height) {
		super.resize(width, height);
	}

	@Override
	protected void setUpScreen() {
		super.setUpScreen();

		// Register buttons
		addElement(new GuiRectangle(()->new Rectangle2D.Float(10*Onslaught.getXScalar(), 10*Onslaught.getXScalar(), getWidth() - 20*Onslaught.getXScalar(), 50*Onslaught.getXScalar()),
				"Return", (r)->{
					Onslaught.instance.setScreenAndDispose(prevScreen);
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
	}
}
