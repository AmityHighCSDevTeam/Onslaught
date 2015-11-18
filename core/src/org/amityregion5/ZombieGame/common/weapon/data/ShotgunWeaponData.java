package org.amityregion5.ZombieGame.common.weapon.data;

import org.json.simple.JSONObject;

public class ShotgunWeaponData extends WeaponData {
	private int		shots;
	private double	spread;

	public ShotgunWeaponData(JSONObject o) {
		super(o);
		if (o.containsKey("shots")) {
			shots = ((Number) o.get("shots")).intValue();
		}
		if (o.containsKey("spread")) {
			spread = ((Number) o.get("spread")).doubleValue();
		}
	}

	/**
	 * @return the shots
	 */
	public int getShots() {
		return shots;
	}

	/**
	 * @param shots
	 *            the shots to set
	 */
	public void setShots(int shots) {
		this.shots = shots;
	}

	/**
	 * @return the spread
	 */
	public double getSpread() {
		return spread;
	}

	/**
	 * @param spread
	 *            the spread to set
	 */
	public void setSpread(double spread) {
		this.spread = spread;
	}
}
