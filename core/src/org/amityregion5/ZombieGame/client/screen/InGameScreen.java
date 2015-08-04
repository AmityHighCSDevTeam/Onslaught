package org.amityregion5.ZombieGame.client.screen;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import org.amityregion5.ZombieGame.client.game.IDrawingLayer;
import org.amityregion5.ZombieGame.client.window.HotbarOverlay;
import org.amityregion5.ZombieGame.client.window.InventoryWindow;
import org.amityregion5.ZombieGame.client.window.Screen;
import org.amityregion5.ZombieGame.client.window.ShopWindow;
import org.amityregion5.ZombieGame.common.bullet.IBullet;
import org.amityregion5.ZombieGame.common.entity.EntityLantern;
import org.amityregion5.ZombieGame.common.entity.EntityPlayer;
import org.amityregion5.ZombieGame.common.entity.EntityZombie;
import org.amityregion5.ZombieGame.common.game.Difficulty;
import org.amityregion5.ZombieGame.common.game.Game;
import org.amityregion5.ZombieGame.common.game.model.IEntityModel;
import org.amityregion5.ZombieGame.common.game.model.LanternModel;
import org.amityregion5.ZombieGame.common.game.model.PlayerModel;
import org.amityregion5.ZombieGame.common.game.model.ZombieModel;

import box2dLight.ConeLight;
import box2dLight.PointLight;
import box2dLight.RayHandler;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;

/**
 *
 * @author sergeys
 *
 */
public class InGameScreen extends GuiScreen {

	private Game				game;
	private Box2DDebugRenderer	debugRenderer;
	private OrthographicCamera	camera;
	private PlayerModel			player;
	private ShapeRenderer		shapeRenderer;
	private RayHandler			rayHandler;
	private GlyphLayout glyph = new GlyphLayout();
	private Screen currentWindow;
	private List<Screen> overlays;

	// Font
	private BitmapFont			font1, font2;

	public InGameScreen(GuiScreen prevScreen, Game game) {
		super(prevScreen);

		this.game = game;

		rayHandler = new RayHandler(game.getWorld());

		shapeRenderer = new ShapeRenderer();
		
		overlays = new ArrayList<Screen>();
		
		EntityPlayer playerEntity = new EntityPlayer();
		playerEntity.setFriction(0.99f);
		playerEntity.setMass(100);
		ConeLight light = new ConeLight(rayHandler, 250, Color.WHITE.mul(1, 1,
				1, 130f/255), 15, 0, 0, 0, 30);

		player = new PlayerModel(playerEntity, game, this, 500 * (Difficulty.diffInvertNum - game.getDifficulty().getDifficultyMultiplier()));
		player.setLight(light);
		player.setCircleLight(new PointLight(rayHandler, 250, Color.WHITE.mul(1, 1,
				1, 130f/255), 3, 0, 0));
		player.setSpeed(0.05f);

		game.addEntityToWorld(player, 0, 0);
		overlays.add(new HotbarOverlay(this, player));
	}

	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(0, 0.15f, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT); // Clear screen

		game.tick(delta);

		camera.translate(player.getEntity().getBody().getWorldCenter().x
				- camera.position.x, player.getEntity().getBody().getWorldCenter().y
				- camera.position.y);
		camera.update();

		debugRenderer.render(game.getWorld(), camera.combined);

		Matrix4 oldBatchMatrix = batch.getProjectionMatrix().cpy();
		batch.setProjectionMatrix(camera.combined);
		for (IEntityModel<?> e : game.getEntities()) {
			for (IDrawingLayer s : e.getDrawingLayers()) {
				s.draw(e, batch, shapeRenderer);
			}
		}
		batch.setProjectionMatrix(oldBatchMatrix);

		rayHandler.setCombinedMatrix(camera.combined);
		rayHandler.updateAndRender();

		super.render(delta);

		shapeRenderer.setProjectionMatrix(camera.combined);
		shapeRenderer.begin(ShapeType.Line);
		for (IBullet bull : new ArrayList<IBullet>(game.getActiveBullets())) {
			if (bull.getEnd() != null) {
				Gdx.gl.glLineWidth(bull.getThickness());
				shapeRenderer.setColor(bull.getColor());

				Vector3 start = new Vector3(bull.getStart().x,
						bull.getStart().y, 0);
				Vector3 end = new Vector3(bull.getEnd().x, bull.getEnd().y, 0);

				shapeRenderer.line(start.x, start.y, end.x, end.y);
			}
			game.getActiveBullets().remove(bull);
		}
		shapeRenderer.end();
		Gdx.gl.glLineWidth(1);
		
		
		for (Screen overlay : overlays) {
			overlay.drawScreen(delta, camera);
		}

		if (currentWindow != null) {
			currentWindow.drawScreen(delta, camera);
		}
	}

	@Override
	protected void drawScreen(float delta) {
		super.drawScreen(delta);

		// font2.draw(batch, "FPS: " + Gdx.graphics.getFramesPerSecond(), 0,
		// font1.getBounds("FPS: " + Gdx.graphics.getFramesPerSecond()).height);
		glyph.setText(font1, "FPS: " + Gdx.graphics.getFramesPerSecond());
		font1.draw(batch, glyph, 0, glyph.height);

		Vector3 mouseCoord = camera.unproject(new Vector3(Gdx.input.getX(),
				Gdx.input.getY(), 0));
		player.setMousePos(new Vector2(mouseCoord.x, mouseCoord.y));
		
		//player.tick(delta);

		drawHUD();
		
		if (Gdx.input.isKeyJustPressed(Keys.L)) {
			LanternModel lantern = new LanternModel(new EntityLantern(), game);
			lantern.setLight(new PointLight(rayHandler, 300,
					LanternModel.getLIGHT_COLOR(), 10, mouseCoord.x, mouseCoord.y));
			lantern.getEntity().setFriction(0.99f);
			lantern.getEntity().setMass(10);
			
			game.addEntityToWorld(lantern, mouseCoord.x, mouseCoord.y);
		}

		if (Gdx.input.isKeyPressed(Keys.G)) {
			camera.zoom += 0.02;
		}
		if (Gdx.input.isKeyPressed(Keys.H) && camera.zoom > 0.02) {
			camera.zoom -= 0.02;
		}

		if (Gdx.input.isKeyJustPressed(Keys.P)) {
			if (currentWindow != null) {
				currentWindow.dispose();
			}
			currentWindow = new ShopWindow(this, player);
		}
		if (Gdx.input.isKeyJustPressed(Keys.I)) {
			if (currentWindow != null) {
				currentWindow.dispose();
			}
			currentWindow = new InventoryWindow(this, player);
		}

		if (Gdx.input.isKeyJustPressed(Keys.ESCAPE)) {
			if (currentWindow != null) {
				currentWindow.dispose();
				currentWindow = null;
			}
		}

		if (Gdx.input.isButtonPressed(Buttons.RIGHT)) {
			EntityZombie zom = new EntityZombie();
			zom.setMass(100);
			zom.setFriction(0.99f);
			
			ZombieModel model = new ZombieModel(zom, game);
			
			model.setAllHealth(5);
			model.setSpeed(0.03f);
			model.setRange((float) (zom.getShape().getRadius()*1.1));
			model.setDamage(5);

			game.addEntityToWorld(model, mouseCoord.x, mouseCoord.y);
		}
	}

	@Override
	public void resize(int width, int height) {
		super.resize(width, height);
	}

	@Override
	protected void setUpScreen() {
		super.setUpScreen();
	}

	@Override
	public void show() {
		super.show();

		// Create the font
		FreeTypeFontGenerator generator = new FreeTypeFontGenerator(
				Gdx.files.internal("font/Calibri.ttf"));

		FreeTypeFontParameter parameter = new FreeTypeFontParameter();
		parameter.size = 20;

		font1 = generator.generateFont(parameter);

		parameter.size = 24;
		font2 = generator.generateFont(parameter);

		generator.dispose();

		font1.setColor(1, 1, 1, 1);
		font2.setColor(0, 0, 0, 1);

		debugRenderer = new Box2DDebugRenderer(true, true, false, true, false,
				true);

		camera = new OrthographicCamera(12, 9);
	}

	private void drawHUD() {
		batch.end();

		Gdx.gl.glEnable(GL20.GL_BLEND);
		Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

		shapeRenderer.setProjectionMatrix(batch.getProjectionMatrix());

		shapeRenderer.begin(ShapeType.Filled);

		shapeRenderer.setColor(75 / 255f, 75 / 255f, 75 / 255f, 75 / 255f);
		shapeRenderer.rect(getWidth() - 400, 0, 400, 200);
		shapeRenderer.end();
		Gdx.gl.glDisable(GL20.GL_BLEND);

		batch.begin();
		font1.draw(batch, player.getCurrentWeapon().getWeapon().getName(),
				getWidth() - 390, 190);
		font1.draw(batch, player.getCurrentWeapon().getAmmoString(),
				getWidth() - 390, 170);
		font1.draw(batch,
				"$" + NumberFormat.getInstance().format(player.getMoney()),
				getWidth() - 390, 150);
	}

	@Override
	protected void buttonClicked(int id) {
		super.buttonClicked(id);
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
		batch.dispose(); // Clear memory
		font1.dispose();
		font2.dispose();
		debugRenderer.dispose();
		game.dispose();
		shapeRenderer.dispose();
		rayHandler.dispose();
		if (currentWindow != null) {
			currentWindow.dispose();
		}
	}

	public Screen getCurrentWindow() {
		return currentWindow;
	}

	public Matrix4 getScreenProjectionMatrix() {
		return batch.getProjectionMatrix();
	}
}
