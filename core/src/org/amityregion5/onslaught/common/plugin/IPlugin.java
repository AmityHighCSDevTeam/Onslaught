package org.amityregion5.onslaught.common.plugin;

import org.amityregion5.onslaught.common.game.Game;

/**
 * Must have a constructor with no arguments. Do not do anything in the constructor.
 *
 * @author sergeys
 */
public interface IPlugin {

	/**
	 * Stuff that would normally go in constuctor This is called after all java files are loaded
	 *
	 * @param container
	 */
	public void init(PluginContainer container);

	/**
	 * Do anything that is required for the rest of loading in here This is called before other files of the plugin are loaded
	 */
	public void preLoad();

	/**
	 * Do anything that is required for this plugin to load in here This is called after the other files of this plugin are loaded
	 */
	public void load();

	/**
	 * Do any compatability or plugin communications stuff in here It is recommended to load images, sounds, and keybinds in this step so that any plugins who want to override can use the load step This is called after all plugins are
	 * finished loading
	 */
	public void postLoad();
	
	/**
	 * Do stuff that you need to do when the game is started
	 * 
	 * @param game the game that is started
	 */
	public void onGameStart(Game game);

	/**
	 * Dispose of any plugin files in here No other plugin methods will be called after this
	 */
	public void dispose();
	// public void activate();
	// public void deactivate();
}
