package org.amityregion5.ZombieGame.common.io;

import java.io.IOException;
import java.util.regex.Pattern;

import org.amityregion5.ZombieGame.ZombieGame;
import org.amityregion5.ZombieGame.client.game.TextureRegistry;
import org.amityregion5.ZombieGame.common.plugin.PluginContainer;
import org.amityregion5.ZombieGame.common.plugin.PluginManager;
import org.amityregion5.ZombieGame.common.weapon.types.IWeapon;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;

/**
 *
 * @author sergeys
 *
 */
public class PluginLoader {

	private static JSONParser	parser	= new JSONParser();
	private PluginManager manager;

	public PluginLoader(PluginManager pluginManager) {
		manager = pluginManager;
	}

	/**
	 *
	 * @param plugins
	 *            the list of files that can possibly be plugins
	 */
	public void loadPluginMeta(FileHandle[] plugins) {
		Gdx.app.log("Plugin Loader", "Starting plugin finding process");
		// Loop through the plugin list
		for (FileHandle p : plugins) {
			if (p.isDirectory()) {// If it is a directory
				Gdx.app.log("Plugin Loader", "Checking: " + p.name());

				FileHandle meta = p.child("plugin.json");
				if (meta.exists()) {
					try {
						PluginContainer plugin = new PluginContainer();

						JSONObject pluginMeta = (JSONObject) parser.parse(meta.reader());
						plugin.setName((String) pluginMeta.get("name"));
						plugin.setDesc((String) pluginMeta.get("desc"));
						if (pluginMeta.containsKey("jarLoc")) {
							String s = (String) pluginMeta.get("jarLoc");
							if (!s.isEmpty() && p.child(s + ".jar").exists()) {
								plugin.setJarLoc(s);

								//TODO: Load jar
							}
						}
						
						plugin.setPluginFolderLoc(p.path());
						
						Gdx.app.log("Plugin Loader", "Plugin Found: " + p.name());
						manager.addPlugin(plugin);
					} catch (IOException | ParseException e) {
						e.printStackTrace();
					}
				}
			}
		}
		Gdx.app.log("Plugin Loader", "Finished Finding Plugins");
	}

	/**
	 *
	 * @param plugins
	 *            the list of files that can possibly be plugins
	 */
	public void loadPlugins(FileHandle[] plugins) {
		Gdx.app.log("Plugin Loader", "Starting loading process");
		// Loop through the plugin list
		for (PluginContainer plugin : manager.getPlugins()) {
			Gdx.app.log("Plugin Loader", "Loading Plugin: " + plugin.getName());
			loadPlugin(Gdx.files.absolute(plugin.getPluginFolderLoc()), "", plugin);
		}
		Gdx.app.log("Plugin Loader", "Loading completed");
	}

	public void loadPlugin(FileHandle handle, String prevPath, PluginContainer plugin) {
		String loc = (prevPath.length() > 0 ? prevPath + "/" + handle.name() : handle.name());
		Gdx.app.debug("Plugin Loader", "Loading: " + loc);

		if (handle.isDirectory()) {
			for (FileHandle subFile : handle.list()) {
				if (subFile.exists() && !subFile.file().isHidden()) {
					loadPlugin(subFile, loc, plugin);
				}
			}
		} else {
			loadFile(handle, prevPath, plugin);
		}
	}

	private void loadFile(FileHandle handle, String prevPath, PluginContainer plugin) {
		String loc = (prevPath.length() > 0 ? prevPath + "/" + handle.name() : handle.name());
		String[] sections = prevPath.split(Pattern.quote("/"));
		if (handle.extension().equals("png")) {
			Gdx.app.debug("Plugin Loader", "Image Found: " + loc);
			Gdx.app.postRunnable(()->TextureRegistry.register(loc, handle));
		}
		if (sections.length >= 2) {
			switch (sections[1]) {
			case "Weapons":
				if (handle.extension().equals("json")) {
					Gdx.app.debug("Plugin Loader", "Weapon Found: " + loc);
					try {
						loadWeapon((JSONObject) parser.parse(handle.reader()), plugin);
					} catch (IOException | ParseException e) {
						e.printStackTrace();
					}
				}
				break;
			}
		}
	}

	private void loadWeapon(JSONObject o, PluginContainer plugin) {
		String className = (String) o.get("className");
		if (className == null) {
			Gdx.app.debug("Plugin Loader", "Failed to load weapon. Error: No class name");
			return; 
		}
		for (Class<? extends IWeapon> c : ZombieGame.instance.weaponRegistry.getWeaponClasses()) {
			if (c.getSimpleName().equals(className)) {
				try {
					IWeapon weapon = c.newInstance();

					if (weapon.loadWeapon(o)) {
						Gdx.app.debug("Plugin Loader", "Succefully loaded weapon: " + weapon.getName());
						plugin.addWeapon(weapon);
						return;
					} else {
						Gdx.app.debug("Plugin Loader", "Failed to load weapon. Error: Weapon Loading Failed");
					}
				} catch (InstantiationException | IllegalAccessException e) {
					e.printStackTrace();
				}
			}
		}
		Gdx.app.debug("Plugin Loader", "Failed to load weapon. Error: Class not found");
	}

	/*
	 * { "name": "NAME", "desc": "DESCRIPTION", "className": "CLASS_NAME",
	 * "weapon": [ { "price": 0, "ammoPrice": 0, "damage": 0, "knockback": 0,
	 * "accuracy": 0, "maxAmmo": 0, "reloadTime": 0, "preFireDelay": 0,
	 * "postFireDelay": 0 }, { "price": 0, "ammoPrice": 0, "damage": 0,
	 * "knockback": 0, "accuracy": 0, "maxAmmo": 0, "reloadTime": 0,
	 * "preFireDelay": 0, "postFireDelay": 0 } ] }
	 */
}
