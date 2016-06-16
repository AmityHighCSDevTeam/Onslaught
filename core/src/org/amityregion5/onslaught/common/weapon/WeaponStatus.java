package org.amityregion5.onslaught.common.weapon;

public enum WeaponStatus {
	RELOAD("Reloading"),
	WARMUP("Warming Up"),
	COOLDOWN("Cooling down"),
	FIRE("Firing"),
	READY("Ready"),
	MISSING("No Weapon");
	
	private String name;
	
	private WeaponStatus(String nme) {
		name = nme;
	}
	
	public String getName() {
		return name;
	}
}
