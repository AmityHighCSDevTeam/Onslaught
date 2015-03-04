package org.amityregion5.ZombieGame;

import org.amityregion5.ZombieGame.client.screen.LoadingScreen;
import org.amityregion5.ZombieGame.client.screen.MainMenu;
import org.amityregion5.ZombieGame.common.io.PluginLoader;
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
	
	public static ZombieGame instance; //The current game
	
	public BitmapFont buttonFont; //Font that buttons use
	public Texture buttonTexture; //Texture that buttons use
	public boolean isServer; //Is the current instance a server
	public WeaponRegistry weaponRegistry; //The registry for the weapons
	public int width, height; //The width and height of the screen
	
	/**
	 * 
	 * @param isServer is the game a server
	 */
	public ZombieGame(boolean isServer) {
		instance = this; //Set the instance
		this.isServer = isServer; //Set if it is a server
	}
	
	@Override
	public void create () {
		Gdx.app.setLogLevel(Application.LOG_DEBUG); //Set the log level

		width = Gdx.graphics.getWidth();
		height = Gdx.graphics.getHeight();
		
		if (!isServer) {
			setScreen(new LoadingScreen()); //Set the screen to a loading screen
		}
		
		
		//Thread for loading the game
		Gdx.app.postRunnable(new Runnable() { 
			@Override
			public void run() {
				
				//The gamedata folder
				FileHandle gameData = Gdx.files.local("ZombieGameData/GameData");
				
				//"Mod" loading list of mods
				FileHandle[] plugins = gameData.list();
				
				//Create the weapon registry
				weaponRegistry = new WeaponRegistry();
				
				//Create the plugin loader
				PluginLoader loader = new PluginLoader();
				//Load the plugins
				loader.loadPlugin(plugins);
				
				//If it is a client
				if (!isServer) {
					//Load the texture for buttons
					buttonTexture = new Texture(Gdx.files.internal("images/button.png"));
					
					//Create the font generator
					FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("font/Calibri.ttf"));
					
					//Size 24 font
					FreeTypeFontParameter parameter = new FreeTypeFontParameter();
					parameter.size = 24;
					
					//Generate the font
					buttonFont = generator.generateFont(parameter);
					
					//Get rid of generator
					generator.dispose();
					
					//Make the font black
					buttonFont.setColor(0, 0, 0, 1);
					
					//Go to main menu
					setScreen(new MainMenu());
				}
			}
		});
	}
	@Override
	public void render () {
		super.render();
	}
	@Override
	public void dispose() {
		super.dispose();
		buttonFont.dispose(); //Get rid of all used memory
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
