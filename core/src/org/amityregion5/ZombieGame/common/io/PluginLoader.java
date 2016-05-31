package org.amityregion5.ZombieGame.common.io;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.amityregion5.ZombieGame.ZombieGame;
import org.amityregion5.ZombieGame.client.asset.TextureRegistry;
import org.amityregion5.ZombieGame.common.buff.BuffApplicator;
import org.amityregion5.ZombieGame.common.game.buffs.Buff;
import org.amityregion5.ZombieGame.common.plugin.IPlugin;
import org.amityregion5.ZombieGame.common.plugin.PluginContainer;
import org.amityregion5.ZombieGame.common.plugin.PluginManager;
import org.amityregion5.ZombieGame.common.weapon.types.IWeapon;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;

/**
 * A class to load plugins
 * @author sergeys
 */
public class PluginLoader {

	//The JSONParser
	private static JSONParser	parser	= new JSONParser();
	//The plugin manager
	private PluginManager		manager;

	public PluginLoader(PluginManager pluginManager) {
		manager = pluginManager;
	}

	/**
	 * @param plugins
	 *            the list of files that can possibly be plugins
	 */
	public void loadPluginMeta(FileHandle[] plugins) {
		ZombieGame.log("Plugin Loader: Starting plugin finding process");
		// Loop through the plugin list
		for (FileHandle p : plugins) {
			if (p.isDirectory()) {// If it is a directory
				ZombieGame.log("Plugin Loader: Checking: " + p.name());

				//Get metadata json file
				FileHandle meta = p.child("plugin.json");
				//If it exists
				if (meta.exists()) {
					try { 
						//Create a plugin container
						PluginContainer plugin = new PluginContainer();

						//Load the plugin metadata
						JSONObject pluginMeta = (JSONObject) parser.parse(meta.reader());
						//Get plugin name
						plugin.setName((String) pluginMeta.get("name"));
						//Get plugin description
						plugin.setDesc((String) pluginMeta.get("desc"));
						//If it has a jar location
						if (pluginMeta.containsKey("jarLoc")) {
							//Get the jar location
							String s = (String) pluginMeta.get("jarLoc");
							//If the jar exists
							if (!s.isEmpty() && p.child(s).exists()) {
								//Load the Jar
								JarLoader loader = new JarLoader(p.child(s).file());

								//Set all of the plugins found and created as the list of plugins in the container
								plugin.setPlugins(loader.getIPlugins());
							}
						}
						
						if (plugin.getPlugins() == null) {
							plugin.setPlugins(new ArrayList<IPlugin>());
						}

						//Set the plugin folder location path
						plugin.setPluginFolderLoc(p.path());

						//Log that it was found
						ZombieGame.log("Plugin Loader: Plugin Found: " + p.name());
						//Add it to the manager
						manager.addPlugin(plugin);
					} catch (IOException | ParseException e) {
						e.printStackTrace();
					}
				}
			}
		}
		ZombieGame.log("Plugin Loader: Finished Finding Plugins");
	}

	/**
	 * @param plugins
	 *            the list of files that can possibly be plugins
	 */
	public void loadPlugins(FileHandle[] plugins) {
		ZombieGame.log("Plugin Loader: Starting loading process");
		// Loop through the plugin list
		for (PluginContainer plugin : manager.getPlugins()) {
			ZombieGame.log("Plugin Loader: Loading Plugin: " + plugin.getName());
			loadPlugin(Gdx.files.absolute(plugin.getPluginFolderLoc()), "", plugin);
		}
		ZombieGame.log("Plugin Loader: Loading completed");
	}

	private void loadPlugin(FileHandle handle, String prevPath, PluginContainer plugin) {
		//Get path
		String loc = (prevPath.length() > 0 ? prevPath + "/" + handle.name() : handle.name());

		//If it is a directory
		if (handle.isDirectory()) {
			ZombieGame.debug("Plugin Loader: Checking Directory: " + loc);
			//Load all children that exist and are not hidden
			for (FileHandle subFile : handle.list()) {
				if (subFile.exists() && !subFile.file().isHidden()) {
					loadPlugin(subFile, loc, plugin);
				}
			}
		} else {
			//Load it as a file if it is a file
			loadFile(handle, prevPath, plugin);
		}
	}

	private void loadFile(FileHandle handle, String prevPath, PluginContainer plugin) {
		//Get path
		String loc = (prevPath.length() > 0 ? prevPath + "/" + handle.name() : handle.name());
		//Get the sections in its path
		String[] sections = prevPath.split(Pattern.quote("/"));

		//If there are at least two sections
		if (sections.length >= 2) {
			switch (sections[1]) {
				case "Weapons":
					//If the second section is 'Weapons'
					//Load it as a weapon
					if (handle.extension().equals("json")) {
						ZombieGame.debug("Plugin Loader: Loading Weapon: " + loc);
						try {
							loadWeapon((JSONObject) parser.parse(handle.reader()), plugin, handle.path());
						} catch (IOException | ParseException e) {
							ZombieGame.error("Plugin Loader: Failed to load: " + loc);
							e.printStackTrace();
						}
					}
					break;
				case "Buffs":
					//If the second section is 'Buffs'
					//Load it as a buff
					if (handle.extension().equals("json")) {
						ZombieGame.debug("Plugin Loader: Loading Buff: " + loc);
						try {
							loadBuff((JSONObject) parser.parse(handle.reader()), plugin, handle.path());
						} catch (IOException | ParseException e) {
							ZombieGame.error("Plugin Loader: Failed to load: " + loc);
							e.printStackTrace();
						}
					}
					break;
				case "Players":
					//If the second section is 'Players'
					//Load the texture
					if (handle.extension().equals("png")) {
						ZombieGame.debug("Plugin Loader: Image Loading: " + loc);
						Gdx.app.postRunnable(() -> TextureRegistry.register(loc, handle));
					}
					break;
				case "Zombies":
					//If the second section is 'Zombies'
					//Load the texture
					if (handle.extension().equals("png")) {
						ZombieGame.debug("Plugin Loader: Image Loading: " + loc);
						Gdx.app.postRunnable(() -> TextureRegistry.register(loc, handle));
					}
					break;
			}
		}
	}

	private void loadBuff(JSONObject o, PluginContainer plugin, String pathName) {
		//Create the buff
		Buff buff = new Buff();

		//Get JSON Array
		JSONArray arr = (JSONArray) o.get("buffs");

		//Get name
		String name = (String) o.get("name");

		//Get icon path
		String icon = (String) o.get("icon");

		//Get price
		double price = ((Number) o.get("price")).doubleValue();

		//Loop through array
		for (Object obj : arr) {
			JSONObject aO = (JSONObject) obj;

			//Get type of buff data
			String type = (String) aO.get("type");
			//Get buff data key
			String key = (String) aO.get("key");
			//Get buff data value
			double value = ((Number) aO.get("val")).doubleValue();

			//If the type is multiplicative
			if (type.equals("mult")) {
				//Add it as a muliplicative buff
				buff.addMult(key, value);
			} else if (type.equals("add")) {
				//If it is additive
				//Add it as an additive buff
				buff.addAdd(key, value);
			}
		}

		//Create the buff applicator
		BuffApplicator applicator = new BuffApplicator(buff, name, price, icon);

		//Add it to the plugin
		plugin.addBuffApplicator(applicator);
	}

	private void loadWeapon(JSONObject o, PluginContainer plugin, String pathName) {
		//Get the class name of the weapon
		String className = (String) o.get("className");
		//If it doesn't exist register this as an error
		if (className == null) {
			ZombieGame.error("Plugin Loader: Failed to load weapon: " + pathName + " Error: No class name");
			return;
		}

		//The matched class
		Class<? extends IWeapon> matchedClass = null;

		//Get all full name matching classes
		List<Class<? extends IWeapon>> fullName = ZombieGame.instance.pluginManager.getActivatedWeaponClasses().parallelStream().filter((c)->c.getName().equals(className)).collect(Collectors.toList());
		//If full name not used
		if (fullName == null || fullName.isEmpty() || fullName.size() > 1) {
			List<Class<? extends IWeapon>> matched = ZombieGame.instance.pluginManager.getActivatedWeaponClasses().parallelStream().filter((c)->c.getSimpleName().equals(className)).collect(Collectors.toList());
			//if simple name didnt help return
			if (matched == null || matched.isEmpty() || matched.size() > 1) {
				return;
			} else {
				//if it did help
				matchedClass = matched.get(0);
			}
		} else {
			//If full name found it
			matchedClass = fullName.get(0);
		}

		//If the class is still null
		if (matchedClass == null) {
			return;
		} else {
			//If found load the class
			try {
				//Instantiate weapon
				IWeapon weapon = matchedClass.newInstance();

				//Load it from JSON
				if (weapon.loadWeapon(o, pathName)) {
					//If successful
					ZombieGame.debug("Plugin Loader: Succefully loaded weapon: " + weapon.getName());
					//Add it to list of weapons in plugin
					plugin.addWeapon(weapon);
					return;
				} else {
					//If not successful log the error
					ZombieGame.error("Plugin Loader: Failed to load weapon: " + pathName + " Error: Weapon Loading Failed");
				}
			} catch (InstantiationException | IllegalAccessException e) {
				e.printStackTrace();
			}
		}
		ZombieGame.error("Plugin Loader: Failed to load weapon: " + pathName + " Error: Class not found");
	}
}
