package org.amityregion5.ZombieGame.common.weapon;

import org.amityregion5.ZombieGame.ZombieGame;
import org.amityregion5.ZombieGame.common.plugin.PluginManager;
import org.amityregion5.ZombieGame.common.weapon.types.IWeapon;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;

/**
 * A mostly obsolete class
 * 
 * @author sergeys
 */
public class WeaponRegistry {
	//The plugin manager
	private PluginManager pluginManager;

	public WeaponRegistry(PluginManager pluginManager) {
		this.pluginManager = pluginManager;
	}

	/**
	 * Gets a gun from its ID
	 * 
	 * @param id the ID of the gun
	 * @return the gun
	 */
	public IWeapon getWeaponFromID(String id) {
		return pluginManager.getActivatedWeapons().parallelStream().filter((w) -> w.getID().equals(id)).findAny().orElse(null);
	}
	
	/**
	 * Refreshes the weapons from their files
	 */
	public void refreshWeapons() {
		ZombieGame.log("Refreshing Weapons");
		JSONParser parser = new JSONParser();
		pluginManager.getActivatedWeapons().forEach((w)->{
			FileHandle fh = Gdx.files.absolute(w.getPathName());
			try {
				JSONObject obj = (JSONObject) parser.parse(fh.reader());
				w.loadWeapon(obj, w.getPathName());
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
		ZombieGame.log("Weapons Refreshed");
	}
}
