package org.amityregion5.ZombieGame.common.weapon.data;

import org.json.simple.JSONObject;

public class GrenadeData implements IWeaponDataBase {
	private double	price, ammoPrice, strength, accuracy, fuseTime, throwSpeed,
			preFireDelay, postFireDelay, reloadTime;
	private int		maxAmmo;
	private String iconTextureString, gameTextureString;
	private boolean isAuto;

	public GrenadeData(JSONObject o) {
		if (o.containsKey("price")) {
			price = ((Number) o.get("price")).doubleValue();
		}
		if (o.containsKey("ammoPrice")) {
			ammoPrice = ((Number) o.get("ammoPrice")).doubleValue();
		}
		if (o.containsKey("strength")) {
			strength = ((Number) o.get("strength")).doubleValue();
		}
		if (o.containsKey("fuseTime")) {
			fuseTime = ((Number) o.get("fuseTime")).doubleValue();
		}
		if (o.containsKey("throwSpeed")) {
			throwSpeed = ((Number) o.get("throwSpeed")).doubleValue();
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
		if (o.containsKey("iconTxtr")) {
			iconTextureString = ((String) o.get("iconTxtr"));
		} else {
			iconTextureString = "";
		}
		if (o.containsKey("gameTxtr")) {
			gameTextureString = ((String) o.get("gameTxtr"));
		} else {
			gameTextureString = "";
		}
		if (o.containsKey("isAuto")) {
			isAuto = Boolean.valueOf(((String) o.get("isAuto")));
		}
	}

	/**
	 * @return the throwSpeed
	 */
	public double getThrowSpeed() {
		return throwSpeed;
	}

	/**
	 * @param throwSpeed the throwSpeed to set
	 */
	public void setThrowSpeed(double throwSpeed) {
		this.throwSpeed = throwSpeed;
	}

	/**
	 * @return the fuseTime
	 */
	public double getFuseTime() {
		return fuseTime;
	}

	/**
	 * @param fuseTime the fuseTime to set
	 */
	public void setFuseTime(double fuseTime) {
		this.fuseTime = fuseTime;
	}

	/**
	 * @return the isAuto
	 */
	public boolean isAuto() {
		return isAuto;
	}

	/**
	 * @param isAuto the isAuto to set
	 */
	public void setAuto(boolean isAuto) {
		this.isAuto = isAuto;
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

	/**
	 * @return the iconTextureString
	 */
	public String getIconTextureString() {
		return iconTextureString;
	}

	/**
	 * @param iconTextureString the iconTextureString to set
	 */
	public void setIconTextureString(String iconTextureString) {
		this.iconTextureString = iconTextureString;
	}

	/**
	 * @return the gameTextureString
	 */
	public String getGameTextureString() {
		return gameTextureString;
	}

	/**
	 * @param gameTextureString the gameTextureString to set
	 */
	public void setGameTextureString(String gameTextureString) {
		this.gameTextureString = gameTextureString;
	}

	/**
	 * @return the strength
	 */
	public double getStrength() {
		return strength;
	}

	/**
	 * @param strength the strength to set
	 */
	public void setStrength(double strength) {
		this.strength = strength;
	}

}
