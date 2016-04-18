package org.amityregion5.ZombieGame.common.weapon.data;

import org.json.simple.JSONObject;

public class RocketData extends GrenadeData {
	//The acceleration of the rocket
	private double acceleration;

	public RocketData(JSONObject o) {
		super(o);
		acceleration = WeaponDataUtils.getClampedDouble(o, "accel", 0, Double.MAX_VALUE, 0);
	}

	/**
	 * @return the acceleration
	 */
	public double getAcceleration() {
		return acceleration;
	}

	/**
	 * @param acceleration
	 *            the acceleration to set
	 */
	public void setAcceleration(double acceleration) {
		this.acceleration = acceleration;
	}
}
