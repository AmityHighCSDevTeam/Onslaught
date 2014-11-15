package org.amityregion5.ZombieGame.common.weapon;

/**
 * 
 * @author sergeys
 *
 */
public class WeaponWrapper {
	//The weapon it is storing
	private IWeapon weapon;
	
	public WeaponWrapper() {}
	
	/**
	 * 
	 * @param weapon the weapon to initialize the wrapper with
	 */
	public WeaponWrapper(IWeapon weapon) {
		this.weapon = weapon;
	}
	/**
	 * 
	 * @return the weapon that is being wrapped
	 */
	public IWeapon getWeapon() {
		return weapon;
	}
	/**
	 * 
	 * @param weapon the weapon that should be wrapped
	 */
	public void setWeapon(IWeapon weapon) {
		this.weapon = weapon;
	}
}
