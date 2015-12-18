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
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;

/**
 * @author sergeys
 */
public class ZombieGame extends Game {

	public static ZombieGame	instance;	// The current game
	public static String		workingDir;	// The directory the game works out of

	public FreeTypeFontGenerator	fontGenerator;
	public BitmapFont				mainFont, bigFont;					// Font that buttons use
	public Texture					buttonTexture;						// Texture that buttons use
	public Texture					missingTexture;						// The texture for when texture is
	// missing
	public boolean					isServer;							// Is the current instance a
	// server
	public WeaponRegistry			weaponRegistry;						// The registry for the weapons
	public int						width, height;						// The width and height of the
	// screen
	public Random					random;								// The game's Random Number Generator
	public FileHandle				gameData;							// The gamedata file
	public FileHandle				settingsFile;						// The settings file
	public Settings					settings;							// The settings object
	public PluginManager			pluginManager;						// The plugin manager
	public boolean					isCheatModeAllowed;					// Has cheat mode been enabled
	public String					version	= "Error: Version Not Set";	// The game version
	public String					newestVersion;						// The newest version

	private static float uiscale = 1;

	/**
	 * @param isServer
	 *            is the game a server
	 * @param cheatMode is cheat mode enabled
	 * @throws FileNotFoundException when it is not able to make the output stream
	 */
	public ZombieGame(boolean isServer, boolean cheatMode) throws FileNotFoundException {
		instance = this; // Set the instances
		isCheatModeAllowed = cheatMode; //Set the cheat mode
		this.isServer = isServer; // Set if it is a server
		random = new Random(); //Create the random

		//Determine the working directory
		try {
			String temp = ZombieGame.class.getProtectionDomain().getCodeSource().getLocation().getPath();

			workingDir = URLDecoder.decode(temp, "UTF-8");
			workingDir = (new File(workingDir).getParent());
		} catch (UnsupportedEncodingException e) {
			workingDir = Gdx.files.getLocalStoragePath(); // Hopefully this will work on your computer if that doesn't
		}

		//Log file output stream
		FileOutputStream fos = new FileOutputStream(workingDir + "/ZombieGameData/log.log");

		//Output stream
		System.setOut(new PrintStream(new MultiOutputStream(System.out, fos)));
		//Error stream
		System.setErr(new PrintStream(new MultiOutputStream(System.err, fos)));
	}

	@Override
	public void create() {
		//Read the version File
		FileHandle versionFile = Gdx.files.absolute(workingDir + "/ZombieGameData/version.txt");
		version = versionFile.readString();

		//Start a thread ot determine the most recent verison that has been uploaded
		Thread newerVersionThread = new Thread(() -> {
			try {
				//Get URL
				URL url = new URL("https://raw.githubusercontent.com/AmityHighCSDevTeam/ZombieGame/master/core/ZombieGameData/version.txt");
				//Scanner for the URL
				Scanner s = new Scanner(url.openStream());

				//Time out after 10 seconds
				Thread timeOutThread = new Thread(() -> {
					try {
						Thread.sleep(10000);
					} catch (Exception e) {
						e.printStackTrace();
					}
					s.close();
				} , "Time Out Thread");

				//Start the time out thread
				timeOutThread.setDaemon(true);
				timeOutThread.start();

				//Read the newest version
				newestVersion = s.nextLine();

				//Close the scanner
				s.close();

				debug("Found most up to date version to be = " + newestVersion);
			} catch (IOException e) {
				error("Failed to measure most up to date version.");
				e.printStackTrace();
			}
		} , "Newer Version Thread");

		//Start the internets thread
		newerVersionThread.setDaemon(true);
		newerVersionThread.start();

		//Log the current time and version
		log("Current Time = " + new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(Calendar.getInstance().getTime()));
		log("Version = " + version);

		Gdx.app.setLogLevel(Application.LOG_DEBUG); // Set the log level

		//Set width and height
		width = 1200;
		height = 900;

		ZombieGame.log("Loading: Starting the loading process");
		if (!isServer) {
			setScreen(new LoadingScreen()); // Set the screen to a loading
			// screen
		}

		// Thread for loading the game
		Thread loadingThread = new Thread(() -> {
			// The gamedata folder
			gameData = Gdx.files.absolute(workingDir + "/ZombieGameData/GameData");

			if (!isServer) {
				//Get settings file
				settingsFile = Gdx.files.absolute(workingDir + "/ZombieGameData/settings.json");
				//Create and load the settings
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

			//Load the base plugin data
			loader.loadPluginMeta(plugins);

			//Set the core plugin (Prevents overriding of Core)
			pluginManager.getCorePlugin().setPlugins(Arrays.asList(new IPlugin[] {new CorePlugin()}));

			//Initialize the plugins
			log("Loading: Initializing Plugins");
			pluginManager.getPlugins().forEach((p) -> p.getPlugins().forEach((ip) -> ip.init(p)));

			//Preload the plugins
			log("Loading: Beginning Plugin Preloading");
			pluginManager.getPlugins().forEach((p) -> p.getPlugins().forEach((ip) -> ip.preLoad()));

			//Load guns and stuff
			log("Loading: Loading Plugin Data");
			loader.loadPlugins(plugins);

			//Load the plugin
			log("Loading: Beginning Plugin Loading");
			pluginManager.getPlugins().forEach((p) -> p.getPlugins().forEach((ip) -> ip.load()));

			//Post load the plugin
			log("Loading: Beginning Plugin Postloading");
			pluginManager.getPlugins().forEach((p) -> p.getPlugins().forEach((ip) -> ip.postLoad()));

			// If it is a client
			if (!isServer) {
				// Load the texture for buttons
				ZombieGame.log("Loading: Loading button texture");
				Gdx.app.postRunnable(() -> {
					buttonTexture = new Texture(Gdx.files.internal("images/button.png"));
				});

				// Load the missing texture
				ZombieGame.log("Loading: Loading missing texture");
				Gdx.app.postRunnable(() -> missingTexture = new Texture(Gdx.files.internal("images/missing.png")));

				// Create the font generator
				ZombieGame.log("Loading: Loading main font");
				fontGenerator = new FreeTypeFontGenerator(Gdx.files.internal("font/Helvetica.ttf"));

				// Generate the font
				Gdx.app.postRunnable(() -> {
					// Size 24 font
					FreeTypeFontParameter parameter = new FreeTypeFontParameter();
					parameter.size = (int) (24 * getYScalar());
					parameter.borderWidth = 0.025f;
					parameter.borderColor = Color.WHITE;

					mainFont = fontGenerator.generateFont(parameter);
					// Make the font black
					mainFont.setColor(1, 1, 1, 1);
				});

				// Generate the font
				Gdx.app.postRunnable(() -> {
					// Size 36 font
					FreeTypeFontParameter parameter = new FreeTypeFontParameter();
					parameter.size = (int) (30 * getYScalar());
					parameter.borderWidth = 0.025f;
					parameter.borderColor = Color.WHITE;

					bigFont = fontGenerator.generateFont(parameter);
					// Make the font black
					bigFont.setColor(1, 1, 1, 1);
				});

				// Go to main menu
				ZombieGame.log("Loading: Loading completed");
				Gdx.app.postRunnable(() -> setScreen(new MainMenu()));

				settings.save();
			}
		} , "Main Loading Thread");

		//Start the loading thread
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
		pluginManager.getPlugins().forEach((p) -> p.getPlugins().forEach((ip) -> ip.dispose()));
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
		this.width = width; // Save screen size
		this.height = height;

		if (settings != null) {
			uiscale = (float) settings.getUiScale();
		}

		super.resize(width, height);

		// Generate the font
		Gdx.app.postRunnable(() -> {			
			if (mainFont != null) {
				mainFont.dispose();

				// Size 24 font
				FreeTypeFontParameter parameter = new FreeTypeFontParameter();
				parameter.size = (int) (24 * getYScalar());
				parameter.borderWidth = 0.025f;
				parameter.borderColor = Color.WHITE;

				if (parameter.size < 2) {
					parameter.size = 2;
				}

				mainFont = fontGenerator.generateFont(parameter);
				// Make the font black
				mainFont.setColor(1, 1, 1, 1);
			}
		});

		// Generate the font
		Gdx.app.postRunnable(() -> {
			if (bigFont != null) {
				bigFont.dispose();

				// Size 36 font
				FreeTypeFontParameter parameter = new FreeTypeFontParameter();
				parameter.size = (int) (30 * getYScalar());
				parameter.borderWidth = 0.025f;
				parameter.borderColor = Color.WHITE;

				if (parameter.size < 2) {
					parameter.size = 2;
				}

				bigFont = fontGenerator.generateFont(parameter);
				// Make the font black
				bigFont.setColor(1, 1, 1, 1);
			}
		});

	}

	@Override
	public void resume() {
		super.resume();
	}

	public static void debug(String message) {
		Gdx.app.debug("[Debug]", message);
	}

	public static void log(String message) {
		Gdx.app.log("[Log]", message);
	}

	public static void error(String message) {
		Gdx.app.error("[ERROR]", message);
	}

	public static float getYScalar() {
		return Gdx.graphics.getHeight() / 900f * uiscale;
	}

	public static float getXScalar() {
		return Gdx.graphics.getWidth() / 1600f * uiscale;
	}
	public static float getAScalar() {
		return (getXScalar()+getYScalar())/2;
	}

	public static float getScaledY(float y) {
		return y * getYScalar();
	}

	public static float getScaledX(float x) {
		return x * getXScalar();
	}
}
