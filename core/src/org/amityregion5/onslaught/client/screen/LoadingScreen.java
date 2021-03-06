package org.amityregion5.onslaught.client.screen;

import org.amityregion5.onslaught.Onslaught;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.badlogic.gdx.utils.Align;

/**
 * The file representing the loading screen
 * @author sergeys
 */
public class LoadingScreen implements Screen {

	private SpriteBatch	batch;
	private BitmapFont	calibri30;
	private int			dots	= 0;
	private float		dCount	= 0;

	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		batch.begin();

		//Draw loading then dots
		calibri30.draw(batch, "Loading" + (dots > 0 ? "." : "") + (dots > 1 ? "." : "") + (dots > 2 ? "." : ""), 10, 400, 1180, Align.center, false);

		if (dCount > 0.5) {
			dots++;
			dCount = 0f;
		}

		dCount += delta;

		if (dots > 3) {
			dots = 0;
		}

		batch.end();
	}

	@Override
	public void resize(int width, int height) {}

	@Override
	public void show() {
		//Create the font
		FreeTypeFontGenerator generator = Onslaught.instance.fontGenerator;
		boolean disposeGenerator = false;

		if (generator == null) {
			generator = new FreeTypeFontGenerator(Gdx.files.internal("font/Helvetica.ttf"));
			disposeGenerator = true;
		} else {
			generator = Onslaught.instance.fontGenerator;
		}

		FreeTypeFontParameter parameter = new FreeTypeFontParameter();
		parameter.size = (26);

		calibri30 = generator.generateFont(parameter);

		calibri30.setColor(1, 1, 1, 1);

		batch = new SpriteBatch();

		if (disposeGenerator) {
			generator.dispose();
		}
	}

	@Override
	public void hide() {}

	@Override
	public void pause() {}

	@Override
	public void resume() {}

	@Override
	public void dispose() {
		batch.dispose();
		calibri30.dispose();
	}
}
