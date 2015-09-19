package org.amityregion5.ZombieGame;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Random;

import org.amityregion5.ZombieGame.client.game.TextureRegistry;
import org.amityregion5.ZombieGame.client.screen.LoadingScreen;
import org.amityregion5.ZombieGame.client.screen.MainMenu;
import org.amityregion5.ZombieGame.common.entity.EntityLantern;
import org.amityregion5.ZombieGame.common.game.model.LanternModel;
import org.amityregion5.ZombieGame.common.io.PluginLoader;
import org.amityregion5.ZombieGame.common.plugin.PluginManager;
import org.amityregion5.ZombieGame.common.weapon.WeaponRegistry;
import org.amityregion5.ZombieGame.common.weapon.types.Grenade;
import org.amityregion5.ZombieGame.common.weapon.types.Placeable;
import org.amityregion5.ZombieGame.common.weapon.types.SemiAuto;
import org.amityregion5.ZombieGame.common.weapon.types.Shotgun;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
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

	public BitmapFont			mainFont;	// Font that buttons use
	public Texture				buttonTexture;	// Texture that buttons use
	public Texture				missingTexture; //The texture for when texture is missing
	public boolean				isServer;		// Is the current instance a
												// server
	public WeaponRegistry		weaponRegistry; // The registry for the weapons
	public int					width, height;	// The width and height of the
												// screen
	public Random				random;

	/**
	 *
	 * @param isServer
	 *            is the game a server
 * @param config 
	 */
	public ZombieGame(boolean isServer) {
		instance = this; // Set the instance
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
		Gdx.app.setLogLevel(Application.LOG_DEBUG); // Set the log level

		width = Gdx.graphics.getWidth();
		height = Gdx.graphics.getHeight();

		Gdx.app.log("Loading", "Starting the loading process");
		if (!isServer) {
			setScreen(new LoadingScreen()); // Set the screen to a loading
											// screen
		}
		
		

		// Thread for loading the game
		new Thread(
				() -> {
					// The gamedata folder
					FileHandle gameData = Gdx.files
							.absolute(workingDir + "/ZombieGameData/GameData");

					
					
					// "Mod" loading list of mods
					FileHandle[] plugins = gameData.list();
					
					PluginManager pluginManager = new PluginManager();

					// Create the weapon registry
					weaponRegistry = new WeaponRegistry(pluginManager);

					// Create the plugin loader
					PluginLoader loader = new PluginLoader(pluginManager);
					// Load the plugins
					Gdx.app.log("Loading", "Plugins will be loaded from " + gameData.file().getAbsolutePath());
					
					loader.loadPluginMeta(plugins);
					
					pluginManager.loadPluginJars();
					
					pluginManager.getCorePlugin().addWeaponClass(SemiAuto.class);
					pluginManager.getCorePlugin().addWeaponClass(Shotgun.class);
					pluginManager.getCorePlugin().addWeaponClass(Placeable.class);
					pluginManager.getCorePlugin().addWeaponClass(Grenade.class);
					
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
						Gdx.app.log("Loading", "Loading button texture");
						Gdx.app.postRunnable(() -> {
							buttonTexture = new Texture(Gdx.files.internal("images/button.png"));
						});

						// Load the missing texture
						Gdx.app.log("Loading", "Loading missing texture");
						Gdx.app.postRunnable(() -> missingTexture = new Texture(
								Gdx.files.internal("images/missing.png")));

						// Create the font generator
						Gdx.app.log("Loading", "Loading main font");
						FreeTypeFontGenerator generator = new FreeTypeFontGenerator(
								Gdx.files.internal("font/Calibri.ttf"));

						// Size 24 font
						FreeTypeFontParameter parameter = new FreeTypeFontParameter();
						parameter.size = 24;

						// Generate the font
						Gdx.app.postRunnable(() -> {
							mainFont = generator.generateFont(parameter);
							// Make the font black
							mainFont.setColor(0, 0, 0, 1);
							// Get rid of generator
							generator.dispose();
						});

						// Go to main menu
						Gdx.app.log("Loading", "Loading completed");
						Gdx.app.postRunnable(() -> setScreen(new MainMenu()));
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
		mainFont.dispose(); // Get rid of all used memory
		buttonTexture.dispose();

	}

	@Override
	public void pause() {
		super.pause();
	}

	@Override
	public void resize(int width, int height) {
		//this.width = width; //Save screen size
		//this.height = height;

		super.resize(width, height);
	}

	@Override
	public void resume() {
		super.resume();
	}
}
