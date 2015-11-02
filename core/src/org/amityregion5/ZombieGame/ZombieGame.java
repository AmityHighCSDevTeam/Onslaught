package org.amityregion5.ZombieGame;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Random;

import org.amityregion5.ZombieGame.client.asset.SoundRegistry;
import org.amityregion5.ZombieGame.client.asset.TextureRegistry;
import org.amityregion5.ZombieGame.client.screen.LoadingScreen;
import org.amityregion5.ZombieGame.client.screen.MainMenu;
import org.amityregion5.ZombieGame.client.settings.InputData;
import org.amityregion5.ZombieGame.client.settings.Settings;
import org.amityregion5.ZombieGame.common.entity.EntityLantern;
import org.amityregion5.ZombieGame.common.game.model.entity.LanternModel;
import org.amityregion5.ZombieGame.common.io.PluginLoader;
import org.amityregion5.ZombieGame.common.plugin.PluginManager;
import org.amityregion5.ZombieGame.common.weapon.WeaponRegistry;
import org.amityregion5.ZombieGame.common.weapon.types.Grenade;
import org.amityregion5.ZombieGame.common.weapon.types.Placeable;
import org.amityregion5.ZombieGame.common.weapon.types.Rocket;
import org.amityregion5.ZombieGame.common.weapon.types.SemiAuto;
import org.amityregion5.ZombieGame.common.weapon.types.Shotgun;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;

import box2dLight.PointLight;

/**
 *
 * @author sergeys
 *
 */
public class ZombieGame extends Game {

	public static ZombieGame	instance;		// The current game
	public static String workingDir;

	public FreeTypeFontGenerator fontGenerator;
	public BitmapFont			mainFont;	// Font that buttons use
	public Texture				buttonTexture;	// Texture that buttons use
	public Texture				missingTexture; //The texture for when texture is missing
	public boolean				isServer;		// Is the current instance a
												// server
	public WeaponRegistry		weaponRegistry; // The registry for the weapons
	public int					width, height;	// The width and height of the
												// screen
	public Random				random;
	public FileHandle			gameData;
	public FileHandle			settingsFile;
	private FileHandle logFile;
	public Settings settings;
	public PluginManager pluginManager;
	public boolean isCheatModeAllowed;

	/**
	 *
	 * @param isServer
	 *            is the game a server
	 * @param cheatMode 
 * @param config 
	 */
	public ZombieGame(boolean isServer, boolean cheatMode) {
		instance = this; // Set the instances
		this.isCheatModeAllowed = cheatMode;
		this.isServer = isServer; // Set if it is a server
		random = new Random();
		try {
			String temp = ZombieGame.class.getProtectionDomain().getCodeSource().getLocation().getPath();
			workingDir = URLDecoder.decode(temp, "UTF-8");
			workingDir = (new File(workingDir).getParent());
		} catch (UnsupportedEncodingException e) {
			workingDir = Gdx.files.getLocalStoragePath(); //Hopefully this will work on your computer if that doesn't
		}
	}

	@Override
	public void create() {
		logFile = Gdx.files
				.absolute(workingDir + "/ZombieGameData/log.log");
		
		logFile.writeString("", false);

		Gdx.app.setLogLevel(Application.LOG_DEBUG); // Set the log level

		//width = Gdx.graphics.getWidth();
		width = 1200;
		height = 900;
		//height = Gdx.graphics.getHeight();

		ZombieGame.log("Loading: Starting the loading process");
		if (!isServer) {
			setScreen(new LoadingScreen()); // Set the screen to a loading
											// screen
		}
		
		

		// Thread for loading the game
		new Thread(
				() -> {
					// The gamedata folder
					gameData = Gdx.files
							.absolute(workingDir + "/ZombieGameData/GameData");
					
					if (!isServer) {
						settingsFile = Gdx.files
								.absolute(workingDir + "/ZombieGameData/settings.json");
						settings = new Settings();
						settings.load();
					}
					
					// "Mod" loading list of mods
					FileHandle[] plugins = gameData.list();
					
					pluginManager = new PluginManager();

					// Create the weapon registry
					weaponRegistry = new WeaponRegistry(pluginManager);

					// Create the plugin loader
					PluginLoader loader = new PluginLoader(pluginManager);
					// Load the plugins
					ZombieGame.log("Loading: Plugins will be loaded from " + gameData.file().getAbsolutePath());
					
					loader.loadPluginMeta(plugins);
					
					pluginManager.loadPluginJars();
					
					pluginManager.getCorePlugin().addWeaponClass(SemiAuto.class);
					pluginManager.getCorePlugin().addWeaponClass(Shotgun.class);
					pluginManager.getCorePlugin().addWeaponClass(Placeable.class);
					pluginManager.getCorePlugin().addWeaponClass(Grenade.class);
					pluginManager.getCorePlugin().addWeaponClass(Rocket.class);
					
					Placeable.registeredObjects.put("Lantern_0", (g, vector)->{
						LanternModel lantern = new LanternModel(new EntityLantern(), g, LanternModel.getLIGHT_COLOR(), "Core/Entity/Lantern/0.png");
						lantern.setLight(new PointLight(g.getLighting(), 300,
								lantern.getColor(), 10, vector.x, vector.y));
						lantern.getEntity().setFriction(0.99f);
						lantern.getEntity().setMass(10);
						return lantern;
					});
					Placeable.registeredObjects.put("Lantern_1", (g, vector)->{
						LanternModel lantern = new LanternModel(new EntityLantern(), g, new Color(1,0,0,1), "Core/Entity/Lantern/1.png");
						lantern.setLight(new PointLight(g.getLighting(), 300,
								lantern.getColor(), 10, vector.x, vector.y));
						lantern.getEntity().setFriction(0.99f);
						lantern.getEntity().setMass(10);
						return lantern;
					});
					
					loader.loadPlugins(plugins);

					// If it is a client
					if (!isServer) {
						// Load the texture for buttons
						ZombieGame.log("Loading: Loading button texture");
						Gdx.app.postRunnable(() -> {
							buttonTexture = new Texture(Gdx.files.internal("images/button.png"));
						});

						// Load the missing texture
						ZombieGame.log("Loading: Loading missing texture");
						Gdx.app.postRunnable(() -> missingTexture = new Texture(
								Gdx.files.internal("images/missing.png")));

						// Create the font generator
						ZombieGame.log("Loading: Loading main font");
						fontGenerator = new FreeTypeFontGenerator(
								Gdx.files.internal("font/Helvetica.ttf"));

						// Size 24 font
						FreeTypeFontParameter parameter = new FreeTypeFontParameter();
						parameter.size = (int) (24 * getYScalar());

						// Generate the font
						Gdx.app.postRunnable(() -> {
							mainFont = fontGenerator.generateFont(parameter);
							// Make the font black
							mainFont.setColor(1, 1, 1, 1);
						});
						
						TextureRegistry.tryRegisterAs("Core/explosion.png", "explosion");
						TextureRegistry.tryRegisterAs("Core/backgroundTile2.png", "backgroundTile");
						TextureRegistry.tryRegisterAs("Core/HealthBox.png", "healthPack");
						//TextureRegistry.tryRegister("Core/backgroundTile.png");
						
						SoundRegistry.tryRegister("Core/Audio/Zombie/ZombieGrowl1.wav");
						SoundRegistry.tryRegister("Core/Audio/Zombie/ZombieGrowl2.wav");
						SoundRegistry.tryRegister("Core/Audio/Zombie/ZombieGrowl3.wav");
						SoundRegistry.tryRegister("Core/Audio/Zombie/ZombieGrowl4.wav");
						SoundRegistry.tryRegister("Core/Audio/Zombie/ZombieGrowl5.wav");
						SoundRegistry.tryRegister("Core/Audio/Zombie/ZombieGrowl6.wav");
						
						SoundRegistry.tryRegister("Core/Audio/explode.wav");
 
						// Go to main menu
						ZombieGame.log("Loading: Loading completed");
						Gdx.app.postRunnable(() -> setScreen(new MainMenu()));
						
						settings.registerInput("Shoot", new InputData(false, Buttons.LEFT));
						settings.registerInput("Melee", new InputData(false, Buttons.RIGHT));
						settings.registerInput("Move_Up", new InputData(true, Keys.W));
						settings.registerInput("Move_Down", new InputData(true, Keys.S));
						settings.registerInput("Move_Right", new InputData(true, Keys.D));
						settings.registerInput("Move_Left", new InputData(true, Keys.A));
						settings.registerInput("Toggle_Flashlight", new InputData(true, Keys.F));
						settings.registerInput("Buy_Ammo", new InputData(true, Keys.B));
						settings.registerInput("Reload", new InputData(true, Keys.R));
						settings.registerInput("Hotbar_1", new InputData(true, Keys.NUM_1));
						settings.registerInput("Hotbar_2", new InputData(true, Keys.NUM_2));
						settings.registerInput("Hotbar_3", new InputData(true, Keys.NUM_3));

						settings.registerInput("Shop_Window", new InputData(true, Keys.P));
						settings.registerInput("Inventory_Window", new InputData(true, Keys.I));
						settings.registerInput("Close_Window", new InputData(true, Keys.ESCAPE));
						
						settings.save();
					}
				}).start();
		;
	}

	@Override
	public void render() {
		super.render();
	}

	@Override
	public void dispose() {
		super.dispose();
		TextureRegistry.dispose();
		SoundRegistry.dispose();
		mainFont.dispose(); // Get rid of all used memory
		buttonTexture.dispose();
		fontGenerator.dispose();
	}

	@Override
	public void pause() {
		super.pause();
	}

	@Override
	public void resize(int width, int height) {
		this.width = width; //Save screen size
		this.height = height;
		
		//error("XSclr: " + getXScalar() + " ySclr: " + getYScalar() + " w: " + this.width + " h: " + this.height + " gw: " + width + " gh: " + height);

		super.resize(width, height);
	}

	@Override
	public void resume() {
		super.resume();
	}
	
	public static void debug(String message){
		Gdx.app.debug("[Debug]", message);
		instance.logFile.writeString("[Debug]: " + message + "\n", true);
	}
	
	public static void log(String message){
		Gdx.app.log("[Log]", message);
		instance.logFile.writeString("[Log]: " + message + "\n", true);
	}
	
	public static void error(String message){
		Gdx.app.error("[ERROR]", message);
		instance.logFile.writeString("[ERROR]: " + message + "\n", true);
	}
	
	public static float getYScalar() {
		return Gdx.graphics.getHeight()/900f;
	}
	
	public static float getXScalar() {
		return Gdx.graphics.getWidth()/1200f;
	}
	
	public static float getScaledY(float y) {
		return y*getYScalar();
	}
	
	public static float getScaledX(float x) {
		return x*getXScalar();
	}
}
