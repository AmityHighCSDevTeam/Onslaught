package org.amityregion5.ZombieGame.client.screen;

import java.awt.geom.Rectangle2D;

import org.amityregion5.ZombieGame.ZombieGame;
import org.amityregion5.ZombieGame.client.gui.GuiRectangle;
import org.amityregion5.ZombieGame.client.music.MusicHandler;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.utils.Align;

/**
 * The file representing the Main Menu
 * @author sergeys
 */
public class MainMenu extends GuiScreen {

	private GlyphLayout		glyph				= new GlyphLayout();
	private int				newerVersionMode	= 0;

	public MainMenu() {
		super(null);
	}

	// Title image
	private Texture	titleTexture;
	// Title position
	private float	titleHeight, titleWidth;

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

		batch.draw(titleTexture, camera.viewportWidth/2 - titleWidth/2, camera.viewportHeight - titleHeight - 10, titleWidth, titleHeight);

		float y = 15;
		//Draw version
		glyph.setText(ZombieGame.instance.mainFont, "Version " + ZombieGame.instance.version, Color.WHITE, getWidth(), Align.left, false);
		ZombieGame.instance.mainFont.draw(batch, glyph, 10, 10*ZombieGame.getYScalar() + y + glyph.height / 2);
		y += glyph.height + 15;

		//If a newer version available set mode to 1
		if (newerVersionMode == 0 && ZombieGame.instance.newestVersion != null && !ZombieGame.instance.newestVersion.equals(ZombieGame.instance.version)) {
			newerVersionMode = 1;
		}

		//If mode is 1
		if (newerVersionMode == 1) {
			batch.end();

			float w = 400*ZombieGame.getXScalar();
			float h = 300*ZombieGame.getYScalar();
			float x = getWidth() / 2 - w / 2;
			float y1 = getHeight() / 2 - h / 2;

			shape.begin(ShapeType.Filled);

			//Fill red box
			shape.setColor(200f / 255f, 0, 0, 1);
			shape.rect(x, y1, w, h);

			shape.end();

			shape.begin(ShapeType.Line);

			//Draw whiteish border
			shape.setColor(0.9f, 0.9f, 0.9f, 0.5f);
			shape.rect(x, y1, w, h);

			shape.end();

			batch.begin();

			//Write that new version is available
			glyph.setText(ZombieGame.instance.mainFont, "A new version is available: " + ZombieGame.instance.newestVersion + "\nClick to close", Color.WHITE,
					w - 20, Align.center, false);
			ZombieGame.instance.mainFont.draw(batch, glyph, x, y1 + (h + glyph.height) / 2);

			//If clicked disable the window
			if (Gdx.input.isTouched() && Gdx.input.getX() > x && Gdx.input.getX() < x + w && Gdx.graphics.getHeight() - Gdx.input.getY() > y1
					&& Gdx.graphics.getHeight() - Gdx.input.getY() < y1 + h) {
				newerVersionMode = 2;
			}
		}
	}

	@Override
	public void resize(int width, int height) {
		// Compute title position

		titleWidth = titleHeight/titleTexture.getHeight()*titleTexture.getWidth();
		titleHeight = ZombieGame.getScaledY(titleTexture.getHeight());

		if (titleWidth > getWidth()) {
			titleWidth = getWidth();
			titleHeight = titleWidth/titleTexture.getWidth()*titleTexture.getHeight();
		}

		super.resize(width, height);
	}

	@Override
	public void show() {
		super.show();
		// Initialize the title texture
		titleTexture = new Texture(Gdx.files.internal("images/ZombieGameTitle.png"));

		titleWidth = titleHeight/titleTexture.getHeight()*titleTexture.getWidth();
		titleHeight = ZombieGame.getScaledY(titleTexture.getHeight());

		if (titleWidth > getWidth()) {
			titleWidth = getWidth();
			titleHeight = titleWidth/titleTexture.getWidth()*titleTexture.getHeight();
		}

		MusicHandler.setMusicPlaying(MusicHandler.menuMusic);
	}

	@Override
	protected void setUpScreen() {
		super.setUpScreen();

		addElement(new GuiRectangle(()->new Rectangle2D.Float(10 * ZombieGame.getXScalar(),getHeight() - titleHeight - 10 - ZombieGame.getScaledY(10 + 50 + 60 * 0),
				getWidth() - ZombieGame.getScaledX(20),ZombieGame.getScaledY(50)),
				"Play Game", (r)->{
					if (newerVersionMode != 1) {
						ZombieGame.instance.setScreen(new PlayGameMenu(this));
					}
				}));
		addElement(new GuiRectangle(()->new Rectangle2D.Float(10 * ZombieGame.getXScalar(),getHeight() - titleHeight - 10 - ZombieGame.getScaledY(10 + 50 + 60 * 1),
				getWidth() - ZombieGame.getScaledX(20),ZombieGame.getScaledY(50)),
				"Options", (r)->{
					if (newerVersionMode != 1) {
						ZombieGame.instance.setScreen(new OptionMenu(this));
					}
				}));
		addElement(new GuiRectangle(()->new Rectangle2D.Float(10 * ZombieGame.getXScalar(),getHeight() - titleHeight - 10 - ZombieGame.getScaledY(10 + 50 + 60 * 2),
				getWidth() - ZombieGame.getScaledX(20),ZombieGame.getScaledY(50)),
				"Credits", (r)->{
					if (newerVersionMode != 1) {
						ZombieGame.instance.setScreen(new CreditsMenu(this));
					}
				}));
		addElement(new GuiRectangle(()->new Rectangle2D.Float(10 * ZombieGame.getXScalar(),getHeight() - titleHeight - 10 - ZombieGame.getScaledY(10 + 50 + 60 * 4),
				getWidth() - ZombieGame.getScaledX(20),ZombieGame.getScaledY(50)),
				"Quit", (r)->{
					if (newerVersionMode != 1) {
						Gdx.app.exit();
					}
				}));
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
		titleTexture.dispose();
	}
}
