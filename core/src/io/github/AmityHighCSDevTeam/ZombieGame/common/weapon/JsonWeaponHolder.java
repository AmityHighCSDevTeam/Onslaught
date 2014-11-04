package io.github.AmityHighCSDevTeam.ZombieGame.common.weapon;

public class JsonWeaponHolder {
	private IWeapon weapon;
	
	public JsonWeaponHolder() {
	}
	
	public JsonWeaponHolder(IWeapon weapon) {
		super();
		this.weapon = weapon;
	}
	
	public IWeapon getWeapon() {
		return weapon;
	}
	
	public void setWeapon(IWeapon weapon) {
		this.weapon = weapon;
	}
}
