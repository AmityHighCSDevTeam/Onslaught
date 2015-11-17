package org.amityregion5.ZombieGame.client.screen;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.amityregion5.ZombieGame.ZombieGame;
import org.amityregion5.ZombieGame.client.asset.SoundPlayingData;
import org.amityregion5.ZombieGame.client.asset.SoundRegistry;
import org.amityregion5.ZombieGame.client.asset.TextureRegistry;
import org.amityregion5.ZombieGame.client.game.IDrawingLayer;
import org.amityregion5.ZombieGame.client.window.HUDOverlay;
import org.amityregion5.ZombieGame.client.window.InventoryWindow;
import org.amityregion5.ZombieGame.client.window.PauseWindow;
import org.amityregion5.ZombieGame.client.window.Screen;
import org.amityregion5.ZombieGame.client.window.ShopWindow;
import org.amityregion5.ZombieGame.common.bullet.IBullet;
import org.amityregion5.ZombieGame.common.entity.EntityLantern;
import org.amityregion5.ZombieGame.common.entity.EntityPlayer;
import org.amityregion5.ZombieGame.common.entity.EntityZombie;
import org.amityregion5.ZombieGame.common.game.Game;
import org.amityregion5.ZombieGame.common.game.model.IEntityModel;
import org.amityregion5.ZombieGame.common.game.model.IParticle;
import org.amityregion5.ZombieGame.common.game.model.entity.LanternModel;
import org.amityregion5.ZombieGame.common.game.model.entity.PlayerModel;
import org.amityregion5.ZombieGame.common.game.model.entity.ZombieModel;
import org.amityregion5.ZombieGame.common.game.model.particle.HealthPackParticle;
import org.amityregion5.ZombieGame.common.helper.MathHelper;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
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

import box2dLight.ConeLight;
import box2dLight.Light;
import box2dLight.PointLight;
import box2dLight.RayHandler;

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
	private boolean renderHitboxes = false;

	// Font
	private BitmapFont			font1, font2;

	public InGameScreen(GuiScreen prevScreen, Game game, boolean isNewGame) {
		super(prevScreen);

		this.game = game;

		Light.setGlobalContactFilter((short)1, (short)0, (short)1);

		rayHandler = new RayHandler(game.getWorld());
		RayHandler.useDiffuseLight(true);

		game.setLighting(rayHandler);

		shapeRenderer = new ShapeRenderer();

		overlays = new ArrayList<Screen>();

		ConeLight light = new ConeLight(rayHandler, 250, Color.WHITE.cpy().mul(1, 1,
				1, 130f/255), 15, 0, 0, 0, 30);
		if (isNewGame) {
			EntityPlayer playerEntity = new EntityPlayer();
			playerEntity.setFriction(0.99f);
			playerEntity.setMass(100);

			player = new PlayerModel(playerEntity, game, this, game.getDifficulty().getStartingMoney(), "*/Players/**.png");
		} else {
			player = game.getSingleplayerPlayer();
			player.setScreen(this);
		}

		player.setLight(light);
		player.setCircleLight(new PointLight(rayHandler, 250, Color.WHITE.cpy().mul(1, 1,
				1, 130f/255), 3, 0, 0));
		player.setSpeed(0.05f);

		if (isNewGame) {
			game.addEntityToWorld(player, 0, 0);
		}
		overlays.add(new HUDOverlay(this, player));
	}

	@Override
	public void render(float delta) {
		//Gdx.gl.glClearColor(0.5f, 1f, 0.5f, 1);
		Gdx.gl.glClearColor(1f, 1f, 1f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT); // Clear screen
		//Gdx.gl.glClearColor(0f, 1f, 0f, 0.1f);
		//Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT); // Clear screen

		game.tick(delta);

		camera.translate(player.getEntity().getBody().getWorldCenter().x
				- camera.position.x, player.getEntity().getBody().getWorldCenter().y
				- camera.position.y);

		camera.translate((float)(player.getScreenVibrate()*game.getRandom().nextDouble() - player.getScreenVibrate()/2), 
				(float)(player.getScreenVibrate()*game.getRandom().nextDouble() - player.getScreenVibrate()/2));

		camera.update();

		Matrix4 oldBatchMatrix = batch.getProjectionMatrix().cpy();

		Texture tex = TextureRegistry.getTexturesFor("backgroundTile").get(0);
		float wM = 10.24f;
		float hM = 10.24f;

		batch.setProjectionMatrix(camera.combined);
		shapeRenderer.setProjectionMatrix(camera.combined);

		float tileX = wM;
		float tileY = hM;

		Vector2 posStart = new Vector2(camera.position.x - camera.viewportWidth*camera.zoom, camera.position.y - camera.viewportHeight*camera.zoom*camera.zoom);
		Vector2 posEnd = new Vector2(camera.position.x + camera.viewportWidth*camera.zoom, camera.position.y + camera.viewportHeight*camera.zoom*camera.zoom);

		int startTileX = (int)((posStart.x)/tileX);
		if (posStart.x < 0)
			startTileX--;
		int startTileY = (int)((posStart.y)/tileY);
		if (posStart.y < 0)
			startTileY--;

		startTileX--;
		startTileY--;

		int endTileX = (int)((posEnd.x)/tileX);
		int endTileY = (int)((posEnd.y)/tileY);

		endTileX++;
		endTileY++;

		batch.begin();
		Color c = batch.getColor();
		batch.setColor(1, 1, 1, 1);
		for (int x = startTileX; x<endTileX; x++) {
			for (int y = startTileY; y<endTileY; y++) {
				batch.draw(tex, x*tileX, y*tileY, tileX, tileY);
			}
		}
		batch.end();
		batch.setColor(c);


		batch.setProjectionMatrix(camera.combined);

		for (IParticle p : game.getParticles()) {
			for (IDrawingLayer s : p.getBackDrawingLayers()) {
				s.draw(p, batch, shapeRenderer);
			}
		}
		for (IEntityModel<?> e : game.getEntities()) {
			for (IDrawingLayer s : e.getDrawingLayers()) {
				s.draw(e, batch, shapeRenderer);
			}
		}
		for (IParticle p : game.getParticles()) {
			for (IDrawingLayer s : p.getFrontDrawingLayers()) {
				s.draw(p, batch, shapeRenderer);
			}
		}
		batch.setProjectionMatrix(oldBatchMatrix);

		if (game.isLightingEnabled()) {
			rayHandler.setCombinedMatrix(camera);
			rayHandler.updateAndRender();
		}

		super.render(delta);

		shapeRenderer.setProjectionMatrix(camera.combined);
		shapeRenderer.begin(ShapeType.Line);
		for (IBullet bull : new ArrayList<IBullet>(game.getActiveBullets())) {
			if (bull.getEnd() != null && bull.doDraw()) {
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

		if (game.isCheatMode()) {
			if (renderHitboxes) {
				debugRenderer.render(game.getWorld(), camera.combined);
			}
		}

		for (Screen overlay : overlays) {
			overlay.drawScreen(delta, camera);
		}

		if (currentWindow != null) {
			currentWindow.drawScreen(delta, camera);
		}
		
		batch.setProjectionMatrix(camera.combined);
		for (IParticle p : game.getParticles()) {
			for (IDrawingLayer s : p.getMaxDrawingLayers()) {
				s.draw(p, batch, shapeRenderer);
			}
		}
		batch.setProjectionMatrix(oldBatchMatrix);

		Iterator<SoundPlayingData> iterator = player.getSoundsToPlay().listIterator();
		while (iterator.hasNext()) {
			SoundPlayingData soundData = iterator.next();

			Sound sound = SoundRegistry.getSoundsFor(soundData.getSound()).get(0);

			sound.play((float)MathHelper.clamp(0, 1, soundData.getVolume()*ZombieGame.instance.settings.getMasterVolume()), (float)MathHelper.clamp(0.5, 2, soundData.getPitch()), 0);

			iterator.remove();
		}

		if (!game.isGameRunning()) {
			dispose();
			ZombieGame.instance.setScreen(prevScreen);			
		}
	}

	@Override
	protected void drawScreen(float delta) {
		super.drawScreen(delta);

		// font2.draw(batch, "FPS: " + Gdx.graphics.getFramesPerSecond(), 0,
		// font1.getBounds("FPS: " + Gdx.graphics.getFramesPerSecond()).height);
		glyph.setText(font1, "FPS: " + Gdx.graphics.getFramesPerSecond());
		font1.draw(batch, glyph, 0, glyph.height);

		float y = glyph.height + 5;

		glyph.setText(font1, "Version " + ZombieGame.instance.version);
		font1.draw(batch, glyph, 0, y + glyph.height);

		Vector3 mouseCoord = camera.unproject(new Vector3(Gdx.input.getX(),
				Gdx.input.getY(), 0));
		player.setMousePos(new Vector2(mouseCoord.x, mouseCoord.y));

		//player.tick(delta);

		if (game.isCheatMode()) {
			if (Gdx.input.isKeyJustPressed(Keys.L)) {
				LanternModel lantern = new LanternModel(new EntityLantern(), game, LanternModel.getLIGHT_COLOR(), "Core/Entity/Lantern/0.png", "Lantern_0");
				lantern.setLight(new PointLight(rayHandler, 300,
						lantern.getColor(), 10, mouseCoord.x, mouseCoord.y));
				lantern.getEntity().setFriction(0.99f);
				lantern.getEntity().setMass(10);

				game.addEntityToWorld(lantern, mouseCoord.x, mouseCoord.y);
			}
			if (Gdx.input.isKeyJustPressed(Keys.F3)) {
				renderHitboxes = !renderHitboxes;
			}
			if (Gdx.input.isKeyPressed(Keys.G)) {
				camera.zoom += 0.02;
			}
			if (Gdx.input.isKeyPressed(Keys.H) && camera.zoom > 0.02) {
				camera.zoom -= 0.02;
			}

			if (Gdx.input.isKeyJustPressed(Keys.E)) {
				game.makeExplosion(new Vector2(mouseCoord.x, mouseCoord.y), 50, player);
			}

			if (Gdx.input.isKeyJustPressed(Keys.R)) {
				game.addParticleToWorld(new HealthPackParticle(mouseCoord.x, mouseCoord.y, game));
			}

			if (Gdx.input.isKeyJustPressed(Keys.PERIOD)) {
				player.setMoney(player.getMoney()+1000);
			}

			if (Gdx.input.isKeyJustPressed(Keys.COMMA)) {
				player.setMoney(Math.max(0,player.getMoney()-1000));
			}

			if (Gdx.input.isButtonPressed(Buttons.RIGHT)) {
				EntityZombie zom = new EntityZombie(0.15f);
				zom.setMass(100);
				zom.setFriction(0.99f);

				ZombieModel model = new ZombieModel(zom, game,1);

				model.setAllHealth(100);
				model.setSpeed(0.03f);
				model.setRange((float) (zom.getShape().getRadius()*1.1));
				model.setDamage(5);

				game.addEntityToWorld(model, mouseCoord.x, mouseCoord.y);
			}
		}

		if (currentWindow == null && ZombieGame.instance.settings.getInput("Shop_Window").isJustDown()) {
			if (currentWindow != null) {
				currentWindow.dispose();
			}
			currentWindow = new ShopWindow(this, player);
		}
		if (currentWindow == null && ZombieGame.instance.settings.getInput("Inventory_Window").isJustDown()) {
			if (currentWindow != null) {
				currentWindow.dispose();
			}
			currentWindow = new InventoryWindow(this, player);
		}

		if (ZombieGame.instance.settings.getInput("Close_Window").isJustDown()) {
			if (currentWindow != null) {
				game.setPaused(false);
				currentWindow.dispose();
				currentWindow = null;
			} else {
				if (game.isSinglePlayer()) {
					game.setPaused(true);
					currentWindow = new PauseWindow(this, player);
				}
			}
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
		FreeTypeFontGenerator generator = ZombieGame.instance.fontGenerator;

		FreeTypeFontParameter parameter = new FreeTypeFontParameter();
		parameter.size = 16;

		font1 = generator.generateFont(parameter);

		parameter.size = 20;
		parameter.borderWidth = 1;
		parameter.borderStraight = true;
		parameter.borderColor = new Color(0,0,0,1);
		font2 = generator.generateFont(parameter);

		font1.setColor(1, 1, 1, 1);
		font2.setColor(1, 1, 1, 1);

		debugRenderer = new Box2DDebugRenderer(true, true, false, true, false,
				true);

		camera = new OrthographicCamera(12, 9);
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
		if (currentWindow != null) {
			currentWindow.dispose();
		}
		batch.dispose(); // Clear memory
		font1.dispose();
		font2.dispose();
		debugRenderer.dispose();
		game.dispose();
		shapeRenderer.dispose();
		rayHandler.dispose();
	}

	public Screen getCurrentWindow() {
		return currentWindow;
	}

	public Matrix4 getScreenProjectionMatrix() {
		return batch.getProjectionMatrix();
	}

	public BitmapFont getFont1() {
		return font1;
	}

	public BitmapFont getFont2() {
		return font2;
	}

	public Game getGame() {
		return game;
	}

	public void setCurrentWindow(Screen currentWindow) {
		this.currentWindow = currentWindow;
	}
}
