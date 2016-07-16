package org.amityregion5.onslaught.common.weapon.data;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.amityregion5.onslaught.client.asset.TextureRegistry;
import org.amityregion5.onslaught.common.buff.Buff;
import org.amityregion5.onslaught.common.util.MapUtil;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;

public class PlaceableWeaponData implements IWeaponDataBase {
	//Variables
	private double			price, ammoPrice, preFireDelay, postFireDelay, reloadTime, maxRange, gameScale, gameOffX, gameOffY, warmup;
	private int				maxAmmo, gameOrgX, gameOrgY;
	private String			iconTextureString, gameTextureString, placingObject;
	private List<SoundData>	sounds;
	private HashMap<String, JsonElement> extraData;
	private Buff			buff;

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
	 * @return the maxRange
	 */
	public double getMaxRange() {
		return maxRange;
	}

	/**
	 * @param maxRange
	 *            the maxRange to set
	 */
	public void setMaxRange(double maxRange) {
		this.maxRange = maxRange;
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
	 * @return the placingObject
	 */
	public String getPlacingObject() {
		return placingObject;
	}

	/**
	 * @param placingObject
	 *            the placingObject to set
	 */
	public void setPlacingObject(String placingObject) {
		this.placingObject = placingObject;
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
	 * @param warmup
	 *            the warmup to set
	 */
	public void setWarmup(double warmup) {
		this.warmup = warmup;
	}

	public double getWarmup() {
		return warmup;
	}
	
	public HashMap<String, JsonElement> getExtraData() {
		return extraData;
	}
	
	public static class Deserializor implements JsonDeserializer<PlaceableWeaponData> {
		@Override
		public PlaceableWeaponData deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
			return deserialize(json, typeOfT, context, new PlaceableWeaponData());
		}
		public static PlaceableWeaponData deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context, PlaceableWeaponData wd) throws JsonParseException {
			JsonObject o = json.getAsJsonObject();
			
			wd.price = WeaponDataUtils.getClampedDouble(o, "price", 0, Double.MAX_VALUE, 0);
			wd.ammoPrice = WeaponDataUtils.getClampedDouble(o, "ammoPrice", 0, Double.MAX_VALUE, 0);
			wd.maxAmmo = WeaponDataUtils.getClampedInt(o, "maxAmmo", 0, Integer.MAX_VALUE, 0);
			wd.reloadTime = WeaponDataUtils.getClampedDouble(o, "reloadTime", 0, Double.MAX_VALUE, 0);
			wd.warmup = WeaponDataUtils.getClampedDouble(o, "warmup", 0, Double.MAX_VALUE, 0);
			wd.preFireDelay = WeaponDataUtils.getClampedDouble(o, "preFireDelay", 0, Double.MAX_VALUE, 0);
			wd.postFireDelay = WeaponDataUtils.getClampedDouble(o, "postFireDelay", 0, Double.MAX_VALUE, 0);
			wd.gameScale = WeaponDataUtils.getClampedDouble(o, "gameScale", 0, Double.MAX_VALUE, 1);
			wd.gameOffX = WeaponDataUtils.getClampedDouble(o, "gameOffX", -Double.MAX_VALUE, Double.MAX_VALUE, 0);
			wd.gameOffY = WeaponDataUtils.getClampedDouble(o, "gameOffY", -Double.MAX_VALUE, Double.MAX_VALUE, 0);
			wd.gameOrgX = WeaponDataUtils.getClampedInt(o, "gameOriginX", 0, Integer.MAX_VALUE, 0);
			wd.gameOrgY = WeaponDataUtils.getClampedInt(o, "gameOriginY", 0, Integer.MAX_VALUE, 0);
			wd.maxRange = WeaponDataUtils.getClampedDouble(o, "maxRange", 0, Double.MAX_VALUE, 0);
			
			if (o.has("object")) {
				wd.placingObject = o.get("object").getAsString();
			} else {
				wd.placingObject = "";
			}
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
			
			//Create list of sounds
			wd.sounds = new ArrayList<SoundData>();
			if (o.has("sounds")) {
				wd.sounds = context.deserialize(o.get("sounds"), new TypeToken<ArrayList<SoundData>>(){}.getType());
			}
			//Load buff
			if (o.has("buff")) {
				wd.buff = context.deserialize(o.get("buff"), Buff.class);
			}
			
			if (o.has("data")) {
				wd.extraData = MapUtil.convertToHashMap(o.get("data").getAsJsonObject().entrySet());
			} else {
				wd.extraData = new HashMap<String, JsonElement>();
			}

			
			return wd;
		}
	}
}
