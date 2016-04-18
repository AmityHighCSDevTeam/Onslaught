package org.amityregion5.ZombieGame.common.weapon.data;

import java.util.ArrayList;
import java.util.List;

import org.amityregion5.ZombieGame.client.asset.TextureRegistry;
import org.amityregion5.ZombieGame.common.game.buffs.Buff;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class GrenadeData implements IWeaponDataBase {
	//All the variables!
	private double			price, ammoPrice, strength, accuracy, fuseTime, throwSpeed, warmup, preFireDelay, postFireDelay, reloadTime, gameScale, gameOffX,
	gameOffY, size;
	private int				maxAmmo, gameOrgX, gameOrgY;
	private String			iconTextureString, gameTextureString, fieldTextureString;
	private boolean			isAuto;
	private List<SoundData>	sounds;
	private Buff			buff;

	public GrenadeData(JSONObject o) {
		price = WeaponDataUtils.getClampedDouble(o, "price", 0, Double.MAX_VALUE, 0);
		ammoPrice = WeaponDataUtils.getClampedDouble(o, "ammoPrice", 0, Double.MAX_VALUE, 0);
		strength = WeaponDataUtils.getClampedDouble(o, "strength", 0, Double.MAX_VALUE, 0);
		fuseTime = WeaponDataUtils.getClampedDouble(o, "fuseTime", 0, Double.MAX_VALUE, 0);
		throwSpeed = WeaponDataUtils.getClampedDouble(o, "throwSpeed", 0, Double.MAX_VALUE, 0);
		accuracy = WeaponDataUtils.getClampedDouble(o, "accuracy", 0, 360, 0);
		maxAmmo = WeaponDataUtils.getClampedInt(o, "maxAmmo", 0, Integer.MAX_VALUE, 0);
		reloadTime = WeaponDataUtils.getClampedDouble(o, "reloadTime", 0, Double.MAX_VALUE, 0);
		warmup = WeaponDataUtils.getClampedDouble(o, "warmup", 0, Double.MAX_VALUE, 0);
		preFireDelay = WeaponDataUtils.getClampedDouble(o, "preFireDelay", 0, Double.MAX_VALUE, 0);
		postFireDelay = WeaponDataUtils.getClampedDouble(o, "postFireDelay", 0, Double.MAX_VALUE, 0);
		size = WeaponDataUtils.getClampedDouble(o, "size", 0, Double.MAX_VALUE, 0);
		gameScale = WeaponDataUtils.getClampedDouble(o, "gameScale", 0, Double.MAX_VALUE, 1);
		gameOffX = WeaponDataUtils.getClampedDouble(o, "gameOffX", 0, Double.MAX_VALUE, 0);
		gameOffY = WeaponDataUtils.getClampedDouble(o, "gameOffY", 0, Double.MAX_VALUE, 0);
		gameOrgX = WeaponDataUtils.getClampedInt(o, "gameOriginX", 0, Integer.MAX_VALUE, 0);
		gameOrgY = WeaponDataUtils.getClampedInt(o, "gameOriginY", 0, Integer.MAX_VALUE, 0);
		if (o.containsKey("iconTxtr")) {
			iconTextureString = ((String) o.get("iconTxtr"));
			TextureRegistry.tryRegister(iconTextureString);
		} else {
			iconTextureString = "";
		}
		if (o.containsKey("gameTxtr")) {
			gameTextureString = ((String) o.get("gameTxtr"));
			TextureRegistry.tryRegister(gameTextureString);
		} else {
			gameTextureString = "";
		}
		if (o.containsKey("fieldTxtr")) {
			fieldTextureString = ((String) o.get("fieldTxtr"));
			TextureRegistry.tryRegister(fieldTextureString);
		} else {
			fieldTextureString = "";
		}
		if (o.containsKey("isAuto")) {
			isAuto = (Boolean) o.get("isAuto");
		}
		sounds = new ArrayList<SoundData>();
		if (o.containsKey("sounds")) {
			JSONArray arr = (JSONArray) o.get("sounds");

			for (Object obj : arr) {
				JSONObject soundJSON = (JSONObject) obj;
				SoundData sound = SoundData.getSoundData(soundJSON);
				if (sound != null) {
					sounds.add(sound);
				}
			}
		}
		if (o.containsKey("buff")) {
			buff = Buff.getFromJSON((JSONObject) o.get("buff"));
		}
	}

	/**
	 * @return the size
	 */
	public double getSize() {
		return size;
	}

	/**
	 * @param size
	 *            the size to set
	 */
	public void setSize(double size) {
		this.size = size;
	}

	/**
	 * @return the throwSpeed
	 */
	public double getThrowSpeed() {
		return throwSpeed;
	}

	/**
	 * @param throwSpeed
	 *            the throwSpeed to set
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
	 * @param fuseTime
	 *            the fuseTime to set
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
	 * @param isAuto
	 *            the isAuto to set
	 */
	public void setAuto(boolean isAuto) {
		this.isAuto = isAuto;
	}

	/**
	 * @return the price
	 */
	@Override
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
	@Override
	public String getIconTextureString() {
		return iconTextureString;
	}

	/**
	 * @param iconTextureString
	 *            the iconTextureString to set
	 */
	public void setIconTextureString(String iconTextureString) {
		this.iconTextureString = iconTextureString;
	}

	/**
	 * @return the gameTextureString
	 */
	@Override
	public String getGameTextureString() {
		return gameTextureString;
	}

	/**
	 * @param gameTextureString
	 *            the gameTextureString to set
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
	 * @param strength
	 *            the strength to set
	 */
	public void setStrength(double strength) {
		this.strength = strength;
	}

	@Override
	public double getGameTextureScale() {
		return gameScale;
	}

	@Override
	public double getGameTextureOffsetX() {
		return gameOffX;
	}

	@Override
	public double getGameTextureOffsetY() {
		return gameOffY;
	}

	@Override
	public int getGameTextureOriginX() {
		return gameOrgX;
	}

	@Override
	public int getGameTextureOriginY() {
		return gameOrgY;
	}

	/**
	 * @return the sounds
	 */
	public List<SoundData> getSounds() {
		return sounds;
	}

	/**
	 * @param sounds
	 *            the sounds to set
	 */
	public void setSounds(List<SoundData> sounds) {
		this.sounds = sounds;
	}

	public String getFieldTextureString() {
		return fieldTextureString;
	}

	public void setFieldTextureString(String fieldTextureString) {
		this.fieldTextureString = fieldTextureString;
	}

	@Override
	public Buff getBuff() {
		return buff;
	}

	/**
	 * @param warmup
	 *            the warmup to set
	 */
	public void setWarmup(double warmup) {
		this.warmup = warmup;
	}

	public double getWarmup() {
		return warmup;
	}
}
