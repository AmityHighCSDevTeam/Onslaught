package org.amityregion5.ZombieGame.common.weapon.data;

import java.util.ArrayList;
import java.util.List;

import org.amityregion5.ZombieGame.client.asset.TextureRegistry;
import org.amityregion5.ZombieGame.common.game.buffs.Buff;
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
		//If exists set value of it
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
		if (o.containsKey("warmup")) {
			warmup = ((Number) o.get("warmup")).doubleValue();
		}
		if (o.containsKey("preFireDelay")) {
			preFireDelay = ((Number) o.get("preFireDelay")).doubleValue();
		}
		if (o.containsKey("postFireDelay")) {
			postFireDelay = ((Number) o.get("postFireDelay")).doubleValue();
		}
		//If doesn't exist set to 1
		if (o.containsKey("gameScale")) {
			gameScale = ((Number) o.get("gameScale")).doubleValue();
		} else {
			gameScale = 1;
		}
		if (o.containsKey("gameOffX")) {
			gameOffX = ((Number) o.get("gameOffX")).doubleValue();
		}
		if (o.containsKey("gameOffY")) {
			gameOffY = ((Number) o.get("gameOffY")).doubleValue();
		}
		if (o.containsKey("gameOriginX")) {
			gameOrgX = ((Number) o.get("gameOriginX")).intValue();
		}
		if (o.containsKey("gameOriginY")) {
			gameOrgY = ((Number) o.get("gameOriginY")).intValue();
		}
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
		if (o.containsKey("bulletThickness")) {
			bulletThickness = ((Number) o.get("bulletThickness")).floatValue();
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
