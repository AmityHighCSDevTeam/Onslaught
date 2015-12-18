package org.amityregion5.ZombieGame.common.weapon;

import org.amityregion5.ZombieGame.common.plugin.PluginManager;
import org.amityregion5.ZombieGame.common.weapon.types.IWeapon;

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
}
