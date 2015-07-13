package org.amityregion5.ZombieGame.common.weapon;

import org.json.simple.JSONObject;

public class WeaponData {
	private double	price, ammoPrice, damage, knockback, accuracy,
			preFireDelay, postFireDelay, reloadTime;
	private int		maxAmmo;

	public WeaponData(JSONObject o) {
		if (o.containsKey("price")) {
			price = ((Number) o.get("price")).doubleValue();
		}
		if (o.containsKey("ammoPrice")) {
			ammoPrice = ((Number) o.get("ammoPrice")).doubleValue();
		}
		if (o.containsKey("damage")) {
			damage = ((Number) o.get("damage")).doubleValue();
		}
		if (o.containsKey("knockback")) {
			knockback = ((Number) o.get("knockback")).doubleValue();
		}
		if (o.containsKey("accuracy")) {
			accuracy = ((Number) o.get("accuracy")).doubleValue();
		}
		if (o.containsKey("maxAmmo")) {
			maxAmmo = ((Number) o.get("maxAmmo")).intValue();
		}
		if (o.containsKey("reloadTime")) {
			reloadTime = ((Number) o.get("reloadTime")).doubleValue();
		}
		if (o.containsKey("preFireDelay")) {
			preFireDelay = ((Number) o.get("preFireDelay")).doubleValue();
		}
		if (o.containsKey("postFireDelay")) {
			postFireDelay = ((Number) o.get("postFireDelay")).doubleValue();
		}
	}

	/**
	 * @return the price
	 */
	public double getPrice() {
		return price;
	}

	/**
	 * @param price
	 *            the price to set
	 */
	public void setPrice(double price) {
		this.price = price;
	}

	/**
	 * @return the ammoPrice
	 */
	public double getAmmoPrice() {
		return ammoPrice;
	}

	/**
	 * @param ammoPrice
	 *            the ammoPrice to set
	 */
	public void setAmmoPrice(double ammoPrice) {
		this.ammoPrice = ammoPrice;
	}

	/**
	 * @return the damage
	 */
	public double getDamage() {
		return damage;
	}

	/**
	 * @param damage
	 *            the damage to set
	 */
	public void setDamage(double damage) {
		this.damage = damage;
	}

	/**
	 * @return the knockback
	 */
	public double getKnockback() {
		return knockback;
	}

	/**
	 * @param knockback
	 *            the knockback to set
	 */
	public void setKnockback(double knockback) {
		this.knockback = knockback;
	}

	/**
	 * @return the accuracy
	 */
	public double getAccuracy() {
		return accuracy;
	}

	/**
	 * @param accuracy
	 *            the accuracy to set
	 */
	public void setAccuracy(double accuracy) {
		this.accuracy = accuracy;
	}

	/**
	 * @return the preFireDelay
	 */
	public double getPreFireDelay() {
		return preFireDelay;
	}

	/**
	 * @param preFireDelay
	 *            the preFireDelay to set
	 */
	public void setPreFireDelay(double preFireDelay) {
		this.preFireDelay = preFireDelay;
	}

	/**
	 * @return the postFireDelay
	 */
	public double getPostFireDelay() {
		return postFireDelay;
	}

	/**
	 * @param postFireDelay
	 *            the postFireDelay to set
	 */
	public void setPostFireDelay(double postFireDelay) {
		this.postFireDelay = postFireDelay;
	}

	/**
	 * @return the reloadTime
	 */
	public double getReloadTime() {
		return reloadTime;
	}

	/**
	 * @param reloadTime
	 *            the reloadTime to set
	 */
	public void setReloadTime(double reloadTime) {
		this.reloadTime = reloadTime;
	}

	/**
	 * @return the maxAmmo
	 */
	public int getMaxAmmo() {
		return maxAmmo;
	}

	/**
	 * @param maxAmmo
	 *            the maxAmmo to set
	 */
	public void setMaxAmmo(int maxAmmo) {
		this.maxAmmo = maxAmmo;
	}

}
