package org.amityregion5.onslaught.client.screen;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.amityregion5.onslaught.Onslaught;
import org.amityregion5.onslaught.client.asset.SoundPlayingData;
import org.amityregion5.onslaught.client.asset.SoundRegistry;
import org.amityregion5.onslaught.client.asset.TextureRegistry;
import org.amityregion5.onslaught.client.game.IDrawingLayer;
import org.amityregion5.onslaught.client.music.MusicHandler;
import org.amityregion5.onslaught.client.window.HUDOverlay;
import org.amityregion5.onslaught.client.window.InventoryWindow;
import org.amityregion5.onslaught.client.window.PauseWindow;
import org.amityregion5.onslaught.client.window.Screen;
import org.amityregion5.onslaught.client.window.ShopWindow;
import org.amityregion5.onslaught.common.bullet.IBullet;
import org.amityregion5.onslaught.common.entity.EntityLantern;
import org.amityregion5.onslaught.common.entity.EntityPlayer;
import org.amityregion5.onslaught.common.entity.EntityZombie;
import org.amityregion5.onslaught.common.game.Game;
import org.amityregion5.onslaught.common.game.model.IEntityModel;
import org.amityregion5.onslaught.common.game.model.IParticle;
import org.amityregion5.onslaught.common.game.model.entity.LanternModel;
import org.amityregion5.onslaught.common.game.model.entity.PlayerModel;
import org.amityregion5.onslaught.common.game.model.entity.ZombieModel;
import org.amityregion5.onslaught.common.game.model.particle.HealthPackParticle;
import org.amityregion5.onslaught.common.helper.MathHelper;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;

import box2dLight.ConeLight;
import box2dLight.Light;
import box2dLight.PointLight;
import box2dLight.RayHandler;

/**
 * The class representing the in game screen
 * @author sergeys
 */
public class InGameScreen extends GuiScreen {

	private Game				game; //The game
	private Box2DDebugRenderer	debugRenderer; //The debug renderer
	private PlayerModel			player; //The player
	private RayHandler			rayHandler; //The lighting
	private GlyphLayout			glyph			= new GlyphLayout(); //The glyph
	private Screen				currentWindow; //The current window
	private List<Screen>		overlays; //A list of all overlays
	private boolean				doDebugRender	= false; //Should it debug render
	private OrthographicCamera		inGameCamera; //The camera
	private boolean saveScore = true;
	private TextureRegion backgroundRegion;
	private int u_trans, u_texLoc, u_texSze, u_scale;
	private Mesh quad;

	// Font
	private BitmapFont smallOutlineFont;

	public InGameScreen(GuiScreen prevScreen, Game game, boolean isNewGame) {
		super(prevScreen);

		this.game = game; //Save the game

		//Set lighting stuffs
		Light.setGlobalContactFilter((short) 1, (short) 0, (short) 1);
		rayHandler = new RayHandler(game.getWorld());
		RayHandler.useDiffuseLight(true);
		game.setLighting(rayHandler);

		//Create shape renderer
		//shape = new ShapeRenderer();
		//Create array of overlays
		overlays = new ArrayList<Screen>();

		//Create a light for the player
		ConeLight light = new ConeLight(rayHandler, 250, Color.WHITE.cpy().mul(1, 1, 1, 130f / 255), 15, 0, 0, 0, 30);
		if (isNewGame) {
			//If it is a new game make a new player
			EntityPlayer playerEntity = new EntityPlayer();
			playerEntity.setFriction(0.99f);
			playerEntity.setMass(100);

			player = new PlayerModel(playerEntity, game, this, game.getDifficulty().getStartingMoney(), "*/Players/**.png");
		} else {
			//If it is an old game get a player
			player = game.getSingleplayerPlayer();
			player.setScreen(this);
		}

		//Give the player a light
		player.setLight(light);
		player.setCircleLight(new PointLight(rayHandler, 250, Color.WHITE.cpy().mul(1, 1, 1, 130f / 255), 3, 0, 0));
		player.setSpeed(0.05f);

		//If it is a new game add the player to the game
		if (isNewGame) {
			game.addEntityToWorld(player, 0, 0);
		}
		
		quad = new Mesh(true, 4, 0, 
				new VertexAttribute(Usage.Position, 2, "a_position"), 
				new VertexAttribute(Usage.TextureCoordinates, 2, "a_texCoord0"));
		
		quad.setVertices(new float[] {
			-1f, -1f, 0f, 0f,
			-1f,  1f, 0f, 1f,
			 1f, -1f, 1f, 0f,
			 1f,  1f, 1f, 1f
		});
		
		backgroundRegion = TextureRegistry.getAtlas().findRegion("backgroundTile");
		
		u_trans = Onslaught.instance.backgroundShader.getUniformLocation("u_trans");
		u_texLoc = Onslaught.instance.backgroundShader.getUniformLocation("u_texLoc");
		u_texSze = Onslaught.instance.backgroundShader.getUniformLocation("u_texSze");
		u_scale = Onslaught.instance.backgroundShader.getUniformLocation("u_scale");

		//Add an HUD to the overlays
		overlays.add(new HUDOverlay(this, player));
	}

	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(1f, 1f, 1f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT); // Clear screen

		//Tick the game
		game.tick(delta);

		//Center the camera on the palyer
		inGameCamera.translate(player.getEntity().getBody().getWorldCenter().x - inGameCamera.position.x,
				player.getEntity().getBody().getWorldCenter().y - inGameCamera.position.y);

		//Apply screen vibration
		inGameCamera.translate((float) (player.getScreenVibrate() * game.getRandom().nextDouble() - player.getScreenVibrate() / 2),
				(float) (player.getScreenVibrate() * game.getRandom().nextDouble() - player.getScreenVibrate() / 2));

		//Update the camer
		inGameCamera.update();
		
		backgroundRegion.getTexture().bind();
		Onslaught.instance.backgroundShader.begin();
		Onslaught.instance.backgroundShader.setUniformf(u_trans, new Vector2(inGameCamera.viewportWidth, inGameCamera.viewportHeight).scl(inGameCamera.zoom));
		Onslaught.instance.backgroundShader.setUniformf(u_texLoc, new Vector2(inGameCamera.position.x, inGameCamera.position.y));
		Onslaught.instance.backgroundShader.setUniformf(u_texSze, new Vector2(getWidth(), getHeight()));
		Onslaught.instance.backgroundShader.setUniformf(u_scale, 10.24f);
		quad.render(Onslaught.instance.backgroundShader, GL20.GL_TRIANGLE_STRIP, 0, 4);
		Onslaught.instance.backgroundShader.end();
		batch.begin();

		//Get the old batch matrix
		Matrix4 oldBatchMatrix = batch.getProjectionMatrix().cpy();

		shape.setProjectionMatrix(inGameCamera.combined);
		batch.setProjectionMatrix(inGameCamera.combined);
		
		Rectangle camRect = new Rectangle(inGameCamera.position.x - inGameCamera.viewportWidth/2, inGameCamera.position.y - inGameCamera.viewportHeight/2, inGameCamera.viewportWidth, inGameCamera.viewportHeight);

		//Draw particles and entities
		for (IParticle p : game.getParticles()) {
			for (IDrawingLayer s : p.getBackDrawingLayers()) {
				s.draw(p, batch, shape, camRect);
			}
		}
		for (IEntityModel<?> e : game.getEntities()) {
			for (IDrawingLayer s : e.getDrawingLayers()) {
				s.draw(e, batch, shape, camRect);
			}
		}
		for (IParticle p : game.getParticles()) {
			for (IDrawingLayer s : p.getFrontDrawingLayers()) {
				s.draw(p, batch, shape, camRect);
			}
		}

		//If lighting is enabled
		if (game.isLightingEnabled()) {
			//Apply lighting
			batch.end();
			rayHandler.setCombinedMatrix(inGameCamera);
			rayHandler.updateAndRender();
			batch.begin();
		}

		for (IParticle p : game.getParticles()) {
			for (IDrawingLayer s : p.getPostLightingDrawingLayers()) {
				s.draw(p, batch, shape, camRect);
			}
		}
		batch.setProjectionMatrix(oldBatchMatrix);

		//Call super render
		super.render(delta);

		//Draw bullets
		batch.end();
		shape.setProjectionMatrix(inGameCamera.combined);
		shape.begin(ShapeType.Line);
		for (IBullet bull : new ArrayList<IBullet>(game.getActiveBullets())) {
			if (bull.getEnd() != null && bull.doDraw()) {
				Gdx.gl.glLineWidth(bull.getThickness());
				shape.setColor(bull.getColor());

				Vector3 start = new Vector3(bull.getStart().x, bull.getStart().y, 0);
				Vector3 end = new Vector3(bull.getEnd().x, bull.getEnd().y, 0);

				shape.line(start.x, start.y, end.x, end.y);
			}
			game.getActiveBullets().remove(bull);
		}
		shape.end();
		Gdx.gl.glLineWidth(1);

		//If cheat mode and debug render are enabled
		if (game.isCheatMode()) {
			if (doDebugRender) {
				//Apply the debug renderer
				debugRenderer.render(game.getWorld(), inGameCamera.combined);
			}
		}

		//Apply overlays
		for (Screen overlay : overlays) {
			overlay.drawScreen(delta, inGameCamera);
		}

		//Draw the current window
		if (currentWindow != null) {
			currentWindow.drawScreen(delta, inGameCamera);
		}

		//Update projection matrix
		batch.setProjectionMatrix(inGameCamera.combined);
		batch.begin();
		//Draw the particle max layers
		for (IParticle p : game.getParticles()) {
			for (IDrawingLayer s : p.getMaxDrawingLayers()) {
				s.draw(p, batch, shape, camRect);
			}
		}
		batch.end();
		//Reset the projection matrix
		batch.setProjectionMatrix(oldBatchMatrix);

		Gdx.gl.glEnable(GL20.GL_BLEND);
		if (player.getCurrentWeapon().getTotalAmmo() == 0 ^ player.getCurrentWeapon().getAmmo() == 0) {
			shape.setProjectionMatrix(camera.combined);
			shape.begin(ShapeType.Filled);
			shape.setColor(1, 1, 0, (float)Onslaught.instance.settings.getAAlpha());
			shape.circle(Gdx.input.getX(), getHeight() - Gdx.input.getY(), (float)Onslaught.instance.settings.getARadius(), 20);
			shape.end();
			shape.setProjectionMatrix(inGameCamera.combined);
		}

		if (player.getCurrentWeapon().getTotalAmmo() == 0 && player.getCurrentWeapon().getAmmo() == 0) {
			shape.setProjectionMatrix(camera.combined);
			shape.begin(ShapeType.Filled);
			shape.setColor(1, 0, 0, (float)Onslaught.instance.settings.getAAlpha());
			shape.circle(Gdx.input.getX(), getHeight() - Gdx.input.getY(), (float)Onslaught.instance.settings.getARadius(), 20);
			shape.end();
			shape.setProjectionMatrix(inGameCamera.combined);
		}
		Gdx.gl.glDisable(GL20.GL_BLEND);

		//Play sounds
		Iterator<SoundPlayingData> iterator = player.getSoundsToPlay().listIterator();
		while (iterator.hasNext()) {
			SoundPlayingData soundData = iterator.next();

			Sound sound = SoundRegistry.getSoundsFor(soundData.getSound()).get(0);

			sound.play((float) MathHelper.clamp(0, 1, soundData.getVolume() * Onslaught.instance.settings.getMasterVolume()),
					(float) MathHelper.clamp(0.5, 2, soundData.getPitch()), 0);

			iterator.remove();
		}

		//If the game isnt runnign
		if (!game.isGameRunning()) {
			//Dispose and return to previous screen
			if (game.getDifficulty().getUniqueID().equals("TUTORIAL") || !saveScore) {
				Onslaught.instance.setScreenAndDispose(prevScreen);
			} else {
				Onslaught.instance.setScreenAndDispose(new ScoreMenu(prevScreen, game.getDifficulty(), player.getScore()));
			}
		}
	}

	@Override
	protected void drawScreen(float delta) {
		super.drawScreen(delta);

		//Draw FPS
		glyph.setText(smallOutlineFont, "FPS: " + Gdx.graphics.getFramesPerSecond());
		smallOutlineFont.draw(batch, glyph, 0, glyph.height);

		float y = glyph.height + 5;

		//Draw version
		glyph.setText(smallOutlineFont, "Version " + Onslaught.instance.version);
		smallOutlineFont.draw(batch, glyph, 0, y + glyph.height); y+=glyph.height+5;

		if (doDebugRender) {
			//If debug renderer is enabled draw hostile mob count
			glyph.setText(smallOutlineFont, "Hostile Mobs: " + game.getHostiles());
			smallOutlineFont.draw(batch, glyph, 0, y + glyph.height); y+=glyph.height+5;
		}

		//Mouse world position
		Vector3 mouseCoord = inGameCamera.unproject(new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0));
		//Update player's mouse position
		player.setMousePos(new Vector2(mouseCoord.x, mouseCoord.y));

		//If cheat mode is enabled
		if (game.isCheatMode()) {
			if (Gdx.input.isKeyJustPressed(Keys.L)) {
				LanternModel lantern = new LanternModel(new EntityLantern(), game, LanternModel.getLIGHT_COLOR(), "Core/Entity/Lantern/0.png", "Lantern_0", 90);
				lantern.setLight(new PointLight(rayHandler, 300, lantern.getColor(), 10, mouseCoord.x, mouseCoord.y));
				lantern.getEntity().setFriction(0.99f);
				lantern.getEntity().setMass(10);

				game.addEntityToWorld(lantern, mouseCoord.x, mouseCoord.y);
			}
			if (Gdx.input.isKeyJustPressed(Keys.F3)) {
				doDebugRender = !doDebugRender;
			}
			if (Gdx.input.isKeyJustPressed(Keys.F4)) {
				game.setLightingEnabled(!game.isLightingEnabled());
			}
			if (Gdx.input.isKeyJustPressed(Keys.F5)) {
				MusicHandler.setMusicPlaying(MusicHandler.getCurrentMusicRegex(), false);
			}
			
			if (Gdx.input.isKeyJustPressed(Keys.F6)) {
				game.setAiDisabled(!game.isAIDisabled());
			}
			
			if (Gdx.input.isKeyJustPressed(Keys.F7)){
				Onslaught.instance.weaponRegistry.refreshWeapons();
			}

			if (Gdx.input.isKeyPressed(Keys.G)) {
				inGameCamera.zoom += 0.02;
			}
			if (Gdx.input.isKeyPressed(Keys.H) && inGameCamera.zoom > 0.02) {
				inGameCamera.zoom -= 0.02;
			}

			if (Gdx.input.isKeyJustPressed(Keys.E)) {
				game.makeExplosion(new Vector2(mouseCoord.x, mouseCoord.y), 50, player);
			}

			if (Gdx.input.isKeyJustPressed(Keys.R)) {
				game.addParticleToWorld(new HealthPackParticle(mouseCoord.x, mouseCoord.y, game));
			}

			if (Gdx.input.isKeyJustPressed(Keys.PERIOD)) {
				player.setMoney(player.getMoney() + 1000);
			}

			if (Gdx.input.isKeyJustPressed(Keys.COMMA)) {
				player.setMoney(Math.max(0, player.getMoney() - 1000));
			}

			if (Gdx.input.isButtonPressed(Buttons.RIGHT)) {
				EntityZombie zom = new EntityZombie(0.15f);
				zom.setMass(100);
				zom.setFriction(0.99f);

				ZombieModel model = new ZombieModel(zom, game, 1);

				model.setAllHealth(5);
				model.setSpeed(0.03f);
				model.setRange((float) (zom.getShape().getRadius() * 1.1));
				model.setDamage(5);

				game.addEntityToWorld(model, mouseCoord.x, mouseCoord.y);
			}
		}

		//If shop window pressed open shop window
		if (currentWindow == null && Onslaught.instance.settings.getInput("Shop_Window").isJustDown()) {
			if (currentWindow != null) {
				currentWindow.dispose();
			}
			currentWindow = new ShopWindow(this, player);
		}
		//If inventory window pressed open inventory window
		if (currentWindow == null && Onslaught.instance.settings.getInput("Inventory_Window").isJustDown()) {
			if (currentWindow != null) {
				currentWindow.dispose();
			}
			currentWindow = new InventoryWindow(this, player);
		}

		//If close window pressed close a window or open the pause window
		if (Onslaught.instance.settings.getInput("Close_Window").isJustDown()) {
			if (currentWindow != null) {
				currentWindow.dispose();
				currentWindow = null;
			} else {
				if (game.isSinglePlayer()) {
					currentWindow = new PauseWindow(this, player);
				}
			}
		}
		
		if (game.isSinglePlayer() && currentWindow != null && Onslaught.instance.settings.isAutoPauseInSP() && currentWindow.pauseIfOpenAsWindow()) {
			game.setPaused(true);
		} else {
			game.setPaused(false);
		}
	}

	@Override
	public void resize(int width, int height) {
		super.resize(width, height);

		//Resize the font
		Gdx.app.postRunnable(() -> {			
			if (smallOutlineFont != null) {
				smallOutlineFont.dispose();

				// Size 20 font
				FreeTypeFontParameter parameter = new FreeTypeFontParameter();
				parameter.size = (int) (20 * Onslaught.getYScalar());
				parameter.borderWidth = 1;
				parameter.borderStraight = true;
				parameter.borderColor = new Color(0, 0, 0, 1);

				if (parameter.size < 2) {
					parameter.size = 2;
				}

				smallOutlineFont = Onslaught.instance.fontGenerator.generateFont(parameter);
				// Make the font black
				smallOutlineFont.setColor(1, 1, 1, 1);
			}
		});

		//Calculate correct camera size
		float hUp = height/9;
		float wUp = width/16;

		float avgUp = (wUp + hUp)/2;

		inGameCamera.setToOrtho(false, width/avgUp, height/avgUp);
	}

	@Override
	protected void setUpScreen() {
		super.setUpScreen();
	}

	@Override
	public void show() {
		super.show();

		// Create the font
		FreeTypeFontGenerator generator = Onslaught.instance.fontGenerator;

		FreeTypeFontParameter parameter = new FreeTypeFontParameter();

		parameter.size = 20;
		parameter.borderWidth = 1;
		parameter.borderStraight = true;
		parameter.borderColor = new Color(0, 0, 0, 1);
		smallOutlineFont = generator.generateFont(parameter);

		smallOutlineFont.setColor(1, 1, 1, 1);

		debugRenderer = new Box2DDebugRenderer(true, true, false, true, false, true);

		inGameCamera = new OrthographicCamera(12, 9);

		MusicHandler.setMusicPlaying(MusicHandler.gameMusic);
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
		smallOutlineFont.dispose();
		debugRenderer.dispose();
		game.dispose();
		rayHandler.dispose();
	}

	public Screen getCurrentWindow() {
		return currentWindow;
	}

	public Matrix4 getScreenProjectionMatrix() {
		return batch.getProjectionMatrix();
	}

	public BitmapFont getSmallOutlineFont() {
		return smallOutlineFont;
	}

	public Game getGame() {
		return game;
	}

	public void setCurrentWindow(Screen currentWindow) {
		this.currentWindow = currentWindow;
	}

	public void setSaveScore(boolean saveScore) {
		this.saveScore = saveScore;
	}
}
