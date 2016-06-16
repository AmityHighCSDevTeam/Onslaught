package org.amityregion5.onslaught.common.weapon.data;

import java.util.ArrayList;
import java.util.List;

import org.amityregion5.onslaught.client.asset.TextureRegistry;
import org.amityregion5.onslaught.common.game.buffs.Buff;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.badlogic.gdx.graphics.Color;

public class WeaponData implements IWeaponDataBase {
	//Various Variables
	private double			price, ammoPrice, damage, knockback, accuracy, warmup, preFireDelay, postFireDelay, reloadTime, gameScale, gameOffX, gameOffY;
	private int				maxAmmo, gameOrgX, gameOrgY;
	private float			bulletThickness;
	private Color			bulletColor;
	private String			iconTextureString, gameTextureString;
	private boolean			isAuto;
	private List<SoundData>	sounds;
	private Buff			buff;

	public WeaponData(JSONObject o) {
		price = WeaponDataUtils.getClampedDouble(o, "price", 0, Double.MAX_VALUE, 0);
		ammoPrice = WeaponDataUtils.getClampedDouble(o, "ammoPrice", 0, Double.MAX_VALUE, 0);
		damage = WeaponDataUtils.getClampedDouble(o, "damage", 0, Double.MAX_VALUE, 0);
		knockback = WeaponDataUtils.getClampedDouble(o, "knockback", 0, Double.MAX_VALUE, 0);
		ammoPrice = WeaponDataUtils.getClampedDouble(o, "ammoPrice", 0, Double.MAX_VALUE, 0);
		accuracy = WeaponDataUtils.getClampedDouble(o, "accuracy", 0, 360, 0);
		maxAmmo = WeaponDataUtils.getClampedInt(o, "maxAmmo", 0, Integer.MAX_VALUE, 0);
		reloadTime = WeaponDataUtils.getClampedDouble(o, "reloadTime", 0, Double.MAX_VALUE, 0);
		warmup = WeaponDataUtils.getClampedDouble(o, "warmup", 0, Double.MAX_VALUE, 0);
		preFireDelay = WeaponDataUtils.getClampedDouble(o, "preFireDelay", 0, Double.MAX_VALUE, 0);
		postFireDelay = WeaponDataUtils.getClampedDouble(o, "postFireDelay", 0, Double.MAX_VALUE, 0);
		gameScale = WeaponDataUtils.getClampedDouble(o, "gameScale", 0, Double.MAX_VALUE, 1);
		gameOffX = WeaponDataUtils.getClampedDouble(o, "gameOffX", -Double.MAX_VALUE, Double.MAX_VALUE, 0);
		gameOffY = WeaponDataUtils.getClampedDouble(o, "gameOffY", -Double.MAX_VALUE, Double.MAX_VALUE, 0);
		gameOrgX = WeaponDataUtils.getClampedInt(o, "gameOriginX", 0, Integer.MAX_VALUE, 0);
		gameOrgY = WeaponDataUtils.getClampedInt(o, "gameOriginY", 0, Integer.MAX_VALUE, 0);
		bulletThickness = WeaponDataUtils.getClampedFloat(o, "bulletThickness", 0, Float.MAX_VALUE, 0);
		//If doesn't exist set to empty string
		if (o.containsKey("iconTxtr")) {
			iconTextureString = ((String) o.get("iconTxtr"));
			TextureRegistry.tryRegister(iconTextureString);
		} else {
			iconTextureString = "";
		}
		//If doesn't exist set to empty string
		if (o.containsKey("gameTxtr")) {
			gameTextureString = ((String) o.get("gameTxtr"));
			TextureRegistry.tryRegister(gameTextureString);
		} else {
			gameTextureString = "";
		}
		if (o.containsKey("bulletColor")) {
			String color = ((String) o.get("bulletColor"));

			if (color == null) {
				color = "00000000";
			}

			bulletColor = Color.valueOf(color);
		}
		if (o.containsKey("isAuto")) {
			isAuto = (Boolean) o.get("isAuto");
		}
		
		//Create list of sounds
		sounds = new ArrayList<SoundData>();
		if (o.containsKey("sounds")) {
			JSONArray arr = (JSONArray) o.get("sounds");

			//Add all sounds to the list
			for (Object obj : arr) {
				JSONObject soundJSON = (JSONObject) obj;
				SoundData sound = SoundData.getSoundData(soundJSON);
				if (sound != null) {
					sounds.add(sound);
				}
			}
		}
		//Load buff
		if (o.containsKey("buff")) {
			buff = Buff.getFromJSON((JSONObject) o.get("buff"));
		}
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

	/**
	 * @return the bulletThickness
	 */
	public float getBulletThickness() {
		return bulletThickness;
	}

	/**
	 * @param bulletThickness
	 *            the bulletThickness to set
	 */
	public void setBulletThickness(float bulletThickness) {
		this.bulletThickness = bulletThickness;
	}

	/**
	 * @return the bulletColor
	 */
	public Color getBulletColor() {
		return bulletColor;
	}

	/**
	 * @param bulletColor
	 *            the bulletColor to set
	 */
	public void setBulletColor(Color bulletColor) {
		this.bulletColor = bulletColor;
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

	@Override
	public Buff getBuff() {
		return buff;
	}

	/**
	 * @return the warmup
	 */
	public double getWarmup() {
		return warmup;
	}

	/**
	 * @param warmup
	 *            the warmup to set
	 */
	public void setWarmup(double warmup) {
		this.warmup = warmup;
	}
}