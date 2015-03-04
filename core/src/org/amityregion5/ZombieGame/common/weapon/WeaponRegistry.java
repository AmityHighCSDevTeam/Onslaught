package org.amityregion5.ZombieGame.common.weapon;

import com.badlogic.gdx.utils.Array;


/**
 * 
 * @author sergeys
 *
 */
public class WeaponRegistry {
	
	private Array<IWeapon> weapons = new Array<IWeapon>();
	
	/**
	 * Register a weapon
	 * @param wrapper the WeaponWrapper containing the weapon
	 */
	public void registerWeapon(WeaponWrapper wrapper) {
		IWeapon weapon = wrapper.getWeapon();
		
		weapons.add(weapon);
	}
	
	public Array<IWeapon> getWeapons() {
		return weapons;
	}
}
