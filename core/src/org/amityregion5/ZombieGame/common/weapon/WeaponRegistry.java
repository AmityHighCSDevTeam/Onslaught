package org.amityregion5.ZombieGame.common.weapon;

import java.util.List;

import org.amityregion5.ZombieGame.common.plugin.PluginManager;
import org.amityregion5.ZombieGame.common.weapon.types.IWeapon;

/**
 *
 * @author sergeys
 *
 */
public class WeaponRegistry {
	private PluginManager pluginManager;

	public WeaponRegistry(PluginManager pluginManager) {
		this.pluginManager = pluginManager;
	}
	/*
	public List<IWeapon> getWeapons() {
		return pluginManager.getActivatedWeapons();
	}*/

	public List<Class<? extends IWeapon>> getWeaponClasses() {
		return pluginManager.getActivatedWeaponClasses();
	}
	
	public IWeapon getWeaponFromID(String id) {
		return pluginManager.getActivatedWeapons().parallelStream().filter((w)->w.getID().equals(id)).findAny().orElse(null);
	}
}
