package org.amityregion5.ZombieGame;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Random;
import java.util.Scanner;

import org.amityregion5.ZombieGame.client.asset.SoundRegistry;
import org.amityregion5.ZombieGame.client.asset.TextureRegistry;
import org.amityregion5.ZombieGame.client.screen.LoadingScreen;
import org.amityregion5.ZombieGame.client.screen.MainMenu;
import org.amityregion5.ZombieGame.client.settings.Settings;
import org.amityregion5.ZombieGame.common.MultiOutputStream;
import org.amityregion5.ZombieGame.common.io.PluginLoader;
import org.amityregion5.ZombieGame.common.plugin.CorePlugin;
import org.amityregion5.ZombieGame.common.plugin.IPlugin;
import org.amityregion5.ZombieGame.common.plugin.PluginManager;
import org.amityregion5.ZombieGame.common.weapon.WeaponRegistry;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;

/**
 *
 * @author sergeys
 *
 */
public class ZombieGame extends Game {

	public static ZombieGame	instance;		// The current game
	public static String workingDir;

	public FreeTypeFontGenerator fontGenerator;
	public BitmapFont			mainFont, bigFont;	// Font that buttons use
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
	public Settings settings;
	public PluginManager pluginManager;
	public boolean isCheatModeAllowed;
	public String version = "Error: Version Not Set";
	public String newestVersion = null;


	/**
	 *
	 * @param isServer
	 *            is the game a server
	 * @param cheatMode 
	 * @param config 
	 * @throws FileNotFoundException 
	 */
	public ZombieGame(boolean isServer, boolean cheatMode) throws FileNotFoundException {
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

		FileOutputStream fos = new FileOutputStream(workingDir + "/ZombieGameData/log.log");

		System.setOut(new PrintStream(
				new MultiOutputStream(
						System.out, 
						fos
						)));
		System.setErr(new PrintStream(
				new MultiOutputStream(
						System.err, 
						fos
						)));
	}

	@Override
	public void create() {
		FileHandle versionFile = Gdx.files
				.absolute(workingDir + "/ZombieGameData/version.txt");
		version = versionFile.readString();

		Thread newerVersionThread = new Thread(()->{
			try {
				URL url = new URL("https://raw.githubusercontent.com/AmityHighCSDevTeam/ZombieGame/master/core/ZombieGameData/version.txt");
				Scanner s = new Scanner(url.openStream());
				
				Thread timeOutThread = new Thread(()->{
					try {
						Thread.sleep(10000);
					} catch (Exception e) {
						e.printStackTrace();
					}
					s.close();
				}, "Time Out Thread");
				
				timeOutThread.setDaemon(true);
				timeOutThread.start();
				
				newestVersion = s.nextLine();
				
				s.close();
			
				debug("Found most up to date version to be = " + newestVersion);
			} catch (IOException e) {
				error("Failed to measure most up to date version.");
				e.printStackTrace();
			}
		}, "Newer Version Thread");
		
		newerVersionThread.setDaemon(true);
		
		newerVersionThread.start();
		
		log("Current Time = " + new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(Calendar.getInstance().getTime()));
		log("Version = " + version);

		Gdx.app.setLogLevel(Application.LOG_DEBUG); // Set the log level

		width = 1200;
		height = 900;

		ZombieGame.log("Loading: Starting the loading process");
		if (!isServer) {
			setScreen(new LoadingScreen()); // Set the screen to a loading
			// screen
		}

		// Thread for loading the game
		Thread loadingThread = new Thread(
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

					pluginManager.getCorePlugin().setPlugins(Arrays.asList(new IPlugin[]{new CorePlugin()}));

					log("Loading: Initializing Plugins");
					pluginManager.getPlugins().forEach((p)->p.getPlugins().forEach((ip)->ip.init(p)));

					log("Loading: Beginning Plugin Preloading");
					pluginManager.getPlugins().forEach((p)->p.getPlugins().forEach((ip)->ip.preLoad()));

					log("Loading: Loading Plugin Data");
					loader.loadPlugins(plugins);

					log("Loading: Beginning Plugin Loading");
					pluginManager.getPlugins().forEach((p)->p.getPlugins().forEach((ip)->ip.load()));

					log("Loading: Beginning Plugin Postloading");
					pluginManager.getPlugins().forEach((p)->p.getPlugins().forEach((ip)->ip.postLoad()));

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

						// Generate the font
						Gdx.app.postRunnable(() -> {
							// Size 24 font
							FreeTypeFontParameter parameter = new FreeTypeFontParameter();
							parameter.size = (int) (24 * getYScalar());
							
							mainFont = fontGenerator.generateFont(parameter);
							// Make the font black
							mainFont.setColor(1, 1, 1, 1);
						});

						// Generate the font
						Gdx.app.postRunnable(() -> {
							// Size 36 font
							FreeTypeFontParameter parameter = new FreeTypeFontParameter();
							parameter.size = (int) (30 * getYScalar());
							
							bigFont = fontGenerator.generateFont(parameter);
							// Make the font black
							bigFont.setColor(1, 1, 1, 1);
						});

						// Go to main menu
						ZombieGame.log("Loading: Loading completed");
						Gdx.app.postRunnable(() -> setScreen(new MainMenu()));

						settings.save();
					}
				}, "Main Loading Thread");
		
		loadingThread.setDaemon(true);
		loadingThread.start();
	}

	@Override
	public void render() {
		super.render();
	}

	@Override
	public void dispose() {
		super.dispose();
		pluginManager.getPlugins().forEach((p)->p.getPlugins().forEach((ip)->ip.dispose()));
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

		super.resize(width, height);
	}

	@Override
	public void resume() {
		super.resume();
	}

	public static void debug(String message){
		Gdx.app.debug("[Debug]", message);
	}

	public static void log(String message){
		Gdx.app.log("[Log]", message);
	}

	public static void error(String message){
		Gdx.app.error("[ERROR]", message);
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
