package org.amityregion5.ZombieGame.common.io;

import java.io.IOException;
import java.util.regex.Pattern;

import org.amityregion5.ZombieGame.ZombieGame;
import org.amityregion5.ZombieGame.client.asset.TextureRegistry;
import org.amityregion5.ZombieGame.common.buff.BuffApplicator;
import org.amityregion5.ZombieGame.common.game.buffs.Buff;
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
		ZombieGame.log("Plugin Loader: Starting plugin finding process");
		// Loop through the plugin list
		for (FileHandle p : plugins) {
			if (p.isDirectory()) {// If it is a directory
				ZombieGame.log("Plugin Loader: Checking: " + p.name());

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
						
						ZombieGame.log("Plugin Loader: Plugin Found: " + p.name());
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
	 *
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

	public void loadPlugin(FileHandle handle, String prevPath, PluginContainer plugin) {
		String loc = (prevPath.length() > 0 ? prevPath + "/" + handle.name() : handle.name());

		if (handle.isDirectory()) {
			ZombieGame.debug("Plugin Loader: Checking Directory: " + loc);
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
		//if (handle.extension().equals("png")) {
		//	Gdx.app.debug("[Debug]", "Plugin Loader: Image Found: " + loc);
		//	Gdx.app.postRunnable(()->TextureRegistry.register(loc, handle));
		//}
		if (sections.length >= 2) {
			switch (sections[1]) {
			case "Weapons":
				if (handle.extension().equals("json")) {
					ZombieGame.debug("Plugin Loader: Loading Weapon: " + loc);
					try {
						loadWeapon((JSONObject) parser.parse(handle.reader()), plugin, handle.path());
					} catch (IOException | ParseException e) {
						e.printStackTrace();
					}
				}
				break;
			case "Buffs":
				if (handle.extension().equals("json")) {
					ZombieGame.debug("Plugin Loader: Loading Buff: " + loc);
					try {
						loadBuff((JSONObject) parser.parse(handle.reader()), plugin, handle.path());
					} catch (IOException | ParseException e) {
						e.printStackTrace();
					}
				}
				break;
			case "Players":
				if (handle.extension().equals("png")) {
					ZombieGame.debug("Plugin Loader: Image Loading: " + loc);
					Gdx.app.postRunnable(()->TextureRegistry.register(loc, handle));
				}
				break;
			case "Zombies":
				if (handle.extension().equals("png")) {
					ZombieGame.debug("Plugin Loader: Image Loading: " + loc);
					Gdx.app.postRunnable(()->TextureRegistry.register(loc, handle));
				}
				break;
			}
		}
	}

	private void loadBuff(JSONObject o, PluginContainer plugin, String pathName) {
		Buff buff = new Buff();
		
		JSONArray arr = (JSONArray) o.get("buffs");
		
		String name = (String) o.get("name");
		
		String icon = (String) o.get("icon");
		
		double price = ((Number) o.get("price")).doubleValue();
		
		for (Object obj : arr) {
			JSONObject aO = (JSONObject) obj;
			
			String type = (String) aO.get("type");
			String key = (String) aO.get("key");
			double value = ((Number) aO.get("val")).doubleValue();
			
			if (type.equals("mult")) {
				buff.addMult(key, value);
			} else if (type.equals("add")) {
				buff.addAdd(key, value);
			}
		}
		
		BuffApplicator applicator = new BuffApplicator(buff, name, price, icon);
		
		plugin.addBuffApplicator(applicator);
	}

	private void loadWeapon(JSONObject o, PluginContainer plugin, String pathName) {
		String className = (String) o.get("className");
		if (className == null) {
			ZombieGame.error("Plugin Loader: Failed to load weapon: " + pathName +  " Error: No class name");
			return; 
		}
		for (Class<? extends IWeapon> c : ZombieGame.instance.weaponRegistry.getWeaponClasses()) {
			if (c.getSimpleName().equals(className)) {
				try {
					IWeapon weapon = c.newInstance();

					if (weapon.loadWeapon(o)) {
						ZombieGame.debug("Plugin Loader: Succefully loaded weapon: " + weapon.getName());
						plugin.addWeapon(weapon);
						return;
					} else {
						ZombieGame.error("Plugin Loader: Failed to load weapon: " + pathName + " Error: Weapon Loading Failed");
					}
				} catch (InstantiationException | IllegalAccessException e) {
					e.printStackTrace();
				}
			}
		}
		ZombieGame.error("Plugin Loader: Failed to load weapon: " + pathName + " Error: Class not found");
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
