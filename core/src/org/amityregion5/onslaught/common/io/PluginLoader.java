package org.amityregion5.onslaught.common.io;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.amityregion5.onslaught.Onslaught;
import org.amityregion5.onslaught.client.asset.TextureRegistry;
import org.amityregion5.onslaught.common.buff.BuffApplicator;
import org.amityregion5.onslaught.common.plugin.IPlugin;
import org.amityregion5.onslaught.common.plugin.PluginContainer;
import org.amityregion5.onslaught.common.plugin.PluginManager;
import org.amityregion5.onslaught.common.weapon.types.IWeapon;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * A class to load plugins
 * @author sergeys
 */
public class PluginLoader {
	//The json parser
	private JsonParser parser = new JsonParser();
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
		Onslaught.log("Plugin Loader: Starting plugin finding process");
		// Loop through the plugin list
		for (FileHandle p : plugins) {
			if (p.isDirectory()) {// If it is a directory
				Onslaught.log("Plugin Loader: Checking: " + p.name());

				//Get metadata json file
				FileHandle meta = p.child("plugin.json");
				//If it exists
				if (meta.exists()) {
						//Create a plugin container
						PluginContainer plugin = Onslaught.instance.gson.fromJson(meta.reader(), PluginContainer.class);
						
						if (plugin.getJarLoc() != null) {
							//Get the jar location
							String s = plugin.getJarLoc();
							//If the jar exists
							if (!s.isEmpty() && p.child(s).exists()) {
								try {
									//Load the Jar
									JarLoader loader = new JarLoader(p.child(s).file());

									//Set all of the plugins found and created as the list of plugins in the container
									plugin.setPlugins(loader.getIPlugins());
								} catch (IOException e) {
									e.printStackTrace();
								}
							}
						}
						
						if (plugin.getPlugins() == null) {
							plugin.setPlugins(new ArrayList<IPlugin>());
						}

						//Set the plugin folder location path
						plugin.setPluginFolderLoc(p.path());

						//Log that it was found
						Onslaught.log("Plugin Loader: Plugin Found: " + p.name());
						//Add it to the manager
						manager.addPlugin(plugin);
				}
			}
		}
		Onslaught.log("Plugin Loader: Finished Finding Plugins");
	}

	/**
	 * @param plugins
	 *            the list of files that can possibly be plugins
	 */
	public void loadPlugins(FileHandle[] plugins) {
		Onslaught.instance.gson = Onslaught.instance.gsonBuilder.create();
		
		Onslaught.log("Plugin Loader: Starting loading process");
		// Loop through the plugin list
		for (PluginContainer plugin : manager.getPlugins()) {
			Onslaught.log("Plugin Loader: Loading Plugin: " + plugin.getName());
			loadPlugin(Gdx.files.absolute(plugin.getPluginFolderLoc()), "", plugin);
		}
		Onslaught.log("Plugin Loader: Loading completed");
	}

	private void loadPlugin(FileHandle handle, String prevPath, PluginContainer plugin) {
		//Get path
		String loc = (prevPath.length() > 0 ? prevPath + "/" + handle.name() : handle.name());

		//If it is a directory
		if (handle.isDirectory()) {
			Onslaught.debug("Plugin Loader: Checking Directory: " + loc);
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
						Onslaught.debug("Plugin Loader: Loading Weapon: " + loc);
						loadWeapon(handle, plugin, handle.path());
					}
					break;
				case "Buffs":
					//If the second section is 'Buffs'
					//Load it as a buff
					if (handle.extension().equals("json")) {
						Onslaught.debug("Plugin Loader: Loading Buff: " + loc);
						loadBuff(handle, plugin, handle.path());
					}
					break;
				case "Players":
					//If the second section is 'Players'
					//Load the texture
					if (handle.extension().equals("png")) {
						Onslaught.debug("Plugin Loader: Image Loading: " + loc);
						Gdx.app.postRunnable(() -> TextureRegistry.register(loc, handle));
					}
					break;
				case "Zombies":
					//If the second section is 'Zombies'
					//Load the texture
					if (handle.extension().equals("png")) {
						Onslaught.debug("Plugin Loader: Image Loading: " + loc);
						Gdx.app.postRunnable(() -> TextureRegistry.register(loc, handle));
					}
					break;
			}
		}
	}

	private void loadBuff(FileHandle handle, PluginContainer plugin, String pathName) {
		BuffApplicator app = Onslaught.instance.gson.fromJson(handle.reader(), BuffApplicator.class);
		
		//Add it to the plugin
		plugin.addBuffApplicator(app);
	}

	private void loadWeapon(FileHandle handle, PluginContainer plugin, String pathName) {
		JsonObject o = parser.parse(handle.reader()).getAsJsonObject();
		
		//Get the class name of the weapon
		String className = o.get("className").getAsString();
		//If it doesn't exist register this as an error
		if (className == null) {
			Onslaught.error("Plugin Loader: Failed to load weapon: " + pathName + " Error: No class name");
			return;
		}

		//The matched class
		Class<? extends IWeapon> matchedClass = null;

		//Get all full name matching classes
		List<Class<? extends IWeapon>> fullName = Onslaught.instance.pluginManager.getActivatedWeaponClasses().parallelStream().filter((c)->c.getName().equals(className)).collect(Collectors.toList());
		//If full name not used
		if (fullName == null || fullName.isEmpty() || fullName.size() > 1) {
			List<Class<? extends IWeapon>> matched = Onslaught.instance.pluginManager.getActivatedWeaponClasses().parallelStream().filter((c)->c.getSimpleName().equals(className)).collect(Collectors.toList());
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
					Onslaught.debug("Plugin Loader: Succefully loaded weapon: " + weapon.getName());
					//Add it to list of weapons in plugin
					plugin.addWeapon(weapon);
					return;
				} else {
					//If not successful log the error
					Onslaught.error("Plugin Loader: Failed to load weapon: " + pathName + " Error: Weapon Loading Failed");
				}
			} catch (InstantiationException | IllegalAccessException e) {
				e.printStackTrace();
			}
		}
		Onslaught.error("Plugin Loader: Failed to load weapon: " + pathName + " Error: Class not found");
	}
}
