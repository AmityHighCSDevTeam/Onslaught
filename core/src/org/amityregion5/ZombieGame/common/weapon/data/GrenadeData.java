package org.amityregion5.ZombieGame.common.weapon.data;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import org.amityregion5.ZombieGame.client.asset.TextureRegistry;
import org.amityregion5.ZombieGame.common.buff.Buff;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;

public class GrenadeData implements IWeaponDataBase {
	//All the variables!
	private double			price, ammoPrice, strength, accuracy, fuseTime, throwSpeed, warmup, preFireDelay, postFireDelay, reloadTime, gameScale, gameOffX,
	gameOffY, size;
	private int				maxAmmo, gameOrgX, gameOrgY;
	private String			iconTextureString, gameTextureString, fieldTextureString;
	private boolean			isAuto;
	private List<SoundData>	sounds;
	private Buff			buff;

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
	
	public static class Deserializor implements JsonDeserializer<GrenadeData> {
		@Override
		public GrenadeData deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
			return deserialize(json, typeOfT, context, new GrenadeData());
		}
		public static GrenadeData deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context, GrenadeData wd) throws JsonParseException {
			JsonObject o = json.getAsJsonObject();
			wd.price = WeaponDataUtils.getClampedDouble(o, "price", 0, Double.MAX_VALUE, 0);
			wd.ammoPrice = WeaponDataUtils.getClampedDouble(o, "ammoPrice", 0, Double.MAX_VALUE, 0);
			wd.strength = WeaponDataUtils.getClampedDouble(o, "strength", 0, Double.MAX_VALUE, 0);
			wd.fuseTime = WeaponDataUtils.getClampedDouble(o, "fuseTime", 0, Double.MAX_VALUE, 0);
			wd.throwSpeed = WeaponDataUtils.getClampedDouble(o, "throwSpeed", 0, Double.MAX_VALUE, 0);
			wd.accuracy = WeaponDataUtils.getClampedDouble(o, "accuracy", 0, 360, 0);
			wd.maxAmmo = WeaponDataUtils.getClampedInt(o, "maxAmmo", 0, Integer.MAX_VALUE, 0);
			wd.reloadTime = WeaponDataUtils.getClampedDouble(o, "reloadTime", 0, Double.MAX_VALUE, 0);
			wd.warmup = WeaponDataUtils.getClampedDouble(o, "warmup", 0, Double.MAX_VALUE, 0);
			wd.preFireDelay = WeaponDataUtils.getClampedDouble(o, "preFireDelay", 0, Double.MAX_VALUE, 0);
			wd.postFireDelay = WeaponDataUtils.getClampedDouble(o, "postFireDelay", 0, Double.MAX_VALUE, 0);
			wd.size = WeaponDataUtils.getClampedDouble(o, "size", 0, Double.MAX_VALUE, 0);
			wd.gameScale = WeaponDataUtils.getClampedDouble(o, "gameScale", 0, Double.MAX_VALUE, 1);
			wd.gameOffX = WeaponDataUtils.getClampedDouble(o, "gameOffX", -Double.MAX_VALUE, Double.MAX_VALUE, 0);
			wd.gameOffY = WeaponDataUtils.getClampedDouble(o, "gameOffY", -Double.MAX_VALUE, Double.MAX_VALUE, 0);
			wd.gameOrgX = WeaponDataUtils.getClampedInt(o, "gameOriginX", 0, Integer.MAX_VALUE, 0);
			wd.gameOrgY = WeaponDataUtils.getClampedInt(o, "gameOriginY", 0, Integer.MAX_VALUE, 0);
			if (o.has("iconTxtr")) {
				wd.iconTextureString = o.get("iconTxtr").getAsString();
				TextureRegistry.tryRegister(wd.iconTextureString);
			} else {
				wd.iconTextureString = "";
			}
			if (o.has("gameTxtr")) {
				wd.gameTextureString = o.get("gameTxtr").getAsString();
				TextureRegistry.tryRegister(wd.gameTextureString);
			} else {
				wd.gameTextureString = "";
			}
			if (o.has("fieldTxtr")) {
				wd.fieldTextureString = o.get("fieldTxtr").getAsString();
				TextureRegistry.tryRegister(wd.fieldTextureString);
			} else {
				wd.fieldTextureString = "";
			}
			if (o.has("isAuto")) {
				wd.isAuto = o.get("isAuto").getAsBoolean();
			}

			//Create list of sounds
			wd.sounds = new ArrayList<SoundData>();
			if (o.has("sounds")) {
				wd.sounds = context.deserialize(o.get("sounds"), new TypeToken<ArrayList<SoundData>>(){}.getType());
			}
			//Load buff
			if (o.has("buff")) {
				wd.buff = context.deserialize(o.get("buff"), Buff.class);
			}
			
			return wd;
		}
	}
}
